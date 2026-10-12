package com.example.englishlearningsoftware;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AuthUiTest {
    @Test
    public void signUpTabSurvivesActivityRecreation() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.btnToggleSignUp)).perform(scrollTo(), click());
            onView(withId(R.id.etSignUpFirstName)).check(matches(isDisplayed()));
            scenario.recreate();
            onView(withId(R.id.etSignUpFirstName)).check(matches(isDisplayed()));
            onView(withId(R.id.btnToggleSignIn)).perform(scrollTo(), click());
            onView(withId(R.id.etLoginEmail)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void previewOpensHomeAndReturnsToLoginWithoutBackend() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.btnPreview)).perform(scrollTo(), click());
            onView(withId(R.id.tvHomeStatus)).check(matches(withText(R.string.preview_status)));
            onView(withId(R.id.btnLogout)).perform(scrollTo(), click());
            onView(withId(R.id.etLoginEmail)).check(matches(isDisplayed()));
        }
    }
}
