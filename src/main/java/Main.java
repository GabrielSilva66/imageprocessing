import handleimages.ImageProcessingBenchmark;
import nu.pattern.OpenCV;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


/**
 * Main program
 *
 * @author <a href="mailto:"> </a>
 */
public class Main {
    static {
        OpenCV.loadLocally();
    }

    /**
     * Main method.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) throws IOException {
        var path = Paths.get("image_urls_valid.txt");
        if (!Files.exists(path)) {
            System.err.println("Arquivo não encontrado: " + path.toAbsolutePath());
            return;
        }
        List<String> imageUrls = Files.readAllLines(path);

        ImageProcessingBenchmark benchmark = new ImageProcessingBenchmark(imageUrls);
        benchmark.run();
    }
}
