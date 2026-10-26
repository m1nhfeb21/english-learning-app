package com.example.englishlearningsoftware;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Locale;

/**
 * Lightweight local storage for the UI demo.
 * Each account receives its own preferences when a backend accountId is available.
 */
public final class ProfileStore {
    private static final String NAME_KEY = "display_name";
    private static final String GOAL_KEY = "learning_goal";
    private static final String MINUTES_KEY = "daily_minutes";

    private ProfileStore() {}

    public static SharedPreferences forIntent(Context context, Intent intent) {
        int accountId = intent.getIntExtra(HomeActivity.EXTRA_ACCOUNT_ID, 0);
        String scope;
        if (intent.getBooleanExtra(HomeActivity.EXTRA_PREVIEW, false)) {
            scope = "preview";
        } else if (accountId > 0) {
            scope = "account_" + accountId;
        } else {
            String name = originalName(intent).toLowerCase(Locale.ROOT);
            scope = "name_" + Integer.toHexString(name.hashCode());
        }
        return context.getSharedPreferences("profile_ui_" + scope, Context.MODE_PRIVATE);
    }

    public static String originalName(Intent intent) {
        String first = intent.getStringExtra(HomeActivity.EXTRA_FIRST_NAME);
        String last = intent.getStringExtra(HomeActivity.EXTRA_LAST_NAME);
        return ((first == null ? "" : first) + " " + (last == null ? "" : last)).trim();
    }

    public static String getName(Context context, Intent intent) {
        String original = originalName(intent);
        return forIntent(context, intent).getString(NAME_KEY,
                original.isEmpty() ? context.getString(R.string.profile_guest) : original);
    }

    public static String getGoal(Context context, Intent intent) {
        return forIntent(context, intent).getString(GOAL_KEY, context.getString(R.string.profile_goal));
    }

    public static int getMinutes(Context context, Intent intent) {
        return forIntent(context, intent).getInt(MINUTES_KEY, 20);
    }

    public static void save(Context context, Intent intent, String name,
                            String goal, int minutes) {
        forIntent(context, intent).edit()
                .putString(NAME_KEY, name)
                .putString(GOAL_KEY, goal)
                .putInt(MINUTES_KEY, minutes)
                .apply();
    }
}
