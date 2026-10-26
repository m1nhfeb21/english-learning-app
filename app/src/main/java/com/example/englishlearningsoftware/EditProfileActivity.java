package com.example.englishlearningsoftware;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editName;
    private EditText editGoal;
    private EditText editMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.etProfileName);
        editGoal = findViewById(R.id.etProfileGoal);
        editMinutes = findViewById(R.id.etProfileMinutes);

        editName.setText(ProfileStore.getName(this, getIntent()));
        editGoal.setText(ProfileStore.getGoal(this, getIntent()));
        editMinutes.setText(String.valueOf(ProfileStore.getMinutes(this, getIntent())));

        findViewById(R.id.btnCancelEditProfile).setOnClickListener(view -> finish());
        findViewById(R.id.btnSaveProfile).setOnClickListener(view -> saveProfile());
    }

    private void saveProfile() {
        String name = editName.getText().toString().trim();
        String goal = editGoal.getText().toString().trim();
        String rawMinutes = editMinutes.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError(getString(R.string.profile_name_required));
            editName.requestFocus();
            return;
        }
        if (goal.isEmpty()) {
            editGoal.setError(getString(R.string.profile_goal_required));
            editGoal.requestFocus();
            return;
        }

        int minutes;
        try {
            minutes = Integer.parseInt(rawMinutes);
        } catch (NumberFormatException exception) {
            editMinutes.setError(getString(R.string.profile_minutes_error));
            editMinutes.requestFocus();
            return;
        }
        if (minutes < 1 || minutes > 480) {
            editMinutes.setError(getString(R.string.profile_minutes_error));
            editMinutes.requestFocus();
            return;
        }

        ProfileStore.save(this, getIntent(), name, goal, minutes);
        Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
