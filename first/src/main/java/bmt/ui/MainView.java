package bmt.ui;

import bmt.model.User;
import bmt.model.Word;
import bmt.service.UserService;
import bmt.service.WordService;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainView {
    private final UserService userService;
    private final WordService wordService;
    private final User currentUser;
    private final Stage primaryStage;

    public MainView(Stage primaryStage, User user) {
        this.userService = new UserService();
        this.wordService = new WordService();
        this.currentUser = user;
        this.primaryStage = primaryStage;
    }

    public void show() {
        primaryStage.setTitle("Kelime Ezberleme Oyunu - Ana Menü");

        BorderPane borderPane = new BorderPane();

        // Top Menu
        HBox topMenu = new HBox(10);
        topMenu.setPadding(new Insets(10));
        topMenu.setAlignment(Pos.CENTER_RIGHT);

        Button settingsBtn = new Button("Ayarlar");
        Button logoutBtn = new Button("Çıkış Yap");
        topMenu.getChildren().addAll(settingsBtn, logoutBtn);

        // Center Content
        VBox centerContent = new VBox(10);
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.CENTER);

        Text welcomeText = new Text("Hoş Geldiniz, " + currentUser.getUsername());
        welcomeText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        Button addWordBtn = new Button("Yeni Kelime Ekle");
        Button startTestBtn = new Button("Quize Başla");
        Button viewStatsBtn = new Button("İstatistikler");

        centerContent.getChildren().addAll(welcomeText, addWordBtn, startTestBtn, viewStatsBtn);

        // Set up the layout
        borderPane.setTop(topMenu);
        borderPane.setCenter(centerContent);

        // Event Handlers
        addWordBtn.setOnAction(e -> showAddWordDialog());
        startTestBtn.setOnAction(e -> startTestScreen());
        viewStatsBtn.setOnAction(e -> showStatistics());
        settingsBtn.setOnAction(e -> showSettings());
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            loginView.show();
        });

        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
    }

    private void showAddWordDialog() {
        Dialog<Word> dialog = new Dialog<>();
        dialog.setTitle("Yeni Kelime Ekle");
        dialog.setHeaderText("Kelime Bilgilerini Girin");

        // Create the custom dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField englishField = new TextField();
        TextField turkishField = new TextField();
        TextArea exampleField = new TextArea();
        TextField imageUrlField = new TextField();
        TextField audioUrlField = new TextField();

        grid.add(new Label("İngilizce:"), 0, 0);
        grid.add(englishField, 1, 0);
        grid.add(new Label("Türkçe:"), 0, 1);
        grid.add(turkishField, 1, 1);
        grid.add(new Label("Örnek Cümle:"), 0, 2);
        grid.add(exampleField, 1, 2);
        grid.add(new Label("Resim URL:"), 0, 3);
        grid.add(imageUrlField, 1, 3);
        grid.add(new Label("Ses URL:"), 0, 4);
        grid.add(audioUrlField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Ekle", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Word word = new Word();
                word.setEnglish(englishField.getText());
                word.setTurkish(turkishField.getText());
                word.setExampleSentence(exampleField.getText());
                word.setImageUrl(imageUrlField.getText());
                word.setAudioUrl(audioUrlField.getText());
                word.setUserId(currentUser.getId());
                return word;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(word -> {
            try {
                wordService.addWord(word);
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kelime başarıyla eklendi.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Kelime eklenirken bir hata oluştu: " + ex.getMessage());
            }
        });
    }

    private void startReview() {
        List<Word> wordsForReview = wordService.getWordsForReview(currentUser.getId());
        if (wordsForReview.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Bilgi", "Şu an için tekrar edilecek kelime bulunmuyor.");
            return;
        }

        ReviewView reviewView = new ReviewView(primaryStage, currentUser, wordsForReview);
        reviewView.show();
    }

    private void startTestScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bmt/ui/TestView.fxml"));
            Parent root = loader.load();
            Stage testStage = new Stage();
            testStage.setScene(new Scene(root));
            testStage.setTitle("Kelime Testi");
            testStage.initOwner(primaryStage);
            testStage.initModality(Modality.APPLICATION_MODAL);
            testStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Test ekranı yüklenemedi: " + e.getMessage());
        }
    }

    private void showStatistics() {
        StatisticsView statisticsView = new StatisticsView(primaryStage, currentUser);
        statisticsView.show();
    }

    private void showSettings() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Ayarlar");
        dialog.setHeaderText("Günlük Kelime Limiti");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Spinner<Integer> wordLimitSpinner = new Spinner<>(1, 100, currentUser.getDailyWordLimit());
        grid.add(new Label("Günlük Kelime Limiti:"), 0, 0);
        grid.add(wordLimitSpinner, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    userService.updateDailyWordLimit(currentUser.getId(), wordLimitSpinner.getValue());
                    currentUser.setDailyWordLimit(wordLimitSpinner.getValue());
                    showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Ayarlar kaydedildi.");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Hata", "Ayarlar kaydedilirken bir hata oluştu: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 