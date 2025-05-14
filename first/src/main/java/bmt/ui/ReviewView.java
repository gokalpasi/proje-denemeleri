package bmt.ui;

import bmt.model.User;
import bmt.model.Word;
import bmt.service.WordService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class ReviewView {
    private final WordService wordService;
    private final User currentUser;
    private final Stage primaryStage;
    private final List<Word> words;
    private int currentWordIndex = 0;

    public ReviewView(Stage primaryStage, User user, List<Word> words) {
        this.wordService = new WordService();
        this.currentUser = user;
        this.primaryStage = primaryStage;
        this.words = words;
    }

    public void show() {
        primaryStage.setTitle("Kelime Ezberleme Oyunu - Tekrar");

        BorderPane borderPane = new BorderPane();

        // Progress Bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress((double) currentWordIndex / words.size());
        progressBar.setMaxWidth(Double.MAX_VALUE);
        borderPane.setTop(progressBar);

        // Center Content
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.CENTER);

        if (currentWordIndex < words.size()) {
            Word currentWord = words.get(currentWordIndex);
            
            Text wordText = new Text(currentWord.getEnglish());
            wordText.setFont(Font.font("Tahoma", FontWeight.BOLD, 24));

            TextField answerField = new TextField();
            answerField.setPromptText("Türkçe karşılığını yazın");
            answerField.setMaxWidth(300);

            Button checkButton = new Button("Kontrol Et");
            checkButton.setDefaultButton(true);

            Text exampleText = new Text(currentWord.getExampleSentence());
            exampleText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
            exampleText.setWrappingWidth(400);

            centerContent.getChildren().addAll(wordText, answerField, checkButton, exampleText);

            checkButton.setOnAction(e -> {
                String answer = answerField.getText().trim().toLowerCase();
                String correctAnswer = currentWord.getTurkish().toLowerCase();
                boolean isCorrect = answer.equals(correctAnswer);

                wordService.updateReviewStage(currentWord.getId(), 
                    getCurrentStage(currentWord), isCorrect);

                if (isCorrect) {
                    showAlert(Alert.AlertType.INFORMATION, "Doğru!", 
                        "Tebrikler! Doğru cevap verdiniz.");
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Yanlış", 
                        "Doğru cevap: " + currentWord.getTurkish());
                }

                currentWordIndex++;
                if (currentWordIndex < words.size()) {
                    show();
                } else {
                    showCompletionDialog();
                }
            });
        }

        borderPane.setCenter(centerContent);

        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
    }

    private int getCurrentStage(Word word) {
        for (Word.ReviewStage stage : word.getReviewStages()) {
            if (!stage.isCompleted()) {
                return stage.getStage();
            }
        }
        return 1; // Default to first stage if all are completed
    }

    private void showCompletionDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tekrar Tamamlandı");
        alert.setHeaderText(null);
        alert.setContentText("Tüm kelimeleri tekrar ettiniz!");

        alert.showAndWait();

        MainView mainView = new MainView(primaryStage, currentUser);
        mainView.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 