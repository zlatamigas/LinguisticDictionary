package com.zlatamigas.linguisticdictionary.service;

import com.zlatamigas.linguisticdictionary.model.entity.WordTableInfo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Finding {

    public static ArrayList<WordTableInfo> find(ArrayList<WordTableInfo> dictionary, String wordPart) {
        ArrayList<WordTableInfo> records = new ArrayList<>();

        Pattern pattern = Pattern.compile("^"+wordPart);
        Matcher matcher;

        for (WordTableInfo r : dictionary) {
            matcher = pattern.matcher(r.getWord());
            if(matcher.find())
                records.add(r);
        }

        return records;
    }
}
