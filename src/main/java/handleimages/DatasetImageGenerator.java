package handleimages;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Utility class for generating and validating image URLs.
 * <p>
 * This class can create a list of sample image URLs and save them to a file,
 * as well as check whether each URL is reachable online.
 * </p>
 */
public class DatasetImageGenerator {

    /** Number of sample images to generate. */
    private static final int IMAGE_COUNT = 2500;

    /** Output file for storing image URLs. */
    private static final String OUTPUT_FILE = "image_urls.txt";

    /**
     * Main method for generating and validating image URLs.
     *
     * @param args command-line arguments (not used)
     * @throws IOException if an I/O error occurs while writing the file
     */
    public static void main(String[] args) throws IOException {
        List<String> urls = generateUrls();
        List<String> validUrls = urls.stream()
                .filter(DatasetImageGenerator::exists)
                .toList();
        Files.write(Path.of(OUTPUT_FILE), validUrls);
        System.out.println("Generated and validated " + validUrls.size() + " URLs.");
    }

    /**
     * Generates a list of sample image URLs using Picsum Photos.
     *
     * @return a list of generated image URLs
     */
    private static List<String> generateUrls() {
        final String baseUrl = "https://picsum.photos/seed/%d/80/60";
        return IntStream.range(0, IMAGE_COUNT)
                .mapToObj(i -> String.format(baseUrl, i))
                .toList();
    }

    /**
     * Checks whether the given image URL is reachable.
     * <p>
     * Sends a lightweight HTTP request (only 1KB range) to minimize bandwidth usage.
     * </p>
     *
     * @param urlString the image URL to check
     * @return {@code true} if the URL exists or is reachable, {@code false} otherwise
     */
    private static boolean exists(String urlString) {
        try {
            URL url = new URI(urlString).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // prevent blocks
            conn.setRequestProperty("Range", "bytes=0-1024"); // only fetch 1KB

            int code = conn.getResponseCode();
            return code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL;
        } catch (IOException | URISyntaxException e) {
            return false;
        }
    }
}
