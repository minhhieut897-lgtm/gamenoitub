package org.example.noitu;

import jakarta.annotation.PostConstruct; // Hoặc javax.annotation.PostConstruct tùy version Spring Boot của bạn
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

    private List<String> dictionary = new ArrayList<>();
    private List<String> simpleStarterWords = new ArrayList<>();

    private String currentWord = "";
    private int turnCount = 0;

    // Tự động đọc file words.txt từ thư mục resources khi ứng dụng vừa khởi động
    @PostConstruct
    public void initDictionary() {
        try {
            ClassPathResource resource = new ClassPathResource("words.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim().toLowerCase();
                    // Chỉ lấy các từ chuẩn gồm đúng 2 tiếng (có 1 khoảng trắng ở giữa)
                    if (!line.isEmpty() && line.split("\\s+").length == 2) {
                        dictionary.add(line);
                    }
                }
            }

            // LỌC THÔNG MINH: Lọc bỏ những từ mà khi bot ra đề sẽ bị "cụt" (không có từ nào khác nối tiếp được)
            // Chỉ giữ lại các từ an toàn để bot dùng làm từ ra đề ở các lượt đầu
            for (String word : dictionary) {
                String[] parts = word.split(" ");
                String lastSyllable = parts[parts.length - 1];

                // Kiểm tra xem trong từ điển có từ nào bắt đầu bằng lastSyllable không
                boolean hasNextWord = false;
                for (String w : dictionary) {
                    if (w.startsWith(lastSyllable + " ") && !w.equalsIgnoreCase(word)) {
                        hasNextWord = true;
                        break;
                    }
                }

                // Nếu có từ nối tiếp và từ này ngắn gọn (dưới 15 ký tự), đưa vào danh sách từ ra đề của bot
                if (hasNextWord && word.length() <= 15) {
                    simpleStarterWords.add(word);
                }
            }

            System.out.println("Đã nạp thành công " + dictionary.size() + " từ từ file words.txt!");
            System.out.println("Số lượng từ an toàn để bot ra đề: " + simpleStarterWords.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        // 1. Kiểm tra định dạng (đúng 2 tiếng)
        if (userParts.length != 2) {
            return "❌ <b>Lỗi định dạng:</b> Vui lòng nhập chính xác <b>một từ gồm đúng 2 tiếng</b>.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        String firstSyllableOfUser = userParts[0];

        // 2. Kiểm tra luật nối từ
        if (!firstSyllableOfUser.equalsIgnoreCase(lastSyllableOfCurrent)) {
            return "❌ <b>Sai luật nối từ!</b> Từ của bạn phải bắt đầu bằng tiếng <b>'" + lastSyllableOfCurrent + "'</b>.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        // 3. Kiểm tra từ điển hợp lệ (Tra cứu trực tiếp trong kho 53k+ từ từ file words.txt)
        if (!dictionary.contains(word)) {
            return "❌ <b>Từ không hợp lệ!</b> Từ này không có trong từ điển tiếng Việt.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        currentWord = word;
        turnCount++;

        String targetStart = userParts[userParts.length - 1];
        String botReply = null;

        // Ưu tiên chọn từ dễ trong 3 lượt đầu
        if (turnCount < 3 && !simpleStarterWords.isEmpty()) {
            Random rand = new Random();
            for (int i = 0; i < 20; i++) { // Thử tìm ngẫu nhiên vài lần cho mượt
                String w = simpleStarterWords.get(rand.nextInt(simpleStarterWords.size()));
                if (w.startsWith(targetStart + " ") && !w.equalsIgnoreCase(currentWord)) {
                    botReply = w;
                    break;
                }
            }
        }

        // Nếu chưa tìm thấy, quét toàn bộ kho 53k+ từ (dùng cả các từ khó để phản đòn người chơi)
        if (botReply == null) {
            for (String w : dictionary) {
                if (w.startsWith(targetStart + " ") && !w.equalsIgnoreCase(currentWord)) {
                    botReply = w;
                    break;
                }
            }
        }

        // TRƯỜNG HỢP: Người chơi thắng (Bot bí đường phản đòn)
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