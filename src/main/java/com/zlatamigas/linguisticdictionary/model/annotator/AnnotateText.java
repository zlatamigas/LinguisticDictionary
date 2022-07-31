package com.zlatamigas.linguisticdictionary.model.annotator;

import com.zlatamigas.linguisticdictionary.model.entity.AnnotatedWord;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

public class AnnotateText {

    public static ArrayList<AnnotatedWord> annotateText(String fileName) throws IOException {

        ArrayList<AnnotatedWord> words = new ArrayList<>();

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        File fileToRead = new File(fileName);

        Scanner sc = null;
        String input;
        StringTokenizer tokenizer;

        String word;

        try {
            sc = new Scanner(fileToRead);
        } catch (FileNotFoundException ex) {
            System.out.println("File " + fileToRead.getName() + " not found!");
        }

        while (sc.hasNextLine()) {

            input = sc.nextLine();
            tokenizer = new StringTokenizer(input, " .,!?\"'\\/-()[]{}%@#$;:^*+=&\t", true);

            while (tokenizer.hasMoreTokens()) {

                word = tokenizer.nextToken();

                if(!"\n\t ".contains(word)) {
                    words.add(new AnnotatedWord(word, pipeline.processToCoreDocument(word).tokens().get(0).tag()));
                }
            }

        }

        return words;
    }
}
