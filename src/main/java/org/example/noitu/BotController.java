package org.example.noitu;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class BotController {

    // Kho từ vựng khổng lồ theo chủ đề Vua Tiếng Việt
    private final List<String> dictionary = Arrays.asList(
            // Vần A - B - C
            "an toàn", "an ninh", "an ủi", "áp lực", "ẩn ý", "áo dài", "áo ấm", "âm thanh", "âm nhạc", "ánh sáng",
            "bà con", "ba ba", "bà ngoại", "bà nội", "bác sĩ", "bạc đãi", "bạch tuộc", "bàn bạc",
            "bàn chân", "bàn ghế", "bàn là", "bàn tay", "bản sắc", "bản tin", "bảng đen", "bánh chưng", "bánh dày", "bánh mì",
            "bảo tàng", "bảo vệ", "bắc cầu", "bắc hải", "bập bẹ", "bật mí", "bầu trời", "bầu bạn", "béo tốt", "bênh vực",
            "bình an", "bình minh", "bình tĩnh", "bí ẩn", "bí mật", "bơ vơ", "bờ biển", "bờ vực", "bụi bặm", "buồn bã",
            "buồn vui", "buổi chiều", "buổi sáng", "buổi trưa", "búp bê", "bứt rứt", "bước chân", "bướng bỉnh",

            // Vần C - D - Đ
            "ca dao", "ca hát", "ca sĩ", "cá chép", "cá heo", "cá mập", "cá voi", "can đảm", "can thiệp", "canh cánh",
            "cao cấp", "cao ngạo", "cao ốc", "cào cào", "càu nhàu", "câu cá", "câu lạc bộ", "cầu chì", "cầu lông", "cầu vồng",
            "cây cối", "cây cỏ", "cha mẹ", "chai lọ", "chăm chỉ", "chăm sóc", "chân thành", "chân tay", "chấp nhận", "chất phác",
            "chật chội", "chầu chực", "chế biến", "chế độ", "chiến thắng", "chiến tranh", "chim chóc", "chính trị", "chóng mặt", "chu đáo",
            "chuẩn bị", "chuồn chuồn", "chuyện trò", "chữ viết", "co giật", "co ro", "cơ bắp", "cơ hội", "cơ sở", "con cháu",
            "con đường", "con người", "côn trùng", "công bằng", "công nghệ", "công nhân", "công việc", "cồng kềnh", "cốt lõi", "cố gắng",
            "cổ kính", "cổ vũ", "cục cằn", "cuộc đời", "cuộc sống", "cuốn hút", "cuộn tròn", "cười duyên", "cương quyết", "cương vị",

            // Vần D - Đ
            "da dẻ", "da cam", "da thịt", "dài dòng", "dăm ba", "dân cư", "dân gian", "dân tộc", "dâng hiến", "dấu hiệu",
            "dấu vết", "dầu gió", "dầu khí", "dạy học", "dăm bông", "dần dần", "dâng trào", "dập dềnh", "dễ dãi", "dễ chịu",
            "dễ thương", "du lịch", "du mục", "du xuân", "dung dịch", "dũng cảm", "dữ dội", "dư dả", "dư âm", "dương lịch",
            "đá banh", "đá quý", "đà điểu", "đại gia", "đại dương", "đại học", "đại lý", "đảm bảo", "đảm đang", "đầm lầy",
            "đất đai", "đất liền", "đặc biệt", "đặc sản", "đăng ký", "đăng quang", "đắm say", "đầu bếp", "đầu độc", "đầu tiên",
            "đập phá", "đất nước", "đầy đủ", "đẹp đẽ", "đêm ngày", "đêm tối", "đi đứng", "đi lại", "điểm mấu", "điểm số",
            "điện ảnh", "điện thoại", "điều kiện", "định mệnh", "đoàn kết", "đoán mò", "đỏ rực", "đói kém", "đơn côi", "đơn giản",
            "đời sống", "đu đủ", "đúng đắn", "đường đi", "đường phố", "đương đầu", "được việc",

            // Vần G - H - K
            "gà gô", "gà trống", "gác xép", "gai góc", "giao lưu", "giảm giá", "gian khổ", "gian nan", "giang sơn", "giao tiếp",
            "giấu giếm", "giết chóc", "giễu cợt", "giảm sút", "giản dị", "giáo dục", "giáo sư", "giàu có", "giấc mơ", "giếng nước",
            "giữ gìn", "giữa đường", "giúp đỡ", "gia đình", "gia tài", "gia vị", "gọn gàng", "gọi điện", "gù lưng", "gửi gắm",
            "hà mã", "hà tiện", "hạ bệ", "hạ cánh", "hạ long", "hải sản", "hải tặc", "hải yến", "hào hứng", "hào phóng",
            "hành động", "hành khách", "hành tinh", "hạnh phúc", "hấp dẫn", "hắt hiu", "hết lòng", "hiền hòa", "hiền lành", "hiểu biết",
            "hiệu quả", "hoa hồng", "hoa quả", "hoài niệm", "hoàn hảo", "hoảng sợ", "hoạt động", "họa sĩ", "học hỏi", "hội chợ",
            "hôm nay", "hồng hào", "hợp tác", "hờn dỗi", "hướng dẫn", "hướng dương", "hương vị", "hưu trí", "hy sinh", "hy vọng",
            "kỳ bí", "kỳ diệu", "kỹ sư", "kỷ niệm", "kỷ luật", "kẻ cắp", "kéo co", "kế hoạch", "kết quả", "khen ngợi",
            "kho báu", "khoa học", "khoẻ mạnh", "khôn ngoan", "không khí", "không gian", "khó khăn", "khởi nghiệp", "khủng long", "khuyến khích",

            // Vần L - M - N - P
            "la đà", "la hét", "lá cờ", "lá gan", "lạc quan", "lai lịch", "làm ăn", "làm việc", "làng xóm", "lan tràn",
            "láng giềng", "lanh lợi", "lão hóa", "lễ hội", "lễ phép", "lịch sự", "lịch trình", "liên kết", "liên lạc", "long trọng",
            "lòng vòng", "lợi ích", "lớn lao", "lời nói", "lương tâm", "lướt ván", "lược sử", "lũ lụt", "luyện tập", "lững lờ",
            "ma quỷ", "ma lực", "mạ kẽm", "mang vác", "manh mối", "màu mỡ", "màu sắc", "máy bay", "máy tính", "mây mù",
            "mật mã", "mật ong", "mẫu giáo", "mẫu mã", "mận đào", "mất mát", "mật thiết", "mềm mại", "mến khách", "miền nam",
            "miền trung", "miền bắc", "miền núi", "miễn phí", "minh bạch", "mơ màng", "mơ ước", "mở cửa", "mở rộng", "mưa gió",
            "mưu trí", "mực tàu", "muôn màu", "muôn năm", "mượt mà", "na ná", "náo động", "nam giới", "năm tháng",
            "năng lực", "năng động", "nắng ấm", "nền tảng", "nếp sống", "nết na", "ngà voi", "ngạc nhiên", "ngân hàng", "ngây thơ",
            "nghiêm túc", "ngoại ô", "ngọc trai", "nguồn gốc", "người lớn", "người mẫu", "nhà cửa", "nhà ga", "nhà nước", "nhà thơ",
            "nhanh nhẹn", "nhiệt huyết", "nhịp điệu", "nhỏ nhẻ", "nhung lụa", "niềm tin", "no đủ", "nô đùa", "nội bộ",
            "nội trợ", "nông dân", "nông nghiệp", "nước mắt", "nước ngọt", "nước sôi", "nước rút", "nước uống", "nứt nẻ",

            // Vần Q - S - T - V - X
            "qua lại", "quá khứ", "quà cáp", "quản lý", "quảng cáo", "quảng đại", "quang cảnh", "quốc gia", "quốc tế",
            "ra vào", "ra đi", "rừng rậm", "rực rỡ", "rắn rỏi", "rất tốt", "rộn ràng", "rộng rãi", "rút gọn", "rũ rượi",
            "sa mạc", "sa sút", "sẵn sàng", "sáng chói", "sáng tạo", "sắp xếp", "sắc bén", "sắp tới", "sắt đá", "siêng năng",
            "sinh hoạt", "sinh nhật", "sôi nổi", "sống động", "sông ngòi", "sơn ca", "sự nghiệp", "sự thật", "sức khỏe", "sức mạnh",
            "tai nạn", "tài ba", "tài chính", "tài đức", "tài nguyên", "tài sản", "tài trợ", "tâm huyết", "tâm trạng",
            "tân tiến", "tập thể", "tập trung", "tất cả", "tất bật", "tất nhiên", "thà rằng", "thả diều", "thái độ", "thảm họa",
            "thần kỳ", "thần tốc", "thần thái", "thắng lợi", "thiên nhiên", "thiên tài", "thiết kế", "thông minh", "thời gian", "thời tiết",
            "thu nhập", "thuận lợi", "thực phẩm", "thực tế", "tiến lên", "tiến sĩ", "tiết kiệm", "tiểu sử", "tin cậy", "tinh tế",
            "tổ quốc", "tổ chức", "tự do", "tự hào", "tự nhiên", "từ bi", "từ điển", "từ giã", "từ ngữ", "từ thiện",
            "va chạm", "vạch trần", "vạn vật", "vàng bạc", "vất vả", "vây quanh", "vẻ đẹp", "vẹn toàn", "việc làm", "viễn tưởng",
            "vinh quang", "vô cùng", "vô địch", "vô hình", "vui sướng", "vui vẻ", "vườn tược", "vương giả", "vương quốc", "vững vàng",
            "xa xôi", "xã hội", "xác định", "xanh biếc", "xinh đẹp", "xoay sở", "xuất sắc", "xuất chúng", "xung quanh", "xứng đáng"
    );

    private String currentWord = "";

    @GetMapping("/webhook")
    public String startGame() {
        Random random = new Random();
        currentWord = dictionary.get(random.nextInt(dictionary.size()));
        return "Trò chơi bắt đầu! Từ đầu tiên là: <b>" + currentWord + "</b>. Hãy nối tiếp từ cuối!";
    }

    @GetMapping("/webhook/play")
    public String playWord(@RequestParam("word") String word) {
        word = word.trim().toLowerCase();

        // Tách tiếng cuối của từ hiện tại và tiếng đầu của từ người chơi
        String[] currentParts = currentWord.split(" ");
        String lastSyllableOfCurrent = currentParts[currentParts.length - 1];

        String[] userParts = word.split(" ");

        // Kiểm tra định dạng từ gồm 2 tiếng
        if (userParts.length != 2) {
            return "❌ Từ không hợp lệ! Vui lòng nhập đúng <b>định dạng gồm 2 tiếng</b> (Ví dụ: <i>an toàn</i>).<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        String firstSyllableOfUser = userParts[0];

        // Kiểm tra quy tắc nối từ
        if (!firstSyllableOfUser.equalsIgnoreCase(lastSyllableOfCurrent)) {
            return "❌ Sai luật nối từ! Từ của bạn phải bắt đầu bằng tiếng <b>'" + lastSyllableOfCurrent + "'</b>.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        // Cập nhật từ mới của người chơi
        currentWord = word;

        // Bot tìm từ tiếp theo bắt đầu bằng tiếng cuối của người chơi
        String targetStart = userParts[userParts.length - 1];
        String botReply = null;

        for (String w : dictionary) {
            if (w.startsWith(targetStart + " ") && !w.equalsIgnoreCase(currentWord)) {
                botReply = w;
                break;
            }
        }

        if (botReply == null) {
            return "🎉 Chúc mừng bạn! Bạn đã làm bot phải chịu thua vì hết từ nối với tiếng <b>'" + targetStart + "'</b>!";
        }

        currentWord = botReply;
        return "✅ Chính xác! Bot nối tiếp từ: <b>" + botReply + "</b>.<br>Lượt bạn, nối từ bắt đầu bằng tiếng: <b>" + botReply.split(" ")[1] + "</b>";
    }

    @GetMapping("/webhook/reset")
    public String resetGame() {
        Random random = new Random();
        currentWord = dictionary.get(random.nextInt(dictionary.size()));
        return "🔄 Đã bắt đầu ván mới! Từ xuất phát là: <b>" + currentWord + "</b>. Mời bạn đi trước!";
    }
}