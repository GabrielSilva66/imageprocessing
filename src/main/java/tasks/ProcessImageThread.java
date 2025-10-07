package tasks;

/**
 * Thread class responsible for processing an image concurrently.
 * <p>
 * Each thread converts a downloaded image to grayscale using a {@link ProcessImage} instance.
 * </p>
 */
public class ProcessImageThread extends Thread {
    /** The image processing task. */
    protected ProcessImage processImage;
    /** The path of the downloaded image to be processed. */
    protected String downloadPath;

    /**
     * Creates a new thread for processing an image.
     *
     * @param name     the thread name
     * @param fileName the output file name for the processed image
     */
    public ProcessImageThread(String name, String fileName) {
        super(name);
        this.processImage = new ProcessImage(fileName);
    }

    /**
     * Runs the image processing task.
     * <p>
     * This method is executed when the thread starts.
     * </p>
     */
    @Override
    public void run() {
        processImage.run(downloadPath);
    }

    /**
     * Sets the path of the image to be processed.
     *
     * @param downloadPath the path of the downloaded image
     */
    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}
