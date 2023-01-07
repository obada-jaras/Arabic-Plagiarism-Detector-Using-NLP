import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LanguageModel {
    private static int numberOfWordsInCorpus = 0;

    private static final HashMap<String, Token> MODEL_TABLE = new HashMap<>();
    private static final int GRAM = 4;
    private static final File INPUT_FILE = new File("Data/Corpus/StemmedCorpus.txt");
    private static final File OUTPUT_FILE = new File("Data/LanguageModel.csv");

    public static int getGram() {return GRAM;}

    //////////////////////////////

    public static void main(String[] args) throws IOException {
        createModel();
        saveModelToCsv();
    }
    private static void createModel() throws FileNotFoundException {
        Scanner inputScanner = new Scanner(INPUT_FILE, StandardCharsets.UTF_8.name());

        while (inputScanner.hasNextLine()) {
            String line = inputScanner.nextLine();

            ArrayList<Chunk> chunks = new ArrayList<>();

            String[] words = line.split(" ");
            for (String word: words)
                chunks.add(new Chunk(word, 1));

            for (int i = 2; i <= GRAM; i++) {
                ArrayList<String> arrayOfChunksAsStrings = splitStringIntoChunksOfNWords(line, i);
                for (String chunkString: arrayOfChunksAsStrings) {
                    chunks.add(new Chunk(chunkString, i));
                }
            }

            for (Chunk chunk: chunks) {
                if (chunk.numberOfWords == 1) numberOfWordsInCorpus++;

                String tokenString = chunk.text;
                Token token = MODEL_TABLE.get(tokenString);

                if (token == null) {
                    token = new Token(tokenString.trim(), chunk.numberOfWords);
                    MODEL_TABLE.put(tokenString, token);

                } else {
                    token.count++;
                }
            }
        }

        inputScanner.close();
        setProbabilities();
    }
    private static void setProbabilities() {
        for(Map.Entry<String, Token> entry : MODEL_TABLE.entrySet()) {
            Token token = entry.getValue();

            if (token.gram == 1)
                token.probability = token.count/ (double) numberOfWordsInCorpus;

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
            return text.substring(0, lastIndex);
    }


    // methods for development purposes
    private static void printOrderedHashMapToCSV() {
        // Create a list to hold the keys in the map
        List<String> keys = new ArrayList<>(MODEL_TABLE.keySet());
        // Sort the list of keys based on the gram value in the Model object
        Collections.sort(keys, (key1, key2) -> MODEL_TABLE.get(key1).gram - MODEL_TABLE.get(key2).gram);
        // Open a BufferedWriter to write to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            // Write the header row
            bw.write("Word,Gram,Count,Probabilities");
            bw.newLine();
            // Write each key-value pair in the sorted order
            for (String key : keys) {
                Token model = MODEL_TABLE.get(key);
                bw.write(key + "," + model.gram + "," + model.count + "," + model.probability);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    private static Token findToken(HashMap<String, Token> hashMap, String text) {
        for (Map.Entry<String, Token> entry : hashMap.entrySet()) {
            Token modelToken = entry.getValue();

            if (modelToken.text.equals(text)) return modelToken;
        }

        return null;
    }
    private static Token maxToken() {
        Token token = new Token("", 1);
        int maxCount = 0;

        for (Map.Entry<String, Token> entry : MODEL_TABLE.entrySet()) {
            Token modelToken = entry.getValue();

            if (modelToken.count > maxCount) {
                maxCount = modelToken.count;
                token = modelToken;
            }
        }

        return token;
    }
    private static void printModelTable(HashMap<String, Token> modelTable, int limit) {
        limit = Math.min(limit, modelTable.size());

        int counter = 0;

        // Print the table header
        System.out.println("Token\tNGram\tCount\tProbability");

        // Iterate over the entries in the HashMap and print each one
        for (Map.Entry<String, Token> entry : modelTable.entrySet()) {
            String token = entry.getKey();
            Token tokenData = entry.getValue();
            System.out.println(token + "\t\t" + tokenData.gram + "\t\t" + tokenData.count + "\t\t" + tokenData.probability);

            // Increment the counter
            counter++;

            // If the counter reaches 1000, break out of the loop
            if (counter == limit) {
                break;
            }
        }
    }
    private static void printModelTableSortedByCount(HashMap<String, Token> modelTable, int limit) {
        limit = Math.min(limit, modelTable.size());

        // Create a TreeMap with a custom comparator that compares the count fields of the Token objects
        TreeMap<String, Token> sortedModelTable = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String token1, String token2) {
                int count1 = modelTable.get(token1).count;
                int count2 = modelTable.get(token2).count;
                return Integer.compare(count2, count1);
            }
        });

        // Put all the entries from the modelTable HashMap into the sortedModelTable TreeMap
        sortedModelTable.putAll(modelTable);

        int counter = 0;

        // Print the table header
        System.out.println("Token\tNGram\tCount\tProbability");

        // Iterate over the entries in the TreeMap and print each one
        for (Map.Entry<String, Token> entry : sortedModelTable.entrySet()) {
            String token = entry.getKey();
            Token tokenData = entry.getValue();
            System.out.println(token + "\t" + tokenData.gram + "\t" + tokenData.count + "\t" + tokenData.probability);

            // Increment the counter
            counter++;

            // If the counter reaches 1000, break out of the loop
            if (counter == limit) {
                break;
            }
        }
    }
    private static void printModelTableSortedByGram(HashMap<String, Token> modelTable, int limit) {
        limit = Math.min(limit, modelTable.size());

        // Create a list of the entries in the HashMap
        List<Map.Entry<String, Token>> entries = new ArrayList<>(modelTable.entrySet());

        // Sort the entries using a custom comparator that compares the nGram field of the Token object
        Collections.sort(entries, (e1, e2) -> {
            Token token1 = e1.getValue();
            Token token2 = e2.getValue();
            return Integer.compare(token1.gram, token2.gram);
        });

        int counter = 0;

        // Print the table header
        System.out.println("Token\tNGram\tCount\tProbability");

        // Iterate over the sorted entries and print each one
        for (Map.Entry<String, Token> entry : entries) {
            String token = entry.getKey();
            Token tokenData = entry.getValue();
            System.out.println(token + "\t\t" + tokenData.gram + "\t\t" + tokenData.count + "\t\t" + tokenData.probability);

            // Increment the counter
            counter++;

            // If the counter reaches 1000, break out of the loop
            if (counter == limit) {
                break;
            }
        }
    }
}
