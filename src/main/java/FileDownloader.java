import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
            System.out.println("Downloading image from " + url + " to " + filename);
            FileUtils.copyURLToFile(new URI(url).toURL(), new File(filename));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error while downloading image from " + url);
            System.exit(1);
        }
    }
}
