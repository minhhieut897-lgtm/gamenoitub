package org.example.noitu;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class BotController {

    private static Set<String> dictionary = new HashSet<>();
    private static List<String> starterWords = new ArrayList<>();
    private static boolean isLoaded = false;

    // Khối static khởi động siêu tốc và an toàn tuyệt đối với file trong resources
    static {
        try {
            InputStream inputStream = BotController.class.getClassLoader().getResourceAsStream("words.txt");
            if (inputStream != null) {
                Set<String> tempDict = new HashSet<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
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

                dictionary = tempDict;
                starterWords = tempStarters;
                isLoaded = true;
                System.out.println("Đã nạp thành công toàn bộ " + dictionary.size() + " từ từ file words.txt!");
            } else {
                System.err.println("Không tìm thấy file words.txt trong thư mục resources!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String currentWord = "an ninh";
    private int turnCount = 0;

    @GetMapping("/")
    public String home() {
        return "Bot Nối Từ đang chạy! Tổng số từ trong từ điển: " + dictionary.size();
    }

    @GetMapping("/webhook")
    public String startGame() {
        if (!isLoaded || starterWords.isEmpty()) return "⏳ Kho từ vựng đang khởi tạo...";
        Random random = new Random();
        turnCount = 0;
        currentWord = starterWords.get(random.nextInt(starterWords.size()));
        return "Trò chơi bắt đầu! Từ đầu tiên là: <b>" + currentWord + "</b>. Hãy nối tiếp từ cuối!";
    }

    @GetMapping("/webhook/play")
    public String playWord(@RequestParam("word") String word) {
        if (!isLoaded) return "⏳ Hệ thống đang tải từ điển...";

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
            return "❌ <b>Từ không hợp lệ!</b> Từ này không có trong kho 53k+ từ.<br>Từ hiện tại: <b>" + currentWord + "</b>";
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
        if (!isLoaded || starterWords.isEmpty()) return "⏳ Kho từ vựng đang tải...";
        Random random = new Random();
        turnCount = 0;
        currentWord = starterWords.get(random.nextInt(starterWords.size()));
        return "🔄 Đã bắt đầu ván mới! Từ xuất phát là: <b>" + currentWord + "</b>. Mời bạn đi trước!";
    }
}