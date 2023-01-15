package LanguageProcesses;

import java.io.*;
import java.util.*;

import LanguageProcesses.Utils.TextBuilder;

public class Corpus {
    /**
     * initialize the input file that contains the messy corpus.
     * initialize the output file that will contain the clean corpus.
     * initialize the stop words file that contains the stop words list to be removed.
     * initialize the stop words hash set that will contain the stop words list to be removed
     *      after reading it from the file to use it in the removeStopWords method.
     */
    private static final File INPUT_FILE = new File("Data/Corpus/MessyCorpus.txt");
    private static final File OUTPUT_FILE = new File("Data/Corpus/CleanCorpus.txt");
    private static final File STOP_WORDS_FILE = new File("Data/StopWords.txt");

    private static final HashSet<String> STOP_WORDS_HASHSET = new HashSet<>();

    //////////////////////////////

    /**
     * The main method of the program.
     * Reads a list of common words (stop words) from a file, saving it to the HashSet,
     * and then reads and cleans a text document (corpus), saving the cleaned version to a new file.
     *
     * @param args not used in this program
     * @throws IOException if there is an error reading from or writing to a file
     */
    public static void main(String[] args) throws IOException {
        readStopWordsFromFile();
        readMessyCorpusAndSaveCleanCorpusToFile();
    }

    /**
     * Reads a list of stop words from a file and adds them to a HashSet after normalizing them.
     * The file containing the stop words should be located at the path specified by
     * the STOP_WORDS_FILE constant.
     * Each line in the file should contain one stop word.
     *
     * @throws IOException if there is an error reading from the stop words file.
     */
    public static void readStopWordsFromFile() throws IOException {
        BufferedReader inputReader = new BufferedReader(new FileReader(STOP_WORDS_FILE));
        String line;
        while ((line = inputReader.readLine()) != null) {
            String word = normalizeText(line);
            STOP_WORDS_HASHSET.add(word.trim());
        }
        inputReader.close();
    }

