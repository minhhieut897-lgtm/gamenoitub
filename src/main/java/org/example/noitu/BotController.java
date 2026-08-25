package org.example.noitu;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/webhook")
public class BotController {

    private final Map<String, List<String>> wordDictionary = new HashMap<>();
    private final Random random = new Random();

    public BotController() {
        initializeDictionary();
    }

    private void initializeDictionary() {
        List<String> rawWords = Arrays.asList(
                // Nhóm từ thông dụng, dễ chơi (ưu tiên cho đầu game)
                "chào cờ", "cờ bạc", "bạc mệnh", "mệnh lệnh", "lệnh truyền", "truyền kỳ", "kỳ tích", "tích cực", "cực nhọc", "nhọc nhằn",
                "năng lượng", "lượng giác", "giác ngộ", "ngộ nhận", "nhận thức", "thức giấc", "giấc mơ", "mơ màng", "màng ảnh", "ảnh hưởng",
                "hưởng thụ", "thụ động", "động lực", "lực sĩ", "sĩ quan", "quan sát", "sát cánh", "cánh đồng", "đồng lòng", "lòng vòng",
                "vòng tròn", "tròn trịa", "trị giá", "giá trị", "trí tuệ", "tuệ mẫn", "mẫn cảm", "cảm ơn", "ơn huệ", "huệ trắng",
                "trắng tinh", "tinh tươm", "tươm tất", "tất cả", "cả tin", "tin tưởng", "tưởng tượng", "tượng trưng", "trưng bày", "bày tỏ",
                "tỏ tình", "tình cảm", "cảm xúc", "xúc động", "động viên", "viên mãn", "mãn nguyện", "nguyện ước", "ước mơ", "mơ mộng",
                "mộng du", "du lịch", "lịch trình", "trình bày", "bày vẽ", "vẽ vời", "buồn bã", "bã trầu", "trầu cau", "cau mày",
                "mày râu", "râu ria", "ria mép", "bàn ghế", "ghế đá", "đá bóng", "bóng bàn", "bàn bạc", "bạc tiền", "tiền tài",
                "tài chính", "chính sách", "sách vở", "vở kịch", "kịch bản", "bản lĩnh", "lĩnh vực", "vực sâu", "sâu thẳm", "thừa nhận",
                "nhận ra", "ra về", "về nguồn", "nguồn cội", "tài năng", "năng nổ", "tác giả", "tác phẩm", "ứng dụng", "dụng cụ",
                "cụ thể", "thể thao", "thao trường", "hàng hóa", "hóa đơn", "đơn sơ", "sơ khai", "khai phá", "phá hoại", "họa mi",
                "tử vong", "vong hồn", "hồn nhiên", "nhiên liệu", "liệu pháp", "pháp luật", "luật sư", "nhà cửa", "cửa sổ", "sổ sách",
                "s sách báo", "báo chí", "chí hướng", "hướng dẫn", "dẫn dắt", "đất nước", "nước non", "non sông", "sông ngòi", "ngòi bút",
                "bút mực", "mực tàu", "tàu xe", "xe cộ", "thời gian", "gian nan", "nan giải", "giải quyết", "quyết tâm", "tâm sự",
                "sự nghiệp", "nghiệp vụ", "vụ việc", "việc làm", "làm ăn", "ăn uống", "nhớ nguồn", "nguồn mạch", "mạch máu", "máu lửa",
                "lửa hồng", "hồng tâm", "tâm huyết", "mạch lạc", "lạc quan", "quan hệ", "hệ thống", "thống nhất", "nhất trí", "trí lực",
                "lực lượng", "lượng thứ", "thứ bậc", "bậc thầy", "thầy giáo", "giáo dục", "dục vọng", "vọng tưởng", "tưởng nhớ",
                "nhớ thương", "thương yêu", "yêu thương", "thương mến", "mến khách", "khách quan", "quan niệm", "niệm chú", "chú ý",
                "ý kiến", "kiến thiết", "thiết kế", "kế hoạch", "hoạt động", "động đậy", "điềm đạm", "đạm bạc", "nhà nước", "nước hoa",
                "hoa hồng", "hồng ngoại", "ngoại ô", "ô tô", "tô điểm", "điểm tô", "nhà xe", "xe đạp", "đạp xe", "xe hơi", "hơi thở",
                "nhà hàng", "hàng quán", "quán xá", "lợi ích", "ích kỷ", "kỷ luật", "luật lệ", "thanh âm", "âm thanh", "thanh xuân",
                "xuân sắc", "sắc màu", "hình ảnh", "thời sự", "sự việc", "thầm lặng", "lặng lẽ", "lẽ phải", "minh bạch", "sáng chói",
                "chói lọi", "đèn pin", "pin sạc", "sạc điện", "điện thoại", "thoại ngữ", "ngữ pháp", "pháp định", "định mệnh",
                "ký ức", "ức chế", "chế độ", "độ lượng", "thế giới", "giới hạn", "hạn chế", "chế tạo", "tạo lập", "lập trường",
                "trường học", "học tập", "tập trung", "chung thủy", "thủy chung", "bình yên", "yên bình", "bình minh", "minh mẫn",
                "tiết kiệm", "kiệm ước", "khoáng sản", "sản xuất", "xuất bản", "bản sắc", "sắc đẹp", "đẹp đẽ", "cày bừa", "bãi cát",
                "cát tường", "tường tận", "tận tụy", "tạng người", "người đẹp", "đẹp trời", "trời đất", "đất liền"
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
            word = getRandomEasyWord(session);
            session.setAttribute("currentWord", word);
        }
        return word;
    }

