package com.zlatamigas.linguisticdictionary.model.builder;

import com.zlatamigas.linguisticdictionary.model.entity.AnnotatedWord;
import com.zlatamigas.linguisticdictionary.model.entity.TagsInfo;
import com.zlatamigas.linguisticdictionary.model.entity.TextTagsStatisticsTableInfo;

import java.io.*;
import java.util.*;

public class AnnotatorBuilder {

    private static String RESOURCES_PATH = "customResources";
    private static String ANNOTATOR_PATH = "customResources\\texts";
    private static String ANNOTATED_FILE_STATISTICS = "customResources\\texts\\annotated_statistics";
    private static String ANNOTATED_FILE_TEXT_TAGS_STATISTICS = "customResources\\texts\\annotated_text_tags_statistics";
    private static String ANNOTATED_FILE_TEXTS = "customResources\\texts\\annotated_texts";

    private static String ANNOTATED_INDEX_FILE = "annotated.ind";
    private static String ANNOTATED_FILE_TEXT_EXTENSION = ".ann";
    private static String ANNOTATED_FILE_STATISTICS_EXTENSION = ".ast";
    private static String ANNOTATED_FILE_TEXT_TAGS_STATISTICS_EXTENSION = ".tst";

    public ArrayList<String> getRealTimeAnnotatedIndex() throws IOException {

        File resourcesPath = new File(RESOURCES_PATH);

        if (!resourcesPath.exists())
            resourcesPath.mkdir();

        File directoryPath = new File(ANNOTATOR_PATH);

        if (!directoryPath.exists())
            directoryPath.mkdir();

        if (!new File(ANNOTATED_FILE_STATISTICS).exists())
            new File(ANNOTATED_FILE_STATISTICS).mkdir();

        if (!new File(ANNOTATED_FILE_TEXTS).exists())
            new File(ANNOTATED_FILE_TEXTS).mkdir();

        if (!new File(ANNOTATED_FILE_TEXT_TAGS_STATISTICS).exists())
            new File(ANNOTATED_FILE_TEXT_TAGS_STATISTICS).mkdir();

        File indexFile = new File(ANNOTATOR_PATH, ANNOTATED_INDEX_FILE);

        if (indexFile.createNewFile()) {

            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(ANNOTATOR_PATH + "\\" + ANNOTATED_INDEX_FILE, false));

            writer.write(Integer.toString(0) + "\n");
            writer.close();
        }

        ArrayList<String> annotatedFromIndex = new ArrayList<>();
        ArrayList<String> annotatedFromDir = new ArrayList<>();

        Scanner sc = new Scanner(indexFile);
        int n = sc.nextInt();
        sc.nextLine();
        for (int i = 0; i < n; ++i) {
            if (sc.hasNextLine())
                annotatedFromIndex.add(sc.nextLine());
        }
        sc.close();

        File filesList[] = new File(ANNOTATED_FILE_TEXTS).listFiles();
        File tmp;


        for (String at : annotatedFromIndex) {

            tmp = new File(ANNOTATED_FILE_TEXTS + "\\" + at + ANNOTATED_FILE_TEXT_EXTENSION);
            if (tmp.exists()) {
                tmp = new File(ANNOTATED_FILE_STATISTICS + "\\" + at + ANNOTATED_FILE_STATISTICS_EXTENSION);
                if (!tmp.exists()) {
                    buildAnnotatedFileStatistics(at, readExistingAnnotatedFile(at));
                }
                tmp = new File(ANNOTATED_FILE_TEXT_TAGS_STATISTICS + "\\" + at + ANNOTATED_FILE_TEXT_TAGS_STATISTICS_EXTENSION);
                if (!tmp.exists()) {
                    buildAnnotatedFileTextTagsStatistics(at, readExistingAnnotatedFile(at));
                }

                if (!annotatedFromDir.contains(at))
                    annotatedFromDir.add(at);
            }

        }

        String at;
        for (File file : filesList) {

            at = file.getName().substring(0, file.getName().length() - 4);

            tmp = new File(ANNOTATED_FILE_STATISTICS + "\\" + at +  ANNOTATED_FILE_STATISTICS_EXTENSION);
            if (!tmp.exists()) {
                buildAnnotatedFileStatistics(at, readExistingAnnotatedFile(at));
            }
            tmp = new File(ANNOTATED_FILE_TEXT_TAGS_STATISTICS + "\\" + at +  ANNOTATED_FILE_TEXT_TAGS_STATISTICS_EXTENSION);
            if (!tmp.exists()) {
                buildAnnotatedFileTextTagsStatistics(at, readExistingAnnotatedFile(at));
            }

            if (!annotatedFromDir.contains(at))
                annotatedFromDir.add(at);


        }