    /**
     * Reads a messy corpus from a file, processes it, and writes the cleaned corpus
     * to a new file.
     * The input file should be located at the path specified by the INPUT_FILE
     * constant.
     * The cleaned corpus will be written to the file located at the path specified
     * by the OUTPUT_FILE constant.
     * This method applies the following operations to each line of the input file:
     * - Normalization of text
     * - Removal of stop words
     * - Cleaning of line
     *
     * @throws IOException if there is an error reading from the input file or
     *                     writing to the output file.
     */
    private static void readMessyCorpusAndSaveCleanCorpusToFile() throws IOException {
        BufferedReader inputReader = new BufferedReader(new FileReader(INPUT_FILE));
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE));

        String line;
        while ((line = inputReader.readLine()) != null) {
            TextBuilder textBuilder = new TextBuilder(line)
                    .normalizeText()
                    .removeStopWords()
                    .cleanTheLine();
            line = textBuilder.build();

            if (!line.equals("")) {
                outputWriter.write(line);
                outputWriter.newLine();
            }
        }

        inputReader.close();
        outputWriter.close();
    }

    //////////////////////////////

    /**
     * Applies a series of normalization operations to a given text.
     *
     * @param text The text to be normalized.
     * @return The normalized text.
     *
     * for reference on Arabic letters in unicode please refer to the following link:
     * https://en.wikipedia.org/wiki/Arabic_script_in_Unicode
     */
    public static String normalizeText(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceUnderscoreWithSpace()   // to handle hashtags
                .replaceSpecialCharactersWithWords()
                .removeKashida()
                .replaceAlternativeLetters()
                .removeConsecutiveRedundantCharacters(3)
                .replaceArabicLettersToMatch()
                .removeNonArabic();

        return textBuilder.build();
    }

    /**
     * Removes stop words from a given text.
     * The stop words are stored in a HashSet.
     *
     * @param text The text from which stop words should be removed.
     * @return The text with stop words removed.
     */
    public static String removeStopWords(String text) {
        String[] lineWords = text.split(" ");

        StringBuilder sb = new StringBuilder();
        for (String word: lineWords) {
            if (!STOP_WORDS_HASHSET.contains(word)) {
                sb.append(word).append(" ");
            }
        }

        text = sb.toString();
        return text.trim();
    }

    /**
     * Cleans a given text by removing words with less than 3 characters.
     *
     * @param text The text to be cleaned.
     * @return The cleaned text.
     */
    public static String cleanTheLine(String text) {
        String[] words = text.split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String word: words) {
            if (word.trim().length() > 2)
                sb.append(word.trim()).append(" ");
        }
        text = sb.toString().trim();

        return text;
    }

    //////////////////////////////


    /**
     * Replaces all underscores in a given text with spaces.
     *
     * @param text The text to be processed.
     * @return The text with underscores replaced by spaces.
     */
    public static String replaceUnderscoreWithSpace(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("_", " ");
        return textBuilder.build();
    }

    /**
     * Replaces special characters in a given text with their corresponding word
     * representation.
     *
     * @param text The text to be processed.
     * @return The text with special characters replaced by their corresponding word
     *         representation.
     */
    public static String replaceSpecialCharactersWithWords(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u0603", "صفحة")
                .replaceAll("\u0610", "صلى الله عليه وسلم")
                .replaceAll("\u0611", "عليه السلام")
                .replaceAll("\u0612", "رحمة الله عليه")
                .replaceAll("\u0613", "رضي الله عنه")
                .replaceAll("\uFDF3", "الله")
                .replaceAll("\uFDF4", "محمد")
                .replaceAll("\uFDF5", "صلى الله عليه وسلم")
                .replaceAll("\uFDF6", "رسول")
                .replaceAll("\uFDF7", "عليه")
                .replaceAll("\uFDF8", "وسلم")
                .replaceAll("\uFDF9", "صلى")
                .replaceAll("\uFDFA", "صلى الله عليه وسلم")
                .replaceAll("\uFDFB", "جل جلاله")
                .replaceAll("\uFDFC", "ريال")
                .replaceAll("\uFDFD", "بسم الله الرحمن الرحيم");

        return textBuilder.build();
    }

    /**
     * Removes Kashida characters from a given text.
     * Kashida characters are used to extend the length of certain Arabic letters.
     * For example, "كـــــتـاب" will be converted to "كتاب".
     *
     * @param text The text to be processed.
     * @return The text with Kashida characters removed.
     */
    public static String removeKashida(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u0640", "");   // كـــــتـاب -> كتاب
        return textBuilder.build();        
    }

    /**
     * Replaces alternative Arabic letters with their corresponding standard Arabic
     * letters.
     *
     * @param text The text to be processed.
     * @return The text with alternative Arabic letters replaced by their corresponding
     *         standard Arabic letters.
     */
    public static String replaceAlternativeLetters(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceWithLetterAlef()
                .replaceWithLetterBeh()
                .replaceWithLetterTeh()
                .replaceWithLetterJeem()
                .replaceWithLetterHah()
                .replaceWithLetterKhah()
                .replaceWithLetterDal()
                .replaceWithLetterReh()
                .replaceWithLetterZain()
                .replaceWithLetterSeen()
                .replaceWithLetterSheen()
                .replaceWithLetterSad()
                .replaceWithLetterDad()
                .replaceWithLetterTah()
                .replaceWithLetterAin()
                .replaceWithLetterGhain()
                .replaceWithLetterFeh()
                .replaceWithLetterQaf()
                .replaceWithLetterKaf()
                .replaceWithLetterLam()
                .replaceWithLetterMeem()
                .replaceWithLetterNoon()
                .replaceWithLetterHeh()
                .replaceWithLetterWaw()
                .replaceWithLetterYeh();

        return textBuilder.build();
    }

    /**
     * Removes consecutive redundant characters in a given text.
     * For example, "انااااا ممن يحبوووون الكتب" will be converted to "انا ممن يحبون الكتب", where
     * the number of consecutive redundant characters is 3.
     *
     * @param text                          The text to be processed.
     * @param numberOfConsecutiveCharacters The number of consecutive redundant
     *                                      characters to be removed.
     * @return The text with consecutive redundant characters removed.
     */
    public static String removeConsecutiveRedundantCharacters(String text, int numberOfConsecutiveCharacters) {
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

    /**
     * Replaces certain Arabic letters with their matching forms to unify the text.
     * This method replaces the letter "ة" with "ه" and "ى" with "ا".
     *
     * @param text The text to be processed.
     * @return The text with certain Arabic letters replaced by their matching
     *         forms.
     */
    public static String replaceArabicLettersToMatch(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceUnderAlefWithUpperAlef()
                .replaceTehMarbutaWithHeh();

        return textBuilder.build();
    }

    /**
     * Removes non-Arabic characters from a given text.
     * This method only keeps the basic main Arabic letters.
     *
     * @param text The text to be processed.
     * @return The text with non-Arabic characters removed.
     */
    public static String removeNonArabic(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[^\u0621-\u064A\\s]", "");

        return textBuilder.build();
    }

    //////////////////////////////

    public static String replaceWithLetterAlef(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u0621-\u0627]", "ا")
                .replaceAll("[\u0654-\u0655]", "ا")
                .replaceAll("[\u0671-\u0678]", "ا")
                .replaceAll("[\u0773-\u0774]", "ا");

        return textBuilder.build();
    }
    public static String replaceWithLetterBeh(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u066E", "ب")
                .replaceAll("\u067B", "ب")
                .replaceAll("\u067E", "ب")
                .replaceAll("\u0680", "ب")
                .replaceAll("\u0750", "ب")
                .replaceAll("\u0752", "ب")
                .replaceAll("[\u0750-\u0756]", "ب")
                .replaceAll("[\u08A0-\u08A1]", "ب")
                .replaceAll("[\u08B6-\u08B7]", "ب");

        return textBuilder.build();
    }
    public static String replaceWithLetterTeh(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u0679-\u067A]", "ت")
                .replaceAll("[\u067C-\u067D]", "ت")
                .replaceAll("\u067F", "ت")
                .replaceAll("\u08B8", "ت");

        return textBuilder.build();
    }
    public static String replaceWithLetterJeem(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u0683-\u0684]", "ج")
                .replaceAll("[\u0686-\u0687]", "ج")
                .replaceAll("\u0758", "ج")
                .replaceAll("\u08A2", "ج");

        return textBuilder.build();
    }
    public static String replaceWithLetterHah(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u0681-\u0682]", "ح")
                .replaceAll("\u0685", "ح")
                .replaceAll("\u0757", "ح")
                .replaceAll("[\u076E-\u076F]", "ح")
                .replaceAll("\u0772", "ح")
                .replaceAll("\u077C", "ح");

        return textBuilder.build();
    }
    public static String replaceWithLetterKhah(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u06BF", "خ");

        return textBuilder.build();
    }
    public static String replaceWithLetterDal(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u0688-\u0690]", "د")
                .replaceAll("\u06EE", "د")
                .replaceAll("[\u0759-\u075A]", "د")
                .replaceAll("\u08AE", "د");

        return textBuilder.build();
    }
    public static String replaceWithLetterReh(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u0691-\u0699]", "ر")
                .replaceAll("\u06EF", "ر")
                .replaceAll("\u075B", "ر")
                .replaceAll("[\u076B-\u076C]", "ر")
                .replaceAll("\u0771", "ر")
                .replaceAll("\u08AA", "ر")
                .replaceAll("\u08B9", "ر");

        return textBuilder.build();
    }
    public static String replaceWithLetterZain(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u08B2", "ز");

        return textBuilder.build();
    }
    public static String replaceWithLetterSeen(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u069A-\u069C]", "س")
                .replaceAll("\u075C", "س")
                .replaceAll("\u076D", "س")
                .replaceAll("\u0770", "س")
                .replaceAll("[\u077D-\u077E]", "س");

        return textBuilder.build();
    }
    public static String replaceWithLetterSheen(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u06FA", "ش");

        return textBuilder.build();
    }
    public static String replaceWithLetterSad(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u069D-\u069E]", "ص")
                .replaceAll("\u08AF", "ص");

        return textBuilder.build();
    }
    public static String replaceWithLetterDad(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u06FB", "ض");

        return textBuilder.build();
    }
    public static String replaceWithLetterTah(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u069F", "ط")
                .replaceAll("\u08A3", "ط");

        return textBuilder.build();
    }
    public static String replaceWithLetterAin(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u060F", "ع")
                .replaceAll("\u06A0", "ع")
                .replaceAll("[\u075D-\u075F]", "ع")
                .replaceAll("\u08B3", "ع");

        return textBuilder.build();
    }
    public static String replaceWithLetterGhain(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u06FC", "غ");

        return textBuilder.build();
    }
    public static String replaceWithLetterFeh(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u06A1-\u06A6]", "ف")
                .replaceAll("[\u0760-\u0761]", "ف")
                .replaceAll("\u08A4", "ف")
                .replaceAll("\u08BB", "ف");

        return textBuilder.build();
    }
    public static String replaceWithLetterQaf(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u066F", "ق")
                .replaceAll("[\u06A7-\u06A8]", "ق")
                .replaceAll("\u08A5", "ق")
                .replaceAll("\u08BC", "ق");

        return textBuilder.build();
    }
    public static String replaceWithLetterKaf(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u063B-\u063C]", "ك")
                .replaceAll("[\u06A9-\u06B4]", "ك")
                .replaceAll("[\u0762-\u0764]", "ك")
                .replaceAll("\u077F", "ك")
                .replaceAll("\u08B0", "ك")
                .replaceAll("\u08B4", "ك");

        return textBuilder.build();
    }
    public static String replaceWithLetterLam(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u06B5-\u06B8]", "ل")
                .replaceAll("\u076A", "ل")
                .replaceAll("\u08A6", "ل");

        return textBuilder.build();
    }
    public static String replaceWithLetterMeem(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u0765-\u0766]", "م")
                .replaceAll("\u08A7", "م");

        return textBuilder.build();
    }
    public static String replaceWithLetterNoon(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u06B9-\u06BD]", "ن")
                .replaceAll("[\u0767-\u0769]", "ن")
                .replaceAll("\u08BD", "ن");
        return textBuilder.build();
    }
    public static String replaceWithLetterHeh(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u06BE", "ه")
                .replaceAll("[\u06C0-\u06C3]", "ه")
                .replaceAll("\u06FF", "ه");
        return textBuilder.build();
    }
    public static String replaceWithLetterWaw(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("[\u06C4-\u06CB]", "و")
                .replaceAll("\u06CF", "و")
                .replaceAll("[\u0778-\u0779]", "و")
                .replaceAll("\u08AB", "و")
                .replaceAll("\u08B1", "و");
        return textBuilder.build();
    }
    public static String replaceWithLetterYeh(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("\u0620", "ي")
                .replaceAll("[\u063D-\u063F]", "ي")
                .replaceAll("[\u06CC-\u06CE]", "ي")
                .replaceAll("[\u06D0-\u06D3]", "ي")
                .replaceAll("[\u0775-\u0777]", "ي")
                .replaceAll("[\u077A-\u077B]", "ي")
                .replaceAll("[\u08A8-\u08A9]", "ي")
                .replaceAll("\u08AC", "ي")
                .replaceAll("\u08BA", "ي");
        return textBuilder.build();
    }
    public static String replaceTehMarbutaWithHeh(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("ة", "ه");
        return textBuilder.build();
    }
    public static String replaceUnderAlefWithUpperAlef(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .replaceAll("ى", "ا");
        return textBuilder.build();
    }
}