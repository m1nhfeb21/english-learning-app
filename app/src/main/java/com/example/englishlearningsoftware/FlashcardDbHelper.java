package com.example.englishlearningsoftware;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý database SQLite lưu từ vựng cục bộ trong máy
 */
public class FlashcardDbHelper extends SQLiteOpenHelper {

    // Tên file database và phiên bản
    private static final String DATABASE_NAME = "flashcards.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột
    public static final String TABLE_FLASHCARDS = "flashcards";
    public static final String KEY_ID = "_id";
    public static final String KEY_WORD = "word";
    public static final String KEY_TYPE = "type";
    public static final String KEY_MEANING = "meaning";
    public static final String KEY_EXAMPLE = "example";

    public FlashcardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Tạo bảng flashcards khi mở database lần đầu
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_FLASHCARDS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_WORD + " TEXT NOT NULL, "
                + KEY_TYPE + " TEXT, "
                + KEY_MEANING + " TEXT NOT NULL, "
                + KEY_EXAMPLE + " TEXT"
                + ");";

        db.execSQL(CREATE_TABLE);

        // Chèn sẵn dữ liệu ban đầu vào database
        chenTuBanDau(db, "Vocabulary", "Noun", "Từ vựng", "Expanding your vocabulary is essential for learning English.");
        chenTuBanDau(db, "Resilience", "Noun", "Sự kiên cường, khả năng phục hồi", "She showed great resilience in overcoming challenges.");
        chenTuBanDau(db, "Diligent", "Adjective", "Chăm chỉ, siêng năng", "Diligent students usually achieve good results.");
        chenTuBanDau(db, "Fluency", "Noun", "Sự trôi chảy, lưu loát", "He speaks English with native fluency.");
        chenTuBanDau(db, "Ponder", "Verb", "Suy ngẫm, trầm ngâm", "Take time to ponder before answering difficult questions.");
    }

    // Chèn 1 dòng dữ liệu vào database
    private void chenTuBanDau(SQLiteDatabase db, String word, String type, String meaning, String example) {
        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word);
        values.put(KEY_TYPE, type);
        values.put(KEY_MEANING, meaning);
        values.put(KEY_EXAMPLE, example);
        db.insert(TABLE_FLASHCARDS, null, values);
    }

    // Xóa bảng cũ và tạo lại khi nâng version database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARDS);
        onCreate(db);
    }

    // Thêm từ vựng mới vào database
    public long addFlashcard(Flashcard card) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WORD, card.getWord());
        values.put(KEY_TYPE, card.getType());
        values.put(KEY_MEANING, card.getMeaning());
        values.put(KEY_EXAMPLE, card.getExample());

        long id = db.insert(TABLE_FLASHCARDS, null, values);
        db.close();
        return id;
    }

    // Lấy toàn bộ danh sách từ vựng trong database
    public List<Flashcard> getAllFlashcards() {
        List<Flashcard> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_FLASHCARDS + " ORDER BY " + KEY_ID + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt từng dòng bằng cursor để lấy dữ liệu
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int wordIndex = cursor.getColumnIndex(KEY_WORD);
                int typeIndex = cursor.getColumnIndex(KEY_TYPE);
                int meaningIndex = cursor.getColumnIndex(KEY_MEANING);
                int exampleIndex = cursor.getColumnIndex(KEY_EXAMPLE);

                int id = cursor.getInt(idIndex);
                String word = cursor.getString(wordIndex);
                String type = cursor.getString(typeIndex);
                String meaning = cursor.getString(meaningIndex);
                String example = cursor.getString(exampleIndex);

                Flashcard card = new Flashcard(id, word, type, meaning, example);
                list.add(card);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return list;
    }

    // Xóa từ vựng khỏi database theo id
    public boolean deleteFlashcard(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_FLASHCARDS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }
}
