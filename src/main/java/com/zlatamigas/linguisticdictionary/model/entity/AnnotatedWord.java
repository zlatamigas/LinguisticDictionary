package com.zlatamigas.linguisticdictionary.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class AnnotatedWord implements Serializable {

    private String word;
    private String tag;

    public AnnotatedWord(String word, String tag) {
        this.word = word;
        this.tag = tag;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotatedWord that = (AnnotatedWord) o;
        return Objects.equals(word, that.word) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, tag);
    }

    @Override
    public String toString() {
        return word + " ('" + tag + ')';
    }
}
