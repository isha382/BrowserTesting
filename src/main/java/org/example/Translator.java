package org.example;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for translating titles using RapidAPI Translate Multi Traduction.
 */
public class Translator {
    private static final String API_URL = "https://rapid-translate-multi-traduction.p.rapidapi.com/t";
    private static final String API_KEY = "b79eeb942emsh9876ebed8d0e392p13e89ejsna7cd451e46c9"; // Replace with your actual RapidAPI key

    /**
     * Translates a list of Spanish titles to English.
     *
     * @param spanishTitles List of Spanish titles to translate
     * @return Map with Spanish titles as keys and English translations as values
     * @throws IllegalArgumentException if the input list is null or empty
     */
    public static Map<String, String> translateTitles(List<String> spanishTitles) {
        if (spanishTitles == null || spanishTitles.isEmpty()) {
            throw new IllegalArgumentException("Spanish titles list cannot be null or empty");
        }

        Map<String, String> translatedTitles = new HashMap<>();
        for (String title : spanishTitles) {
            if (title != null && !title.trim().isEmpty()) {
                translatedTitles.put(title, translate(title, "es", "en"));
            } else {
                translatedTitles.put(title, "[Invalid Title]");
            }
        }
        return translatedTitles;
    }

    /**
     * Translates a single text from source language to target language using RapidAPI.
     *
     * @param text       Text to translate
     * @param sourceLang Source language code (e.g., "es" for Spanish)
     * @param targetLang Target language code (e.g., "en" for English)
     * @return Translated text or an error message if translation fails
     */
    private static String translate(String text, String sourceLang, String targetLang) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("X-RapidAPI-Key", API_KEY);
            request.setHeader("X-RapidAPI-Host", "rapid-translate-multi-traduction.p.rapidapi.com");

            // Build JSON request body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("from", sourceLang);
            jsonBody.put("to", targetLang);
            jsonBody.put("q", text);  // Use "q" instead of "text"
            request.setEntity(new StringEntity(jsonBody.toString(), StandardCharsets.UTF_8));

            // Execute request and process response
            try (CloseableHttpResponse response = httpClient.execute(request);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Print API response for debugging
                System.out.println("API Response: " + result.toString());

                // Parse JSON response correctly (API returns an array)
                JSONArray results = new JSONArray(result.toString());
                if (results.length() > 0) {
                    return results.getString(0);  // Fix: Get the first element from array
                } else {
                    return "[No Translation Returned]";
                }
            }
        } catch (Exception e) {
            System.err.println("Translation failed for text: " + text + ". Error: " + e.getMessage());
            return "[Translation Failed]";
        }
    }

}