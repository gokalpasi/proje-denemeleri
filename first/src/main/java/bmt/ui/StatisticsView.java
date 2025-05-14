package bmt.ui;

import bmt.model.User;
import bmt.model.Word;
import bmt.service.WordService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatisticsView {
    private final WordService wordService;
    private final User currentUser;
    private final Stage primaryStage;

    public StatisticsView(Stage primaryStage, User user) {
        this.wordService = new WordService();
        this.currentUser = user;
        this.primaryStage = primaryStage;
    }

    public void show() {
        primaryStage.setTitle("Kelime Ezberleme Oyunu - İstatistikler");

        BorderPane borderPane = new BorderPane();

        // Top Menu
        HBox topMenu = new HBox(10);
        topMenu.setPadding(new Insets(10));
        topMenu.setAlignment(Pos.CENTER_RIGHT);

        Button exportPdfBtn = new Button("PDF Olarak Dışa Aktar");
        Button backBtn = new Button("Geri Dön");
        topMenu.getChildren().addAll(exportPdfBtn, backBtn);

        // Center Content
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.CENTER);

        // Create chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Aşama");
        yAxis.setLabel("Doğru Cevap Oranı (%)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Kelime Öğrenme İstatistikleri");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doğru Cevap Oranı");

        List<Word> words = wordService.getWordsForReview(currentUser.getId());
        int[] stageStats = new int[6];
        int[] totalAttempts = new int[6];

        for (Word word : words) {
            for (Word.ReviewStage stage : word.getReviewStages()) {
                int stageIndex = stage.getStage() - 1;
                stageStats[stageIndex] += stage.getCorrectAnswers();
                totalAttempts[stageIndex] += stage.getTotalAttempts();
            }
        }

        for (int i = 0; i < 6; i++) {
            double percentage = totalAttempts[i] > 0 ? 
                (double) stageStats[i] / totalAttempts[i] * 100 : 0;
            series.getData().add(new XYChart.Data<>("Aşama " + (i + 1), percentage));
        }

        chart.getData().add(series);

        // Summary statistics
        Label totalWordsLabel = new Label("Toplam Kelime Sayısı: " + words.size());
        Label completedWordsLabel = new Label("Tamamlanan Kelimeler: " + 
            words.stream().filter(w -> w.getReviewStages().stream()
                .allMatch(Word.ReviewStage::isCompleted)).count());

        centerContent.getChildren().addAll(chart, totalWordsLabel, completedWordsLabel);

        // Event Handlers
        exportPdfBtn.setOnAction(e -> exportToPdf(words));
        backBtn.setOnAction(e -> {
            MainView mainView = new MainView(primaryStage, currentUser);
            mainView.show();
        });

        borderPane.setTop(topMenu);
        borderPane.setCenter(centerContent);

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
    }

    private void exportToPdf(List<Word> words) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("PDF Kaydet");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Dosyaları", "*.pdf"));
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setInitialFileName("istatistikler_" + timestamp + ".pdf");

        try {
            java.io.File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Add title
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Paragraph title = new Paragraph("Kelime Öğrenme İstatistikleri", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                // Add user info
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
                Paragraph userInfo = new Paragraph("Kullanıcı: " + currentUser.getUsername(), normalFont);
                userInfo.setSpacingAfter(10);
                document.add(userInfo);

                // Add statistics
                Paragraph stats = new Paragraph();
                stats.add(new Chunk("Toplam Kelime Sayısı: " + words.size() + "\n", normalFont));
                stats.add(new Chunk("Tamamlanan Kelimeler: " + 
                    words.stream().filter(w -> w.getReviewStages().stream()
                        .allMatch(Word.ReviewStage::isCompleted)).count() + "\n", normalFont));
                document.add(stats);

                // Add stage statistics
                Paragraph stageStats = new Paragraph("\nAşama Bazında İstatistikler:\n", normalFont);
                int[] stageCorrect = new int[6];
                int[] stageTotal = new int[6];

                for (Word word : words) {
                    for (Word.ReviewStage stage : word.getReviewStages()) {
                        int stageIndex = stage.getStage() - 1;
                        stageCorrect[stageIndex] += stage.getCorrectAnswers();
                        stageTotal[stageIndex] += stage.getTotalAttempts();
                    }
                }

                for (int i = 0; i < 6; i++) {
                    double percentage = stageTotal[i] > 0 ? 
                        (double) stageCorrect[i] / stageTotal[i] * 100 : 0;
                    stageStats.add(new Chunk(String.format("Aşama %d: %.1f%% doğru (%d/%d)\n", 
                        i + 1, percentage, stageCorrect[i], stageTotal[i]), normalFont));
                }
                document.add(stageStats);

                document.close();

                showAlert(Alert.AlertType.INFORMATION, "Başarılı", 
                    "İstatistikler PDF olarak kaydedildi: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Hata", 
                "PDF oluşturulurken bir hata oluştu: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 