package LanguageProcesses;

import java.io.*;
import java.util.*;

public class Corpus {
    private static final File INPUT_FILE = new File("Data/Corpus/MessyCorpus.txt");
    private static final File OUTPUT_FILE = new File("Data/Corpus/CleanCorpus.txt");
    private static final File STOP_WORDS_FILE = new File("Data/StopWords.txt");

    private static final HashSet<String> STOP_WORDS_HASHSET = new HashSet<>();

    //////////////////////////////

    public static void main(String[] args) throws IOException {
        readStopWordsFromFile();
        readMessyCorpusAndSaveCleanCorpusToFile();
    }
    public static void readStopWordsFromFile() throws IOException {
        BufferedReader inputReader = new BufferedReader(new FileReader(STOP_WORDS_FILE));
        String line;
        while ((line = inputReader.readLine()) != null) {
            String word = normalizeText(line);
            STOP_WORDS_HASHSET.add(word.trim());
        }
        inputReader.close();
    }

    private static void readMessyCorpusAndSaveCleanCorpusToFile() throws IOException {
        BufferedReader inputReader = new BufferedReader(new FileReader(INPUT_FILE));
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE));

        String line;
        while ((line = inputReader.readLine()) != null) {
            line = normalizeText(line);
            line = removeStopWords(line);
            line = cleanTheLine(line);

            if (!line.equals("")) {
                outputWriter.write(line);
                outputWriter.newLine();
            }
        }

        inputReader.close();
        outputWriter.close();
    }

    //////////////////////////////

    public static String normalizeText(String text) {
//      https://en.wikipedia.org/wiki/Arabic_script_in_Unicode

        text = replaceUnderscoreWithSpace(text);
        text = replaceSpecialCharactersWithWords(text);
        text = removeKashida(text);
        text = replaceAlternativeLetters(text);
        text = removeConsecutiveRedundantCharacters(text, 3);
        text = replaceArabicLettersToMatch(text);
        text = removeNonArabic(text);

        return text;
    }
    public static String removeStopWords(String text) {
        String[] lineWords = text.split(" ");

        StringBuilder textBuilder = new StringBuilder();
        for (String word: lineWords) {
            if (!STOP_WORDS_HASHSET.contains(word)) {
                textBuilder.append(word).append(" ");
            }
        }

        text = textBuilder.toString();
        return text.trim();
    }
    public static String cleanTheLine(String text) {
        String[] words = text.split("\\s+");
        StringBuilder textBuilder = new StringBuilder();

        for (String word: words) {
            if (word.trim().length() > 2)
                textBuilder.append(word.trim()).append(" ");
        }
        text = textBuilder.toString().trim();

        return text;
    }

    //////////////////////////////

    private static String replaceUnderscoreWithSpace(String text) {
        // to handle hashtags
        return text.replaceAll("_", " ");
    }
    private static String replaceSpecialCharactersWithWords(String text) {
        text = text.replaceAll("\u0603", "صفحة");
        text = text.replaceAll("\u0610", "صلى الله عليه وسلم");
        text = text.replaceAll("\u0611", "عليه السلام");
        text = text.replaceAll("\u0612", "رحمة الله عليه");
        text = text.replaceAll("\u0613", "رضي الله عنه");
        text = text.replaceAll("\uFDF3", "الله");
        text = text.replaceAll("\uFDF4", "محمد");
        text = text.replaceAll("\uFDF5", "صلى الله عليه وسلم");
        text = text.replaceAll("\uFDF6", "رسول");
        text = text.replaceAll("\uFDF7", "عليه");
        text = text.replaceAll("\uFDF8", "وسلم");
        text = text.replaceAll("\uFDF9", "صلى");
        text = text.replaceAll("\uFDFA", "صلى الله عليه وسلم");
        text = text.replaceAll("\uFDFB", "جل جلاله");
        text = text.replaceAll("\uFDFC", "ريال");
        text = text.replaceAll("\uFDFD", "بسم الله الرحمن الرحيم");

        return text;
    }
    private static String removeKashida(String text) {
        text = text.replaceAll("\u0640", "");   // كـــــتـاب -> كتاب

        return text;
    }
    private static String replaceAlternativeLetters(String text) {
        text = replaceWithLetterAlef(text);
        text = replaceWithLetterBeh(text);
        text = replaceWithLetterTeh(text);
        text = replaceWithLetterJeem(text);
        text = replaceWithLetterHah(text);
        text = replaceWithLetterKhah(text);
        text = replaceWithLetterDal(text);
        text = replaceWithLetterReh(text);
        text = replaceWithLetterZain(text);
        text = replaceWithLetterSeen(text);
        text = replaceWithLetterSheen(text);
        text = replaceWithLetterSad(text);
        text = replaceWithLetterDad(text);
        text = replaceWithLetterTah(text);
        text = replaceWithLetterAin(text);
        text = replaceWithLetterGhain(text);
        text = replaceWithLetterFeh(text);
        text = replaceWithLetterQaf(text);
        text = replaceWithLetterKaf(text);
        text = replaceWithLetterLam(text);
        text = replaceWithLetterMeem(text);
        text = replaceWithLetterNoon(text);
        text = replaceWithLetterHeh(text);
        text = replaceWithLetterWaw(text);
        text = replaceWithLetterYeh(text);

        return text;
    }
    private static String removeConsecutiveRedundantCharacters(String text, int numberOfConsecutiveCharacters) {
        text += "\0";
        StringBuilder sb = new StringBuilder();

        int count = 1;
        char prevChar = text.charAt(0);

        for (int i = 1; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            if (currentChar == prevChar) {
                count++;
            } else {
                if (count > numberOfConsecutiveCharacters)
                    sb.append(prevChar);
                else
                    sb.append(String.valueOf(prevChar).repeat(count));

                count = 1;
                prevChar = currentChar;
            }
        }
        return sb.toString();
    }
    private static String replaceArabicLettersToMatch(String text) {
        text = replaceUnderAlefWithUpperAlef(text);
        text = replaceTehMarbutaWithHeh(text);

        return text;
    }
    private static String removeNonArabic(String text) {
        text = text.replaceAll("[^\u0621-\u064A\\s]", "");

        return text;
    }

    //////////////////////////////

    private static String replaceWithLetterAlef(String text) {
        text = text.replaceAll("[\u0621-\u0627]", "ا");
        text = text.replaceAll("[\u0654-\u0655]", "ا");
        text = text.replaceAll("[\u0671-\u0678]", "ا");
        text = text.replaceAll("[\u0773-\u0774]", "ا");

        return text;
    }
    private static String replaceWithLetterBeh(String text) {
        text = text.replaceAll("\u066E", "ب");
        text = text.replaceAll("\u067B", "ب");
        text = text.replaceAll("\u067E", "ب");
        text = text.replaceAll("\u0680", "ب");
        text = text.replaceAll("\u0750", "ب");
        text = text.replaceAll("\u0752", "ب");
        text = text.replaceAll("[\u0750-\u0756]", "ب");
        text = text.replaceAll("[\u08A0-\u08A1]", "ب");
        text = text.replaceAll("[\u08B6-\u08B7]", "ب");

        return text;
    }
    private static String replaceWithLetterTeh(String text) {
        text = text.replaceAll("[\u0679-\u067A]", "ت");
        text = text.replaceAll("[\u067C-\u067D]", "ت");
        text = text.replaceAll("\u067F", "ت");
        text = text.replaceAll("\u08B8", "ت");

        return text;
    }
    private static String replaceWithLetterJeem(String text) {
        text = text.replaceAll("[\u0683-\u0684]", "ج");
        text = text.replaceAll("[\u0686-\u0687]", "ج");
        text = text.replaceAll("\u0758]", "ج");
        text = text.replaceAll("\u08A2]", "ج");

        return text;
    }
    private static String replaceWithLetterHah(String text) {
        text = text.replaceAll("[\u0681-\u0682]", "ح");
        text = text.replaceAll("\u0685", "ح");
        text = text.replaceAll("\u0757", "ح");
        text = text.replaceAll("[\u076E-\u076F]", "ح");
        text = text.replaceAll("\u0772", "ح");
        text = text.replaceAll("\u077C", "ح");

        return text;
    }
    private static String replaceWithLetterKhah(String text) {
        text = text.replaceAll("\u06BF", "خ");

        return text;
    }
    private static String replaceWithLetterDal(String text) {
        text = text.replaceAll("[\u0688-\u0690]", "د");
        text = text.replaceAll("\u06EE", "د");
        text = text.replaceAll("[\u0759-\u075A]", "د");
        text = text.replaceAll("\u08AE", "د");

        return text;
    }
    private static String replaceWithLetterReh(String text) {
        text = text.replaceAll("[\u0691-\u0699]", "ر");
        text = text.replaceAll("\u06EF", "ر");
        text = text.replaceAll("\u075B", "ر");
        text = text.replaceAll("[\u076B-\u076C]", "ر");
        text = text.replaceAll("\u0771", "ر");
        text = text.replaceAll("\u08AA", "ر");
        text = text.replaceAll("\u08B9", "ر");

        return text;
    }
    private static String replaceWithLetterZain(String text) {
        text = text.replaceAll("\u08B2", "ز");

        return text;
    }
    private static String replaceWithLetterSeen(String text) {
        text = text.replaceAll("[\u069A-\u069C]", "س");
        text = text.replaceAll("\u075C", "س");
        text = text.replaceAll("\u076D", "س");
        text = text.replaceAll("\u0770", "س");
        text = text.replaceAll("[\u077D-\u077E]", "س");

        return text;
    }
    private static String replaceWithLetterSheen(String text) {
        text = text.replaceAll("\u06FA", "ش");

        return text;
    }
    private static String replaceWithLetterSad(String text) {
        text = text.replaceAll("[\u069D-\u069E]", "ص");
        text = text.replaceAll("\u08AF", "ص");

        return text;
    }
    private static String replaceWithLetterDad(String text) {
        text = text.replaceAll("\u06FB", "ض");

        return text;
    }
    private static String replaceWithLetterTah(String text) {
        text = text.replaceAll("\u069F", "ط");
        text = text.replaceAll("\u08A3", "ط");

        return text;
    }
    private static String replaceWithLetterAin(String text) {
        text = text.replaceAll("\u060F", "ع");
        text = text.replaceAll("\u06A0", "ع");
        text = text.replaceAll("[\u075D-\u075F]", "ع");
        text = text.replaceAll("\u08B3", "ع");

        return text;
    }
    private static String replaceWithLetterGhain(String text) {
        text = text.replaceAll("\u06FC", "غ");

        return text;
    }
    private static String replaceWithLetterFeh(String text) {
        text = text.replaceAll("[\u06A1-\u06A6]", "ف");
        text = text.replaceAll("[\u0760-\u0761]", "ف");
        text = text.replaceAll("\u08A4", "ف");
        text = text.replaceAll("\u08BB", "ف");

        return text;
    }
    private static String replaceWithLetterQaf(String text) {
        text = text.replaceAll("\u066F", "ق");
        text = text.replaceAll("[\u06A7-\u06A8]", "ق");
        text = text.replaceAll("\u08A5", "ق");
        text = text.replaceAll("\u08BC", "ق");

        return text;
    }
    private static String replaceWithLetterKaf(String text) {
        text = text.replaceAll("[\u063B-\u063C]", "ك");
        text = text.replaceAll("[\u06A9-\u06B4]", "ك");
        text = text.replaceAll("[\u0762-\u0764]", "ك");
        text = text.replaceAll("\u077F", "ك");
        text = text.replaceAll("\u08B0", "ك");
        text = text.replaceAll("\u08B4", "ك");

        return text;
    }
    private static String replaceWithLetterLam(String text) {
        text = text.replaceAll("[\u06B5-\u06B8]", "ل");
        text = text.replaceAll("\u076A", "ل");
        text = text.replaceAll("\u08A6", "ل");

        return text;
    }
    private static String replaceWithLetterMeem(String text) {
        text = text.replaceAll("[\u0765-\u0766]", "م");
        text = text.replaceAll("\u08A7", "م");

        return text;
    }
    private static String replaceWithLetterNoon(String text) {
        text = text.replaceAll("[\u06B9-\u06BD]", "ن");
        text = text.replaceAll("[\u0767-\u0769]", "ن");
        text = text.replaceAll("\u08BD", "ن");

        return text;
    }
    private static String replaceWithLetterHeh(String text) {
        text = text.replaceAll("\u06BE", "ه");
        text = text.replaceAll("[\u06C0-\u06C3]", "ه");
        text = text.replaceAll("\u06FF", "ه");

        return text;
    }
    private static String replaceWithLetterWaw(String text) {
        text = text.replaceAll("[\u06C4-\u06CB]", "و");
        text = text.replaceAll("\u06CF", "و");
        text = text.replaceAll("[\u0778-\u0779]", "و");
        text = text.replaceAll("\u08AB", "و");
        text = text.replaceAll("\u08B1", "و");

        return text;
    }
    private static String replaceWithLetterYeh(String text) {
        text = text.replaceAll("\u0620", "ي");
        text = text.replaceAll("[\u063D-\u063F]", "ي");
        text = text.replaceAll("[\u06CC-\u06CE]", "ي");
        text = text.replaceAll("[\u06D0-\u06D3]", "ي");
        text = text.replaceAll("[\u0775-\u0777]", "ي");
        text = text.replaceAll("[\u077A-\u077B]", "ي");
        text = text.replaceAll("[\u08A8-\u08A9]", "ي");
        text = text.replaceAll("\u08AC", "ي");
        text = text.replaceAll("\u08BA", "ي");

        return text;
    }
    private static String replaceTehMarbutaWithHeh(String text){
        text = text.replaceAll("ة", "ه");

        return text;
    }
    private static String replaceUnderAlefWithUpperAlef(String text) {
        text = text.replaceAll("ى", "ا");

        return text;
    }
}