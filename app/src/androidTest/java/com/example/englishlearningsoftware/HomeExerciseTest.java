package com.example.englishlearningsoftware;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class HomeExerciseTest {
    @Test
    public void filtersSearchAndRecreationKeepCorrectCards() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HomeActivity.class)
                .putExtra(HomeActivity.EXTRA_PREVIEW, true);
        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(home -> {
                assertCards(home, true, true, true, true);
                home.findViewById(R.id.filterListening).performClick();
                assertCards(home, false, true, false, false);
                home.findViewById(R.id.filterSpeaking).performClick();
                assertCards(home, false, false, false, false);
                assertEquals(View.VISIBLE, home.findViewById(R.id.tvExerciseEmpty).getVisibility());
                home.findViewById(R.id.filterWriting).performClick();
                assertCards(home, false, false, false, true);
                home.findViewById(R.id.filterReading).performClick();
                assertCards(home, true, false, true, false);
                ((EditText) home.findViewById(R.id.etExerciseSearch)).setText("DIEN TU");
                assertCards(home, false, false, true, false);
            });
            scenario.recreate();
            scenario.onActivity(home -> {
                assertTrue(home.findViewById(R.id.filterReading).isSelected());
                assertCards(home, false, false, true, false);
                home.findViewById(R.id.filterListening).performClick();
                assertCards(home, false, false, false, false);
                ((EditText) home.findViewById(R.id.etExerciseSearch)).setText("");
                assertCards(home, false, true, false, false);
                home.findViewById(R.id.filterAll).performClick();
                assertCards(home, true, true, true, true);
            });
        }
    }

    private static void assertCards(HomeActivity home, boolean... visible) {
        int[] ids = {R.id.btnOpenQuiz, R.id.btnOpenListening, R.id.btnOpenFillBlank, R.id.btnOpenWriting};
        for (int i = 0; i < ids.length; i++) {
            assertEquals(visible[i] ? View.VISIBLE : View.GONE, home.findViewById(ids[i]).getVisibility());
        }
    }
}
