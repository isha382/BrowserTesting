package org.example;

import java.io.*;
import java.net.URL;

public class ImageDownloader {
    public static void downloadImage(String imageUrl, String fileName) {
        // Define the "images" folder inside the current directory
        String folderPath = "images";
        File directory = new File(folderPath);

        // Create the folder if it doesn't exist
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Set the full path for the image file
        File file = new File(directory, fileName);

        try (InputStream in = new URL(imageUrl).openStream();
             FileOutputStream out = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("Image saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to download image: " + e.getMessage());
        }
    }
}