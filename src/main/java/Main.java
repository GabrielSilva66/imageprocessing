import nu.pattern.OpenCV;

import java.io.File;
import java.util.List;

/**
 * Main program
 *
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public class Main {
    /** Directory to store downloaded images */
    private final static String IMAGES_DIR = "images";

    /** Directory to store processed images */
    private final static String OUTPUT_DIR = "gs-images";

    /**
     * Main method.
     * The number of images to process is provided via the command line.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        OpenCV.loadLocally();       // Loading OpenCV native libraries locally in Java

        int numimages = Integer.parseInt(args[0]);
        List<String> imageUrls = URLGenerator.generateImageURLs(numimages);

        // For each image URL, downloads the image and converts to grayscale
        for (int i = 0; i < imageUrls.size(); i++) {
            String imageFile = IMAGES_DIR + File.separator + (i + 1) + imageUrls.get(i).
                    substring(imageUrls.get(i).lastIndexOf('.'));
            String grayFile = OUTPUT_DIR + File.separator + (i + 1) + imageUrls.get(i).
                    substring(imageUrls.get(i).lastIndexOf('.'));

            FileDownloader.downloadImage(imageUrls.get(i),
                    OUTPUT_DIR + File.separator + imageFile);
            ImageProcessor.toGrayscale(imageFile, grayFile);
        }
    }
}
