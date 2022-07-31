package com.zlatamigas.linguisticdictionary.service;

import com.zlatamigas.linguisticdictionary.model.entity.WordTableInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Sorting {
    public enum SortingType {

        WORD_ASCENDING(1),
        WORD_DESCENDING(2),
        FREQUENCY_ASCENDING(3),
        FREQUENCY_DESCENDING(4);

        private int id;

        SortingType(int id){
            this.id = id;
        }

        public static SortingType getTypeById(int id){
            switch (id){
                case 1:
                    return WORD_ASCENDING;
                case 2:
                    return WORD_DESCENDING;
                case 3:
                    return FREQUENCY_ASCENDING;
                case 4:
                    return  FREQUENCY_DESCENDING;
                default:
                    throw new EnumConstantNotPresentException(SortingType.class, String.valueOf(id));
            }
        }
    }

    public static void sort(ArrayList<WordTableInfo> wordsInTable, int operationId){

        SortingType operation = SortingType.getTypeById(operationId);
        Comparator<WordTableInfo> comp;

        switch (operation){
            case WORD_ASCENDING:
                comp = (o1, o2) -> o1.getWord().compareToIgnoreCase(o2.getWord());
                break;
            case WORD_DESCENDING:
                comp = (o1, o2) -> o2.getWord().compareToIgnoreCase(o1.getWord());
                break;
            case FREQUENCY_ASCENDING:
                comp = (o1, o2) -> (o1.getFullFrequency() - o2.getFullFrequency());
                break;
            case FREQUENCY_DESCENDING:
                comp = (o1, o2) -> (o2.getFullFrequency() - o1.getFullFrequency());
                break;
            default:
                throw new EnumConstantNotPresentException(SortingType.class, String.valueOf(operation));
        }

        Collections.sort(wordsInTable, comp);
    }
}
