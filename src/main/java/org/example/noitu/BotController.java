package org.example.noitu;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/webhook")
public class BotController {

    // Kho từ điển tiếng Việt mở rộng với hàng trăm từ ghép chuẩn nghĩa 100%
    private final Map<String, List<String>> wordDictionary = new HashMap<>();
    private final Random random = new Random();

    public BotController() {
        initializeDictionary();
    }

    private void initializeDictionary() {
        List<String> rawWords = Arrays.asList(
                // Nhóm vần C - D - Đ
                "chào cờ", "cờ bạc", "bạc mệnh", "mệnh lệnh", "lệnh truyền", "truyền kỳ", "kỳ tích", "tích cực", "cực nhọc", "nhọc nhằn",
                "năng lượng", "lượng giác", "giác ngộ", "ngộ nhận", "nhận thức", "thức giấc", "giấc mơ", "mơ màng", "màng ảnh", "ảnh hưởng",
                "hưởng thụ", "thụ động", "động lực", "lực sĩ", "sĩ quan", "quan sát", "sát cánh", "cánh đồng", "đồng lòng", "lòng vòng",
                "vòng tròn", "tròn trịa", "trị giá", "giá trị", "trí tuệ", "tuệ mẫn", "mẫn cảm", "cảm ơn", "ơn huệ", "huệ trắng",
                "trắng tinh", "tinh tươm", "tươm tất", "tất cả", "cả tin", "tin tưởng", "tưởng tượng", "tượng trưng", "trưng bày", "bày tỏ",
                "tỏ tình", "tình cảm", "cảm xúc", "xúc động", "động viên", "viên mãn", "mãn nguyện", "nguyện ước", "ước mơ", "mơ mộng",
                "mộng du", "du lịch", "lịch trình", "trình bày", "bày vẽ", "vẽ vời", "vời vợi", "buồn bã", "bã trầu", "trầu cau",
                "cau mày", "mày râu", "râu ria", "ria mép", "mép bàn", "bàn ghế", "ghế đá", "đá bóng", "bóng bàn", "bàn bạc",
                "bạc tiền", "tiền tài", "tài chính", "chính sách", "sách vở", "vở kịch", "kịch bản", "bản lĩnh", "lĩnh vực", "vực sâu",
                "sâu thẳm", "thừa nhận", "nhận ra", "ra về", "về nguồn", "nguồn cội", "tài năng", "năng nổ", "tác giả", "tác phẩm",
                "ứng dụng", "dụng cụ", "cụ thể", "thể thao", "thao trường", "hàng hóa", "hóa đơn", "đơn sơ", "sơ khai", "khai phá",
                "phá hoại", "hoại tử", "tử vong", "vong hồn", "hồn nhiên", "nhiên liệu", "liệu pháp", "pháp luật", "luật sư", "nhà cửa",
                "cửa sổ", "sổ sách", "sách báo", "báo chí", "chí hướng", "hướng dẫn", "dẫn dắt", "dắt dìu", "đất nước", "nước non",
                "non sông", "sông ngòi", "ngòi bút", "bút mực", "mực tàu", "tàu xe", "xe cộ", "thời gian", "gian nan", "nan giải",
                "giải quyết", "quyết tâm", "tâm sự", "sự nghiệp", "nghiệp vụ", "vụ việc", "việc làm", "làm ăn", "ăn uống", "nhớ nguồn",
                "nguồn mạch", "mạch máu", "máu lửa", "lửa hồng", "hồng tâm", "tâm huyết", "mạch lạc", "lạc quan", "quan hệ", "hệ thống",
                "thống nhất", "nhất trí", "trí lực", "lực lượng", "lượng thứ", "thứ bậc", "bậc thầy", "thầy giáo", "giáo dục", "dục vọng",
                "vọng tưởng", "tưởng nhớ", "nhớ thương", "thương yêu", "yêu thương", "thương mến", "mến khách", "khách quan", "quan niệm", "niệm chú",
                "chú ý", "ý kiến", "kiến thiết", "thiết kế", "kế hoạch", "hoạt động", "động đậy", "điềm đạm", "đạm bạc", "nhà nước",
                "nước hoa", "hoa hồng", "hồng ngoại", "ngoại ô", "ô tô", "tô điểm", "điểm tô", "nhà xe", "xe đạp", "đạp xe",
                "xe hơi", "hơi thở", "tả tơi", "nhà hàng", "hàng quán", "quán xá", "xá lợi", "lợi ích", "ích kỷ", "kỷ luật",
                "luật lệ", "đài phát", "thanh âm", "âm thanh", "thanh xuân", "xuân sắc", "sắc màu", "màu mè", "hình ảnh", "thời sự",
                "sự việc", "thầm lặng", "lặng lẽ", "lẽ phải", "minh bạch",

                // Bổ sung thêm kho từ vựng phong phú
                "sáng chói", "chói lọi", "lọi đèn", "đèn pin", "pin sạc", "sạc điện", "điện thoại", "thoại ngữ", "ngữ pháp",
                "pháp định", "định mệnh", "mệnh ký", "ký ức", "ức chế", "chế độ", "độ lượng", "thế giới", "giới hạn", "hạn chế",
                "chế tạo", "tạo lập", "lập trường", "trường học", "học tập", "tập trung", "chung thủy", "thủy chung", "chub bình",
                "bình yên", "yên bình", "bình minh", "minh mẫn", "mẫn tiệp", "tiệp khánh", "khánh tiết", "tiết kiệm", "kiệm ước",
                "nước khoáng", "khoáng sản", "sản xuất", "xuất bản", "bản sắc", "sắc sảo", "sảo quyệt", "quyệt thái", "thái độ",
                "độ bền", "bền bỉ", "bỉ sắc", "sắc đẹp", "đẹp đẽ", "đẽo cày", "cày bừa", "bừa bãi", "bãi cát", "cát tường",
                "tường tận", "tận tụy", "tụy tạng", "tạng người", "người đẹp", "đẹp trời", "trời đất", "đất liền", "liền mạch"
        );

        for (String word : rawWords) {
            addWordToDictionary(word);
        }
    }

