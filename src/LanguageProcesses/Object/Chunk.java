package LanguageProcesses.Object;

public class Chunk {
    public String text;
    public int numberOfWords;

    public Chunk(String chunk, int numberOfWords) {
        this.text = chunk;
        this.numberOfWords = numberOfWords;
    }
}
