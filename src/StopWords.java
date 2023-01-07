import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class StopWords {
    public static void main(String[] args) throws IOException {
        // Set to store unique words
        Set<String> words = new TreeSet<>();

        // Read the file and store the words in the set
        BufferedReader reader = new BufferedReader(new FileReader("Data/StopWords.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            words.add(line);
        }
        reader.close();

        // Write the words back to the file in alphabetical order (Unique)
        BufferedWriter writer = new BufferedWriter(new FileWriter("Data/StopWords.txt"));
        for (String word : words) {
            writer.write(word);
            writer.newLine();
        }
        writer.close();
    }
}
