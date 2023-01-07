package LanguageProcesses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Plagiarism {
    private static final int GRAM = LanguageModel.getGram();
    private static final String LANGUAGE_MODEL_CSV_FILE_PATH = "Data/LanguageModel.csv";
    private static final HashMap<String, Token> LANGUAGE_MODEL = new HashMap<>();
    private static HashMap<String, String> STEMMED_WORDS_HASH = new HashMap<>();

    //////////////////////////////

    public static void main(String[] args) throws IOException {
        Corpus.readStopWordsFromFile();
        fillLanguageModelFromCsvFile();
    }

    //////////////////////////////

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
    public static void setStemmedWordsHash(String text) throws IOException {
        STEMMED_WORDS_HASH = stemTheSentence(processText(text));
    }

    public static double getPlagiarismOfSentence(String sentence) throws IOException {
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
    public static ArrayList<Token> getSentenceWordByWordWithProbability(String sentence) throws IOException {
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

    public static String processText(String text) {
        text = Corpus.normalizeText(text);
        text = Corpus.removeStopWords(text);
        text = Corpus.cleanTheLine(text);
        return text;
    }
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
    private static double curveUp(double score) {
        return (Math.log(score+1)/Math.log(10)) * 50;   //log(x+1)*50   //   https://www.desmos.com/calculator (try y=log(x+1)*50 and y=x)
    }

    private static HashSet<String> getChunksForWord(int i, String[] arrayOfWords) {
        HashSet<String> set = new HashSet<>();

        set.addAll(getChunksBefore(i, arrayOfWords));
        set.addAll(getChunksAfter(i, arrayOfWords));

        return set;
    }
    private static double getProbabilityFromLanguageModel(String text) {
        Token token = LANGUAGE_MODEL.get(text);
        return (token != null) ? token.probability : 0;
    }

    //////////////////////////////

    private static String stem(String sentence) throws IOException {
        String result = runPythonScript("src/LanguageProcesses/PythonScripts/StemSentence.py", sentence);
        return (result == null) ? "" : result;
    }

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

    //////////////////////////////

    private static String runPythonScript(String scriptFileName, String argument) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("python", scriptFileName, argument);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader.readLine();
    }
    public static int numberOfWords(String text) {
        if (text == null) return 0;
        return text.trim().split(" ").length;
    }
}
