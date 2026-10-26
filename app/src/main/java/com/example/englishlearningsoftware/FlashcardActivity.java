package com.example.englishlearningsoftware;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình học flashcard và quản lý từ vựng
 */
public class FlashcardActivity extends AppCompatActivity {

    // Database helper thao tác với SQLite
    private FlashcardDbHelper dbHelper;
    // Danh sách flashcard lấy từ database
    private List<Flashcard> flashcardList = new ArrayList<>();
    // Adapter đổ dữ liệu lên ListView
    private FlashcardAdapter adapter;

    // Vị trí thẻ đang hiển thị ở khung trên
    private int currentIndex = 0;

    // Các thành phần giao diện
    private FrameLayout cardContainer;
    private LinearLayout layoutCardFront;
    private LinearLayout layoutCardBack;
    private TextView tvCardWord;
    private TextView tvCardType;
    private TextView tvCardMeaning;
    private TextView tvCardExample;
    private TextView tvCardCounter;
    private Button btnPrevCard;
    private Button btnNextCard;
    private Button btnBackHome;
    private Button btnToggleAddForm;

    // Form thêm từ mới
    private LinearLayout layoutAddForm;
    private EditText etNewWord;
    private EditText etNewType;
    private EditText etNewMeaning;
    private EditText etNewExample;
    private Button btnSaveWord;

    // ListView hiển thị danh sách từ
    private ListView lvFlashcards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        // Khởi tạo database
        dbHelper = new FlashcardDbHelper(this);

        // Ánh xạ các view
        anhXaView();

        // Lấy dữ liệu từ database và đưa lên ListView
        loadDataTuDatabase();

        // Bấm vào thẻ để lật mặt trước và mặt sau
        xuLyLatThe();

        // Chuyển qua lại giữa các thẻ từ
        xuLyChuyenThe();

        // Bật tắt form và lưu từ mới
        xuLyFormThemTu();

        // Bấm vào 1 dòng trên danh sách để xem chi tiết
        xuLyClickListView();

        // Nhấn giữ lâu để xóa từ khỏi database
        xuLyLongClickXoaTu();

