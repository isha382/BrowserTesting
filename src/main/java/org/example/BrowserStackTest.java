package org.example;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrowserStackTest {
    private static final List<Article> articles = Collections.synchronizedList(new ArrayList<>());

    @DataProvider(name = "browserConfigs", parallel = true)
    public Object[][] browserConfigs() {
        return new Object[][]{
                {"chrome", "Windows", ""},           // Desktop: Chrome on Windows
                {"firefox", "Windows", ""},          // Desktop: Firefox on Windows
                {"safari", "OS X", ""},              // Desktop: Safari on macOS
                {"chrome", "Android", "Samsung Galaxy S22"}, // Mobile: Chrome on Android
                {"safari", "iOS", "iPhone 13"}       // Mobile: Safari on iOS
        };
    }

    @Test(dataProvider = "browserConfigs")
    public void scrapeArticles(String browser, String os, String device) throws Exception {
        WebDriver driver = null;
        String config = browser + "/" + (device.isEmpty() ? os : device);
        try {
            driver = BrowserStackConfig.getWebDriver(browser, os, device);
            System.out.println("Started WebDriver for " + config);

            List<String[]> articleLinks = ElPaisScraper.getArticleLinks();
            System.out.println("Found " + (articleLinks == null ? 0 : articleLinks.size()) + " article links for " + config);

            if (articleLinks != null && !articleLinks.isEmpty()) {
                String[] link = articleLinks.get(0);
                Article article = ElPaisScraper.scrapeArticleContent(driver, link[0], link[1]);
                System.out.println("Scraped article: " + article.getTitle() + " on " + config);
                articles.add(article);
            } else {
                System.out.println("No article links found for " + config);
            }
        } catch (Exception e) {
            System.err.println("Error in " + config + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @AfterSuite
    public void generateReport() {
        System.out.println("Articles scraped: " + articles.size());
        for (Article article : articles) {
            System.out.println("Title: " + article.getTitle());
            System.out.println("Content: " + (article.getContent().isEmpty() ? "No Content" : "Available (" + article.getContent().length() + " chars)"));
            System.out.println("Image URL: " + (article.getImageUrl().isEmpty() ? "No Image" : article.getImageUrl()));
            System.out.println();
        }
    }
}