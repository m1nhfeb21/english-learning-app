package com.example.englishlearningsoftware;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Main navigation screen and skill-filtered practice list. */
public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_FIRST_NAME = "firstName";
    public static final String EXTRA_LAST_NAME  = "lastName";
    public static final String EXTRA_ACCOUNT_ID = "accountId";
    public static final String EXTRA_PREVIEW    = "preview";

    private TextView welcome;
    private boolean  preview;
    private String selectedSkill = "";
    private EditText exerciseSearch;
    private LinearLayout exerciseList;
    private Button[] skillFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preview = getIntent().getBooleanExtra(EXTRA_PREVIEW, false);
        welcome = findViewById(R.id.tvWelcome);
        Button btnLogout = findViewById(R.id.btnLogout);

        if (preview) {
            welcome.setText(R.string.preview_welcome);
            btnLogout.setText(R.string.back_to_login);
        }

        // ── Hồ sơ ───────────────────────────────────────────────────────────
        findViewById(R.id.btnOpenProfile).setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        });
        findViewById(R.id.homeAvatarContainer).setOnClickListener(
                view -> findViewById(R.id.btnOpenProfile).performClick());

        // ── Bộ thẻ & Camera ─────────────────────────────────────────────────
        findViewById(R.id.btnOpenFlashcards).setOnClickListener(
                view -> openIfInstalled("FlashcardActivity"));
        findViewById(R.id.btnOpenCamera).setOnClickListener(
                view -> openIfInstalled("ScannerActivity"));

        setupExercises(savedInstanceState);

        // ── Bài làm chung (dự phòng) ────────────────────────────────────────
        findViewById(R.id.btnOpenPractice).setOnClickListener(
                view -> openIfInstalled("QuizActivity", "PracticeActivity", "ExerciseActivity"));

        // ── Đăng xuất ───────────────────────────────────────────────────────
        btnLogout.setOnClickListener(view -> {
            if (!preview) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            finish();
        });

        View root = findViewById(R.id.homeRoot);
        final int padding = root.getPaddingLeft();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(padding + bars.left, padding + bars.top,
                    padding + bars.right, padding + bars.bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProfileAvatar.bind(this, getIntent(), findViewById(R.id.ivHomeAvatar),
                findViewById(R.id.tvHomeAvatar));
        if (!preview && welcome != null) {
            String name = ProfileStore.getName(this, getIntent());
            welcome.setText(name.trim().isEmpty()
                    ? getString(R.string.welcome_back)
                    : getString(R.string.welcome_name, name));
        }
    }

    private void setupExercises(Bundle savedInstanceState) {
        exerciseList = findViewById(R.id.exerciseList);
        exerciseSearch = findViewById(R.id.etExerciseSearch);
        setupExercise(R.id.btnOpenQuiz, "Trắc nghiệm từ vựng", "Đọc", QuizActivity.class);
        setupExercise(R.id.btnOpenListening, "Nghe và chọn từ", "Nghe", ListeningActivity.class);
        setupExercise(R.id.btnOpenFillBlank, "Điền từ vào câu", "Đọc", FillBlankActivity.class);
        setupExercise(R.id.btnOpenWriting, "Viết từ tiếng Anh", "Viết", WritingActivity.class);
        skillFilters = new Button[]{findViewById(R.id.filterAll), findViewById(R.id.filterListening),
                findViewById(R.id.filterSpeaking), findViewById(R.id.filterReading),
                findViewById(R.id.filterWriting)};
        if (savedInstanceState != null) {
            selectedSkill = savedInstanceState.getString("exercise_skill", "");
        }
        for (Button filter : skillFilters) {
            filter.setOnClickListener(view -> {
                selectedSkill = filter == skillFilters[0] ? "" : filter.getText().toString();
                filterExercises();
            });
        }
        exerciseSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExercises();
            }
            @Override public void afterTextChanged(Editable text) {}
        });
        filterExercises();
    }

    private void setupExercise(int id, String title, String skill, Class<?> activityClass) {
        View card = findViewById(id);
        card.setTag(skill);
        ((TextView) card.findViewById(R.id.exerciseTitle)).setText(title);
        ((TextView) card.findViewById(R.id.exerciseSkill)).setText(skill);
        View.OnClickListener start = view -> startActivity(
                new Intent(this, activityClass).putExtras(getIntent()));
        card.setOnClickListener(start);
        card.setFocusable(true);
        View button = card.findViewById(R.id.btnStartExercise);
        button.setContentDescription("Bắt đầu: " + title);
        button.setOnClickListener(start);
    }

    private void filterExercises() {
        String query = exerciseSearch.getText().toString();
        int count = 0;
        for (int i = 0; i < exerciseList.getChildCount(); i++) {
            View card = exerciseList.getChildAt(i);
            String title = ((TextView) card.findViewById(R.id.exerciseTitle)).getText().toString();
            boolean matches = ExerciseFilter.matches((String) card.getTag(), selectedSkill, title, query);
            card.setVisibility(matches ? View.VISIBLE : View.GONE);
            if (matches) count++;
        }
        for (Button filter : skillFilters) {
            boolean selected = filter == skillFilters[0]
                    ? selectedSkill.isEmpty() : selectedSkill.contentEquals(filter.getText());
            filter.setSelected(selected);
            filter.setBackgroundResource(selected ? R.drawable.bg_primary_red : R.drawable.bg_btn_outline);
            filter.setTextColor(Color.parseColor(selected ? "#FFFFFF" : "#616873"));
        }
        TextView empty = findViewById(R.id.tvExerciseEmpty);
        empty.setText(selectedSkill.equals("Nói")
                ? "Chưa có bài luyện nói. Bạn có thể chọn kỹ năng khác."
                : "Không tìm thấy bài tập phù hợp. Thử từ khóa hoặc kỹ năng khác.");
        empty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("exercise_skill", selectedSkill);
        super.onSaveInstanceState(outState);
    }

    private void openIfInstalled(String... classNames) {
        for (String name : classNames) {
            try {
                Class<?> activityClass = Class.forName(getPackageName() + "." + name);
                Intent intent = new Intent(this, activityClass);
                intent.putExtras(getIntent());
                startActivity(intent);
                return;
            } catch (ClassNotFoundException | ActivityNotFoundException ignored) {
                // The corresponding teammate's feature may not have been merged yet.
            }
        }
        Toast.makeText(this, R.string.home_feature_pending, Toast.LENGTH_SHORT).show();
    }
}
