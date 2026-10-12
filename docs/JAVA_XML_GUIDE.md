# Hướng dẫn Java + XML theo môn học

Project dùng cách tổ chức tương ứng mẫu **Empty Views Activity** trong ảnh đã gửi.
Mỗi màn hình có một class Java kế thừa `AppCompatActivity` và một file layout XML.
Code ứng dụng và test đều là Java. Các file `build.gradle.kts` là cấu hình Gradle,
không phải code màn hình Kotlin. Không dùng Jetpack Compose.

## Đối chiếu slide môn học

| Slide | Áp dụng trong project |
| --- | --- |
| 01.1 Your first Android app, trang 12–14 | Activity, layout và thư mục `java`, `res`, manifest. Slide cũ gọi mẫu là Empty Activity; ảnh Android Studio hiện tại chọn Empty Views Activity. |
| 01.2 Layouts and resources for the UI | Layout XML, tài nguyên `R.layout`, `R.id`, `R.string`, `findViewById` và `setOnClickListener`. |
| 02.1 Activities and Intents, slide 12–14, 21 và 30 | `extends AppCompatActivity`, `setContentView`, khai báo Activity trong manifest, chuyển màn bằng explicit `Intent`. |
| 02.2 Activity lifecycle and state | Lưu tab đăng ký bằng `onSaveInstanceState`; hủy request đang chạy ở `onDestroy`. |
| 04.1 Buttons and clickable images, slide 10 | Java `View.OnClickListener` cho các nút. |
| 04.2 Input controls | `EditText`, bàn phím email/điện thoại/mật khẩu, kiểm tra các ô bắt buộc. |

Phần gọi API nằm trong `AccountApi.java`, dùng OkHttp callback Java để tránh chạy
mạng trên luồng giao diện. Đây là phần giữ lại chức năng mạng của repo gốc,
không phải nội dung được khẳng định là có trong slide.

## Thêm một màn hình

1. Tạo `VocabularyActivity.java` trong package `com.example.englishlearningsoftware`.
2. Tạo `res/layout/activity_vocabulary.xml`.
3. Trong `onCreate`, gọi `setContentView(R.layout.activity_vocabulary)`.
4. Gắn view bằng `findViewById`, xử lý nút bằng `setOnClickListener`.
5. Khai báo `<activity android:name=".VocabularyActivity" android:exported="false" />`
   trong `AndroidManifest.xml`.
6. Nút ở Home mở màn bằng `new Intent(HomeActivity.this, VocabularyActivity.class)`.

```java
public class VocabularyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
    }
}
```

Thêm import `android.os.Bundle` và `androidx.appcompat.app.AppCompatActivity`
cho ví dụ trên. Mỗi thành viên làm Activity và XML riêng để giảm xung đột.

## Gợi ý phân công 5 người cho phần giao diện

| Người | Phạm vi | File dự kiến |
| --- | --- | --- |
| 1 | Đăng nhập/đăng ký, hồ sơ và mục tiêu học | `MainActivity`, `ProfileActivity` và XML |
| 2 | Thư viện từ vựng, chi tiết từ | `VocabularyActivity`, `WordDetailActivity` và XML |
| 3 | Flashcard, giao diện ôn tập | `FlashcardActivity`, `ReviewActivity` và XML |
| 4 | Giao diện camera/chọn từ, giải thích AI | `ScanActivity`, `ExplanationActivity` và XML |
| 5 | Trắc nghiệm/điền từ/nghe, thống kê | `PracticeActivity`, `ProgressActivity` và XML |

Các màn trên là công việc tiếp theo, chưa được triển khai trong bản chuyển đổi.
Nhóm thống nhất ID view, màu, chuỗi và cách điều hướng trước khi ghép vào Home.
