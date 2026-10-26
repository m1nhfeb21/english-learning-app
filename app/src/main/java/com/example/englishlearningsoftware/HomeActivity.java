package com.example.englishlearningsoftware;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Main navigation screen. Login/Register and the original Home design stay unchanged. */
public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_FIRST_NAME = "firstName";
    public static final String EXTRA_LAST_NAME = "lastName";
    public static final String EXTRA_ACCOUNT_ID = "accountId";
    public static final String EXTRA_PREVIEW = "preview";

    private TextView welcome;
    private boolean preview;

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

        findViewById(R.id.btnOpenProfile).setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        });
        // Feature branches will add their own Activities and manifest declarations.
        // Until merged, these shortcuts show an honest "not yet available" message.
        findViewById(R.id.btnOpenFlashcards).setOnClickListener(
                view -> openIfInstalled("FlashcardActivity"));
        findViewById(R.id.btnOpenCamera).setOnClickListener(
                view -> openIfInstalled("ScannerActivity"));
        findViewById(R.id.btnOpenPractice).setOnClickListener(
                view -> openIfInstalled("PracticeActivity", "ExerciseActivity"));

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
        if (!preview && welcome != null) {
            String name = ProfileStore.getName(this, getIntent());
            welcome.setText(name.trim().isEmpty()
                    ? getString(R.string.welcome_back)
                    : getString(R.string.welcome_name, name));
        }
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
