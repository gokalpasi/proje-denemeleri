package bmt.model;

import java.util.List;

/**
 * Test ekranı için bir soru ve cevap seçeneklerini tutar.
 * @param correctWord Doğru kelime nesnesi.
 * @param englishWord Sorulacak İngilizce kelime.
 * @param allTurkishOptions Karıştırılmış Türkçe seçenekler (biri doğru).
 * @param correctTurkishAnswer Doğru Türkçe cevap.
 */
public record QuizQuestion(
    Word correctWord,
    String englishWord,
    List<String> allTurkishOptions,
    String correctTurkishAnswer
) {
} 