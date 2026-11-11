package com.example.englishlearningsoftware;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExerciseFilterTest {
    private static final String[][] EXERCISES = {
            {"Đọc", "Trắc nghiệm từ vựng"},
            {"Đọc", "Điền từ vào câu"},
            {"Nghe", "Nghe và chọn từ"},
            {"Viết", "Viết từ tiếng Anh"}
    };

    @Test
    public void skillSelectionShowsOnlyMappedExercises() {
        for (String[] exercise : EXERCISES) {
            assertTrue(ExerciseFilter.matches(exercise[0], "", exercise[1], ""));
            for (String selectedSkill : new String[]{"Nghe", "Nói", "Đọc", "Viết"}) {
                boolean expected = exercise[0].equals(selectedSkill);
                assertEquals(expected,
                        ExerciseFilter.matches(exercise[0], selectedSkill, exercise[1], ""));
            }
        }
    }

    @Test
    public void queryAndSkillMustBothMatch() {
        assertTrue(ExerciseFilter.matches("Đọc", "Đọc", "Điền từ vào câu", "dien tu"));
        assertFalse(ExerciseFilter.matches("Đọc", "Nghe", "Điền từ vào câu", "dien tu"));
        assertFalse(ExerciseFilter.matches("Đọc", "Đọc", "Trắc nghiệm từ vựng", "dien tu"));
    }

    @Test
    public void searchIgnoresVietnameseAccentsCaseAndDecomposition() {
        assertTrue(ExerciseFilter.matches("Đọc", "", "Điền từ vào câu", "DIEN TU"));
        assertTrue(ExerciseFilter.matches("Đọc", "", "Trắc nghiệm từ vựng", "doc"));
        assertTrue(ExerciseFilter.matches("Viết", "", "Viết từ tiếng Anh", "vie\u0302\u0301t"));
        assertTrue(ExerciseFilter.matches("Đọc", "", "Điền từ vào câu", "ĐIỀN"));
    }

    @Test
    public void queryIsTrimmedAndEmptyQueryKeepsSkillFilter() {
        assertTrue(ExerciseFilter.matches("Nghe", "", "Nghe và chọn từ", "  chon tu  "));
        assertTrue(ExerciseFilter.matches("Nghe", "Nghe", "Nghe và chọn từ", "   "));
        assertFalse(ExerciseFilter.matches("Nghe", "Viết", "Nghe và chọn từ", "   "));
        assertFalse(ExerciseFilter.matches("Nghe", "", "Nghe và chọn từ", "không tồn tại"));
    }
}
