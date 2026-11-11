package com.example.englishlearningsoftware;

public class Flashcard {

    private int id;

    private String word;

    private String type;

    private String meaning;

    private String example;

    public Flashcard() {
    }

    public Flashcard(String word, String type, String meaning, String example) {
        this.word = word;
        this.type = type;
        this.meaning = meaning;
        this.example = example;
    }

    public Flashcard(int id, String word, String type, String meaning, String example) {
        this.id = id;
        this.word = word;
        this.type = type;
        this.meaning = meaning;
        this.example = example;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
