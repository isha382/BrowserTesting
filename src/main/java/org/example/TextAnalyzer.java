package org.example;
import java.util.HashMap;
import java.util.Map;

public class TextAnalyzer {
    public static void analyzeWords(Iterable<String> translatedTitles) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String title : translatedTitles) {
            String[] words = title.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");
            for (String word : words) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        System.out.println("\nRepeated Words:");
        wordCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 2)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
