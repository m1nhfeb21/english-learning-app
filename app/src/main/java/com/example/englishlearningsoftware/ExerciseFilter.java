package com.example.englishlearningsoftware;

import java.text.Normalizer;
import java.util.Locale;

final class ExerciseFilter {
    private ExerciseFilter() {}

    static boolean matches(String skill, String selectedSkill, String title, String query) {
        return (selectedSkill.isEmpty() || selectedSkill.equals(skill))
                && normalize(title + " " + skill).contains(normalize(query));
    }

    private static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).replace('đ', 'd').trim();
    }
}
