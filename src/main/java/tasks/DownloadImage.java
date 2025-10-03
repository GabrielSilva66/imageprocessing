package tasks;

import utils.FileDownloader;

import java.io.File;

public class DownloadImage {
    protected String url;
    protected String fileName;
    protected static String DOWNLOAD_DIR = "images";

    public DownloadImage(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public void run() {
        FileDownloader.downloadImage2(url, getFilePath());
    }

    public String getFilePath() {
        return DOWNLOAD_DIR + File.separator + fileName;
    }
}
