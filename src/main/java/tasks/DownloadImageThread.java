package tasks;

public class DownloadImageThread extends Thread {
    protected DownloadImage downloadImage;

    public DownloadImageThread(String name, String url, String fileName) {
        super(name);
        this.downloadImage = new DownloadImage(url, fileName);
    }

    @Override
    public void run() {
        downloadImage.run();
    }

    public String getFileName() { return downloadImage.getFileName(); }
    public String getFilePath() {
        return downloadImage.getFilePath();
    }
}
