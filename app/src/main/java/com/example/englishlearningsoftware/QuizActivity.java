package com.example.englishlearningsoftware;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Màn hình trắc nghiệm (Multiple Choice Quiz).
 * Hiển thị câu hỏi, 4 lựa chọn A-D, đếm ngược thời gian,
 * phản hồi đúng/sai và chuyển sang ResultActivity.
 */
public class QuizActivity extends AppCompatActivity {

    // ── DỮ LIỆU MẪU ─────────────────────────────────────────────────────────
    /** Mỗi phần tử: {câu hỏi, optionA, optionB, optionC, optionD, chỉ số đúng (0-3)} */
    private static final String[][] QUESTIONS = {
        {
            "What is the meaning of the word 'vocabulary'?",
            "Từ vựng", "Quyển sách", "Bảng đen", "Cái bàn", "0"
        },
        {
            "Choose the correct meaning of 'fluent':",
            "Lỏng lẻo", "Thành thạo, trôi chảy", "Khó hiểu", "Im lặng", "1"
        },
        {
            "What does 'pronunciation' mean?",
            "Ngữ pháp", "Giao tiếp", "Phát âm", "Chính tả", "2"
        },
        {
            "The word 'comprehension' means:",
            "Nghe", "Nói", "Viết", "Hiểu biết, sự lĩnh hội", "3"
        },
        {
            "What is the meaning of 'bilingual'?",
            "Nói được hai ngôn ngữ", "Học rất chăm", "Người mới học", "Dịch thuật", "0"
        }
    };

    // ── TRẠNG THÁI ───────────────────────────────────────────────────────────
    private int currentIndex   = 0;
    private int selectedOption = -1;
    private boolean answered   = false;
    private int score          = 0;
    private CountDownTimer countDownTimer;
    private static final long TIMER_MS = 30_000L;

    // Lưu kết quả để truyền sang ResultActivity
    private final String[] userAnswers    = new String[QUESTIONS.length];
    private final String[] correctAnswers = new String[QUESTIONS.length];
    private final String[] questionLabels = new String[QUESTIONS.length];

    // ── VIEW REFERENCES ───────────────────────────────────────────────────────
    private TextView tvCounter, tvTimer, tvQuestion, tvTopic;
    private ProgressBar progressBar;

    private LinearLayout[] optionLayouts;
    private TextView[]     tvLetters, tvOptions, tvIcons;

    private LinearLayout layoutFeedback;
    private TextView     tvFeedbackIcon, tvFeedbackText;
    private Button       btnCheck, btnNext;

    // ── LIFECYCLE ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        bindViews();
        setupOptionClickListeners();

