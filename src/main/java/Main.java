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

    /**
     * Main method.
     * The number of images to process is provided via the command line.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) throws IOException {
        OpenCV.loadLocally();       // Loading OpenCV native libraries locally in Java

        List<String> imageUrls = Files.readAllLines(Paths.get("image_urls_valid.txt"));

        ImageProcessingBenchmark benchmark = new ImageProcessingBenchmark(imageUrls);
        benchmark.run();
    }
}
