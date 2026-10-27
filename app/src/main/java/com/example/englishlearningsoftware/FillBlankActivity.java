package com.example.englishlearningsoftware;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Màn hình Điền Từ (Fill in the Blank).
 * Hiển thị câu có chỗ trống, kho từ để chọn, kiểm tra và chuyển sang ResultActivity.
 */
public class FillBlankActivity extends AppCompatActivity {

    private static final String[][] QUESTIONS = {
        {
            "She has a large ",
            " of English words.",
            "💡 Gợi ý: vốn từ vựng (danh từ)",
            "vocabulary",
            "vocabulary", "grammar", "fluency", "reading", "listening", "speaking"
        },
        {
            "He speaks English ",
            " after studying for 2 years.",
            "💡 Gợi ý: trôi chảy, thành thạo (trạng từ)",
            "fluently",
            "slowly", "fluently", "loudly", "quickly", "quietly", "carefully"
        },
        {
            "Correct ",
            " is important when learning a language.",
            "💡 Gợi ý: phát âm (danh từ)",
            "pronunciation",
            "writing", "grammar", "pronunciation", "reading", "listening", "spelling"
        },
        {
            "The IELTS test measures your ",
            " in English.",
            "💡 Gợi ý: năng lực, sự thành thạo (danh từ)",
            "proficiency",
            "fluency", "accent", "proficiency", "vocabulary", "grammar", "tone"
        },
        {
            "You need to ",
            " new words every day to improve.",
            "💡 Gợi ý: ghi nhớ, học thuộc (động từ)",
            "memorize",
            "forget", "skip", "memorize", "ignore", "avoid", "delete"
        }
    };

    // ── TRẠNG THÁI ───────────────────────────────────────────────────────────
    private int     currentIndex    = 0;
    private String  selectedWord    = null;
    private boolean answered        = false;
    private int     score           = 0;
    private int     selectedChipIdx = -1;

    private final String[] userAnswers    = new String[QUESTIONS.length];
    private final String[] correctAnswers = new String[QUESTIONS.length];
    private final String[] questionLabels = new String[QUESTIONS.length];

    // ── VIEW REFERENCES ───────────────────────────────────────────────────────
    private TextView    tvCounter, tvScore, tvHint;
    private TextView    tvSentenceBefore, tvBlankSlot, tvSentenceAfter;
    private ProgressBar progressBar;
    private TextView    tvSelectedWord, btnClearWord;
    private TextView[]  chipViews;
    private LinearLayout layoutFeedback;
    private TextView    tvFeedbackIcon, tvFeedbackText;
    private Button      btnCheck, btnNext;

    // ── LIFECYCLE ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_blank);

        bindViews();
        setupChipListeners();

