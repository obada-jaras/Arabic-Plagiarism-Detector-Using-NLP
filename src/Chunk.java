public class Chunk {
    String text;
    int numberOfWords;

    public Chunk(String chunk, int numberOfWords) {
        this.text = chunk;
        this.numberOfWords = numberOfWords;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "text='" + text + '\'' +
                ", numberOfWords=" + numberOfWords +
                '}';
    }
}
