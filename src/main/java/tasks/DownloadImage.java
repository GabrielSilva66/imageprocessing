package tasks;

import utils.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DownloadImage {
    protected String url;
    protected String fileName;
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

    public DownloadImage(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public void run() {
        FileDownloader.downloadImage(url, getFilePath());
    }

    public String getFileName() { return fileName; }
    public String getFilePath() {
        return DOWNLOAD_DIR + File.separator + fileName;
    }
}
