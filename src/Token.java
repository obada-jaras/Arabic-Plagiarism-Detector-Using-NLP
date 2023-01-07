public class Token {
    public String text;
    public int gram;
    public int count;
    public double probability;

    public Token(String text, int gram, int count, double probability) {
        this.text = text;
        this.gram = gram;
        this.count = count;
        this.probability = probability;
    }

    public Token(String text, int gram) {
        this.text = text;
        this.gram = gram;
        this.count = 1;
        this.probability = 0;
    }

    public Token(String text, double probability) {
        this.text = text;
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "Token{" +
                "text='" + text + '\'' +
                ", gram=" + gram +
                ", count=" + count +
                ", probability=" + probability +
                '}';
    }
}
