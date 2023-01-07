package LanguageProcesses;

import java.io.*;
import java.util.*;

public class LanguageModel {
    private static final int GRAM = 4;
    private static final HashMap<String, Token> MODEL_TABLE = new HashMap<>();
    private static final File INPUT_FILE = new File("Data/Corpus/StemmedCorpus.txt");
    private static final File OUTPUT_FILE = new File("Data/LanguageModel.csv");

    private static int numberOfWordsInCorpus = 0;

    public static int getGram() {return GRAM;}

    //////////////////////////////

    public static void main(String[] args) throws IOException {
        createModel();
        saveModelToCsv();
    }
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
    private static String removeTheLastWordFromString(String text) {
        int lastIndex = text.lastIndexOf(" ");

        if (lastIndex == -1)
            return "";

        else
            return text.substring(0, lastIndex).trim();
    }
}
