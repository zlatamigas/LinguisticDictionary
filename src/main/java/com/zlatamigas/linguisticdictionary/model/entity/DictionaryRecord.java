package com.zlatamigas.linguisticdictionary.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class DictionaryRecord implements Comparable, Serializable {

    private String word;
    private ArrayList<TagFreq> tags;

    private ArrayList<DictionaryRecord> relativeWords;

    public DictionaryRecord(
            String word) {
        this.word = word;
        this.tags = new ArrayList<>();
        this.relativeWords = new ArrayList<>();
    }

    public DictionaryRecord(
            String word,
            ArrayList<TagFreq> tags,
            ArrayList relativeWords) {
        this.word = word;
        this.tags = tags;
        this.relativeWords = relativeWords;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void addTag(String tag, int freq) {

        if (tags.contains(new TagFreq(tag.toUpperCase(), freq))) {
            int i = tags.indexOf(new TagFreq(tag.toUpperCase(), freq));
            tags.get(i).setFrequency(tags.get(i).getFrequency() + freq);
        } else
            tags.add(new TagFreq(tag.toUpperCase(), freq));

        Collections.sort(tags);
    }

    public ArrayList<TagFreq> getTags() {
        return tags;
    }

    public boolean removeTag(String tag) {
        return tags.remove(new TagFreq(tag, 0));
    }

    public void addRelativeWord(DictionaryRecord dr) {
        if (relativeWords.contains(dr)) {
            int i = relativeWords.indexOf(dr);
            DictionaryRecord f = relativeWords.get(i);
            for (TagFreq tf : dr.tags)
                f.addTag(tf.getTag(), tf.getFrequency());

        } else
            relativeWords.add(dr);

        Collections.sort(relativeWords);
    }

    public boolean removeRelativeWord(DictionaryRecord dr) {
        return relativeWords.remove(dr);
    }

    public ArrayList<DictionaryRecord> getRelativeWords() {
        return relativeWords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryRecord dictionaryRecord = (DictionaryRecord) o;
        return word.equalsIgnoreCase(dictionaryRecord.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word.toLowerCase(Locale.ROOT));
    }

    @Override
    public String toString() {

        StringBuffer s = new StringBuffer("Base form - " + word + ": ");
        for (TagFreq tf : tags)
            s.append(tf);

        s.append("\nRelatives:\n");
        for (DictionaryRecord dr : relativeWords) {
            s.append(dr.word + ": ");
            for (TagFreq tf : dr.tags)
                s.append(tf);
            s.append('\n');
        }
        s.append('\n');

        return s.toString();
    }

    @Override
    public int compareTo(Object o) {
        return word.compareTo(((DictionaryRecord) o).word);
    }
}
