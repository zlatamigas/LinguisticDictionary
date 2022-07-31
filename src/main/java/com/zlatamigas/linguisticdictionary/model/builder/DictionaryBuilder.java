package com.zlatamigas.linguisticdictionary.model.builder;

import com.zlatamigas.linguisticdictionary.model.entity.Dictionary;
import com.zlatamigas.linguisticdictionary.model.entity.*;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.*;
import java.util.*;

public class DictionaryBuilder {

    private static StanfordCoreNLP basepipeline;

    private static AnnotatorBuilder annotatorBuilder = new AnnotatorBuilder();

    private static String RESOURCES_PATH = "customResources";
    private static String DICTIONARY_STORAGE = "customResources\\dictionaries";

    private static String DICTIONARY_INDEX_FILE = "dictionaries.ind";
    private static String DICTIONARY_FILE_EXTENSION = ".dict";
    private static String DICTIONARY_INDEX_FILE_EXTENSION = ".ind";

    public ArrayList<String> getRealTimeDictionaryIndex() throws IOException {

        File resourcesPath = new File(RESOURCES_PATH);

        if (!resourcesPath.exists())
            resourcesPath.mkdir();

        File directoryPath = new File(DICTIONARY_STORAGE);

        if (!directoryPath.exists())
            directoryPath.mkdir();


        File indexFile = new File(DICTIONARY_STORAGE, DICTIONARY_INDEX_FILE);

        if (indexFile.createNewFile()) {

            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(DICTIONARY_STORAGE + "\\" + DICTIONARY_INDEX_FILE, false));

            writer.write(Integer.toString(0) + "\n");
            writer.close();
        }

        ArrayList<String> dictsFromIndex = new ArrayList<>();
        ArrayList<String> dictsFromDir = new ArrayList<>();

        Scanner sc = new Scanner(indexFile);
        int n = sc.nextInt();
        sc.nextLine();
        for (int i = 0; i < n; ++i) {
            if (sc.hasNextLine())
                dictsFromIndex.add(sc.nextLine());
        }
        sc.close();

        String fn;
        File filesList[] = directoryPath.listFiles();
        for (File file : filesList) {

            fn = file.getName();

            if (!file.isDirectory())
                continue;

            String pathF = DICTIONARY_STORAGE + "\\" + fn + "\\" + fn;

            if (
                    !new File(pathF + DICTIONARY_FILE_EXTENSION).exists() ||
                            !new File(pathF + DICTIONARY_INDEX_FILE_EXTENSION).exists())
                continue;

            dictsFromDir.add(fn);

            if (!dictsFromIndex.contains(fn)) {
                dictsFromIndex.add(fn);
            }
        }

        String rfi;
        for (int i = 0; i < dictsFromIndex.size(); ++i) {
            rfi = dictsFromIndex.get(i);

            if (!dictsFromDir.contains(rfi))
                dictsFromIndex.remove(rfi);
        }

