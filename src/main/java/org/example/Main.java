package org.example;

import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    public static void main(String[] args) {
        System.out.println("Starting web scraping...");

        // Parallel execution setup
        String[][] devices = {
                {"Chrome", "Windows", ""},
                {"Firefox", "Windows", ""},
                {"Safari", "OS X", ""},
                {"Chrome", "iOS", "iPhone 13"},
                {"Chrome", "Android", "Samsung Galaxy S22"}
        };

        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<String[]> articleLinks = ElPaisScraper.getArticleLinks();

        if (articleLinks.isEmpty()) {
            System.out.println("No articles found. Exiting...");
            return;
        }

        List<Article> articles = articleLinks.stream().limit(5).map(link -> {
            int index = articleLinks.indexOf(link);
            return executor.submit(() -> {
                WebDriver driver = null;
                try {
                    driver = BrowserStackConfig.getWebDriver(devices[index][0], devices[index][1], devices[index][2]);
                    return ElPaisScraper.scrapeArticleContent(driver, link[0], link[1]);
                } catch (Exception e) {
                    System.err.println("Error scraping: " + link[0] + " | " + e.getMessage());
                    return new Article(link[0], "", "");
                } finally {
                    if (driver != null) driver.quit();
                }
            });
        }).map(future -> {
            try {
                return future.get(); // Wait for result
            } catch (Exception e) {
                System.err.println("Error retrieving article: " + e.getMessage());
                return new Article("Unknown", "", "");
            }
        }).toList();

        executor.shutdown();

        // Display results
        for (Article article : articles) {
            System.out.println("\nTitle: " + article.getTitle());
            if (!article.getContent().isEmpty()) {
                System.out.println("Content: " + article.getContent().substring(0, Math.min(200, article.getContent().length())) + "...");
            } else {
                System.out.println("Content: [No content available]");
            }
            ImageDownloader.downloadImage(article.getImageUrl(), article.getTitle().replace(" ", "_") + ".jpg");
        }

        // Translate Titles
        List<String> spanishTitles = articles.stream().map(Article::getTitle).toList();
        Map<String, String> translatedTitles = Translator.translateTitles(spanishTitles);

        System.out.println("\nTranslated Titles:");
        translatedTitles.forEach((original, translated) ->
                System.out.println(original + " -> " + translated)
        );

        // Analyze Words
        TextAnalyzer.analyzeWords(translatedTitles.values());

        System.out.println("\nScraping & Analysis Completed!");
    }
}
