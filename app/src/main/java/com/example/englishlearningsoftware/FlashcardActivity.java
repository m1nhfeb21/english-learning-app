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

public class FlashcardActivity extends AppCompatActivity {

    private FlashcardDbHelper dbHelper;

    private List<Flashcard> flashcardList = new ArrayList<>();

    private FlashcardAdapter adapter;

    private int currentIndex = 0;

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

    private EditText etNewWord;
    private EditText etNewType;
    private EditText etNewMeaning;
    private EditText etNewExample;
    private Button btnSaveWord;

    private ListView lvFlashcards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        androidx.core.view.WindowInsetsControllerCompat controller =
                new androidx.core.view.WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        dbHelper = new FlashcardDbHelper(this);

        anhXaView();

        loadDataTuDatabase();

        xuLyLatThe();

        xuLyChuyenThe();

        xuLyFormThemTu();

        xuLyClickListView();

        xuLyLongClickXoaTu();

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final View root = findViewById(R.id.flashcardRoot);
        final int padTop = root.getPaddingTop();
        final int padBottom = root.getPaddingBottom();
        final int padLeft = root.getPaddingLeft();
        final int padRight = root.getPaddingRight();
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(root,
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public androidx.core.view.WindowInsetsCompat onApplyWindowInsets(View view,
                            androidx.core.view.WindowInsetsCompat insets) {
                        androidx.core.graphics.Insets bars = insets.getInsets(
                                androidx.core.view.WindowInsetsCompat.Type.systemBars());
                        int keyboardBottom = insets.getInsets(
                                androidx.core.view.WindowInsetsCompat.Type.ime()).bottom;
                        view.setPadding(padLeft + bars.left, padTop + bars.top,
                                padRight + bars.right, padBottom + Math.max(bars.bottom, keyboardBottom));
                        return insets;
                    }
                });
        androidx.core.view.ViewCompat.requestApplyInsets(root);
    }

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

        etNewWord = findViewById(R.id.etNewWord);
        etNewType = findViewById(R.id.etNewType);
        etNewMeaning = findViewById(R.id.etNewMeaning);
        etNewExample = findViewById(R.id.etNewExample);
        btnSaveWord = findViewById(R.id.btnSaveWord);

        lvFlashcards = findViewById(R.id.lvFlashcards);
    }

    private void loadDataTuDatabase() {
        flashcardList.clear();
        flashcardList.addAll(dbHelper.getAllFlashcards());

        if (adapter == null) {
            adapter = new FlashcardAdapter(this, R.layout.item_flashcard, flashcardList);
            lvFlashcards.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

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

        layoutCardFront.setVisibility(View.VISIBLE);
        layoutCardBack.setVisibility(View.GONE);
    }

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

    private void xuLyChuyenThe() {

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

    private void xuLyFormThemTu() {

        btnToggleAddForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddFormVisible(findViewById(R.id.addFormScroll).getVisibility() != View.VISIBLE);
            }
        });

        btnSaveWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = etNewWord.getText().toString().trim();
                String type = etNewType.getText().toString().trim();
                String meaning = etNewMeaning.getText().toString().trim();
                String example = etNewExample.getText().toString().trim();

                if (word.isEmpty() || meaning.isEmpty()) {
                    Toast.makeText(FlashcardActivity.this, "Vui lòng nhập ít nhất Từ vựng và Nghĩa!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Flashcard newCard = new Flashcard(word, type, meaning, example);
                long id = dbHelper.addFlashcard(newCard);

                if (id > 0) {
                    Toast.makeText(FlashcardActivity.this, "Đã lưu từ '" + word + "'", Toast.LENGTH_SHORT).show();

                    etNewWord.setText("");
                    etNewType.setText("");
                    etNewMeaning.setText("");
                    etNewExample.setText("");

                    setAddFormVisible(false);

                    loadDataTuDatabase();
                    currentIndex = 0;
                    hienThiTheTo(currentIndex);
                } else {
                    Toast.makeText(FlashcardActivity.this, "Không thể lưu từ. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setAddFormVisible(boolean visible) {
        // Give the add form the full available height while the keyboard is open.
        findViewById(R.id.addFormScroll).setVisibility(visible ? View.VISIBLE : View.GONE);
        for (int id : new int[]{R.id.cardContainer, R.id.cardNavigation,
                R.id.savedWordsHeader, R.id.lvFlashcards}) {
            findViewById(id).setVisibility(visible ? View.GONE : View.VISIBLE);
        }
        btnToggleAddForm.setText(visible ? "Đóng" : "+ Thêm từ");
        if (visible) {
            etNewWord.requestFocus();
        } else {
            new androidx.core.view.WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                    .hide(androidx.core.view.WindowInsetsCompat.Type.ime());
        }
    }

    private void xuLyClickListView() {
        lvFlashcards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentIndex = position;
                hienThiTheTo(currentIndex);
            }
        });
    }

    private void xuLyLongClickXoaTu() {
        lvFlashcards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Flashcard cardCanXoa = flashcardList.get(position);

                new AlertDialog.Builder(FlashcardActivity.this)
                        .setTitle("Xóa từ vựng")
                        .setMessage("Bạn có chắc muốn xóa từ '" + cardCanXoa.getWord() + "' khỏi danh sách không?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
