package tasks;

import utils.FileDownloader;

import java.io.File;

public class DownloadImageThread extends Thread {
    protected DownloadImage downloadImage;
    protected ProcessImageThread processImageThread;

    public DownloadImageThread(String name, String url, String fileName,
                               ProcessImageThread processImageThread) {
        super(name);
        this.downloadImage = new DownloadImage(url, fileName);
        this.processImageThread = processImageThread;
    }

    @Override
    public void run() {
        downloadImage.run();
        processImageThread.setDownloadPath(getFilePath());
        processImageThread.start();
        System.out.println("Thread " + Thread.currentThread().getName()
                + " download image in " + downloadImage.getFilePath());
    }

    public String getFilePath() {
        return downloadImage.getFilePath();
    }
}