        return annotatedFromDir;
    }

    private void refreshIndex() {

        try {
            ArrayList<String> annotatedFromIndex = getRealTimeAnnotatedIndex();

            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(ANNOTATOR_PATH + "\\" + ANNOTATED_INDEX_FILE, false));

            writer.write(Integer.toString(annotatedFromIndex.size()) + "\n");
            for (String recordFromIndex : annotatedFromIndex)
                writer.write(recordFromIndex + "\n");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshIndex(ArrayList<String> annotatedFromIndex) {

        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(ANNOTATOR_PATH + "\\" + ANNOTATED_INDEX_FILE, false));

            writer.write(Integer.toString(annotatedFromIndex.size()) + "\n");
            for (String recordFromIndex : annotatedFromIndex)
                writer.write(recordFromIndex + "\n");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AnnotatorBuilder() {
        refreshIndex();
    }

    private Map<AnnotatedWord, Integer> getWordsStatistics(ArrayList<AnnotatedWord> words) {

        Map<AnnotatedWord, Integer> wordsToWrite = new HashMap<AnnotatedWord, Integer>();
        for (AnnotatedWord w : words) {

            if (wordsToWrite.containsKey(w)) {
                wordsToWrite.put(w, wordsToWrite.get(w) + 1);
            } else
                wordsToWrite.put(w, 1);
        }

        return wordsToWrite;
    }

    private ArrayList<TextTagsStatisticsTableInfo>[] getTextTagsStatistics(ArrayList<AnnotatedWord> statisticsRawData) {

        ArrayList<TextTagsStatisticsTableInfo> outData[] = new ArrayList[3];

        ArrayList<TextTagsStatisticsTableInfo> codesTextTagsStatisticsTableInfos = new ArrayList<>();
        ArrayList<TextTagsStatisticsTableInfo> wordCodeTextTagsStatisticsTableInfos = new ArrayList<>();
        ArrayList<TextTagsStatisticsTableInfo> codeCodeTextTagsStatisticsTableInfos = new ArrayList<>();

        int tagId = -1;

        String[] tags = TagsInfo.getTagNames();

        for (int i = 0; i < tags.length; ++i) {
            codesTextTagsStatisticsTableInfos.add(new TextTagsStatisticsTableInfo(tags[i], "", 0));
            for (int j = 0; j < tags.length; ++j) {
                codeCodeTextTagsStatisticsTableInfos.add(new TextTagsStatisticsTableInfo(tags[i], tags[j], 0));
            }
        }

        AnnotatedWord previousAw = null;
        TextTagsStatisticsTableInfo tempTv;
        String inputValue;
        int id = -1;

        for (AnnotatedWord aw : statisticsRawData) {

            tagId = TagsInfo.existingTag(aw.getTag());
            if (tagId == -1)
                continue;

            codesTextTagsStatisticsTableInfos.get(tagId).incFreq();

            if (previousAw != null) {

                tempTv = new TextTagsStatisticsTableInfo(previousAw.getTag(), aw.getTag(), 1);

                id = codeCodeTextTagsStatisticsTableInfos.indexOf(tempTv);
                if (id > 0)
                    codeCodeTextTagsStatisticsTableInfos.get(id).incFreq();
                else
                    codeCodeTextTagsStatisticsTableInfos.add(tempTv);

            }

            tempTv = new TextTagsStatisticsTableInfo(aw.getWord(), aw.getTag(), 1);

            id = wordCodeTextTagsStatisticsTableInfos.indexOf(tempTv);
            if (id > 0)
                wordCodeTextTagsStatisticsTableInfos.get(id).incFreq();
            else
                wordCodeTextTagsStatisticsTableInfos.add(tempTv);


            previousAw = aw;
        }

        outData[0] = codesTextTagsStatisticsTableInfos;
        outData[1] = wordCodeTextTagsStatisticsTableInfos;
        outData[2] = codeCodeTextTagsStatisticsTableInfos;

        return outData;
    }

    private void addTextTagsStatistics(ArrayList<TextTagsStatisticsTableInfo>[] outData, ArrayList<TextTagsStatisticsTableInfo>[] readData) {

        int tagId = -1;

        String[] tags = TagsInfo.getTagNames();

        if (outData[2].size() != (tags.length * tags.length) || outData[0].size() != tags.length)
            for (int i = 0; i < tags.length; ++i) {
                if (!outData[0].contains(new TextTagsStatisticsTableInfo(tags[i], "", 0)))
                    outData[0].add(new TextTagsStatisticsTableInfo(tags[i], "", 0));


                for (int j = 0; j < tags.length; ++j) {
                    if (!outData[2].contains(new TextTagsStatisticsTableInfo(tags[i], tags[j], 0)))
                        outData[2].add(new TextTagsStatisticsTableInfo(tags[i], tags[j], 0));
                }
            }

        int id = -1;

        for (int i = 0; i < 3; ++i)
            for (TextTagsStatisticsTableInfo data : readData[i]) {

                id = outData[i].indexOf(data);

                if (id != -1) {
                    outData[i].get(id).changeFreq(data.getFreq());
                } else
                    outData[i].add(data);
            }

    }

    private boolean buildAnnotatedFileStatistics(String fileName, ArrayList<AnnotatedWord> words) {

        Map<AnnotatedWord, Integer> wordsToWrite = getWordsStatistics(words);

        try {

            FileOutputStream fos = new FileOutputStream(ANNOTATED_FILE_STATISTICS + "\\" + fileName + ANNOTATED_FILE_STATISTICS_EXTENSION, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeInt(wordsToWrite.size());
            for (Map.Entry<AnnotatedWord, Integer> entry : wordsToWrite.entrySet()) {
                oos.writeObject(entry.getKey());
                oos.writeInt(entry.getValue());
            }

            oos.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean buildAnnotatedFileTextTagsStatistics(String fileName, ArrayList<AnnotatedWord> words) {

        ArrayList<TextTagsStatisticsTableInfo> outData[] = getTextTagsStatistics(words);

        try {

            FileOutputStream fos = new FileOutputStream(ANNOTATED_FILE_TEXT_TAGS_STATISTICS + "\\" + fileName + ANNOTATED_FILE_TEXT_TAGS_STATISTICS_EXTENSION, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(outData[0]);
            oos.writeObject(outData[1]);
            oos.writeObject(outData[2]);

            oos.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Map<AnnotatedWord, Integer> readAnnotatedFileStatistics(String fileName) {

        Map<AnnotatedWord, Integer> wordsStatistics = new HashMap<AnnotatedWord, Integer>();

        ArrayList<String> annotatedFromIndex = null;
        try {
            annotatedFromIndex = getRealTimeAnnotatedIndex();

            if (!annotatedFromIndex.contains(fileName))
                return null;

            if (!new File(ANNOTATED_FILE_STATISTICS + "\\" + fileName + ANNOTATED_FILE_STATISTICS_EXTENSION).exists())
                buildAnnotatedFileStatistics(fileName, readExistingAnnotatedFile(fileName));

            FileInputStream fisl = new FileInputStream(ANNOTATED_FILE_STATISTICS + "\\" + fileName + ANNOTATED_FILE_STATISTICS_EXTENSION);
            ObjectInputStream oisl = new ObjectInputStream(fisl);

            int n = oisl.readInt();

            for (int i = 0; i < n; i++) {
                wordsStatistics.put((AnnotatedWord) oisl.readObject(), oisl.readInt());
            }

            oisl.close();
            fisl.close();


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return wordsStatistics;
    }

    public ArrayList<TextTagsStatisticsTableInfo>[] readAnnotatedFileTextsTagsStatistics() {

        ArrayList<TextTagsStatisticsTableInfo> outData[] = new ArrayList[3];
        outData[0] = new ArrayList<>();
        outData[1] = new ArrayList<>();
        outData[2] = new ArrayList<>();
        ArrayList<TextTagsStatisticsTableInfo> readData[] = new ArrayList[3];
        readData[0] = new ArrayList<>();
        readData[1] = new ArrayList<>();
        readData[2] = new ArrayList<>();

        ArrayList<String> annotatedFromIndex = null;
        try {
            annotatedFromIndex = getRealTimeAnnotatedIndex();

            for (String fileName : annotatedFromIndex) {

                if (!new File(ANNOTATED_FILE_TEXT_TAGS_STATISTICS + "\\" + fileName + ANNOTATED_FILE_TEXT_TAGS_STATISTICS_EXTENSION).exists())
                    buildAnnotatedFileTextTagsStatistics(fileName, readExistingAnnotatedFile(fileName));

                FileInputStream fisl = new FileInputStream(ANNOTATED_FILE_TEXT_TAGS_STATISTICS + "\\" + fileName + ANNOTATED_FILE_TEXT_TAGS_STATISTICS_EXTENSION);
                ObjectInputStream oisl = new ObjectInputStream(fisl);


                readData[0] = (ArrayList<TextTagsStatisticsTableInfo>) oisl.readObject();
                readData[1] = (ArrayList<TextTagsStatisticsTableInfo>) oisl.readObject();
                readData[2] = (ArrayList<TextTagsStatisticsTableInfo>) oisl.readObject();

                addTextTagsStatistics(outData, readData);

                oisl.close();
                fisl.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return outData;
    }

    public ArrayList<TextTagsStatisticsTableInfo>[] readAnnotatedFileTextsTagsStatistics(ArrayList<String> annotatedFromIndex) {

        ArrayList<TextTagsStatisticsTableInfo> outData[] = new ArrayList[3];
        outData[0] = new ArrayList<>();
        outData[1] = new ArrayList<>();
        outData[2] = new ArrayList<>();
        ArrayList<TextTagsStatisticsTableInfo> readData[] = new ArrayList[3];
        readData[0] = new ArrayList<>();
        readData[1] = new ArrayList<>();
        readData[2] = new ArrayList<>();

        try {

            for (String fileName : annotatedFromIndex) {

                if (!new File(ANNOTATED_FILE_TEXT_TAGS_STATISTICS + "\\" + fileName + ANNOTATED_FILE_TEXT_TAGS_STATISTICS_EXTENSION).exists())
                    buildAnnotatedFileTextTagsStatistics(fileName, readExistingAnnotatedFile(fileName));

                FileInputStream fisl = new FileInputStream(ANNOTATED_FILE_TEXT_TAGS_STATISTICS + "\\" + fileName + ANNOTATED_FILE_TEXT_TAGS_STATISTICS_EXTENSION);
                ObjectInputStream oisl = new ObjectInputStream(fisl);


                readData[0] = (ArrayList<TextTagsStatisticsTableInfo>) oisl.readObject();
                readData[1] = (ArrayList<TextTagsStatisticsTableInfo>) oisl.readObject();
                readData[2] = (ArrayList<TextTagsStatisticsTableInfo>) oisl.readObject();

                addTextTagsStatistics(outData, readData);

                oisl.close();
                fisl.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return outData;
    }


    private ArrayList<AnnotatedWord> readExistingAnnotatedFile(String fileName) {

        ArrayList<AnnotatedWord> words = new ArrayList<>();

        try {

            FileInputStream fisl = new FileInputStream(ANNOTATED_FILE_TEXTS + "\\" + fileName + ANNOTATED_FILE_TEXT_EXTENSION);
            ObjectInputStream oisl = new ObjectInputStream(fisl);

            int n = oisl.readInt();

            for (int i = 0; i < n; i++) {
                words.add((AnnotatedWord) oisl.readObject());
            }

            oisl.close();
            fisl.close();


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return words;
    }


    public ArrayList<AnnotatedWord> readAnnotatedFile(String fileName) {


        ArrayList<String> annotatedFromIndex = null;
        try {
            annotatedFromIndex = getRealTimeAnnotatedIndex();

            if (!annotatedFromIndex.contains(fileName))
                return null;


            return readExistingAnnotatedFile(fileName);


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<AnnotatedWord> readAnnotatedFiles(ArrayList<String> fileNames) {

        ArrayList<AnnotatedWord> words = new ArrayList<>();
        ArrayList<AnnotatedWord> temp;

        for (String file : fileNames) {
            temp = readAnnotatedFile(file);
            if (temp != null)
                words.addAll(temp);
        }

        return words;
    }


    public String addAnnotatedFile(String fileName, ArrayList<AnnotatedWord> words) {

        ArrayList<String> annotatedFromIndex = null;
        try {
            annotatedFromIndex = getRealTimeAnnotatedIndex();

            int num = 1;
            if (annotatedFromIndex.contains(fileName)) {
                while (annotatedFromIndex.contains(fileName + "." + num)) {
                    num++;
                }
                fileName += "." + num;
            }
            annotatedFromIndex.add(fileName);

            refreshIndex(annotatedFromIndex);

            FileOutputStream fos = new FileOutputStream(ANNOTATED_FILE_TEXTS + "\\" + fileName + ANNOTATED_FILE_TEXT_EXTENSION, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeInt(words.size());
            for (int i = 0; i < words.size(); i++) {
                oos.writeObject(words.get(i));
            }
            oos.close();
            fos.close();

            buildAnnotatedFileStatistics(fileName, words);
            buildAnnotatedFileTextTagsStatistics(fileName, words);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return fileName;
    }

    public boolean deleteAnnotatedFile(String fileName) throws IOException {
        ArrayList<String> annotatedFromIndex = null;

        new File(ANNOTATED_FILE_TEXTS + "\\" + fileName + ANNOTATED_FILE_TEXT_EXTENSION).delete();
        new File(ANNOTATED_FILE_STATISTICS + "\\" + fileName + ANNOTATED_FILE_STATISTICS_EXTENSION).delete();
        new File(ANNOTATED_FILE_TEXT_TAGS_STATISTICS + "\\" + fileName + ANNOTATED_FILE_TEXT_TAGS_STATISTICS_EXTENSION).delete();

        refreshIndex();

        DictionaryBuilder db = new DictionaryBuilder();

        for(String dict: db.getRealTimeDictionaryIndex()){
            db.removeAnnotatedTextFromDictionary(dict, new ArrayList<>(Arrays.asList(fileName)));
        }

        return true;
    }
}
