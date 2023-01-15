package LanguageProcesses;

import LanguageProcesses.Object.Chunk;
import LanguageProcesses.Object.Token;

import java.io.*;
import java.util.*;

public class LanguageModel {
    /**
     * Initializes constants for creating a language model.
     * GRAM is the number of consecutive words to be used as a token.
     * MODEL_TABLE is a HashMap that will store the tokens and their frequency in the corpus.
     * INPUT_FILE is the file that contains the stemmed corpus.
     * OUTPUT_FILE is the file where the language model will be saved.
     * numberOfWordsInCorpus is a variable that will hold the total number of words in the corpus.
     */
    private static final int GRAM = 4;
    private static final HashMap<String, Token> MODEL_TABLE = new HashMap<>();
    private static final File INPUT_FILE = new File("Data/Corpus/StemmedCorpus.txt");
    private static final File OUTPUT_FILE = new File("Data/LanguageModel.csv");

    private static int numberOfWordsInCorpus = 0;

    public static int getGram() {return GRAM;}

    //////////////////////////////

    /**
     * The main method of the program.
     * creates a model based on the stemmed corpus and saves the model to a CSV file.
     *
     * @param args not used in this program
     * @throws IOException if there is an error reading from or writing to a file
     */
    public static void main(String[] args) throws IOException {
        createModel();
        saveModelToCsv();
    }

    /**
     * Creates a language model based on the stemmed corpus.
     * Reads the stemmed corpus line by line, splits each line into chunks of
     * GRAM consecutive words and
     * adds each chunk as a token to a HashMap. The token is the key and its value
     * is an object that holds the token's gram, count and probability in the corpus.
     *
     * @throws IOException if there is an error reading from the stemmed corpus file.
     */
    private static void createModel() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));

        String line;
        while ((line = reader.readLine()) != null) {
            ArrayList<Chunk> chunks = new ArrayList<>();

            for (int i = 1; i <= GRAM; i++) {
                ArrayList<String> arrayOfChunksAsStrings = splitStringIntoChunksOfNWords(line, i);
                for (String chunkString: arrayOfChunksAsStrings) {
                    chunks.add(new Chunk(chunkString, i));
                }
            }

            for (Chunk chunk: chunks) {
                if (chunk.numberOfWords == 1) numberOfWordsInCorpus++;

                String tokenString = chunk.text.trim();
                Token token = MODEL_TABLE.get(tokenString);

                if (token == null) {
                    token = new Token(tokenString, chunk.numberOfWords);
                    MODEL_TABLE.put(tokenString, token);

                } else {
                    token.count++;
                }
            }
        }

        reader.close();
        setProbabilities();
    }

    /**
     * Calculates the probability of each token in the language model.
     * For unigrams (tokens with a gram value of 1), the probability is calculated
     * by dividing the token's count by the total number of words in the corpus.
     * For n-grams (tokens with a gram value greater than 1), the probability is
     * calculated by dividing the token's count by the count of the n-1 gram token.
     */
    private static void setProbabilities() {
        for(Map.Entry<String, Token> entry : MODEL_TABLE.entrySet()) {
            Token token = entry.getValue();

            if (token.gram == 1)
                token.probability = token.count / (double)numberOfWordsInCorpus;

            else {
                String chunkWithoutLastWord = removeTheLastWordFromString(token.text);
                Token tokenWithoutLastWord = MODEL_TABLE.get(chunkWithoutLastWord);

                token.probability = token.count / (double)tokenWithoutLastWord.count;
            }
        }
    }

    /**
     * Saves the language model to a CSV file.
     * Writes each token of the language model as a row in the CSV file, where each
     * row consists of four columns:
     * the token's text, its gram value, its count in the corpus, and its
     * probability.
     *
     * @throws IOException if there is an error writing to the CSV file.
     */
    private static void saveModelToCsv() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE));
        for (Map.Entry<String, Token> entry : MODEL_TABLE.entrySet()) {
            Token value = entry.getValue();
            bw.write(value.text + "," + value.gram + "," + value.count + "," + value.probability);
            bw.newLine();
        }
        bw.close();
    }

    //////////////////////////////

    /**
     * Splits a string into chunks of n consecutive words.
     * For example, if the string is "I am a student from" and n is 3, the method will
     * return an ArrayList of two strings: "I am a", "am a student" and "a student from".
     *
     * @param text                   the string to be split
     * @param numberOfWordsPerString the number of consecutive words in each chunk
     * @return An ArrayList of chunks, where each chunk is a string of the specified
     *         number of words.
     */

    public static ArrayList<String> splitStringIntoChunksOfNWords(String text, int numberOfWordsPerString) {
        ArrayList<String> arrayOfChunks = new ArrayList<>();
        String[] words = text.split(" ");

        for (int i = numberOfWordsPerString; i <= words.length; i++) {
            StringBuilder chunk = new StringBuilder();
            for (int j = numberOfWordsPerString; j > 0; j--) {
                chunk.append(words[i - j]).append(" ");
            }
            arrayOfChunks.add(chunk.toString().trim());
        }

        return arrayOfChunks;
    }

    /**
     * Removes the last word from a string.
     * For example, if the string is "I am a student", the method will return "I am a".
     *
     * @param text the string from which the last word will be removed
     * @return the string without the last word
     */
    private static String removeTheLastWordFromString(String text) {
        int lastIndex = text.lastIndexOf(" ");

        if (lastIndex == -1)
            return "";

        else
            return text.substring(0, lastIndex).trim();
    }
}
