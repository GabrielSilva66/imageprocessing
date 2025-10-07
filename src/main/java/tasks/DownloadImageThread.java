package tasks;

/**
 * Thread class responsible for downloading an image concurrently.
 * <p>
 * Each thread downloads a single image using a {@link DownloadImage} instance.
 * </p>
 */
public class DownloadImageThread extends Thread {
    /** The image download task. */
    protected DownloadImage downloadImage;

    /**
     * Creates a new thread for downloading an image.
     *
     * @param name     the thread name
     * @param url      the image URL
     * @param fileName the output file name
     */
    public DownloadImageThread(String name, String url, String fileName) {
        super(name);
        this.downloadImage = new DownloadImage(url, fileName);
    }

    /**
     * Executes the image download task.
     * <p>
     * This method runs when the thread starts.
     * </p>
     */
    @Override
    public void run() {
        downloadImage.run();
    }

    /**
     * Returns the image file name.
     *
     * @return the file name
     */
    public String getFileName() { return downloadImage.getFileName(); }

    /**
     * Returns the full file path of the downloaded image.
     *
     * @return the image file path
     */
    public String getFilePath() {
        return downloadImage.getFilePath();
    }
}
