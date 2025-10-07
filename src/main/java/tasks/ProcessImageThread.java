package tasks;

public class ProcessImageThread extends Thread {
    protected ProcessImage processImage;
    protected String downloadPath;

    public ProcessImageThread(String name, String fileName) {
        super(name);
        this.processImage = new ProcessImage(fileName);
    }

    @Override
    public void run() {
        processImage.run(downloadPath);
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}
