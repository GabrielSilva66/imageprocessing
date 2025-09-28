import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Processes an image
 *
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public class ImageProcessor {
    /**
     * Applies grayscale transformation to an image using facilities from
     * <a href="https://github.com/openpnp/opencv">OpenCV</a>
     *
     * @param inputFile Image file to process
     * @param outputFile Resulting processed image file
     */
    public static void toGrayscale(String inputFile, String outputFile) {
        Mat image = Imgcodecs.imread(inputFile);
        Mat grayscale = new Mat();
        Imgproc.cvtColor(image, grayscale, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(outputFile, grayscale);
    }
}
