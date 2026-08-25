package org.example.noitu;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class BotController {

    // Kho từ vựng đầy đủ (Chứa toàn bộ từ hợp lệ để người chơi nối)
    private final List<String> dictionary = Arrays.asList(
            // Kho từ vựng nền tảng
            "an toàn", "an ninh", "an ủi", "áp lực", "ẩn ý", "áo dài", "áo ấm", "âm thanh", "âm nhạc", "ánh sáng",
            "bà con", "ba ba", "bà ngoại", "bà nội", "bác sĩ", "bạc đãi", "bạch tuộc", "bàn bạc",
            "bàn chân", "bàn ghế", "bàn là", "bàn tay", "bản sắc", "bản tin", "bảng đen", "bánh chưng", "bánh dày", "bánh mì",
            "bảo tàng", "bảo vệ", "bắc cầu", "bắc hải", "bập bẹ", "bật mí", "bầu trời", "bầu bạn", "béo tốt", "bênh vực",
            "bình an", "bình minh", "bình tĩnh", "bí ẩn", "bí mật", "bơ vơ", "bờ biển", "bờ vực", "bụi bặm", "buồn bã",
            "buồn vui", "buổi chiều", "buổi sáng", "buổi trưa", "búp bê", "bứt rứt", "bước chân", "bướng bỉnh",
            "ca dao", "ca hát", "ca sĩ", "cá chép", "cá heo", "cá mập", "cá voi", "can đảm", "can thiệp", "canh cánh",
            "cao cấp", "cao ngạo", "cao ốc", "cào cào", "càu nhàu", "câu cá", "câu lạc bộ", "cầu chì", "cầu lông", "cầu vồng",
            "cây cối", "cây cỏ", "cha mẹ", "chai lọ", "chăm chỉ", "chăm sóc", "chân thành", "chân tay", "chấp nhận", "chất phác",
            "chật chội", "chầu chực", "chế biến", "chế độ", "chiến thắng", "chiến tranh", "chim chóc", "chính trị", "chóng mặt", "chu đáo",
            "chuẩn bị", "chuồn chuồn", "chuyện trò", "chữ viết", "co giật", "co ro", "cơ bắp", "cơ hội", "cơ sở", "con cháu",
            "con đường", "con người", "côn trùng", "công bằng", "công nghệ", "công nhân", "công việc", "cồng kềnh", "cốt lõi", "cố gắng",
            "cổ kính", "cổ vũ", "cục cằn", "cuộc đời", "cuộc sống", "cuốn hút", "cuộn tròn", "cười duyên", "cương quyết", "cương vị",
            "da dẻ", "da cam", "da thịt", "dài dòng", "dăm ba", "dân cư", "dân gian", "dân tộc", "dâng hiến", "dấu hiệu",
            "dấu vết", "dầu gió", "dầu khí", "dạy học", "dăm bông", "dần dần", "dâng trào", "dập dềnh", "dễ dãi", "dễ chịu",
            "dễ thương", "du lịch", "du mục", "du xuân", "dung dịch", "dũng cảm", "dữ dội", "dư dả", "dư âm", "dương lịch",
            "đá banh", "đá quý", "đà điểu", "đại gia", "đại dương", "đại học", "đại lý", "đảm bảo", "đảm đang", "đầm lầy",
            "đất đai", "đất liền", "đặc biệt", "đặc sản", "đăng ký", "đăng quang", "đắm say", "đầu bếp", "đầu độc", "đầu tiên",
            "đập phá", "đất nước", "đầy đủ", "đẹp đẽ", "đêm ngày", "đêm tối", "đi đứng", "đi lại", "điểm mấu", "điểm số",
            "điện ảnh", "điện thoại", "điều kiện", "định mệnh", "đoàn kết", "đoán mò", "đỏ rực", "đói kém", "đơn côi", "đơn giản",
            "đời sống", "đu đủ", "đúng đắn", "đường đi", "đường phố", "đương đầu", "được việc",
            "gà gô", "gà trống", "gác xép", "gai góc", "giao lưu", "giảm giá", "gian khổ", "gian nan", "giang sơn", "giao tiếp",
            "giấu giếm", "giết chóc", "giễu cợt", "giảm sút", "giản dị", "giáo dục", "giáo sư", "giàu có", "giấc mơ", "giếng nước",
            "giữ gìn", "giữa đường", "giúp đỡ", "gia đình", "gia tài", "gia vị", "gọn gàng", "gọi điện", "gù lưng", "gửi gắm",
            "hà mã", "hà tiện", "hạ bệ", "hạ cánh", "hạ long", "hải sản", "hải tặc", "hải yến", "hào hứng", "hào phóng",
            "hành động", "hành khách", "hành tinh", "hạnh phúc", "hấp dẫn", "hắt hiu", "hết lòng", "hiền hòa", "hiền lành", "hiểu biết",
            "hiệu quả", "hoa hồng", "hoa quả", "hoài niệm", "hoàn hảo", "hoảng sợ", "hoạt động", "họa sĩ", "học hỏi", "hội chợ",
            "hôm nay", "hồng hào", "hợp tác", "hờn dỗi", "hướng dẫn", "hướng dương", "hương vị", "hưu trí", "hy sinh", "hy vọng",
            "kỳ bí", "kỳ diệu", "kỹ sư", "kỷ niệm", "kỷ luật", "kẻ cắp", "kéo co", "kế hoạch", "kết quả", "khen ngợi",
            "kho báu", "khoa học", "khoẻ mạnh", "khôn ngoan", "không khí", "không gian", "khó khăn", "khởi nghiệp", "khủng long", "khuyến khích",
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
            "xa xôi", "xã hội", "xác định", "xanh biếc", "xinh đẹp", "xoay sở", "xuất sắc", "xuất chúng", "xung quanh", "xứng đáng",

            // 1. Tính từ màu sắc
            "hồng nhạt", "hồng phấn", "hồng cánh sen", "hồng đậm", "xanh ngọc", "xanh rêu", "xanh biếc", "xanh dương",
            "xanh lá", "xanh non", "vàng chóe", "vàng chanh", "vàng nghệ", "vàng hoe", "vàng tươi", "đỏ thẫm", "đỏ tươi",
            "đỏ au", "đỏ chót", "tím rịm", "tím biếc", "tím huế", "cam sáng", "cam đất", "nâu đất", "nâu sẫm", "xám tro",
            "trắng muốt", "trắng phau", "trắng tinh", "đen nhẻm", "đen thui", "đen nhánh",

            // 2. Sự vật, đồ dùng, công trình
            "cáp treo", "cầu thang", "thang máy", "máy giặt", "tủ lạnh", "điều hòa", "quạt máy", "bóng đèn", "máy sấy",
            "bình nóng", "nồi cơm", "lò vi", "lò nướng", "bếp ga", "bếp từ", "xe đạp", "xe máy", "xe hơi", "tàu hỏa",
            "máy bay", "thuyền máy", "cầu vượt", "hầm chui", "nhà cao", "công viên", "bệnh viện", "trường học", "thư viện",
            "bàn học", "ghế sofa", "giường ngủ", "gối ôm", "chăn bông", "màn cửa", "khung tranh", "đồng hồ", "máy ảnh",

            // 3. Chất liệu / Cấu tạo
            "cửa gỗ", "cầu sắt", "nồi nhôm", "áo len", "nhà kính", "bàn đá", "dép nhựa", "thìa inox", "nhà gỗ", "cầu tre",
            "cửa kính", "bàn gỗ", "ghế nhựa", "tường gạch", "cổng sắt", "nhà gạch", "áo cotton", "giày da", "túi vải",
            "tủ gỗ", "thuyền gỗ", "đũa gỗ", "sàn gỗ", "ấm đồng", "khung thép", "đinh sắt", "tượng đá", "đĩa gốm", "ấm đất",

            // 4. Trạng thái / Đặc điểm
            "cơm nóng", "kem lạnh", "thịt nguội", "nước lạnh", "chè nóng", "nước ấm", "gió lạnh", "trời mưa", "nắng gắt",
            "trời oi", "trưa nắng", "rét đậm", "sương mù", "gió nhẹ", "mưa rào", "cá tươi", "thịt tươi", "sữa chua",
            "canh ngọt", "cơm khô", "bún tươi", "bánh giòn", "quả chín",

            // 5. Hình dạng / Kích thước
            "bàn tròn", "ghế dài", "nhà cao", "đường dài", "ô vuông", "bóng tròn", "cột cao", "hộp vuông", "vòng tròn",
            "hình vuông", "cổng rộng", "sân rộng", "hẻm hẹp", "ao sâu",

            // 6. === NHÓM TÍNH TỪ DẠNG ĐẶC BIỆT (CHỈ ĐỂ NGƯỜI CHƠI NHẬP, BOT KHÔNG LẤY RA ĐỀ) ===
            "cao vút", "cao tít", "đầy đặn", "ốm yếu", "gầy gò", "gầy guộc", "mong manh", "thấp bé", "mập mạp",
            "béo ú", "thanh mảnh", "sừng sững", "mũm mĩm", "gầy còm", "dong dỏng"
    );

    // Danh sách từ thân thuộc cho BOT chọn ra đề (HOÀN TOÀN KHÔNG CHỨA các từ tính từ đặc biệt)
    private final List<String> simpleStarterWords = Arrays.asList(
            "bà con", "ba ba", "bác sĩ", "bàn tay", "bánh mì", "bảo vệ", "bầu trời",
            "ca sĩ", "cá chép", "cá heo", "cây cối", "cha mẹ", "chăm chỉ", "chân thành",
            "da dẻ", "dễ thương", "đá banh", "đất nước", "đẹp đẽ", "điện thoại",
            "gà trống", "gia đình", "gọi điện", "hà mã", "hành động", "hạnh phúc", "hoa hồng",
            "lá cờ", "làm việc", "làng xóm", "máy bay", "máy tính", "mưa gió", "năm tháng",
            "nhà cửa", "nước ngọt", "quà cáp", "sông ngòi", "thả diều", "vui vẻ",
            "hồng nhạt", "xanh ngọc", "vàng chanh", "đỏ tươi", "cáp treo", "cầu thang", "thang máy", "máy giặt",
            "cơm nóng", "cửa gỗ", "bàn tròn", "kem lạnh", "bàn ghế", "xe đạp"
    );

    private String currentWord = "";
    private int turnCount = 0;

    @GetMapping("/webhook")
    public String startGame() {
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

        // 1. Kiểm tra định dạng VIP (đúng 2 tiếng)
        if (userParts.length != 2) {
            return "❌ <b>Lỗi định dạng VIP:</b> Vui lòng nhập chính xác <b>một từ gồm đúng 2 tiếng</b> (Ví dụ: <i>an toàn</i>).<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        String firstSyllableOfUser = userParts[0];

        // 2. Kiểm tra luật nối từ
        if (!firstSyllableOfUser.equalsIgnoreCase(lastSyllableOfCurrent)) {
            return "❌ <b>Sai luật nối từ!</b> Từ của bạn phải bắt đầu bằng tiếng <b>'" + lastSyllableOfCurrent + "'</b>.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        // 3. Kiểm tra từ điển hợp lệ
        if (!dictionary.contains(word)) {
            return "❌ <b>Từ không hợp lệ!</b> Từ này không có trong kho từ vựng tiếng Việt của game.<br>Từ hiện tại: <b>" + currentWord + "</b>";
        }

        currentWord = word;
        turnCount++;

        String targetStart = userParts[userParts.length - 1];
        String botReply = null;

        // Ưu tiên chọn từ dễ trong 3 lượt đầu
        if (turnCount < 3) {
            for (String w : simpleStarterWords) {
                if (w.startsWith(targetStart + " ") && !w.equalsIgnoreCase(currentWord) && dictionary.contains(w)) {
                    botReply = w;
                    break;
                }
            }
        }

        // Sau 3 lượt hoặc nếu từ đơn giản không tìm thấy, bot quét danh sách dictionary
        if (botReply == null) {
            for (String w : dictionary) {
                if (w.startsWith(targetStart + " ") && !w.equalsIgnoreCase(currentWord)) {
                    botReply = w;
                    break;
                }
            }
        }

        // TRƯỜNG HỢP 1: Người chơi thắng
        if (botReply == null) {
            return "VICTORY:🎉 Bạn đã thắng tôi rồi!";
        }

        currentWord = botReply;
        return "✅ Bot nối tiếp: <b>" + botReply + "</b>. Lượt bạn (nối chữ: <b>" + botReply.split(" ")[1] + "</b>)";
    }

    @GetMapping("/webhook/reset")
    public String resetGame() {
        Random random = new Random();
        turnCount = 0;
        currentWord = simpleStarterWords.get(random.nextInt(simpleStarterWords.size()));
        return "🔄 Đã bắt đầu ván mới! Từ xuất phát là: <b>" + currentWord + "</b>. Mời bạn đi trước!";
    }
}
