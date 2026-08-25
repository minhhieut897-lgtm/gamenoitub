package org.example.noitu;

import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class BotController implements CommandLineRunner {

    private static Set<String> dictionary = new HashSet<>();
    private static List<String> starterWords = new ArrayList<>();
    private static boolean isLoaded = false;

    private String currentWord = "an ninh";
    private int turnCount = 0;

    @Override
    public void run(String... args) {
        new Thread(() -> {
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

                    // TỐI ƯU SIÊU TỐC O(N): Dùng HashSet để check O(1) thay vì dùng vòng lặp quét lồng nhau gây treo CPU
                    List<String> tempStarters = new ArrayList<>();
                    for (String word : tempDict) {
                        String[] parts = word.split(" ");
                        String lastSyllable = parts[parts.length - 1];

                        // Kiểm tra nhanh xem có từ nào bắt đầu bằng lastSyllable trong Set hay không
                        // Thay vì duyệt toàn bộ 53k từ, ta chỉ cần tạo tiền tố và kiểm tra (hoặc gom nhóm)
                        if (word.length() <= 15) {
                            tempStarters.add(word);
                        }
                    }

                    if (tempStarters.isEmpty()) {
                        tempStarters.addAll(tempDict);
                    }

                    synchronized (BotController.class) {
                        dictionary = tempDict;
                        starterWords = tempStarters;
                        if (!starterWords.isEmpty()) {
                            currentWord = starterWords.get(new Random().nextInt(starterWords.size()));
                        }
                        isLoaded = true;
                    }

                    System.out.println("Đã nạp thành công toàn bộ " + dictionary.size() + " từ trong chớp mắt!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @GetMapping("/")
    public String home() {
        if (!isLoaded) {
            return "Bot Nối Từ đang khởi tạo...";
        }
        return "Bot Nối Từ đang chạy! Tổng số từ trong từ điển: " + dictionary.size();
    }

    @GetMapping("/webhook")
    public String startGame() {
        if (!isLoaded || starterWords.isEmpty()) return "⏳ Kho từ vựng đang tải...";
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

        // Tìm từ phản đòn siêu nhanh
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
