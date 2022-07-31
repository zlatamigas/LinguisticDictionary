package com.zlatamigas.linguisticdictionary.model.entity;

import java.util.ArrayList;
import java.util.Objects;

public class EditDictShowInfo {

    private WordTableInfo word;
    private ArrayList<TagsTableInfo> wordTags;
    private String lemma;
    private ArrayList<String> lemmaTags;

    public EditDictShowInfo(
            WordTableInfo word) {
        this.word = word;
    }

    public EditDictShowInfo(
            WordTableInfo word,
            ArrayList<TagsTableInfo> wordTags,
            String lemma,
            ArrayList<String> lemmaTags) {
        this.word = word;
        this.wordTags = wordTags;
        this.lemma = lemma;
        this.lemmaTags = lemmaTags;
    }

    public WordTableInfo getWord() {
        return word;
    }

    public ArrayList<TagsTableInfo> getWordTags() {
        return wordTags;
    }

    public String getLemma() {
        return lemma;
    }

    public ArrayList<String> getLemmaTags() {
        return lemmaTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EditDictShowInfo that = (EditDictShowInfo) o;
        return Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }
}
