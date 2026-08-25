package org.example.noitu;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class BotController {

    private Set<String> dictionary = new HashSet<>();
    private List<String> simpleStarterWords = new ArrayList<>();

    private String currentWord = "";
    private int turnCount = 0;

    @PostConstruct
    public void initDictionary() {
        try {
            ClassPathResource resource = new ClassPathResource("words.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim().toLowerCase();
                    if (!line.isEmpty() && line.split("\\s+").length == 2) {
                        dictionary.add(line);
                    }
                }
            }

            // Tối ưu hóa siêu tốc: Dùng cấu trúc Set để check từ nối tiếp O(1) thay vì quét mảng chậm chạp
            for (String word : dictionary) {
                String[] parts = word.split(" ");
                String lastSyllable = parts[parts.length - 1];

                // Kiểm tra xem có từ nào trong từ điển bắt đầu bằng lastSyllable không
                boolean hasNext = false;
                for (String w : dictionary) {
                    if (w.startsWith(lastSyllable + " ")) {
                        hasNext = true;
                        break;
                    }
                }

                if (hasNext && word.length() <= 15) {
                    simpleStarterWords.add(word);
                }
            }

            if (simpleStarterWords.isEmpty()) {
                simpleStarterWords.addAll(dictionary); // Fallback phòng hờ
            }

            System.out.println("Đã nạp thành công " + dictionary.size() + " từ!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/")
    public String home() {
        return "Bot Nối Từ Spring Boot đang chạy thành công trên Render!";
    }

    @GetMapping("/webhook")
    public String startGame() {
        if (simpleStarterWords.isEmpty()) return "Kho từ vựng chưa sẵn sàng!";
        Random random = new Random();
        turnCount = 0;
        currentWord = simpleStarterWords.get(random.nextInt(simpleStarterWords.size()));
        return "Trò chơi bắt đầu! Từ đầu tiên là: <b>" + currentWord + "</b>. Hãy nối tiếp từ cuối!";
    }

    @GetMapping("/webhook/play")
    public String playWord(@RequestParam("word") String word) {
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
        if (simpleStarterWords.isEmpty()) return "Kho từ vựng chưa sẵn sàng!";
        Random random = new Random();
        turnCount = 0;
        currentWord = simpleStarterWords.get(random.nextInt(simpleStarterWords.size()));
        return "🔄 Đã bắt đầu ván mới! Từ xuất phát là: <b>" + currentWord + "</b>. Mời bạn đi trước!";
    }
}