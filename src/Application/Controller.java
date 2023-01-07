package Application;

import LanguageProcesses.Plagiarism;
import LanguageProcesses.Token;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Controller {
    private static final ArrayList<String> RANDOM_TEXTS_LIST = new ArrayList<>();
    private static final String RANDOM_TEXT_FILE = "Data/RandomTexts.txt";
    private static File selectedFile;

    @FXML
    private TabPane tabPane;
    @FXML
    private Button btn_check;
    @FXML
    private Button btn_random;
    @FXML
    private Label lbl_result;
    @FXML
    private TextArea ta_sentences;
    @FXML
    private GridPane gp_result;
    @FXML
    private Label lbl_fileName;

    @FXML
    public void initialize() {
        try {
            Plagiarism.main(null);
            btn_check.setDefaultButton(true);
            readRandomTexts();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error has occurred");
            alert.setContentText(e.getMessage());
            alert.showAndWait();

            lbl_result.setText("Error: " + e.getMessage());
            lbl_result.setStyle("-fx-text-fill: red;");
            btn_check.setDisable(true);
            btn_random.setDisable(true);
        }
    }

    @FXML
    public void handleCheckButton() {
        try {
            String text = getTextFromFieldOrFile();
            if (isValidInput(text))
                throw new IllegalArgumentException("You must enter at least 5 Arabic words");

            Plagiarism.setStemmedWordsHash(text);
            String[] sentences = text.split("[.\n]");

            gp_result.getChildren().clear();
            gp_result.setPadding(new Insets(10));

            int row = 0;
            double sum = 0;
            for (String sentence: sentences) {
                if (isValidSentence(sentence)) {
                    double score = Plagiarism.getPlagiarismOfSentence(sentence);
                    Label resultLabel = createScoreLabel(score);
                    sum += score;

                    ArrayList<Token> words = Plagiarism.getSentenceWordByWordWithProbability(sentence);
                    TextFlow textFlow = createTextFlow(words);

                    gp_result.add(resultLabel, 0, row);
                    gp_result.add(textFlow, 1, row);
                    row++;
                }
            }
            double averageScores = sum / row;
            lbl_result.setText(getScoreAsText(averageScores));

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error has occurred");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    public void handleRandomButton() {
        String text = getRandomText();
        ta_sentences.setText(text);
    }
    @FXML
    public void handleSelectFileBtn() {
        FileChooser fileChooser = new FileChooser();

        if (selectedFile == null)
            fileChooser.setInitialDirectory(new File("C:\\Users\\obada\\OneDrive\\Desktop"));
        else {
            String path = selectedFile.getParent();
            fileChooser.setInitialDirectory(new File(path));
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.csv")
        );
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            selectedFile = file;
            lbl_fileName.setText(selectedFile.getName());
        }
    }
    @FXML
    public void handleOpenFileLabel() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(selectedFile);
        } catch (IOException ignored) {
        }
    }

    private void readRandomTexts() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(RANDOM_TEXT_FILE));
        String line;
        while ((line = br.readLine()) != null) {
            RANDOM_TEXTS_LIST.add(line);
        }
        br.close();
    }
    private String getTextFromFieldOrFile() throws IOException {
        int selectedTab = tabPane.getSelectionModel().getSelectedIndex();
        if (selectedTab == 0) {
            return ta_sentences.getText();
        }
        else {
            if (selectedFile == null) throw new FileNotFoundException("You must choose a file");
            return readFile(selectedFile);
        }
    }
    private String readFile(File file) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            fileContents.append(line);
            fileContents.append("\n");
        }

        bufferedReader.close();

        return fileContents.toString();
    }

    private boolean isValidInput(String text) {
        return text.length() < 10 || Plagiarism.numberOfWords(text) < 5 || Plagiarism.numberOfWords(Plagiarism.processText(text)) < 5;
    }
    private boolean isValidSentence(String sentence) {
        return sentence.length() > 5 && Plagiarism.numberOfWords(sentence) > 5;
    }
    private String getScoreAsText(double score) {
        return (score >= 10) ? String.format("%.1f%%", score) : String.format("%.2f%%", score);
    }
    private Color getColorForPercentage(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        float hue = (float)(0.33333 * (100 - percentage) / 100);
        float saturation = 1.0f;
        float value = 1.0f;

        Color color = Color.getHSBColor(hue, saturation, value);
        return color;
    }
    private String getHexFromColor(Color color) {
        int rgb = color.getRGB() & 0xffffff;
        String hex = Integer.toHexString(rgb);
        while (hex.length() < 6) {
            hex = "0" + hex;
        }
        return "#" + hex;
    }
    private String getTextColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;

        if (luminance < 0.5) {
            return "#fff";
        } else {
            return "#000";
        }
    }

    private String getRandomText() {
        Random random = new Random();
        int numStrings = random.nextInt(4) + 1; // randomly selects 1, 2, 3, or 4
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < numStrings; i++) {
            sb.append(RANDOM_TEXTS_LIST.get(random.nextInt(RANDOM_TEXTS_LIST.size())));
            if (i < numStrings - 1) {
                // randomly select a punctuation mark
                int punctuation = random.nextInt(5);
                if (punctuation == 0) {
                    sb.append(" . ");
                } else if (punctuation == 1) {
                    sb.append(" ، ");
                } else {
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    private Label createScoreLabel(double score) {
        Label label = new Label(getScoreAsText(score));
        label.setPadding(new Insets(15));
        label.getStyleClass().add("score-circle");
        Color circleColor = getColorForPercentage(score);
        String circleColorHex = getHexFromColor(circleColor);
        String textColorHex = getTextColor(circleColor);
        label.setStyle("-fx-background-color:" + circleColorHex + ";-fx-text-fill:" + textColorHex);
        return label;
    }
    private TextFlow createTextFlow(ArrayList<Token> words) {
        TextFlow textFlow = new TextFlow();
        textFlow.setPadding(new Insets(15));
        ObservableList<Node> sentenceLabels = textFlow.getChildren();

        for (Token word : words) {
            Label wordLabel = new Label(word.text + " ");

            if (word.probability >= 0.35) {
                Color wordColor = getColorForPercentage(word.probability * 100);
                String wordColorHex = getHexFromColor(wordColor);
                wordLabel.setStyle("-fx-background-color:" + wordColorHex);
            }
            sentenceLabels.add(wordLabel);
        }

        return textFlow;
    }
}