        return dictsFromIndex;
    }


    ////////////////////////////////////////////////////////////////////////
    // Index control.
    ////////////////////////////////////////////////////////////////////////

    private void refreshIndex() {

        try {
            ArrayList<String> dictsFromIndex = getRealTimeDictionaryIndex();

            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(DICTIONARY_STORAGE + "\\" + DICTIONARY_INDEX_FILE, false));

            writer.write(Integer.toString(dictsFromIndex.size()) + "\n");
            for (String recordFromIndex : dictsFromIndex)
                writer.write(recordFromIndex + "\n");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshIndex(ArrayList<String> dictsFromIndex) {

        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(DICTIONARY_STORAGE + "\\" + DICTIONARY_INDEX_FILE, false));

            writer.write(Integer.toString(dictsFromIndex.size()) + "\n");
            for (String recordFromIndex : dictsFromIndex)
                writer.write(recordFromIndex + "\n");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean contains(String dictName){
        DictionaryBuilder d = new DictionaryBuilder();
        try {
            if(d.getRealTimeDictionaryIndex().contains(dictName))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public DictionaryBuilder() {
        if(basepipeline==null) {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
            basepipeline = new StanfordCoreNLP(props);
        }
        refreshIndex();
    }

    ////////////////////////////////////////////////////////////////////////
    // Save dictionary to files.
    ////////////////////////////////////////////////////////////////////////

    private String createNewDictionaryStructure(String fileName) {

        ArrayList<String> dictionariesFromIndex = null;
        try {
            dictionariesFromIndex = getRealTimeDictionaryIndex();

            int num = 1;
            if (dictionariesFromIndex.contains(fileName)) {
                while (dictionariesFromIndex.contains(fileName + "_" + num)) {
                    num++;
                }
                fileName += "_" + num;
            }
            dictionariesFromIndex.add(fileName);

            refreshIndex(dictionariesFromIndex);

            String dictPath = DICTIONARY_STORAGE + "\\" + fileName;

            if(new File(dictPath).exists())
                new File(dictPath).delete();

            new File(dictPath).mkdir();
            new File(dictPath + "\\" + fileName + DICTIONARY_FILE_EXTENSION).createNewFile();
            new File(dictPath + "\\" + fileName + DICTIONARY_INDEX_FILE_EXTENSION).createNewFile();


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return fileName;
    }

    private boolean fillDictionaryIndFile(String fileName, Dictionary d) {

        ArrayList<String> annotatedFromIndex = null;
        try {
            annotatedFromIndex = getRealTimeDictionaryIndex();

            if (!annotatedFromIndex.contains(fileName))
                return false;


            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(DICTIONARY_STORAGE + "\\" + fileName + "\\" + fileName + DICTIONARY_INDEX_FILE_EXTENSION, false));

            writer.write(Integer.toString(d.getAnnotatedTextsToUse().size()) + "\n");
            for (String annotatedTextsToUse : d.getAnnotatedTextsToUse())
                writer.write(annotatedTextsToUse + "\n");

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean fillDictionaryDictFile(String fileName, Dictionary d) {

        ArrayList<String> annotatedFromIndex = null;
        try {
            annotatedFromIndex = getRealTimeDictionaryIndex();

            if (!annotatedFromIndex.contains(fileName))
                return false;


            FileOutputStream fos = new FileOutputStream(
                    DICTIONARY_STORAGE + "\\" + fileName + "\\" + fileName + DICTIONARY_FILE_EXTENSION, false
            );
            ObjectOutputStream oos = new ObjectOutputStream(fos);


            oos.writeInt(d.getRecords().size());
            for (DictionaryRecord dr : d.getRecords()) {
                oos.writeObject(dr);
            }

            oos.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean saveNewDictionary(String expectedName, Dictionary d) {

        String fileName = createNewDictionaryStructure(expectedName);

        if (fileName == null)
            return false;

        return fillDictionaryIndFile(fileName, d) &&
                fillDictionaryDictFile(fileName, d);
    }

    public boolean saveChangedDictionary(String fileName, Dictionary d) {

        try {

            if (!getRealTimeDictionaryIndex().contains(fileName))
                return false;

            return fillDictionaryIndFile(fileName, d) &&
                    fillDictionaryDictFile(fileName, d);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////
    // Read dictionary from files.
    ////////////////////////////////////////////////////////////////////////

    public ArrayList<String> getAnnotatedTextsFromIndFile(String dictName){

        ArrayList<String> annotatedFromIndex = new ArrayList<>();
        try {

            BufferedReader reader = new BufferedReader(
                    new FileReader(DICTIONARY_STORAGE + "\\" + dictName + "\\" + dictName + DICTIONARY_INDEX_FILE_EXTENSION));

            int num = Integer.parseInt(reader.readLine());
            for (int i = 0; i < num; i++)
                annotatedFromIndex.add(reader.readLine());

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return annotatedFromIndex;
    }

    private boolean readDictionaryIndFile(String fileName, Dictionary d) {

        ArrayList<String> annotatedFromIndex = d.getAnnotatedTextsToUse();
        try {
            if (!getRealTimeDictionaryIndex().contains(fileName))
                return false;


            BufferedReader reader = new BufferedReader(
                    new FileReader(DICTIONARY_STORAGE + "\\" + fileName + "\\" + fileName + DICTIONARY_INDEX_FILE_EXTENSION));

            int num = Integer.parseInt(reader.readLine());
            for (int i = 0; i < num; i++)
                annotatedFromIndex.add(reader.readLine());

            reader.close();

            d.setAnnotatedTextsToUse(annotatedFromIndex);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean readDictionaryDictFile(String fileName, Dictionary d) {

        ArrayList<DictionaryRecord> dictFilling = d.getRecords();
        try {
            if (!getRealTimeDictionaryIndex().contains(fileName))
                return false;

            FileInputStream fos = new FileInputStream(
                    DICTIONARY_STORAGE + "\\" + fileName + "\\" + fileName + DICTIONARY_FILE_EXTENSION
            );
            ObjectInputStream oos = new ObjectInputStream(fos);


            int num = oos.readInt();
            for (int i = 0; i < num; i++) {
                dictFilling.add((DictionaryRecord) oos.readObject());
            }

            oos.close();
            fos.close();

            d.setRecords(dictFilling);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Dictionary readDictionary(String fileName) {

        if (fileName == null)
            return null;

        try {
            if(!getRealTimeDictionaryIndex().contains(fileName))
                return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Dictionary d = new Dictionary(new ArrayList<>(), new ArrayList<>());

        readDictionaryIndFile(fileName, d);
        readDictionaryDictFile(fileName, d);

        return d;
    }

    ////////////////////////////////////////////////////////////////////////
    // Edit object dictionary from using files data.
    ////////////////////////////////////////////////////////////////////////

    public int addAnnotatedTextToDictionary(Dictionary d, ArrayList<String> annotatedFiles) {

        int numAdded = 0;
        ArrayList<String> annotatedTextsToUse = d.getAnnotatedTextsToUse();

        Map<AnnotatedWord, Integer> map;
        for (String text : annotatedFiles) {

            if(!annotatedTextsToUse.contains(text)) {
                map = annotatorBuilder.readAnnotatedFileStatistics(text);

                if (map == null)
                    continue;

                annotatedTextsToUse.add(text);
                for (Map.Entry<AnnotatedWord, Integer> entry : map.entrySet()) {

                    d.addWord(
                            entry.getKey().getWord().toLowerCase(Locale.ROOT),
                            entry.getKey().getTag(),
                            entry.getValue()
                    );
                }
                numAdded++;
            }
        }
        return numAdded;
    }

    public boolean addDictionaryToDictionary(Dictionary dictionaryMain, Dictionary dictionaryPartToAdd) {

        ArrayList<String> annotatedTextsToUse = dictionaryMain.getAnnotatedTextsToUse();
        ArrayList<String> annotatedTextsToAdd = new ArrayList<>();

        for(String annText: dictionaryPartToAdd.getAnnotatedTextsToUse()){
            if(annotatedTextsToUse.contains(annText))
                return false;
            annotatedTextsToAdd.add(annText);
        }

        annotatedTextsToUse.addAll(annotatedTextsToAdd);
        Collections.sort(annotatedTextsToUse);

        String word;

        for(DictionaryRecord lemmaDr: dictionaryPartToAdd.getRecords()){
            word = lemmaDr.getWord();
            for(TagFreq tf : lemmaDr.getTags()){
                dictionaryMain.addWord(word, tf.getTag(), tf.getFrequency());
            }

            for(DictionaryRecord dr: lemmaDr.getRelativeWords()){
                word = dr.getWord();
                for(TagFreq tf : dr.getTags()){
                    dictionaryMain.addWord(word, tf.getTag(), tf.getFrequency());
                }
            }
        }

        return true;
    }

    public boolean addDictionaryToDictionary(Dictionary dictionaryMain, String dictionaryFilePartToAdd) {

        Dictionary dictionaryPartToAdd = readDictionary(dictionaryFilePartToAdd);
        if(dictionaryPartToAdd == null)
            return false;

        return addDictionaryToDictionary(dictionaryMain, dictionaryPartToAdd);
    }

    public void removeAnnotatedTextFromDictionary(String fileName, ArrayList<String> annotatedFiles) {


        try {
            if (!getRealTimeDictionaryIndex().contains(fileName))
                return;

            Dictionary d = new Dictionary(new ArrayList<>(), new ArrayList<>());
            readDictionaryIndFile(fileName, d);
            ArrayList<String> texts = d.getAnnotatedTextsToUse();

            for(String text : annotatedFiles)
                texts.remove(text);

            fillDictionaryIndFile(fileName, d);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    ////////////////////////////////////////////////////////////////////////
    // Delete dictionary.
    ////////////////////////////////////////////////////////////////////////
    private void deleteRecursive(File file) throws IOException {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteRecursive(entry);
                }
            }
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }

    public boolean deleteDictionary(String fileName) {

        if (fileName == null)
            return true;

        try {

            deleteRecursive(new File(DICTIONARY_STORAGE + "\\" + fileName));

            ArrayList<String> dictionaries = getRealTimeDictionaryIndex();

            if(!dictionaries.contains(fileName))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }



        return true;
    }

}