        findViewById(R.id.btnQuizBack).setOnClickListener(v -> finish());
        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> nextQuestion());

        loadQuestion(currentIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    // ── BIND VIEWS ────────────────────────────────────────────────────────────
    private void bindViews() {
        tvCounter    = findViewById(R.id.tvQuizCounter);
        tvTimer      = findViewById(R.id.tvQuizTimer);
        tvQuestion   = findViewById(R.id.tvQuizQuestion);
        tvTopic      = findViewById(R.id.tvQuizTopic);
        progressBar  = findViewById(R.id.quizProgressBar);

        optionLayouts = new LinearLayout[]{
            findViewById(R.id.optionA),
            findViewById(R.id.optionB),
            findViewById(R.id.optionC),
            findViewById(R.id.optionD)
        };
        tvLetters = new TextView[]{
            findViewById(R.id.tvLetterA),
            findViewById(R.id.tvLetterB),
            findViewById(R.id.tvLetterC),
            findViewById(R.id.tvLetterD)
        };
        tvOptions = new TextView[]{
            findViewById(R.id.tvOptionA),
            findViewById(R.id.tvOptionB),
            findViewById(R.id.tvOptionC),
            findViewById(R.id.tvOptionD)
        };
        tvIcons = new TextView[]{
            findViewById(R.id.tvIconA),
            findViewById(R.id.tvIconB),
            findViewById(R.id.tvIconC),
            findViewById(R.id.tvIconD)
        };

        layoutFeedback  = findViewById(R.id.layoutQuizFeedback);
        tvFeedbackIcon  = (TextView) findViewById(R.id.tvQuizFeedbackIcon);
        tvFeedbackText  = (TextView) findViewById(R.id.tvQuizFeedbackText);
        btnCheck        = findViewById(R.id.btnQuizCheck);
        btnNext         = findViewById(R.id.btnQuizNext);
    }

    // ── OPTION CLICK ──────────────────────────────────────────────────────────
    private void setupOptionClickListeners() {
        for (int i = 0; i < optionLayouts.length; i++) {
            final int idx = i;
            optionLayouts[i].setOnClickListener(v -> {
                if (!answered) selectOption(idx);
            });
        }
    }

    private void selectOption(int idx) {
        selectedOption = idx;
        for (int i = 0; i < 4; i++) {
            optionLayouts[i].setBackground(getDrawable(R.drawable.bg_quiz_option));
            tvLetters[i].setBackground(getDrawable(R.drawable.bg_letter_badge));
            tvLetters[i].setTextColor(Color.parseColor("#374151"));
            tvOptions[i].setTextColor(Color.parseColor("#111827"));
        }
        optionLayouts[idx].setBackground(getDrawable(R.drawable.bg_quiz_option_selected));
        tvLetters[idx].setBackground(getDrawable(R.drawable.bg_letter_badge_selected));
        tvLetters[idx].setTextColor(Color.WHITE);
        tvOptions[idx].setTextColor(Color.parseColor("#4F46E5"));

        btnCheck.setEnabled(true);
        btnCheck.setBackground(getDrawable(R.drawable.bg_primary_red));
    }

    // ── TẢI CÂU HỎI ──────────────────────────────────────────────────────────
    private void loadQuestion(int index) {
        cancelTimer();
        answered       = false;
        selectedOption = -1;

        String[] q = QUESTIONS[index];
        int total  = QUESTIONS.length;

        tvCounter.setText((index + 1) + " / " + total);
        progressBar.setProgress((int) (((index + 1) / (float) total) * 100));
        tvQuestion.setText(q[0]);

        // Lưu nhãn kết quả
        questionLabels[index] = q[0];
        correctAnswers[index] = q[Integer.parseInt(q[5]) + 1];

        String[] labels = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            tvLetters[i].setText(labels[i]);
            tvOptions[i].setText(q[i + 1]);
            tvIcons[i].setText("");
            tvIcons[i].setVisibility(View.INVISIBLE);
            optionLayouts[i].setBackground(getDrawable(R.drawable.bg_quiz_option));
            tvLetters[i].setBackground(getDrawable(R.drawable.bg_letter_badge));
            tvLetters[i].setTextColor(Color.parseColor("#374151"));
            tvOptions[i].setTextColor(Color.parseColor("#111827"));
            optionLayouts[i].setEnabled(true);
        }

        btnCheck.setEnabled(false);
        btnCheck.setBackground(getDrawable(R.drawable.bg_btn_disabled));
        btnCheck.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);
        layoutFeedback.setVisibility(View.GONE);

        startTimer();
    }

    // ── KIỂM TRA ─────────────────────────────────────────────────────────────
    private void checkAnswer() {
        if (answered || selectedOption < 0) return;
        answered = true;
        cancelTimer();

        int correctIdx = Integer.parseInt(QUESTIONS[currentIndex][5]);
        boolean isCorrect = (selectedOption == correctIdx);
        if (isCorrect) score++;

        // Lưu đáp án người dùng
        userAnswers[currentIndex] = QUESTIONS[currentIndex][selectedOption + 1];

        for (int i = 0; i < 4; i++) {
            optionLayouts[i].setEnabled(false);
            tvIcons[i].setVisibility(View.VISIBLE);
            if (i == correctIdx) {
                optionLayouts[i].setBackground(getDrawable(R.drawable.bg_quiz_option_correct));
                tvLetters[i].setBackground(getDrawable(R.drawable.bg_letter_badge_correct));
                tvLetters[i].setTextColor(Color.WHITE);
                tvOptions[i].setTextColor(Color.parseColor("#16A34A"));
                tvIcons[i].setText("✓");
                tvIcons[i].setTextColor(Color.parseColor("#16A34A"));
            } else if (i == selectedOption) {
                optionLayouts[i].setBackground(getDrawable(R.drawable.bg_quiz_option_wrong));
                tvLetters[i].setBackground(getDrawable(R.drawable.bg_letter_badge_wrong));
                tvLetters[i].setTextColor(Color.WHITE);
                tvOptions[i].setTextColor(Color.parseColor("#DC2626"));
                tvIcons[i].setText("✗");
                tvIcons[i].setTextColor(Color.parseColor("#DC2626"));
            } else {
                tvIcons[i].setVisibility(View.INVISIBLE);
            }
        }

        layoutFeedback.setVisibility(View.VISIBLE);
        if (isCorrect) {
            layoutFeedback.setBackgroundColor(Color.parseColor("#DCFCE7"));
            tvFeedbackIcon.setText("🎉");
            tvFeedbackText.setText("Chính xác! Rất giỏi!");
            tvFeedbackText.setTextColor(Color.parseColor("#16A34A"));
        } else {
            layoutFeedback.setBackgroundColor(Color.parseColor("#FEE2E2"));
            tvFeedbackIcon.setText("❌");
            tvFeedbackText.setText("Chưa đúng! Đáp án: " + QUESTIONS[currentIndex][correctIdx + 1]);
            tvFeedbackText.setTextColor(Color.parseColor("#DC2626"));
        }

        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        boolean isLast = (currentIndex >= QUESTIONS.length - 1);
        btnNext.setText(isLast ? "XEM KẾT QUẢ 🏆" : "CÂU TIẾP THEO ▶");
    }

    // ── CÂU TIẾP THEO ────────────────────────────────────────────────────────
    private void nextQuestion() {
        if (currentIndex >= QUESTIONS.length - 1) {
            launchResult();
        } else {
            currentIndex++;
            loadQuestion(currentIndex);
        }
    }

    // ── ĐỒNG HỒ ──────────────────────────────────────────────────────────────
    private void startTimer() {
        countDownTimer = new CountDownTimer(TIMER_MS, 1000) {
            @Override
            public void onTick(long ms) {
                long s = ms / 1000;
                tvTimer.setText(String.format("%02d:%02d", s / 60, s % 60));
                tvTimer.setTextColor(s <= 10
                    ? Color.parseColor("#DC2626")
                    : Color.parseColor("#92400E"));
            }
            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                if (!answered) {
                    int correctIdx = Integer.parseInt(QUESTIONS[currentIndex][5]);
                    userAnswers[currentIndex] = "(hết giờ)";
                    answered = true;
                    optionLayouts[correctIdx].setBackground(getDrawable(R.drawable.bg_quiz_option_correct));
                    tvLetters[correctIdx].setBackground(getDrawable(R.drawable.bg_letter_badge_correct));
                    tvLetters[correctIdx].setTextColor(Color.WHITE);
                    layoutFeedback.setVisibility(View.VISIBLE);
                    layoutFeedback.setBackgroundColor(Color.parseColor("#FFF7ED"));
                    tvFeedbackIcon.setText("⏰");
                    tvFeedbackText.setText("Hết giờ! Đáp án: " + QUESTIONS[currentIndex][correctIdx + 1]);
                    tvFeedbackText.setTextColor(Color.parseColor("#92400E"));
                    for (LinearLayout lo : optionLayouts) lo.setEnabled(false);
                    btnCheck.setVisibility(View.GONE);
                    btnNext.setVisibility(View.VISIBLE);
                    boolean isLast = (currentIndex >= QUESTIONS.length - 1);
                    btnNext.setText(isLast ? "XEM KẾT QUẢ 🏆" : "CÂU TIẾP THEO ▶");
                }
            }
        }.start();
    }

    private void cancelTimer() {
        if (countDownTimer != null) { countDownTimer.cancel(); countDownTimer = null; }
    }

    // ── CHUYỂN SANG MÀN HÌNH KẾT QUẢ ────────────────────────────────────────
    private void launchResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_TYPE,      "Trắc Nghiệm");
        intent.putExtra(ResultActivity.EXTRA_SCORE,     score);
        intent.putExtra(ResultActivity.EXTRA_TOTAL,     QUESTIONS.length);
        intent.putExtra(ResultActivity.EXTRA_QUESTIONS, questionLabels);
        intent.putExtra(ResultActivity.EXTRA_USER_ANS,  userAnswers);
        intent.putExtra(ResultActivity.EXTRA_CORRECT,   correctAnswers);
        startActivity(intent);
        finish();
    }
}
