package bmt.ui;

import bmt.model.QuizQuestion;
import bmt.service.TestService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestController {

    @FXML
    private VBox mainVBox;
    @FXML
    private Label lblQuestion;
    @FXML
    private Button btnOption1;
    @FXML
    private Button btnOption2;
    @FXML
    private Button btnOption3;
    @FXML
    private Button btnOption4;
    @FXML
    private Label feedbackLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button nextQuestionButton;

    private TestService testService;
    private QuizQuestion currentQuestion;
    private List<Button> optionButtons;
    private int questionsAnswered = 0;
    private int correctAnswers = 0;
    private final int DEFAULT_DIFFICULTY = 1; // Varsayılan zorluk seviyesi

    public void initialize() {
        testService = new TestService();
        optionButtons = new ArrayList<>(Arrays.asList(btnOption1, btnOption2, btnOption3, btnOption4));
        loadNewQuestion();
    }

    private void loadNewQuestion() {
        currentQuestion = testService.generateQuizQuestion(DEFAULT_DIFFICULTY);

        if (currentQuestion == null) {
            lblQuestion.setText("Soru yüklenemedi.");
            feedbackLabel.setText("Lütfen kelime ekleyin veya farklı bir zorluk seçin.");
            optionButtons.forEach(btn -> {
                btn.setVisible(false);
                btn.setDisable(true);
            });
            nextQuestionButton.setVisible(false);
            scoreLabel.setText("");
            return;
        }

        lblQuestion.setText(currentQuestion.englishWord());
        List<String> options = new ArrayList<>(currentQuestion.allTurkishOptions());
        Collections.shuffle(options);

        for (int i = 0; i < optionButtons.size(); i++) {
            if (i < options.size()) {
                optionButtons.get(i).setText(options.get(i));
                optionButtons.get(i).setVisible(true);
                optionButtons.get(i).setDisable(false);
                optionButtons.get(i).setStyle("");
            } else {
                optionButtons.get(i).setVisible(false);
                optionButtons.get(i).setDisable(true);
            }
        }

        feedbackLabel.setText("");
        feedbackLabel.setStyle("");
        nextQuestionButton.setVisible(false);
        updateScoreLabel();
    }

    @FXML
    private void handleOptionClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String selectedAnswer = clickedButton.getText();
        boolean isCorrect = selectedAnswer.equals(currentQuestion.correctTurkishAnswer());

        questionsAnswered++;

        if (isCorrect) {
            correctAnswers++;
            feedbackLabel.setText("Doğru!");
            feedbackLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            clickedButton.setStyle("-fx-base: lightgreen; -fx-font-weight: bold;");
        } else {
            feedbackLabel.setText("Yanlış! Doğru cevap: " + currentQuestion.correctTurkishAnswer());
            feedbackLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            clickedButton.setStyle("-fx-base: lightcoral; -fx-font-weight: bold;");
            optionButtons.stream()
                .filter(btn -> btn.getText().equals(currentQuestion.correctTurkishAnswer()))
                .findFirst()
                .ifPresent(btn -> btn.setStyle("-fx-base: lightgreen; -fx-font-weight: bold;"));
        }

        optionButtons.forEach(btn -> btn.setDisable(true));
        nextQuestionButton.setVisible(true);
        updateScoreLabel();
    }

    @FXML
    private void handleNextQuestion(ActionEvent event) {
        loadNewQuestion();
    }

    private void updateScoreLabel() {
        scoreLabel.setText(String.format("Skor: %d/%d", correctAnswers, questionsAnswered));
    }
    
    // Bu metodu MainView veya benzeri bir yerden çağırarak Test ekranını gösterebilirsiniz.
    /*
    public static void showTestScreen(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(TestController.class.getResource("/bmt/ui/TestView.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Kelime Testi");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
} 