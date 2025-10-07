package utils;

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
     * @param urlStr URL to the image
     * @param filename Name of the file for the downloaded image
     */
    public static void downloadImage(String urlStr, String filename) {
        try {
            URL url = new URI(urlStr).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(20000);

            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(filename)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Download concluído: " + filename);

        } catch (IOException | URISyntaxException e) {
            System.err.println("Error while downloading image from " + urlStr);
        }
    }
}
