package com.example.englishlearningsoftware;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * Màn hình Luyện Nghe (Listening Exercise).
 * Sử dụng TextToSpeech để phát âm từ tiếng Anh.
 * Người học chọn từ đúng trong 4 lựa chọn.
 */
public class ListeningActivity extends AppCompatActivity {

    // ── DỮ LIỆU MẪU ─────────────────────────────────────────────────────────
    /**
     * Mỗi câu: {từ cần nghe, optionA, optionB, optionC, optionD, correctIndex(0-3), câu ví dụ}
     */
    private static final String[][] QUESTIONS = {
        {"vocabulary",    "vocabulary",    "documentary",  "dictionary",   "laboratory",   "0", "She has a large vocabulary."},
        {"pronunciation", "preparation",   "pronunciation","publication",  "presentation", "1", "Good pronunciation is key."},
        {"fluent",        "frequent",      "relevant",     "fluent",       "pleasant",     "2", "He speaks fluent English."},
        {"comprehension", "competition",   "composition",  "comprehension","compensation", "2", "Reading comprehension is important."},
        {"bilingual",     "binocular",     "bilateral",    "biological",   "bilingual",    "3", "She is bilingual in English and French."}
    };

    // ── TRẠNG THÁI ───────────────────────────────────────────────────────────
    private int     currentIndex   = 0;
    private int     selectedOption = -1;
    private boolean answered       = false;
    private int     score          = 0;

    // Lưu kết quả để truyền sang ResultActivity
    private final String[] userAnswers    = new String[QUESTIONS.length];
    private final String[] correctAnswers = new String[QUESTIONS.length];
    private final String[] questionLabels = new String[QUESTIONS.length];

    private TextToSpeech tts;
    private boolean      ttsReady = false;

    // ── VIEW REFERENCES ───────────────────────────────────────────────────────
    private TextView   tvCounter, tvScore;
    private ProgressBar progressBar;

    private LinearLayout   btnPlayAudio;
    private TextView       btnReplayAudio, tvRevealWord;

    private LinearLayout[] optionLayouts;
    private TextView[]     tvLetters, tvOptions, tvIcons;

    private LinearLayout layoutFeedback;
    private TextView     tvFeedbackIcon, tvFeedbackText;
    private Button       btnCheck, btnNext;

    // ── LIFECYCLE ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        initTts();
        bindViews();
        setupOptionClickListeners();

