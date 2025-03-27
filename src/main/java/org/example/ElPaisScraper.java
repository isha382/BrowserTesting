package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElPaisScraper {

    public static List<String[]> getArticleLinks() {
        List<String[]> articleLinks = new ArrayList<>();
        try {
            WebDriver driver = BrowserStackConfig.getWebDriver("Chrome", "Windows", "");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            driver.get("https://elpais.com/opinion/");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2.c_t a")));

            List<WebElement> articles = driver.findElements(By.cssSelector("h2.c_t a"));
            int count = Math.min(articles.size(), 5);

            for (int i = 0; i < count; i++) {
                WebElement articleElement = articles.get(i);
                String title = articleElement.getText().trim();
                String url = articleElement.getAttribute("href");

                if (!title.isEmpty() && url != null && url.startsWith("https")) {
                    articleLinks.add(new String[]{title, url});
                }
            }
            driver.quit();
        } catch (Exception e) {
            System.err.println("Error collecting article links: " + e.getMessage());
        }
        return articleLinks;
    }

    public static Article scrapeArticleContent(WebDriver driver, String title, String url) {
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        StringBuilder content = new StringBuilder();
        String[] selectors = {"article div.e-a_c p", "article p", "div[itemprop='articleBody'] p", "p"};

        for (String selector : selectors) {
            List<WebElement> paragraphs = driver.findElements(By.cssSelector(selector));
            if (!paragraphs.isEmpty()) {
                for (WebElement p : paragraphs) {
                    content.append(p.getText()).append("\n");
                }
                break;
            }
        }

        String imageUrl = "";
        try {
            WebElement image = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("figure img")));
            imageUrl = image.getAttribute("src");
        } catch (Exception ignored) {}

        return new Article(title, content.toString(), imageUrl);
    }
}