        findViewById(R.id.btnFillBack).setOnClickListener(v -> finish());
        btnClearWord.setOnClickListener(v -> clearSelection());
        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> nextQuestion());

        loadQuestion(currentIndex);
    }

    // ── BIND VIEWS ────────────────────────────────────────────────────────────
    private void bindViews() {
        tvCounter        = (TextView) findViewById(R.id.tvFillCounter);
        tvScore          = (TextView) findViewById(R.id.tvFillScore);
        tvHint           = (TextView) findViewById(R.id.tvFillHint);
        tvSentenceBefore = (TextView) findViewById(R.id.tvSentenceBefore);
        tvBlankSlot      = (TextView) findViewById(R.id.tvBlankSlot);
        tvSentenceAfter  = (TextView) findViewById(R.id.tvSentenceAfter);
        progressBar      = (ProgressBar) findViewById(R.id.fillProgressBar);
        tvSelectedWord   = (TextView) findViewById(R.id.tvSelectedWord);
        btnClearWord     = (TextView) findViewById(R.id.btnClearWord);

        chipViews = new TextView[]{
            (TextView) findViewById(R.id.chipWord0),
            (TextView) findViewById(R.id.chipWord1),
            (TextView) findViewById(R.id.chipWord2),
            (TextView) findViewById(R.id.chipWord3),
            (TextView) findViewById(R.id.chipWord4),
            (TextView) findViewById(R.id.chipWord5)
        };

        layoutFeedback  = (LinearLayout) findViewById(R.id.layoutFillFeedback);
        tvFeedbackIcon  = (TextView) findViewById(R.id.tvFillFeedbackIcon);
        tvFeedbackText  = (TextView) findViewById(R.id.tvFillFeedbackText);
        btnCheck        = (Button) findViewById(R.id.btnFillCheck);
        btnNext         = (Button) findViewById(R.id.btnFillNext);
    }

    // ── CHIP CLICK ────────────────────────────────────────────────────────────
    private void setupChipListeners() {
        for (int i = 0; i < chipViews.length; i++) {
            final int idx = i;
            chipViews[i].setOnClickListener(v -> { if (!answered) selectChip(idx); });
        }
    }

    private void selectChip(int idx) {
        if (selectedChipIdx >= 0 && selectedChipIdx < chipViews.length) {
            chipViews[selectedChipIdx].setBackground(getDrawable(R.drawable.bg_word_chip));
            chipViews[selectedChipIdx].setTextColor(Color.parseColor("#111827"));
        }
        selectedChipIdx = idx;
        selectedWord    = chipViews[idx].getText().toString();

        chipViews[idx].setBackground(getDrawable(R.drawable.bg_word_chip_selected));
        chipViews[idx].setTextColor(Color.parseColor("#DC2626"));

        tvSelectedWord.setText(selectedWord);
        tvSelectedWord.setTextColor(Color.parseColor("#111827"));
        tvSelectedWord.setTypeface(null, android.graphics.Typeface.BOLD);

        tvBlankSlot.setText(selectedWord);
        tvBlankSlot.setTextColor(Color.parseColor("#DC2626"));
        tvBlankSlot.setTypeface(null, android.graphics.Typeface.BOLD);

        btnCheck.setEnabled(true);
        btnCheck.setBackground(getDrawable(R.drawable.bg_primary_red));
    }

    private void clearSelection() {
        if (answered) return;
        if (selectedChipIdx >= 0) {
            chipViews[selectedChipIdx].setBackground(getDrawable(R.drawable.bg_word_chip));
            chipViews[selectedChipIdx].setTextColor(Color.parseColor("#111827"));
        }
        selectedChipIdx = -1;
        selectedWord    = null;
        tvSelectedWord.setText("(chưa chọn)");
        tvSelectedWord.setTextColor(Color.parseColor("#9CA3AF"));
        tvSelectedWord.setTypeface(null, android.graphics.Typeface.ITALIC);
        tvBlankSlot.setText("_ _ _ _ _");
        tvBlankSlot.setTextColor(Color.parseColor("#9CA3AF"));
        tvBlankSlot.setTypeface(null, android.graphics.Typeface.ITALIC);
        btnCheck.setEnabled(false);
        btnCheck.setBackground(getDrawable(R.drawable.bg_btn_disabled));
    }

    // ── TẢI CÂU HỎI ──────────────────────────────────────────────────────────
    private void loadQuestion(int index) {
        answered        = false;
        selectedWord    = null;
        selectedChipIdx = -1;

        String[] q = QUESTIONS[index];
        int total  = QUESTIONS.length;

        tvCounter.setText((index + 1) + " / " + total);
        progressBar.setProgress((int) (((index + 1) / (float) total) * 100));
        tvSentenceBefore.setText(q[0]);
        tvSentenceAfter.setText(q[1]);
        tvHint.setText(q[2]);

        questionLabels[index] = q[0] + "___" + q[1];
        correctAnswers[index] = q[3];

        tvBlankSlot.setText("_ _ _ _ _");
        tvBlankSlot.setTextColor(Color.parseColor("#9CA3AF"));
        tvBlankSlot.setTypeface(null, android.graphics.Typeface.ITALIC);
        tvBlankSlot.setBackground(getDrawable(R.drawable.bg_answer_box));

        for (int i = 0; i < chipViews.length; i++) {
            chipViews[i].setText(i + 4 < q.length ? q[i + 4] : "");
            chipViews[i].setBackground(getDrawable(R.drawable.bg_word_chip));
            chipViews[i].setTextColor(Color.parseColor("#111827"));
            chipViews[i].setEnabled(true);
            chipViews[i].setAlpha(1f);
        }

        tvSelectedWord.setText("(chưa chọn)");
        tvSelectedWord.setTextColor(Color.parseColor("#9CA3AF"));
        tvSelectedWord.setTypeface(null, android.graphics.Typeface.ITALIC);
        layoutFeedback.setVisibility(View.GONE);
        btnCheck.setEnabled(false);
        btnCheck.setBackground(getDrawable(R.drawable.bg_btn_disabled));
        btnCheck.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);
    }

    // ── KIỂM TRA ─────────────────────────────────────────────────────────────
    private void checkAnswer() {
        if (answered || selectedWord == null) return;
        answered = true;

        String correct    = QUESTIONS[currentIndex][3];
        boolean isCorrect = correct.equalsIgnoreCase(selectedWord.trim());
        if (isCorrect) score++;

        userAnswers[currentIndex] = selectedWord;
        tvScore.setText(score + " điểm");

        if (isCorrect) {
            tvBlankSlot.setTextColor(Color.parseColor("#16A34A"));
            tvBlankSlot.setBackground(getDrawable(R.drawable.bg_quiz_option_correct));
        } else {
            tvBlankSlot.setTextColor(Color.parseColor("#DC2626"));
            tvBlankSlot.setBackground(getDrawable(R.drawable.bg_quiz_option_wrong));
        }

        for (TextView chip : chipViews) chip.setEnabled(false);
        for (int i = 0; i < chipViews.length; i++) {
            if (chipViews[i].getText().toString().equalsIgnoreCase(correct)) {
                chipViews[i].setBackground(getDrawable(R.drawable.bg_quiz_option_correct));
                chipViews[i].setTextColor(Color.parseColor("#16A34A"));
            } else if (i == selectedChipIdx && !isCorrect) {
                chipViews[i].setBackground(getDrawable(R.drawable.bg_quiz_option_wrong));
                chipViews[i].setTextColor(Color.parseColor("#DC2626"));
            }
        }

        layoutFeedback.setVisibility(View.VISIBLE);
        if (isCorrect) {
            layoutFeedback.setBackgroundColor(Color.parseColor("#DCFCE7"));
            tvFeedbackIcon.setText("🎉");
            tvFeedbackText.setText("Chính xác! \"" + correct + "\"");
            tvFeedbackText.setTextColor(Color.parseColor("#16A34A"));
        } else {
            layoutFeedback.setBackgroundColor(Color.parseColor("#FEE2E2"));
            tvFeedbackIcon.setText("❌");
            tvFeedbackText.setText("Chưa đúng! Đáp án: \"" + correct + "\"");
            tvFeedbackText.setTextColor(Color.parseColor("#DC2626"));
        }

        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        boolean isLast = (currentIndex >= QUESTIONS.length - 1);
        btnNext.setText(isLast ? "XEM KẾT QUẢ 🏆" : "CÂU TIẾP THEO ▶");
    }

    // ── CÂU TIẾP THEO / KẾT QUẢ ─────────────────────────────────────────────
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
        intent.putExtra(ResultActivity.EXTRA_TYPE,      "Điền Từ");
        intent.putExtra(ResultActivity.EXTRA_SCORE,     score);
        intent.putExtra(ResultActivity.EXTRA_TOTAL,     QUESTIONS.length);
        intent.putExtra(ResultActivity.EXTRA_QUESTIONS, questionLabels);
        intent.putExtra(ResultActivity.EXTRA_USER_ANS,  userAnswers);
        intent.putExtra(ResultActivity.EXTRA_CORRECT,   correctAnswers);
        startActivity(intent);
        finish();
    }
}
