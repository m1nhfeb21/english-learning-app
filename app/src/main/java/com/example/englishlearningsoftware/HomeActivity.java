package com.example.englishlearningsoftware;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Màn Home cơ bản để nhóm tiếp tục xây dựng các chức năng học tập. */
public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_FIRST_NAME = "firstName";
    public static final String EXTRA_LAST_NAME = "lastName";
    public static final String EXTRA_ACCOUNT_ID = "accountId";
    public static final String EXTRA_PREVIEW = "preview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final boolean preview = getIntent().getBooleanExtra(EXTRA_PREVIEW, false);
        TextView welcome = findViewById(R.id.tvWelcome);
        TextView status = findViewById(R.id.tvHomeStatus);
        Button btnLogout = findViewById(R.id.btnLogout);
        if (preview) {
            welcome.setText(R.string.preview_welcome);
            status.setText(R.string.preview_status);
            btnLogout.setText(R.string.back_to_login);
        } else {
            String firstName = getIntent().getStringExtra(EXTRA_FIRST_NAME);
            String lastName = getIntent().getStringExtra(EXTRA_LAST_NAME);
            String fullName = ((firstName == null ? "" : firstName) + " "
                    + (lastName == null ? "" : lastName)).trim();
            welcome.setText(fullName.isEmpty() ? getString(R.string.welcome_back)
                    : getString(R.string.welcome_name, fullName));
            status.setText(R.string.logged_in_status);
        }
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!preview) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                finish();
            }
        });
        View root = findViewById(R.id.homeRoot);
        final int padding = root.getPaddingLeft();
        ViewCompat.setOnApplyWindowInsetsListener(root,
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View view,
                            WindowInsetsCompat windowInsets) {
                        Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                        view.setPadding(padding + bars.left, padding + bars.top,
                                padding + bars.right, padding + bars.bottom);
                        return windowInsets;
                    }
                });
        ViewCompat.requestApplyInsets(root);
    }
}
