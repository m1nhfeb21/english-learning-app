package com.example.englishlearningsoftware;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameView;
    private TextView avatarView;
    private TextView goalView;
    private TextView minutesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameView = findViewById(R.id.tvProfileName);
        avatarView = findViewById(R.id.tvProfileAvatar);
        goalView = findViewById(R.id.tvProfileGoal);
        minutesView = findViewById(R.id.tvProfileMinutes);

        findViewById(R.id.btnProfileBack).setOnClickListener(view -> finish());
        findViewById(R.id.btnEditProfile).setOnClickListener(view -> {
            Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editIntent.putExtras(getIntent());
            startActivity(editIntent);
        });

        // Keep the top header below the status bar without altering the original UI.
        View root = findViewById(R.id.profileRoot);
        final int topPadding = root.getPaddingTop();
        final int bottomPadding = root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, topPadding + bars.top, bars.right,
                    bottomPadding + bars.bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = ProfileStore.getName(this, getIntent());
        nameView.setText(name);
        avatarView.setText(name.isEmpty()
                ? "L" : name.substring(0, 1).toUpperCase(java.util.Locale.ROOT));
        goalView.setText(ProfileStore.getGoal(this, getIntent()));
        minutesView.setText(getString(R.string.profile_minutes_format,
                ProfileStore.getMinutes(this, getIntent())));
    }
}
