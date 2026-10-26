package com.example.englishlearningsoftware;

/**
 * Class lưu thông tin 1 thẻ từ vựng
 */
public class Flashcard {
    // Mã id trong database
    private int id;
    // Từ tiếng Anh
    private String word;
    // Loại từ
    private String type;
    // Nghĩa tiếng Việt
    private String meaning;
    // Câu ví dụ
    private String example;

    // Khởi tạo rỗng
    public Flashcard() {
    }

    // Khởi tạo khi tạo từ mới chưa có id
    public Flashcard(String word, String type, String meaning, String example) {
        this.word = word;
        this.type = type;
        this.meaning = meaning;
        this.example = example;
    }

    // Khởi tạo khi lấy từ database lên đã có sẵn id
    public Flashcard(int id, String word, String type, String meaning, String example) {
        this.id = id;
        this.word = word;
        this.type = type;
        this.meaning = meaning;
        this.example = example;
    }

    // Lấy id
    public int getId() {
        return id;
    }

    // Gán id
    public void setId(int id) {
        this.id = id;
    }

    // Lấy từ tiếng Anh
    public String getWord() {
        return word;
    }

    // Gán từ tiếng Anh
    public void setWord(String word) {
        this.word = word;
    }

    // Lấy loại từ
    public String getType() {
        return type;
    }

    // Gán loại từ
    public void setType(String type) {
        this.type = type;
    }

    // Lấy nghĩa
    public String getMeaning() {
        return meaning;
    }

    // Gán nghĩa
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    // Lấy câu ví dụ
    public String getExample() {
        return example;
    }

    // Gán câu ví dụ
    public void setExample(String example) {
        this.example = example;
    }
}
