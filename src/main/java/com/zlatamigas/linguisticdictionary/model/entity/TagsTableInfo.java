package com.zlatamigas.linguisticdictionary.model.entity;

public class TagsTableInfo {

    private String tag;
    private int frequency;

    public TagsTableInfo(String tag, int frequency) {
        this.frequency = frequency;
        this.tag = tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

}