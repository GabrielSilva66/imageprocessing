package handleimages;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.List;

public class ImageValidator {

    private static final int MAX_SIZE = 200_000; // 200 KB

    public static void main(String[] args) throws IOException {
        List<String> urls = Files.readAllLines(Paths.get("image_urls.txt"));

        int count = 0;
        for (String url : urls) {
            count++;
            if (exists(url)) {
                Files.write(Paths.get("image_urls_valid.txt"), (url + System.lineSeparator()).getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                System.out.println("Add " + count + ": " + url);
            } else {
                System.out.println("Invalid " + count + ": " + url);
            }
        }

        System.out.println("Validação concluída. URLs válidas foram adicionadas ao arquivo.");
    }

    private static boolean exists(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // evita bloqueios
            conn.setRequestProperty("Range", "bytes=0-1024"); // só pega 1KB

            int code = conn.getResponseCode();
            return code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL;
        } catch (IOException e) {
            return false;
        }
    }


}
