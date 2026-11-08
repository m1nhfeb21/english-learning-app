package com.example.englishlearningsoftware;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Màn hình Kết Quả (Result Screen).
 * Nhận dữ liệu từ bất kỳ màn hình luyện tập nào (Quiz, FillBlank, Listening, Writing)
 * và hiển thị: điểm %, sao đánh giá, và chi tiết từng câu đúng/sai.
 */
public class ResultActivity extends AppCompatActivity {

    // ── EXTRA KEYS ────────────────────────────────────────────────────────────
    public static final String EXTRA_TYPE      = "result_type";
    public static final String EXTRA_SCORE     = "result_score";
    public static final String EXTRA_TOTAL     = "result_total";
    public static final String EXTRA_QUESTIONS = "result_questions";
    public static final String EXTRA_USER_ANS  = "result_user_answers";
    public static final String EXTRA_CORRECT   = "result_correct_answers";

    // ── LIFECYCLE ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Lấy dữ liệu từ Intent
        Intent in      = getIntent();
        String type    = in.getStringExtra(EXTRA_TYPE);
        int score      = in.getIntExtra(EXTRA_SCORE, 0);
        int total      = in.getIntExtra(EXTRA_TOTAL, 1);
        String[] questions = in.getStringArrayExtra(EXTRA_QUESTIONS);
        String[] userAns   = in.getStringArrayExtra(EXTRA_USER_ANS);
        String[] correct   = in.getStringArrayExtra(EXTRA_CORRECT);

        if (type == null)      type      = "Bài Tập";
        if (questions == null) questions = new String[0];
        if (userAns   == null) userAns   = new String[0];
        if (correct   == null) correct   = new String[0];

        renderResult(type, score, total, questions, userAns, correct);

        // Nút quay lại
        findViewById(R.id.btnResultBack).setOnClickListener(v -> finish());

        // Nút về Home
        final String[] finalQuestions = questions;
        final String[] finalUserAns   = userAns;
        final String[] finalCorrect   = correct;
        final String   finalType      = type;
        final int      finalScore     = score;
        final int      finalTotal     = total;

        findViewById(R.id.btnResultHome).setOnClickListener(v -> {
            Intent home = new Intent(this, HomeActivity.class);
            home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(home);
            finish();
        });

