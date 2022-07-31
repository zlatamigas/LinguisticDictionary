package com.zlatamigas.linguisticdictionary.util;

import com.zlatamigas.linguisticdictionary.model.entity.*;

import java.util.ArrayList;

public class TableInfoManager {

    public static ArrayList<EditDictShowInfo> getTableInfo(Dictionary dict){

        ArrayList<EditDictShowInfo> info = new ArrayList<>();
        int fullFreq;

        for(DictionaryRecord dr : dict.getRecords()){

            ArrayList<TagsTableInfo> tags = new ArrayList<>();
            ArrayList<String> lemmaTags = new ArrayList<>();

            String word = dr.getWord();
            String lemma = dr.getWord();

            fullFreq = 0;
            for(TagFreq tf : dr.getTags()){
                tags.add(new TagsTableInfo(tf.getTag(), tf.getFrequency()));
                lemmaTags.add(tf.getTag());
                fullFreq+=tf.getFrequency();
            }

            info.add(new EditDictShowInfo(new WordTableInfo(word, fullFreq), tags, lemma, lemmaTags));

            for (DictionaryRecord words : dr.getRelativeWords()){

                fullFreq = 0;
                tags = new ArrayList<>();
                for(TagFreq tf : words.getTags()){
                    tags.add(new TagsTableInfo(tf.getTag(), tf.getFrequency()));
                    fullFreq+=tf.getFrequency();
                }

                info.add(new EditDictShowInfo(new WordTableInfo(words.getWord(), fullFreq), tags, lemma, lemmaTags));
            }
        }

        return info;
    }
}
