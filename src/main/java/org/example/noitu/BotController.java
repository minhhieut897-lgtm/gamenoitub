package org.example.noitu;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class BotController implements CommandLineRunner {

    private Set<String> dictionary = new HashSet<>();
    private List<String> simpleStarterWords = new ArrayList<>();

    private String currentWord = "an toàn";
    private int turnCount = 0;
    private boolean isLoaded = false;

    // Dùng CommandLineRunner để ứng dụng mở cổng web NGAY LẬP TỨC,
    // việc đọc 53k từ sẽ chạy ở luồng nền không làm block server trên Render nữa.
    @Override
    public void run(String... args) {
        new Thread(() -> {
            try {
                ClassPathResource resource = new ClassPathResource("words.txt");
                Set<String> tempDict = new HashSet<>();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim().toLowerCase();
                        if (!line.isEmpty() && line.split("\\s+").length == 2) {
                            tempDict.add(line);
                        }
                    }
                }

                List<String> tempStarters = new ArrayList<>();
                for (String word : tempDict) {
                    String[] parts = word.split(" ");
                    String lastSyllable = parts[parts.length - 1];

                    boolean hasNext = false;
                    for (String w : tempDict) {
                        if (w.startsWith(lastSyllable + " ")) {
                            hasNext = true;
                            break;
                        }
                    }

                    if (hasNext && word.length() <= 15) {
                        tempStarters.add(word);
                    }
                }

                if (tempStarters.isEmpty()) {
                    tempStarters.addAll(tempDict);
                }

                synchronized (this) {
                    dictionary = tempDict;
                    simpleStarterWords = tempStarters;
                    if (!simpleStarterWords.isEmpty()) {
                        currentWord = simpleStarterWords.get(new Random().nextInt(simpleStarterWords.size()));
                    }
                    isLoaded = true;
                }

                System.out.println("Đã nạp ngầm thành công " + dictionary.size() + " từ!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @GetMapping("/")
    public String home() {
        return "Bot Nối Từ đang chạy! Trạng thái từ điển: " + (isLoaded ? "Đã sẵn sàng (" + dictionary.size() + " từ)" : "Đang tải ngầm...");
    }

    @GetMapping("/webhook")
    public String startGame() {
        if (!isLoaded || simpleStarterWords.isEmpty()) return "⏳ Kho từ vựng đang được tải ở chế độ nền, vui lòng thử lại sau vài giây!";
        Random random = new Random();
        turnCount = 0;
        currentWord = simpleStarterWords.get(random.nextInt(simpleStarterWords.size()));
        return "Trò chơi bắt đầu! Từ đầu tiên là: <b>" + currentWord + "</b>. Hãy nối tiếp từ cuối!";
    }

    @GetMapping("/webhook/play")
    public String playWord(@RequestParam("word") String word) {
        if (!isLoaded) return "⏳ Hệ thống đang khởi tạo từ điển, vui lòng đợi một chút!";

        word = word.trim().replaceAll("\\s+", " ").toLowerCase();

        String[] currentParts = currentWord.split(" ");
        String lastSyllableOfCurrent = currentParts[currentParts.length - 1];

        String[] userParts = word.split(" ");

        if (userParts.length != 2) {
            return "❌ <b>Lỗi định dạng:</b> Vui lòng nhập chính xác <b>một từ gồm đúng 2 tiếng</b>.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        String firstSyllableOfUser = userParts[0];

        if (!firstSyllableOfUser.equalsIgnoreCase(lastSyllableOfCurrent)) {
            return "❌ <b>Sai luật nối từ!</b> Từ của bạn phải bắt đầu bằng tiếng <b>'" + lastSyllableOfCurrent + "'</b>.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        if (!dictionary.contains(word)) {
            return "❌ <b>Từ không hợp lệ!</b> Từ này không có trong từ điển.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        currentWord = word;
        turnCount++;

        String targetStart = userParts[userParts.length - 1];
        String botReply = null;

        for (String w : dictionary) {
            if (w.startsWith(targetStart + " ") && !w.equalsIgnoreCase(currentWord)) {
                botReply = w;
                break;
            }
        }

        if (botReply == null) {
            return "VICTORY:🎉 Bạn quá giỏi, tôi đã bí đường và xin hàng!";
        }

        currentWord = botReply;
        return "✅ Bot nối tiếp: <b>" + botReply + "</b>. Lượt bạn (nối chữ: <b>" + botReply.split(" ")[1] + "</b>)";
    }

    @GetMapping("/webhook/reset")
    public String resetGame() {
        if (!isLoaded || simpleStarterWords.isEmpty()) return "⏳ Kho từ vựng đang tải...";
        Random random = new Random();
        turnCount = 0;
        currentWord = simpleStarterWords.get(random.nextInt(simpleStarterWords.size()));
        return "🔄 Đã bắt đầu ván mới! Từ xuất phát là: <b>" + currentWord + "</b>. Mời bạn đi trước!";
    }
}
