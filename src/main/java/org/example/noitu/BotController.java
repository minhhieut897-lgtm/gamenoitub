package org.example.noitu;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class BotController {

    // Kho từ vựng mẫu chất lượng cao tích hợp trực tiếp, chạy siêu tốc không lo lỗi đọc file trên Render
    private static final List<String> DICTIONARY = Arrays.asList(
            "an ninh", "ninh bình", "bình yên", "yên lặng", "lặng thầm", "thầm kín", "kín đáo",
            "áo dài", "dài lâu", "lâu năm", "năm tháng", "tháng ngày", "ngày đêm", "đêm khuya",
            "học tập", "tập thể", "thể thao", "thao lược", "lược dịch", "dịch giả", "giả vờ",
            "vui vẻ", "vẻ vang", "vang dội", "dội ngược", "ngược xuôi", "xuôi tay", "tay chân",
            "chân thành", "thành thật", "thật thà", "thà rằng", "rằng hay", "hay ho", "ho hắng",
            "màu mè", "mè đheo", "eo hẹp", "hẹp hòi", "hòi thăm", "thăm nom", "nom ngó", "ngó ngàng",
            "công nghệ", "nghệ sĩ", "sĩ phu", "phu phen", "phen này", "này nọ", "nọ kia", "kia kìa",
            "phát tài", "tài lộc", "lộc phát", "phát triển", "triển khai", "khai xuân", "xuân xanh",
            "thanh xuân", "xinh đẹp", "đẹp đẽ", "đẽo cày", "cày bừa", "bừa bãi", "bãi cát", "cát tường"
    );

    private String currentWord = "an ninh";
    private int turnCount = 0;

    @GetMapping("/")
    public String home() {
        return "Bot Nối Từ đã sẵn sàng hoạt động trên Render! Tổng số từ: " + DICTIONARY.size();
    }

    @GetMapping("/webhook")
    public String startGame() {
        Random random = new Random();
        turnCount = 0;
        currentWord = DICTIONARY.get(random.nextInt(DICTIONARY.size()));
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

        if (!DICTIONARY.contains(word)) {
            return "❌ <b>Từ không hợp lệ!</b> Từ này không có trong từ điển của bot.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        currentWord = word;
        turnCount++;

        String targetStart = userParts[userParts.length - 1];
        String botReply = null;

        for (String w : DICTIONARY) {
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
        Random random = new Random();
        turnCount = 0;
        currentWord = DICTIONARY.get(random.nextInt(DICTIONARY.size()));
        return "🔄 Đã bắtend ván mới! Từ xuất phát là: <b>" + currentWord + "</b>. Mời bạn đi trước!";
    }
}