        // Nút làm lại → quay lại Activity tương ứng
        Button btnRetry = findViewById(R.id.btnResultRetry);
        btnRetry.setOnClickListener(v -> {
            Class<?> target = resolveActivityClass(finalType);
            if (target != null) {
                startActivity(new Intent(this, target));
            }
            finish();
        });
    }

    // ── RENDER ────────────────────────────────────────────────────────────────
    private void renderResult(String type, int score, int total,
                              String[] questions, String[] userAns, String[] correct) {
        int pct  = total > 0 ? (int) ((score / (float) total) * 100) : 0;
        int wrong = total - score;

        // Loại bài
        ((TextView) findViewById(R.id.tvResultType)).setText(type);

        // Phần trăm + phân số
        TextView tvPct      = findViewById(R.id.tvResultPercent);
        TextView tvFraction = findViewById(R.id.tvResultFraction);
        View     circle     = findViewById(R.id.resultCircle);

        tvPct.setText(pct + "%");
        tvFraction.setText(score + " / " + total);

        // Màu vòng tròn theo % điểm
        if (pct >= 80) {
            circle.setBackground(getDrawable(R.drawable.bg_result_circle_good));
            tvPct.setTextColor(Color.parseColor("#16A34A"));
        } else if (pct >= 60) {
            circle.setBackground(getDrawable(R.drawable.bg_result_circle_avg));
            tvPct.setTextColor(Color.parseColor("#D97706"));
        } else {
            circle.setBackground(getDrawable(R.drawable.bg_result_circle_bad));
            tvPct.setTextColor(Color.parseColor("#DC2626"));
        }

        // Sao
        TextView tvStars = findViewById(R.id.tvResultStars);
        int stars = pct >= 90 ? 5 : pct >= 75 ? 4 : pct >= 60 ? 3 : pct >= 40 ? 2 : 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < stars ? "⭐" : "☆");
        tvStars.setText(sb.toString());

        // Thông điệp
        TextView tvMsg = findViewById(R.id.tvResultMessage);
        if (pct >= 90)      tvMsg.setText("Xuất sắc! Bạn đã thành thạo! 🏆");
        else if (pct >= 75) tvMsg.setText("Rất tốt! Tiếp tục phát huy! 💪");
        else if (pct >= 60) tvMsg.setText("Khá tốt! Cố gắng hơn nhé! 👍");
        else if (pct >= 40) tvMsg.setText("Cần luyện thêm nhiều hơn! 📚");
        else                tvMsg.setText("Hãy ôn lại từ vựng và thử lại! 🔄");

        // Thống kê đúng / sai
        ((TextView) findViewById(R.id.tvResultCorrect)).setText(score + " câu đúng");
        ((TextView) findViewById(R.id.tvResultWrong)).setText(wrong + " câu sai");

        // Chi tiết từng câu
        LinearLayout detailContainer = findViewById(R.id.layoutResultDetails);
        detailContainer.removeAllViews();

        for (int i = 0; i < questions.length; i++) {
            String qLabel  = questions[i];
            String userA   = (i < userAns.length && userAns[i] != null)  ? userAns[i]  : "(bỏ qua)";
            String corrA   = (i < correct.length  && correct[i] != null)  ? correct[i]  : "?";
            boolean ok     = corrA.equalsIgnoreCase(userA);

            // Hàng kết quả
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackground(getDrawable(ok
                    ? R.drawable.bg_result_row_correct
                    : R.drawable.bg_result_row_wrong));

            int px12 = dp(12);
            int px10 = dp(10);
            row.setPadding(px12, px10, px12, px10);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowLp.bottomMargin = dp(8);
            row.setLayoutParams(rowLp);

            // Số thứ tự
            TextView tvNum = new TextView(this);
            tvNum.setText((i + 1) + ".");
            tvNum.setTextColor(ok ? Color.parseColor("#16A34A") : Color.parseColor("#DC2626"));
            tvNum.setTextSize(14f);
            tvNum.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(dp(28), LinearLayout.LayoutParams.WRAP_CONTENT);
            tvNum.setLayoutParams(numLp);

            // Nội dung (câu hỏi + đáp án)
            LinearLayout col = new LinearLayout(this);
            col.setOrientation(LinearLayout.VERTICAL);
            col.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvQ = new TextView(this);
            tvQ.setText(qLabel);
            tvQ.setTextColor(Color.parseColor("#374151"));
            tvQ.setTextSize(13f);

            TextView tvAns = new TextView(this);
            if (ok) {
                tvAns.setText("✓  " + corrA);
                tvAns.setTextColor(Color.parseColor("#16A34A"));
            } else {
                tvAns.setText("✗  Bạn: \"" + userA + "\"  →  Đúng: \"" + corrA + "\"");
                tvAns.setTextColor(Color.parseColor("#DC2626"));
            }
            tvAns.setTextSize(12f);
            tvAns.setTypeface(null, android.graphics.Typeface.BOLD);

            col.addView(tvQ);
            col.addView(tvAns);

            // Icon đúng/sai
            TextView tvIcon = new TextView(this);
            tvIcon.setText(ok ? "✓" : "✗");
            tvIcon.setTextColor(ok ? Color.parseColor("#16A34A") : Color.parseColor("#DC2626"));
            tvIcon.setTextSize(18f);
            tvIcon.setTypeface(null, android.graphics.Typeface.BOLD);
            tvIcon.setGravity(Gravity.CENTER);
            tvIcon.setLayoutParams(new LinearLayout.LayoutParams(dp(28), dp(28)));

            row.addView(tvNum);
            row.addView(col);
            row.addView(tvIcon);
            detailContainer.addView(row);
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private int dp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /** Trả về Activity cần mở khi người dùng nhấn "Làm lại" */
    private Class<?> resolveActivityClass(String type) {
        switch (type) {
            case "Luyện Nghe": return ListeningActivity.class;
            case "Luyện Viết": return WritingActivity.class;
            case "Trắc Nghiệm": return QuizActivity.class;
            case "Điền Từ":    return FillBlankActivity.class;
            default:           return null;
        }
    }
}
