package utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Class to download files.
 *
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public class FileDownloader {
    /**
     * Downloads an image from its URL
     *
     * @param url URL to the image
     * @param filename Name of the file for the downloaded image
     */
    public static void downloadImage(String url, String filename) {
        try {
            FileUtils.copyURLToFile(new URI(url).toURL(), new File(filename));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error while downloading image from " + url);
            System.exit(1);
        }
    }

    public static void downloadImage2(String urlStr, String outputPath) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(outputPath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Download concluído: " + outputPath);

        } catch (IOException e) {
            System.err.println("Error while downloading image from " + urlStr);
            e.printStackTrace();
        }
    }
}
