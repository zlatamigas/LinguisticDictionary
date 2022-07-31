package com.zlatamigas.linguisticdictionary.model.entity;

import java.util.Objects;

public class WordTableInfo {

    private String word;
    private int fullFrequency;

    public WordTableInfo(String word, int fullFrequency) {
        this.word = word;
        this.fullFrequency = fullFrequency;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setFullFrequency(int fullFrequency) {
        this.fullFrequency = fullFrequency;
    }

    public int getFullFrequency() {
        return fullFrequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordTableInfo that = (WordTableInfo) o;
        return Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }
}