    private int getTurnCount(HttpSession session) {
        Integer turns = (Integer) session.getAttribute("turnCount");
        if (turns == null) {
            turns = 0;
            session.setAttribute("turnCount", turns);
        }
        return turns;
    }

    private void incrementTurn(HttpSession session) {
        int turns = getTurnCount(session) + 1;
        session.setAttribute("turnCount", turns);
    }

    private String getRandomEasyWord(HttpSession session) {
        session.setAttribute("turnCount", 0);
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
        String newWord = getRandomEasyWord(session);
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

        if (!firstWordOfUser.equals(lastWordOfCurrent)) {
            // Thay đổi câu báo lỗi khi người chơi nhập sai quy tắc (Bot cà khịa nhẹ)
            return "Bạn hãy về học lại tiếng việt đi và hãy thử lại 😂";
        }

        incrementTurn(session);
        int currentTurn = getTurnCount(session);

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
                if (currentTurn < 5) {
                    List<String> friendlyChoices = new ArrayList<>();
                    for (String w : validChoices) {
                        String[] p = w.split("\\s+");
                        String nextKey = p[p.length - 1];
                        if (wordDictionary.containsKey(nextKey) && !wordDictionary.get(nextKey).isEmpty()) {
                            friendlyChoices.add(w);
                        }
                    }
                    if (!friendlyChoices.isEmpty()) {
                        matchedNext = friendlyChoices.get(random.nextInt(friendlyChoices.size()));
                    } else {
                        matchedNext = validChoices.get(random.nextInt(validChoices.size()));
                    }
                } else {
                    matchedNext = validChoices.get(random.nextInt(validChoices.size()));
                }
            }
        }

        // Thay đổi câu thông báo khi Bot chịu thua (Người chơi thắng)
        if (matchedNext == null) {
            return "Bạn là 1 người tài ba ";
        }

        session.setAttribute("currentWord", matchedNext);
        String[] newParts = matchedNext.split("\\s+");
        String nextRequired = newParts[newParts.length - 1];

        return "Nối chuẩn! Bot đáp lại: <b>" + matchedNext + "</b>. Lượt bạn, từ tiếp theo phải bắt đầu bằng tiếng: '<b>" + nextRequired + "</b>'";
    }
}