        findViewById(R.id.btnListenBack).setOnClickListener(v -> finish());
        btnPlayAudio.setOnClickListener(v -> playCurrentWord());
        btnReplayAudio.setOnClickListener(v -> playCurrentWord());
        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> nextQuestion());

        loadQuestion(currentIndex);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    // ── TTS ───────────────────────────────────────────────────────────────────
    private void initTts() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.ENGLISH);
                ttsReady = (result != TextToSpeech.LANG_MISSING_DATA
                         && result != TextToSpeech.LANG_NOT_SUPPORTED);
            }
        });
    }

    private void playCurrentWord() {
        if (ttsReady && currentIndex < QUESTIONS.length) {
            tts.speak(QUESTIONS[currentIndex][0], TextToSpeech.QUEUE_FLUSH, null, "listen_word");
        }
    }

    // ── BIND VIEWS ────────────────────────────────────────────────────────────
    private void bindViews() {
        tvCounter    = findViewById(R.id.tvListenCounter);
        tvScore      = findViewById(R.id.tvListenScore);
        progressBar  = findViewById(R.id.listenProgressBar);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        btnReplayAudio = (TextView) findViewById(R.id.btnReplayAudio);
        tvRevealWord   = (TextView) findViewById(R.id.tvRevealWord);

        optionLayouts = new LinearLayout[]{
            findViewById(R.id.optionLA),
            findViewById(R.id.optionLB),
            findViewById(R.id.optionLC),
            findViewById(R.id.optionLD)
        };
        tvLetters = new TextView[]{
            findViewById(R.id.tvLetterLA),
            findViewById(R.id.tvLetterLB),
            findViewById(R.id.tvLetterLC),
            findViewById(R.id.tvLetterLD)
        };
        tvOptions = new TextView[]{
            findViewById(R.id.tvOptionLA),
            findViewById(R.id.tvOptionLB),
            findViewById(R.id.tvOptionLC),
            findViewById(R.id.tvOptionLD)
        };
        tvIcons = new TextView[]{
            findViewById(R.id.tvIconLA),
            findViewById(R.id.tvIconLB),
            findViewById(R.id.tvIconLC),
            findViewById(R.id.tvIconLD)
        };

        layoutFeedback  = findViewById(R.id.layoutListenFeedback);
        tvFeedbackIcon  = (TextView) findViewById(R.id.tvListenFeedbackIcon);
        tvFeedbackText  = (TextView) findViewById(R.id.tvListenFeedbackText);
        btnCheck        = findViewById(R.id.btnListenCheck);
        btnNext         = findViewById(R.id.btnListenNext);
    }

    // ── OPTION CLICK ──────────────────────────────────────────────────────────
    private void setupOptionClickListeners() {
        for (int i = 0; i < 4; i++) {
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
        answered       = false;
        selectedOption = -1;
        String[] q     = QUESTIONS[index];
        int total      = QUESTIONS.length;

        tvCounter.setText((index + 1) + " / " + total);
        progressBar.setProgress((int) (((index + 1) / (float) total) * 100));

        // Ghi nhãn câu hỏi để hiển thị kết quả
        questionLabels[index] = "Nghe và chọn: \"" + q[0] + "\"";
        correctAnswers[index] = q[0];

        // Cài 4 lựa chọn
        String[] labels = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            tvLetters[i].setText(labels[i]);
            tvOptions[i].setText(q[i + 1]);
            tvIcons[i].setVisibility(View.INVISIBLE);
            tvIcons[i].setText("");
            optionLayouts[i].setBackground(getDrawable(R.drawable.bg_quiz_option));
            tvLetters[i].setBackground(getDrawable(R.drawable.bg_letter_badge));
            tvLetters[i].setTextColor(Color.parseColor("#374151"));
            tvOptions[i].setTextColor(Color.parseColor("#111827"));
            optionLayouts[i].setEnabled(true);
        }

        tvRevealWord.setVisibility(View.GONE);
        layoutFeedback.setVisibility(View.GONE);
        btnCheck.setEnabled(false);
        btnCheck.setBackground(getDrawable(R.drawable.bg_btn_disabled));
        btnCheck.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);

        // Tự động phát từ sau 600ms
        btnPlayAudio.postDelayed(this::playCurrentWord, 600);
    }

    // ── KIỂM TRA ─────────────────────────────────────────────────────────────
    private void checkAnswer() {
        if (answered || selectedOption < 0) return;
        answered = true;

        int correctIdx = Integer.parseInt(QUESTIONS[currentIndex][5]);
        boolean isCorrect = (selectedOption == correctIdx);
        if (isCorrect) score++;

        // Lưu đáp án người dùng
        userAnswers[currentIndex] = QUESTIONS[currentIndex][selectedOption + 1];

        // Màu đúng/sai
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

        // Hiện từ vừa nghe
        tvRevealWord.setText(QUESTIONS[currentIndex][0]);
        tvRevealWord.setVisibility(View.VISIBLE);

        // Cập nhật điểm
        tvScore.setText(score + " điểm");

        // Banner phản hồi
        layoutFeedback.setVisibility(View.VISIBLE);
        if (isCorrect) {
            layoutFeedback.setBackgroundColor(Color.parseColor("#DCFCE7"));
            tvFeedbackIcon.setText("🎉");
            tvFeedbackText.setText("Chính xác! Từ bạn nghe là: \"" + QUESTIONS[currentIndex][0] + "\"");
            tvFeedbackText.setTextColor(Color.parseColor("#16A34A"));
        } else {
            layoutFeedback.setBackgroundColor(Color.parseColor("#FEE2E2"));
            tvFeedbackIcon.setText("❌");
            tvFeedbackText.setText("Chưa đúng! Đáp án: \"" + QUESTIONS[currentIndex][0] + "\"");
            tvFeedbackText.setTextColor(Color.parseColor("#DC2626"));
        }

        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        boolean isLast = (currentIndex >= QUESTIONS.length - 1);
        btnNext.setText(isLast ? "XEM KẾT QUẢ 🏆" : "CÂU TIẾP THEO ▶");
    }

    // ── CÂU TIẾP THEO / KẾT QUẢ ──────────────────────────────────────────────
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
        intent.putExtra(ResultActivity.EXTRA_TYPE,    "Luyện Nghe");
        intent.putExtra(ResultActivity.EXTRA_SCORE,   score);
        intent.putExtra(ResultActivity.EXTRA_TOTAL,   QUESTIONS.length);
        intent.putExtra(ResultActivity.EXTRA_QUESTIONS, questionLabels);
        intent.putExtra(ResultActivity.EXTRA_USER_ANS,  userAnswers);
        intent.putExtra(ResultActivity.EXTRA_CORRECT,   correctAnswers);
        startActivity(intent);
        finish();
    }
}
