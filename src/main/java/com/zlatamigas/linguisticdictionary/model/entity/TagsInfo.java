package com.zlatamigas.linguisticdictionary.model.entity;

public class TagsInfo {

    public static String[][] TAGS = new String[][]{
            {"CC", "Coordinating conjunction"},
            {"CD", "Cardinal number"},
            {"DT", "Determiner"},
            {"EX", "Existential there"},
            {"FW", "Foreign word"},
            {"IN", "Preposition/subordinating participle conjunction\n"},
            {"VBN", "Verb, past participle"},
            {"JJ", "Adjective"},
            {"JJR", "Adjective, comparative"},
            {"JJS", "Adjective, superlative"},
            {"LS", "List item marker"},
            {"MD", "Modal"},
            {"NN", "Noun, singular or mass"},
            {"NNS", "Noun, plural"},
            {"NNP", "Proper noun, singular"},
            {"NNPS", "Proper noun, plural"},
            {"PDT", "Predeterminer"},
            {"POS", "Possessive ending"},
            {"PRP", "Personal pronoun"},
            {"PP$", "Possessive pronoun"},
            {"RB", "Adverb"},
            {"RBR", "Adverb, comparative"},
            {"RBS", "Adverb, superlative"},
            {"RP", "Particle"},
            {"SYM", "Symbol (mathematical or scientific)"},
            {"TO", "To"},
            {"UH", "Interjection"},
            {"VB", "Verb, base form"},
            {"VBD", "Verb, past tense"},
            {"VBG", "Verb, gerund/present participle"},
            {"VBP", "Verb, non-3rd ps. sing. present"},
            {"VBZ", "Verb, 3rd ps. sing. present"},
            {"WDT", "Wh-determiner"},
            {"WP", "Wh-pronoun"},
            {"WP$", "Possessive wh-pronoun"},
            {"WRB", "Wh-adverb"},
            {"#", "Pound sign"},
            {"$", "Dollar sign"},
            {".", "Dot"},
            {"..", "Sentence-final punctuation"},
            {",", "Comma"},
            {":", "Colon, semi-colon"},
            {"(", "Left bracket character"},
            {")", "Right bracket character"},
            {"'", "Single quote"},
            {"\"", "Open/close double quote"},
            {"NFP", "Not tagged"}
    };

    public static String[][] getTAGS() {
        return TAGS;
    }

    public static String[] getTagNames() {
        String[] tags = new String[TAGS.length];

        for (int i = 0; i < TAGS.length; ++i)
            tags[i] = TAGS[i][0];

        return tags;
    }

    public static String getTagDescription(String tag) {

        int i = 0;
        do {
            if (tag.equalsIgnoreCase(TAGS[i][0]))
                return TAGS[i][1];

            ++i;
        }
        while (i < TAGS.length);

        return null;
    }

    public static String getTagDescription(int tagId) {

        if (tagId < TAGS.length)
            return TAGS[tagId][1];

        return null;
    }


    public static int existingTag(String tag) {

        int i = 0;
        do {
            if (tag.equalsIgnoreCase(TAGS[i][0]))
                return i;

            ++i;
        }
        while (i < TAGS.length);

        return -1;
    }
}
