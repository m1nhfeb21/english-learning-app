package com.example.englishlearningsoftware;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WritingActivity extends AppCompatActivity {

    private static final String[][] QUESTIONS = {
        {"Từ vựng",        "vocabulary",    "danh từ",      "She has a large ___ of English words."},
        {"Thành thạo",     "fluent",        "tính từ",      "He speaks ___ English after 2 years."},
        {"Phát âm",        "pronunciation", "danh từ",      "Good ___ helps communication greatly."},
        {"Ngữ pháp",       "grammar",       "danh từ",      "English ___ can be tricky for beginners."},
        {"Sự lĩnh hội",    "comprehension", "danh từ",      "Reading ___ is a key IELTS skill."}
    };

    private int     currentIndex = 0;
    private boolean answered     = false;
    private int     score        = 0;
    private boolean hintShown    = false;

    private final String[] userAnswers    = new String[QUESTIONS.length];
    private final String[] correctAnswers = new String[QUESTIONS.length];
    private final String[] questionLabels = new String[QUESTIONS.length];

    private TextView    tvCounter, tvScore, tvMeaning, tvPartOfSpeech, tvExample, tvHint;
    private ProgressBar progressBar;

    private EditText     etAnswer;
    private TextView     btnShowHint;

    private LinearLayout layoutFeedback;
    private TextView     tvFeedbackIcon, tvFeedbackText;
    private Button       btnCheck, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        bindViews();
        setupTextWatcher();
        setupImeAction();

        findViewById(R.id.btnWriteBack).setOnClickListener(v -> finish());
        btnShowHint.setOnClickListener(v -> showHint());
        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> nextQuestion());

        loadQuestion(currentIndex);
    }

    private void bindViews() {
        tvCounter       = findViewById(R.id.tvWriteCounter);
        tvScore         = findViewById(R.id.tvWriteScore);
        tvMeaning       = findViewById(R.id.tvWriteMeaning);
        tvPartOfSpeech  = findViewById(R.id.tvWritePartOfSpeech);
        tvExample       = findViewById(R.id.tvWriteExample);
        tvHint          = findViewById(R.id.tvWriteHint);
        progressBar     = findViewById(R.id.writeProgressBar);
        etAnswer        = findViewById(R.id.etWriteAnswer);
        btnShowHint     = (TextView) findViewById(R.id.btnShowHint);
        layoutFeedback  = findViewById(R.id.layoutWriteFeedback);
        tvFeedbackIcon  = (TextView) findViewById(R.id.tvWriteFeedbackIcon);
        tvFeedbackText  = (TextView) findViewById(R.id.tvWriteFeedbackText);
        btnCheck        = findViewById(R.id.btnWriteCheck);
        btnNext         = findViewById(R.id.btnWriteNext);
    }

    private void setupTextWatcher() {
        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!answered) {
                    boolean hasText = s.toString().trim().length() > 0;
                    btnCheck.setEnabled(hasText);
                    btnCheck.setBackground(getDrawable(
                        hasText ? R.drawable.bg_primary_red : R.drawable.bg_btn_disabled));
                }
            }
        });
    }

    private void setupImeAction() {
        etAnswer.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && !answered) {
                if (etAnswer.getText().toString().trim().length() > 0) checkAnswer();
                return true;
            }
            return false;
        });
    }

    private void loadQuestion(int index) {
        answered  = false;
        hintShown = false;

        String[] q = QUESTIONS[index];
        int total  = QUESTIONS.length;

        tvCounter.setText((index + 1) + " / " + total);
        progressBar.setProgress((int) (((index + 1) / (float) total) * 100));

        tvMeaning.setText(q[0]);
        tvPartOfSpeech.setText(q[2]);
        tvExample.setText("Ví dụ: " + q[3]);

        questionLabels[index] = q[0] + " → " + q[1];
        correctAnswers[index] = q[1];

        etAnswer.setText("");
        etAnswer.setBackground(getDrawable(R.drawable.bg_writing_input));
        etAnswer.setEnabled(true);
        etAnswer.setHint("Nhập từ tiếng Anh...");

        tvHint.setVisibility(View.INVISIBLE);
        btnShowHint.setEnabled(true);
        btnShowHint.setAlpha(1f);

        layoutFeedback.setVisibility(View.GONE);
        btnCheck.setEnabled(false);
        btnCheck.setBackground(getDrawable(R.drawable.bg_btn_disabled));
        btnCheck.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);
    }

    private void showHint() {
        if (hintShown || answered) return;
        hintShown = true;
        String word = QUESTIONS[currentIndex][1];

        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(word.charAt(0)));
        for (int i = 1; i < word.length(); i++) {
            sb.append(word.charAt(i) == ' ' ? "  " : " _");
        }
        tvHint.setText(sb.toString());
        tvHint.setVisibility(View.VISIBLE);
        btnShowHint.setEnabled(false);
        btnShowHint.setAlpha(0.5f);
    }

    private void checkAnswer() {
        if (answered) return;
        String userInput = etAnswer.getText().toString().trim();
        if (userInput.isEmpty()) return;

        answered = true;
        userAnswers[currentIndex] = userInput;

        String correct    = QUESTIONS[currentIndex][1];
        boolean isCorrect = correct.equalsIgnoreCase(userInput);

        if (isCorrect) score++;
        tvScore.setText(score + " điểm");

        etAnswer.setEnabled(false);
        if (isCorrect) {
            etAnswer.setBackground(getDrawable(R.drawable.bg_writing_input_correct));
            etAnswer.setTextColor(Color.parseColor("#16A34A"));
        } else {
            etAnswer.setBackground(getDrawable(R.drawable.bg_writing_input_wrong));
            etAnswer.setTextColor(Color.parseColor("#DC2626"));
        }

        layoutFeedback.setVisibility(View.VISIBLE);
        if (isCorrect) {
            layoutFeedback.setBackgroundColor(Color.parseColor("#DCFCE7"));
            tvFeedbackIcon.setText("🎉");
            tvFeedbackText.setText("Chính xác! Câu trả lời: \"" + correct + "\"");
            tvFeedbackText.setTextColor(Color.parseColor("#16A34A"));

            if (!hintShown) { hintShown = true; tvHint.setVisibility(View.INVISIBLE); }
        } else {
            layoutFeedback.setBackgroundColor(Color.parseColor("#FEE2E2"));
            tvFeedbackIcon.setText("❌");
            tvFeedbackText.setText("Chưa đúng! Đáp án: \"" + correct + "\"");
            tvFeedbackText.setTextColor(Color.parseColor("#DC2626"));

            tvHint.setText(correct);
            tvHint.setVisibility(View.VISIBLE);
        }

        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        boolean isLast = (currentIndex >= QUESTIONS.length - 1);
        btnNext.setText(isLast ? "XEM KẾT QUẢ 🏆" : "CÂU TIẾP THEO ▶");
    }

    private void nextQuestion() {
        if (currentIndex >= QUESTIONS.length - 1) {
            launchResult();
        } else {
            currentIndex++;
            loadQuestion(currentIndex);
        }
    }

    private void launchResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_TYPE,      "Luyện Viết");
        intent.putExtra(ResultActivity.EXTRA_SCORE,     score);
        intent.putExtra(ResultActivity.EXTRA_TOTAL,     QUESTIONS.length);
        intent.putExtra(ResultActivity.EXTRA_QUESTIONS, questionLabels);
        intent.putExtra(ResultActivity.EXTRA_USER_ANS,  userAnswers);
        intent.putExtra(ResultActivity.EXTRA_CORRECT,   correctAnswers);
        startActivity(intent);
        finish();
    }
}