    private void addWordToDictionary(String word) {
        String cleanedWord = word.trim().toLowerCase();
        String[] parts = cleanedWord.split("\\s+");
        if (parts.length == 2) {
            String firstWord = parts[0];
            wordDictionary.putIfAbsent(firstWord, new ArrayList<>());
            List<String> list = wordDictionary.get(firstWord);
            if (!list.contains(cleanedWord)) {
                list.add(cleanedWord);
            }
        }
    }

    private String getCurrentWord(HttpSession session) {
        String word = (String) session.getAttribute("currentWord");
        if (word == null) {
            word = getRandomWord();
            session.setAttribute("currentWord", word);
        }
        return word;
    }

    private String getRandomWord() {
        List<String> allKeys = new ArrayList<>(wordDictionary.keySet());
        String randomKey = allKeys.get(random.nextInt(allKeys.size()));
        List<String> words = wordDictionary.get(randomKey);
        return words.get(random.nextInt(words.size()));
    }

    @GetMapping
    public String checkBot(HttpSession session) {
        String currentWord = getCurrentWord(session);
        String[] parts = currentWord.trim().split("\\s+");
        String nextRequired = parts[parts.length - 1];
        return "Đề bài hiện tại: <b>" + currentWord + "</b>. Lượt bạn, hãy nhập từ bắt đầu bằng tiếng: '<b>" + nextRequired + "</b>'";
    }

    @GetMapping("/reset")
    public String resetGame(HttpSession session) {
        String newWord = getRandomWord();
        session.setAttribute("currentWord", newWord);
        String[] parts = newWord.trim().split("\\s+");
        String nextRequired = parts[parts.length - 1];
        return "Đã làm mới bàn chơi! Từ của hệ thống là: <b>" + newWord + "</b>. Lượt bạn, hãy nhập từ bắt đầu bằng tiếng: '<b>" + nextRequired + "</b>'";
    }

    @GetMapping("/play")
    public String playGame(@RequestParam String word, HttpSession session) {
        String userWord = word.trim().toLowerCase();
        String[] userParts = userWord.split("\\s+");

        if (userParts.length != 2) {
            return "Quy định chỉ được nhập từ gồm đúng 2 tiếng (2 từ)!";
        }

        String currentWord = getCurrentWord(session);
        String[] currentParts = currentWord.trim().split("\\s+");
        String lastWordOfCurrent = currentParts[currentParts.length - 1];
        String firstWordOfUser = userParts[0];

        // Kiểm tra người chơi có nối đúng tiếng cuối của hệ thống không
        if (!firstWordOfUser.equals(lastWordOfCurrent)) {
            return "Sai quy tắc! Từ của bạn phải bắt đầu bằng tiếng '<b>" + lastWordOfCurrent + "</b>'";
        }

        // Lấy tiếng cuối cùng của từ người chơi vừa nhập để làm gốc cho Bot tìm từ tiếp theo
        String lastWordOfUser = userParts[userParts.length - 1];

        List<String> possibleNextWords = wordDictionary.get(lastWordOfUser);
        String matchedNext = null;

        if (possibleNextWords != null && !possibleNextWords.isEmpty()) {
            List<String> validChoices = new ArrayList<>();
            for (String w : possibleNextWords) {
                if (!w.equals(userWord)) {
                    validChoices.add(w);
                }
            }
            if (!validChoices.isEmpty()) {
                matchedNext = validChoices.get(random.nextInt(validChoices.size()));
            }
        }

        // Nếu bot không tìm được từ nối tiếp có nghĩa, bot chịu thua
        if (matchedNext == null) {
            return "Chúc mừng bạn đã thắng! Bot chịu thua vì không tìm được từ có nghĩa nào nối tiếp tiếng '<b>" + lastWordOfUser + "</b>'";
        }

        // Lưu lại từ mới của Bot cho phiên chơi hiện tại
        session.setAttribute("currentWord", matchedNext);
        String[] newParts = matchedNext.split("\\s+");
        String nextRequired = newParts[newParts.length - 1];

        return "Nối chuẩn! Bot đáp lại: <b>" + matchedNext + "</b>. Lượt bạn, từ tiếp theo phải bắt đầu bằng tiếng: '<b>" + nextRequired + "</b>'";
    }
}