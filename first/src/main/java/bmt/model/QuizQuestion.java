package bmt.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Test ekranı için bir soru ve cevap seçeneklerini tutar.
 * @param englishWord Sorulacak İngilizce kelime.
 * @param correctTurkishAnswer Doğru Türkçe cevap.
 * @param wrongAnswers Yanlış Türkçe cevaplar.
 */
public record QuizQuestion(
    String englishWord,
    String correctTurkishAnswer,
    List<String> wrongAnswers
) {
    public List<String> allTurkishOptions() {
        List<String> options = new ArrayList<>(wrongAnswers);
        options.add(correctTurkishAnswer);
        return options;
    }
} 