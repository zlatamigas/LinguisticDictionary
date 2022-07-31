package com.zlatamigas.linguisticdictionary.model.entity;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class Dictionary {

    private static final String WORD_PATTERN = "^.*[^a-zA-Z].*$";

    private static StanfordCoreNLP basePipeline;
    private static Properties pipelineProperties;

    static {
        pipelineProperties = new Properties();
        pipelineProperties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
    }

    private ArrayList<String> annotatedTextsToUse = new ArrayList<>();
    private ArrayList<DictionaryRecord> records = new ArrayList<>();

    public Dictionary(ArrayList<String> annotatedTextsToUse, ArrayList<DictionaryRecord> records) {
        this.annotatedTextsToUse = annotatedTextsToUse;
        this.records = records;

        if (basePipeline == null) {
            basePipeline = new StanfordCoreNLP(pipelineProperties);
        }
    }

    public void setRecords(ArrayList<DictionaryRecord> records) {
        this.records = records;
    }

    public void setAnnotatedTextsToUse(ArrayList<String> annotatedTextsToUse) {
        this.annotatedTextsToUse = annotatedTextsToUse;
    }

    public ArrayList<DictionaryRecord> getRecords() {
        return records;
    }

    public ArrayList<String> getAnnotatedTextsToUse() {
        return annotatedTextsToUse;
    }

    @Override
    public String toString() {

        StringBuffer s = new StringBuffer("Dict:\n");
        s.append("\nAnnotated texts:\n");
        for (String t : annotatedTextsToUse)
            s.append(t + "\n");

        s.append("\nWords:\n");
        for (DictionaryRecord r : records) {
            s.append(r);
        }

        return s.toString();
    }

    public boolean containsWord(String word) {

        if (word.equals(""))
            return false;

        String parentWord = basePipeline.processToCoreDocument(word).tokens().get(0).lemma();

        if (records.contains(new DictionaryRecord(parentWord))) {

            int i = records.indexOf(new DictionaryRecord(parentWord));
            DictionaryRecord dr = records.get(i);

            if (word.equalsIgnoreCase(parentWord)) {
                return true;
            } else {

                return dr.getRelativeWords().contains(new DictionaryRecord(
                        word,
                        new ArrayList<>(),
                        new ArrayList<>()
                ));
            }
        } else
            return false;
    }

    public boolean addWord(String word, String wordTag, int freq) {

        if (word.matches(WORD_PATTERN))
            return false;

        if (freq < 0)
            return false;

        if (word.equals(""))
            return false;

        if (wordTag.equals(""))
            wordTag = basePipeline.processToCoreDocument(word).tokens().get(0).tag();

        String parentword = basePipeline.processToCoreDocument(word).tokens().get(0).lemma();

        if (records.contains(new DictionaryRecord(parentword))) {

            int i = records.indexOf(new DictionaryRecord(parentword));
            DictionaryRecord dr = records.get(i);

            if (word.equalsIgnoreCase(parentword)) {
                dr.addTag(wordTag, freq);
            } else {

                dr.addRelativeWord(new DictionaryRecord(
                        word,
                        new ArrayList<>(Arrays.asList(new TagFreq(wordTag, freq))),
                        new ArrayList<>()
                ));
            }
        } else {

            if (word.equalsIgnoreCase(parentword)) {
                return records.add(new DictionaryRecord(
                        parentword,
                        new ArrayList<>(Arrays.asList(new TagFreq(wordTag, freq))),
                        new ArrayList<>()));
            } else {
                return records.add(new DictionaryRecord(
                        parentword,
                        new ArrayList<>(Arrays.asList(new TagFreq(basePipeline.processToCoreDocument(parentword).tokens().get(0).tag(), 0))),
                        new ArrayList<>(
                                Arrays.asList(
                                        new DictionaryRecord(
                                                word,
                                                new ArrayList<>(Arrays.asList(new TagFreq(wordTag, freq))),
                                                new ArrayList<>())
                                )
                        )));
            }
        }

        return true;
    }

    public boolean removeWord(String word) {
        String parentWord = basePipeline.processToCoreDocument(word).tokens().get(0).lemma();

        if (records.contains(new DictionaryRecord(parentWord))) {

            int i = records.indexOf(new DictionaryRecord(parentWord));
            DictionaryRecord dr = records.get(i);

            if (word.equalsIgnoreCase(parentWord)) {

                if (dr.getRelativeWords().size() > 0)
                    return false;
                records.remove(i);
            } else {

                dr.removeRelativeWord(new DictionaryRecord(word));
            }
        } else
            return false;

        return true;
    }

    public boolean removeTagFromWord(String word, String wordTag) {

        if (word.equals(""))
            return false;

        String parentWord = basePipeline.processToCoreDocument(word).tokens().get(0).lemma();

        if (records.contains(new DictionaryRecord(parentWord))) {

            int i = records.indexOf(new DictionaryRecord(parentWord));
            DictionaryRecord dr = records.get(i);

            if (word.equalsIgnoreCase(parentWord)) {

                return dr.removeTag(wordTag);
            } else {

                int id = dr.getRelativeWords().indexOf(new DictionaryRecord(
                        word,
                        new ArrayList<>(),
                        new ArrayList<>()
                ));
                if (id != -1)
                    return dr.getRelativeWords().get(id).removeTag(wordTag);
            }
        } else
            return false;

        return true;
    }

    public static String getLemma(String word) {
        return basePipeline.processToCoreDocument(word).tokens().get(0).lemma();
    }

    public static String getTag(String word) {
        return basePipeline.processToCoreDocument(word).tokens().get(0).tag();
    }
}
