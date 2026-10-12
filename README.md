# English Learning Java

Chuyển từ [npquoc456/mobilefrontend](https://github.com/npquoc456/mobilefrontend)
(commit nguồn `c948dd399692c9ed30843ece49a7d7b4dd786ddb`)
sang **Java + XML**, theo mẫu **Empty Views Activity** và slide Android của môn học.
Giữ package `com.example.englishlearningsoftware` và giao diện đăng nhập/đăng ký gốc.

## Mở và chạy

1. Clone hoặc giải nén project. Android Studio → Open → chọn thư mục có `settings.gradle.kts`.
2. Dùng Gradle JDK **17**. Cài Android SDK Platform **35** và Build Tools **35.0.0**
   qua SDK Manager, rồi Sync Gradle.
3. Chạy module `app` trên emulator/điện thoại Android 7.0 trở lên (API 24+).
4. Chưa có backend: nhấn **Xem thử giao diện (không cần backend)** để mở Home cơ bản.
   Đây là luồng xem thử, không tạo tài khoản hay xác thực người dùng.

Cấu hình dùng AGP **8.9.2**, Gradle **8.11.1**, compile/target SDK **35**, min SDK **24**.
Đã chuyển cấu hình từ AGP 9.4 / Gradle 9.6 / SDK 37 / daemon JDK 25 của repo gốc
sang bộ phiên bản dùng được với JDK 17. Code Java biên dịch ở mức Java 11.
Các file `.gradle.kts` vẫn là cấu hình Gradle, giống ảnh mẫu của bạn.

## Các file chính

| File | Vai trò |
| --- | --- |
| `app/src/main/java/com/example/englishlearningsoftware/MainActivity.java` | Đăng nhập, đăng ký, chuyển tab, kiểm tra dữ liệu và chuyển màn bằng Intent |
| `app/src/main/java/com/example/englishlearningsoftware/AccountApi.java` | Request đăng nhập/đăng ký với callback Java |
| `app/src/main/java/com/example/englishlearningsoftware/HomeActivity.java` | Home cơ bản và quay lại đăng nhập |
| `app/src/main/res/layout/activity_main.xml` | Giao diện đăng nhập/đăng ký |
| `app/src/main/res/layout/activity_home.xml` | Giao diện Home cơ bản |
| `app/src/main/res/values/strings.xml` | Chuỗi mới và địa chỉ backend |
| `docs/JAVA_XML_GUIDE.md` | Đối chiếu slide, cách thêm Activity và phân công nhóm |

## Kết nối backend

Sửa `api_base_url` trong `res/values/strings.xml`:

- Android Emulator: `http://10.0.2.2:8080/` trỏ tới máy tính chạy backend.
- Điện thoại thật: dùng IP LAN của máy tính, ví dụ `http://192.168.1.10:8080/`,
  cùng mạng Wi-Fi và mở cổng 8080 trên máy tính.
- `127.0.0.1` trên Android là chính thiết bị Android, không phải máy tính.

Giữ nguyên hợp đồng API từ repo gốc:

| Chức năng | Request |
| --- | --- |
| Đăng nhập | `GET /api/accounts/login`, query `email`, `phoneNumber`, `password` |
| Đăng ký | `POST /api/accounts/register`, JSON `firstName`, `lastName`, `email`, `phoneNumber`, `password` |

Login thành công đọc JSON `firstName`, `lastName`, `accountId` và mở Home.
Đăng ký thành công quay về tab đăng nhập. Mật khẩu không bị cắt khoảng trắng.
Không lưu mật khẩu hoặc ghi URL đăng nhập/response vào log.

Backend vẫn phải chạy riêng; repo này không chứa server. Luồng GET chứa mật khẩu
và HTTP được giữ để tương thích API cũ khi phát triển. Khi triển khai thực tế,
nhóm cần đổi đồng thời frontend/backend sang POST + HTTPS và cơ chế phiên/token.
Hiện chưa có lưu phiên đăng nhập; checkbox ghi nhớ, quên mật khẩu, Google và
Facebook là phần giao diện gốc, chưa có xử lý. Các chức năng từ vựng, camera, AI,
ôn tập, luyện tập và thống kê cũng chưa được triển khai.

## Kiểm tra

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug assembleDebugAndroidTest
```

Windows dùng `gradlew.bat` thay `./gradlew`. Unit test kiểm tra hợp đồng API,
email/điện thoại và ký tự đặc biệt của mật khẩu. Có test giao diện Java cho
chuyển tab, tái tạo Activity và luồng xem thử Home. Chạy trên emulator/thiết bị bằng:

```bash
./gradlew connectedDebugAndroidTest
```

## Làm việc nhóm trên GitHub

Mỗi người tạo nhánh riêng, ví dụ `feature/vocabulary-ui`, rồi tạo Pull Request
vào `main`. Dùng Activity/XML riêng cho từng màn; thống nhất phần sửa ở manifest,
Home, `colors.xml` và `strings.xml` để dễ ghép code.

Chủ repo thêm thành viên qua **Settings → Collaborators → Add people**.
Giữ tên tác giả và lịch sử nguồn khi chia sẻ bản chuyển đổi.

Tham khảo chính thức:
- https://developer.android.com/build/releases/agp-8-9-0-release-notes
- https://developer.android.com/studio/run/emulator-networking