        // Nút quay về màn hình trước
        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý khoảng cách thanh trạng thái hệ thống
        final View root = findViewById(R.id.flashcardRoot);
        final int padTop = root.getPaddingTop();
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(root,
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public androidx.core.view.WindowInsetsCompat onApplyWindowInsets(View view,
                            androidx.core.view.WindowInsetsCompat insets) {
                        androidx.core.graphics.Insets bars = insets.getInsets(
                                androidx.core.view.WindowInsetsCompat.Type.systemBars());
                        view.setPadding(root.getPaddingLeft(), padTop + bars.top,
                                root.getPaddingRight(), bars.bottom);
                        return insets;
                    }
                });
        androidx.core.view.ViewCompat.requestApplyInsets(root);
    }

    // Ánh xạ các view trên giao diện
    private void anhXaView() {
        cardContainer = findViewById(R.id.cardContainer);
        layoutCardFront = findViewById(R.id.layoutCardFront);
        layoutCardBack = findViewById(R.id.layoutCardBack);
        tvCardWord = findViewById(R.id.tvCardWord);
        tvCardType = findViewById(R.id.tvCardType);
        tvCardMeaning = findViewById(R.id.tvCardMeaning);
        tvCardExample = findViewById(R.id.tvCardExample);
        tvCardCounter = findViewById(R.id.tvCardCounter);
        btnPrevCard = findViewById(R.id.btnPrevCard);
        btnNextCard = findViewById(R.id.btnNextCard);
        btnBackHome = findViewById(R.id.btnBackHome);
        btnToggleAddForm = findViewById(R.id.btnToggleAddForm);

        layoutAddForm = findViewById(R.id.layoutAddForm);
        etNewWord = findViewById(R.id.etNewWord);
        etNewType = findViewById(R.id.etNewType);
        etNewMeaning = findViewById(R.id.etNewMeaning);
        etNewExample = findViewById(R.id.etNewExample);
        btnSaveWord = findViewById(R.id.btnSaveWord);

        lvFlashcards = findViewById(R.id.lvFlashcards);
    }

    // Đọc danh sách từ trong database và cập nhật ListView
    private void loadDataTuDatabase() {
        flashcardList.clear();
        flashcardList.addAll(dbHelper.getAllFlashcards());

        if (adapter == null) {
            adapter = new FlashcardAdapter(this, R.layout.item_flashcard, flashcardList);
            lvFlashcards.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        // Hiển thị từ đầu tiên lên thẻ nếu danh sách có từ
        if (!flashcardList.isEmpty()) {
            if (currentIndex >= flashcardList.size()) {
                currentIndex = 0;
            }
            hienThiTheTo(currentIndex);
        } else {
            tvCardWord.setText("Chưa có từ nào");
            tvCardType.setText("");
            tvCardMeaning.setText("Bấm '+ Thêm từ' ở trên để thêm từ mới");
            tvCardExample.setText("");
            tvCardCounter.setText("0 / 0");
        }
    }

    // Hiển thị thông tin từ vựng lên thẻ to
    private void hienThiTheTo(int index) {
        if (index < 0 || index >= flashcardList.size()) return;

        Flashcard card = flashcardList.get(index);
        tvCardWord.setText(card.getWord());

        if (card.getType() != null && !card.getType().trim().isEmpty()) {
            tvCardType.setVisibility(View.VISIBLE);
            tvCardType.setText("(" + card.getType() + ")");
        } else {
            tvCardType.setVisibility(View.GONE);
        }

        tvCardMeaning.setText(card.getMeaning());

        if (card.getExample() != null && !card.getExample().trim().isEmpty()) {
            tvCardExample.setVisibility(View.VISIBLE);
            tvCardExample.setText("Ví dụ: " + card.getExample());
        } else {
            tvCardExample.setVisibility(View.GONE);
        }

        tvCardCounter.setText("Thẻ " + (index + 1) + " / " + flashcardList.size());

        // Mặc định luôn hiện mặt trước khi đổi từ mới
        layoutCardFront.setVisibility(View.VISIBLE);
        layoutCardBack.setVisibility(View.GONE);
    }

    // Đổi hiển thị giữa mặt trước và mặt sau khi chạm vào thẻ
    private void xuLyLatThe() {
        cardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashcardList.isEmpty()) return;

                if (layoutCardFront.getVisibility() == View.VISIBLE) {
                    layoutCardFront.setVisibility(View.GONE);
                    layoutCardBack.setVisibility(View.VISIBLE);
                } else {
                    layoutCardBack.setVisibility(View.GONE);
                    layoutCardFront.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // Chuyển đổi thẻ lùi về trước hoặc tiến tới sau
    private void xuLyChuyenThe() {
        // Nút lùi về từ trước
        btnPrevCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashcardList.isEmpty()) return;

                if (currentIndex > 0) {
                    currentIndex--;
                } else {
                    currentIndex = flashcardList.size() - 1;
                }
                hienThiTheTo(currentIndex);
            }
        });

        // Nút tiến tới từ tiếp theo
        btnNextCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashcardList.isEmpty()) return;

                if (currentIndex < flashcardList.size() - 1) {
                    currentIndex++;
                } else {
                    currentIndex = 0;
                }
                hienThiTheTo(currentIndex);
            }
        });
    }

    // Đóng mở form và lưu từ mới vào database
    private void xuLyFormThemTu() {
        // Đóng hoặc mở form nhập
        btnToggleAddForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutAddForm.getVisibility() == View.VISIBLE) {
                    layoutAddForm.setVisibility(View.GONE);
                    btnToggleAddForm.setText("+ Thêm từ");
                } else {
                    layoutAddForm.setVisibility(View.VISIBLE);
                    btnToggleAddForm.setText("✕ Đóng form");
                    etNewWord.requestFocus();
                }
            }
        });

        // Lưu thông tin từ mới
        btnSaveWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = etNewWord.getText().toString().trim();
                String type = etNewType.getText().toString().trim();
                String meaning = etNewMeaning.getText().toString().trim();
                String example = etNewExample.getText().toString().trim();

                // Kiểm tra các trường bắt buộc
                if (word.isEmpty() || meaning.isEmpty()) {
                    Toast.makeText(FlashcardActivity.this, "Vui lòng nhập ít nhất Từ vựng và Nghĩa!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo đối tượng và lưu vào database
                Flashcard newCard = new Flashcard(word, type, meaning, example);
                long id = dbHelper.addFlashcard(newCard);

                if (id > 0) {
                    Toast.makeText(FlashcardActivity.this, "Đã lưu từ '" + word + "' vào Database!", Toast.LENGTH_SHORT).show();

                    // Xóa trắng ô nhập sau khi lưu
                    etNewWord.setText("");
                    etNewType.setText("");
                    etNewMeaning.setText("");
                    etNewExample.setText("");

                    // Đóng form nhập
                    layoutAddForm.setVisibility(View.GONE);
                    btnToggleAddForm.setText("+ Thêm từ");

                    // Tải lại danh sách và xem từ vừa thêm
                    loadDataTuDatabase();
                    currentIndex = 0;
                    hienThiTheTo(currentIndex);
                } else {
                    Toast.makeText(FlashcardActivity.this, "Lỗi: Không thể lưu vào Database!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Bấm vào 1 dòng trong danh sách để xem trên thẻ to
    private void xuLyClickListView() {
        lvFlashcards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentIndex = position;
                hienThiTheTo(currentIndex);
            }
        });
    }

    // Nhấn giữ lâu vào 1 dòng để xóa từ khỏi database
    private void xuLyLongClickXoaTu() {
        lvFlashcards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Flashcard cardCanXoa = flashcardList.get(position);

                // Hộp thoại xác nhận xóa
                new AlertDialog.Builder(FlashcardActivity.this)
                        .setTitle("Xóa từ vựng")
                        .setMessage("Bạn có chắc muốn xóa từ '" + cardCanXoa.getWord() + "' khỏi Database không?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Xóa khỏi database
                                boolean daXoa = dbHelper.deleteFlashcard(cardCanXoa.getId());
                                if (daXoa) {
                                    Toast.makeText(FlashcardActivity.this, "Đã xóa từ '" + cardCanXoa.getWord() + "'", Toast.LENGTH_SHORT).show();
                                    loadDataTuDatabase();
                                } else {
                                    Toast.makeText(FlashcardActivity.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();

                return true;
            }
        });
    }
}
