package tasks;

import handleimages.ImageProcessor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class responsible for processing images, converting them to grayscale.
 * <p>
 * The processed image is saved in the default directory {@code gs-images},
 * which is automatically created if it does not exist.
 * </p>
 */
public class ProcessImage {
    /** Output file name for the processed image. */
    protected String fileName;
    /** Default directory for processed images. */
    protected static final String PROCESS_DIR = "gs-images";

    static {
        try {
            var dirPath = Paths.get(PROCESS_DIR);

            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("Diretório '" + PROCESS_DIR + "' foi criado com sucesso.");
            }
        } catch (IOException e) {
            System.err.println("Falha ao criar o diretório: " + PROCESS_DIR);
            throw new RuntimeException("Não foi possível criar o diretório de processamento.", e);
        }
    }

    /**
     * Creates a new {@code ProcessImage} object with the given file name.
     *
     * @param fileName the name of the processed image file
     */
    public ProcessImage(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Converts the given image to grayscale using {@link ImageProcessor}.
     *
     * @param imagePath the path of the original image
     */
    public void run(String imagePath) {
        ImageProcessor.toGrayscale(imagePath, getFilePath());
    }

    /**
     * Returns the full path of the processed image file.
     *
     * @return the path inside the processing directory
     */
    public String getFilePath() {
        return PROCESS_DIR + File.separator + fileName;
    }
}


