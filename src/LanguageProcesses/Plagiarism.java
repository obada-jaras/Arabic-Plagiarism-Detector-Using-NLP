package LanguageProcesses;

import LanguageProcesses.Object.Chunk;
import LanguageProcesses.Object.Token;
import LanguageProcesses.Utils.TextBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Plagiarism {
    /**
     * Initialize the gram size used to split the text into chunks.
     * Initialize the path of the csv file that contains the language model.
     * Initialize the HashMap that will contain the language model.
     * Initialize the HashMap that will contain the stemmed words.
     */
    private static final int GRAM = LanguageModel.getGram();
    private static final String LANGUAGE_MODEL_CSV_FILE_PATH = "Data/LanguageModel.csv";
    private static final HashMap<String, Token> LANGUAGE_MODEL = new HashMap<>();
    private static HashMap<String, String> STEMMED_WORDS_HASH = new HashMap<>();

    //////////////////////////////

    /**
     * The main method of the program.
     * Reads a list of common words (stop words) from a file, saving it to the HashSet,
     * and then loads the language model from csv file.
     *
     * @param args not used in this program
     * @throws IOException if there is an error reading from a file
     */
    public static void main(String[] args) throws IOException {
        Corpus.readStopWordsFromFile();
        fillLanguageModelFromCsvFile();
    }

    //////////////////////////////

    /**
     * Reads the language model from csv file and saves it to a HashMap.
     *
     * @throws IOException if there is an error reading from the csv file.
     */
    private static void fillLanguageModelFromCsvFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(LANGUAGE_MODEL_CSV_FILE_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String tokenText = parts[0];
            int gram = Integer.parseInt(parts[1]);
            int count = Integer.parseInt(parts[2]);
            double probability = Double.parseDouble(parts[3]);
            Token token = new Token(tokenText, gram, count, probability);
            LANGUAGE_MODEL.put(tokenText, token);
        }
        reader.close();
    }

    /**
     * Set the STEMMED_WORDS_HASH variable to the stemmed version of the given text.
     * 
     * @param text the text to be stemmed
     * @throws IOException if an I/O error occurs
     */
    public static void setStemmedWordsHash(String text) throws IOException {
        STEMMED_WORDS_HASH = stemTheSentence(processText(text));
    }

    /**
     * Get the plagiarism percentage of a given sentence.
     * 
     * @param sentence the sentence to check for plagiarism
     * @return a double value representing the plagiarism percentage of the sentence
     */
    public static double getPlagiarismOfSentence(String sentence) {
        sentence = processText(sentence);
        sentence = getStemmedSentence(sentence);
        if (sentence == null || sentence.length() < 2 || !sentence.contains(" "))
            throw new IllegalArgumentException("Too short sentence");

        ArrayList<Chunk> sentenceAsChunks = splitSentenceToChunks(sentence);
        if (isAllMaxGramChunksExist(sentenceAsChunks)) return 100.0;

        double probabilitiesSum = 0;
        int weightsSum = 0;
        for (Chunk chunk: sentenceAsChunks) {
            int gram = chunk.numberOfWords;
            int weight = (gram * (gram +1)) / 2;

            Token tokenFromModel = LANGUAGE_MODEL.get(chunk.text);
            if (tokenFromModel != null) {
                double probability = tokenFromModel.probability;
                probabilitiesSum += probability * weight;
                weightsSum += weight;
            } else {
                weightsSum++;
            }
        }

        double result = 100 * (probabilitiesSum/weightsSum);
        return curveUp(result);
    }

    /**
     * Returns a list of Tokens, each representing a word in the given sentence
     * along with its probability of occurence in the language model.
     * The probability of each word is determined by finding the highest probability
     * among the word itself, as well as chunks of up to GRAM number of words before
     * and after the selected word.
     * 
     * @param sentence the sentence to process
     * @return a list of Tokens
     */
    public static ArrayList<Token> getSentenceWordByWordWithProbability(String sentence) {
        ArrayList<Token> listOfWords = new ArrayList<>();
        String[] words = sentence.split(" ");

        for (int i = 0; i < words.length; i++) {
            double maxProbability = 0;
            HashSet<String> wordChunks = getChunksForWord(i, words);    // up to 4 processed and stemmed words before and 4 after
            for (String chunk: wordChunks) {
                double probability = getProbabilityFromLanguageModel(chunk);
                maxProbability = Math.max(maxProbability, probability);
                if (maxProbability == 1.0) break;
            }
            listOfWords.add(new Token(words[i], maxProbability));
        }

        return listOfWords;
    }

    //////////////////////////////

    /**
     * Stems the given text and returns a map of the original words and their stemmed versions.
     * 
     * @param text the text to stem
     * @return a HashMap with original words as keys and stemmed words as values
     * @throws IOException if an I/O error occurs
     */
    private static HashMap<String, String> stemTheSentence(String text) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        String stemmedSentence = stem(text);

        String[] wordsBeforeStemming = text.split(" ");
        String[] wordsAfterStemming = stemmedSentence.split(" ");

        for (int i = 0; i < wordsBeforeStemming.length; i++) {
            map.put(wordsBeforeStemming[i], wordsAfterStemming[i]);
        }

        return map;
    }

    /**
     * Processes the given text by normalizing, removing stop words, and cleaning it from the 
     * words less than 3 characters.
     * 
     * @param text the text to process
     * @return the processed text
     */
    public static String processText(String text) {
        TextBuilder textBuilder = new TextBuilder(text)
                .normalizeText()
                .removeStopWords()
                .cleanTheLine();

        return textBuilder.build();
    }

    /**
     * Returns the stemmed version of the given token. If the token is a single word, it looks up
     * the stemmed version in the STEMMED_WORDS_HASH.
     * If the token contains multiple words, it gets the stem of each word from the STEMMED_WORDS_HASH
     * individually and returns the stemmed sentence.
     * 
     * @param token the token to stem
     * @return the stemmed version of the token
     */
    private static String getStemmedSentence(String token) {
        if (token == null || token.equals("")) return "";
        if (numberOfWords(token) == 1) return STEMMED_WORDS_HASH.get(token.trim());

        String[] words = token.split(" ");
        StringBuilder stemmedToken = new StringBuilder();
        for (String word: words) {
            stemmedToken.append(STEMMED_WORDS_HASH.get(word)).append(" ");
        }

        return stemmedToken.toString().trim();
    }

    /**
     * Divides the given sentence into smaller chunks of words. The number of words
     * in each chunk is determined by the GRAM variable.
     * 
     * @param sentence the sentence to divide
     * @return a list of chunks
     * 
     * Example:
     * if the GRAM is 3 and the sentence is "hello my name is Obada from"
     * chunks:
     * "hello", "my", "name", "is", "Obada", "from", "hello my", "my name",
     * "name is", "is Obada", "Obada from", "hello my name", "my name is",
     * "is Obada from"
     */
    private static ArrayList<Chunk> splitSentenceToChunks(String sentence) {
        ArrayList<Chunk> tokensAsChunks = new ArrayList<>();

        for (int i = 2; i <= GRAM; i++) {
            ArrayList<String> tokensStrings = LanguageModel.splitStringIntoChunksOfNWords(sentence, i);
            for (String tokenString: tokensStrings) {
                int numberOfWords = tokenString.split(" ").length;
                tokensAsChunks.add(new Chunk(tokenString, numberOfWords));
            }
        }

        return tokensAsChunks;
    }

    /**
     * Check if all chunks of maximum gram size (determined by the GRAM variable)
     * exist in the language model.
     * 
     * @param listOfChunks a list of chunks to check
     * @return true if all chunks of maximum gram size exist in the language model,
     *         false otherwise
     */
    private static boolean isAllMaxGramChunksExist(ArrayList<Chunk> listOfChunks) {
        for (Chunk chunk: listOfChunks) {
            if (chunk.numberOfWords == GRAM) {
                Token tokenFromModel = LANGUAGE_MODEL.get(chunk.text);
                if (tokenFromModel == null)
                    return false;
            }
        }

        return true;
    }

    /**
     * Applies a mathematical function to the given score to increase its value. The
     * function used is log(score+1) * 50.
     * 
     * @param score the score to be increased
     * @return the transformed score
     */
    private static double curveUp(double score) {
        return (Math.log(score+1)/Math.log(10)) * 50;   //log(x+1)*50   //   https://www.desmos.com/calculator (try y=log(x+1)*50 and y=x)
    }

    /**
     * Returns a set of chunks of words that contain the word at the given index,
     * along with up to GRAM number of words before and after the word.
     * 
     * @param i            the index of the word in the array
     * @param arrayOfWords an array of words
     * @return a set of chunks of words
     */
    private static HashSet<String> getChunksForWord(int i, String[] arrayOfWords) {
        HashSet<String> set = new HashSet<>();

        set.addAll(getChunksBefore(i, arrayOfWords));
        set.addAll(getChunksAfter(i, arrayOfWords));

        return set;
    }

    /**
     * Returns the probability of the given text from the language model.
     * 
     * @param text the text to look up
     * @return the probability of the text in the language model, or 0 if the text
     *         does not exist in the model
     */
    private static double getProbabilityFromLanguageModel(String text) {
        Token token = LANGUAGE_MODEL.get(text);
        return (token != null) ? token.probability : 0;
    }

    //////////////////////////////

    /**
     * Applies stemming to the given sentence using a Python script.
     * 
     * @param sentence the sentence to stem
     * @return the stemmed sentence
     * @throws IOException if an I/O error occurs
     */
    private static String stem(String sentence) throws IOException {
        String result = runPythonScript("src/LanguageProcesses/Utils/PythonScripts/StemSentence.py", sentence);
        return (result == null) ? "" : result;
    }

    /**
     * Returns a set of chunks of words that contains the word at the given index,
     * along with up to GRAM number of words before the word.
     *
     * @param i            the index of the word in the array
     * @param arrayOfWords an array of words
     * @return a set of chunks of words
     */
    private static HashSet<String> getChunksBefore(int i, String[] arrayOfWords) {
        HashSet<String> set = new HashSet<>();
        
        int numberOfWordsBefore = 1;
        int processedCount = 0;
        while (numberOfWordsBefore < GRAM - 1) {
            boolean flag = false;
            StringBuilder token = new StringBuilder();
            for (int j = i-processedCount; j <= i; j++) {
                if (j < 0) {
                    flag = true;
                    break;
                }

                token.append(arrayOfWords[j]).append(" ");
            }
            if (flag) break;

            token = new StringBuilder(processText(token.toString().trim()));
            token = new StringBuilder(getStemmedSentence(token.toString()));
            set.add(token.toString());

            processedCount++;
            numberOfWordsBefore = numberOfWords(token.toString()) - 1;
        }

        return set;
    }

    /**
     * Returns a set of chunks of words that contains the word at the given index,
     * along with up to GRAM number of words after the word.
     *
     * @param i            the index of the word in the array
     * @param arrayOfWords an array of words
     * @return a set of chunks of words
     */
    private static HashSet<String> getChunksAfter(int i, String[] arrayOfWords) {
        HashSet<String> set = new HashSet<>();

        int numberOfWordsAfter = 0;
        int processedCount = 0;
        while (numberOfWordsAfter < GRAM - 1) {
            boolean flag = false;
            StringBuilder token = new StringBuilder();
            for (int j = i; j <= i + processedCount; j++) {
                if (j >= arrayOfWords.length) {
                    flag = true;
                    break;
                }

                token.append(arrayOfWords[j]).append(" ");
            }
            if (flag) break;

            token = new StringBuilder(processText(token.toString().trim()));
            token = new StringBuilder(getStemmedSentence(token.toString()));
            set.add(token.toString());

            processedCount++;
            numberOfWordsAfter = numberOfWords(token.toString()) - 1;
        }

        return set;
    }
    // TODO: add getChunksInclude
    // TODO: for example: sentence: "I am a student from Palestine ", GRAM = 4, index = 2
    // TODO: -> "am a student", "I am a student", "am a student from"

    //////////////////////////////

    /**
     * Runs a python script with the given filename and argument, and returns the output.
     *
     * @param scriptFileName the name of the python script file
     * @param argument       the argument to pass to the python script
     * @return the output of the python script
     * @throws IOException if there is an error running the python script
     */
    private static String runPythonScript(String scriptFileName, String argument) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("python", scriptFileName, argument);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader.readLine();
    }

    /**
     * Returns the number of words in the given text.
     *
     * @param text the text to count the number of words in
     * @return the number of words in the text
     */
    public static int numberOfWords(String text) {
        if (text == null) return 0;
        return text.trim().split(" ").length;
    }
}
