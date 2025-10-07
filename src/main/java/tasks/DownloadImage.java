package tasks;

import utils.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class responsible for downloading an image from a given URL.
 * <p>
 * The image will be saved in the default directory {@code images},
 * which is automatically created if it does not exist.
 * </p>
 */
public class DownloadImage {
    /** Image URL to download. */
    protected String url;
    /** Output file name. */
    protected String fileName;
    /** Default download directory. */
    protected static final String DOWNLOAD_DIR = "images";

    static {
        try {
            var dirPath = Paths.get(DOWNLOAD_DIR);

            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("Diretório '" + DOWNLOAD_DIR + "' foi criado com sucesso.");
            }
        } catch (IOException e) {
            System.err.println("Falha ao criar o diretório: " + DOWNLOAD_DIR);
            throw new RuntimeException("Não foi possível criar o diretório de downloads.", e);
        }
    }

    /**
     * Creates a new DownloadImage object with the given URL and file name.
     *
     * @param url      the image URL
     * @param fileName the output file name
     */
    public DownloadImage(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    /**
     * Starts the image download using {@link FileDownloader}.
     */
    public void run() {
        FileDownloader.downloadImage(url, getFilePath());
    }

    /**
     * Returns the output file name.
     *
     * @return the file name
     */
    public String getFileName() { return fileName; }
    /**
     * Returns the full path of the downloaded file.
     *
     * @return the file path inside the download directory
     */
    public String getFilePath() {
        return DOWNLOAD_DIR + File.separator + fileName;
    }
}
