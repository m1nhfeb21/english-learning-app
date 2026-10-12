# Kết quả kiểm tra bản Java

Ngày kiểm tra: 12/10/2026.

| Kiểm tra | Kết quả |
| --- | --- |
| `assembleDebug` | Thành công, tạo APK debug |
| `testDebugUnitTest` | 3 test đạt, 0 lỗi, 0 bỏ qua |
| `lintDebug` | Thành công, 0 lỗi, 42 cảnh báo |
| `assembleDebugAndroidTest` | Thành công, biên dịch và đóng gói test giao diện |
| XML và tham chiếu `R.id`, `R.string` trong Java | Hợp lệ |
| File Kotlin `.kt` trong code/test | Không còn |

Cảnh báo lint gồm chuỗi ghi trực tiếp trong XML, tài nguyên chưa dùng, autofill,
gợi ý khai báo input, overdraw, icon ứng dụng và gợi ý version catalog.
Chưa thực hiện `connectedDebugAndroidTest` vì phiên này không có thiết bị/emulator
đang chạy. Vì vậy chưa xác nhận hiển thị thực tế, bàn phím hay test giao diện trên
thiết bị. Chưa xác nhận đăng nhập/đăng ký với backend thật của nhóm.

Môi trường build: JDK 17, AGP 8.9.2, Gradle 8.11.1, Android SDK 35.
Không cần đưa cấu hình mạng của môi trường kiểm tra vào project.

Bản ZIP giao hàng có thư mục `EnglishLearningJava` để mở trong Android Studio,
`APK/EnglishLearningJava-debug.apk` để cài thử và git bundle trong `history`
để giữ lịch sử nguồn cùng commit chuyển đổi khi cần khôi phục.
