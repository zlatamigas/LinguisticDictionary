package com.zlatamigas.linguisticdictionary.model.entity;


import java.io.Serializable;
import java.util.Objects;

public class TagFreq implements Comparable, Serializable {

    private String tag;
    private int frequency;

    public TagFreq(String tag, int frequency){
        this.tag = tag;
        this.frequency = frequency;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagFreq tagFreq = (TagFreq) o;
        return tag.equalsIgnoreCase(tagFreq.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }

    @Override
    public String toString() {
        return '{' + tag + '}' +
                " - " + frequency +
                "; ";
    }

    @Override
    public int compareTo(Object o) {
        return tag.compareTo(((TagFreq)o).tag);
    }
}
