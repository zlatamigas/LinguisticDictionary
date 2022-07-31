package com.zlatamigas.linguisticdictionary.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class TextTagsStatisticsTableInfo implements Serializable {

    private String value1;
    private String value2;
    private int freq;

    public TextTagsStatisticsTableInfo() {
        this.value1 = "";
        this.value2 = "";
        this.freq = 0;
    }

    public TextTagsStatisticsTableInfo(String value1, String value2, int freq) {
        this.value1 = value1;
        this.value2 = value2;
        this.freq = freq;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue2() {
        return value2;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public boolean changeFreq(int freq) {
        if (this.freq + freq < 0)
            return false;
        this.freq += freq;
        return true;
    }

    public int getFreq() {
        return freq;
    }

    public void incFreq() {
        freq++;
    }

    public boolean decFreq() {
        if (freq <= 0)
            return false;

        freq--;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextTagsStatisticsTableInfo that = (TextTagsStatisticsTableInfo) o;
        return value1.equalsIgnoreCase(that.value1) && value2.equalsIgnoreCase(that.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value1);
    }
}
