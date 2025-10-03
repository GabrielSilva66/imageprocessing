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
        System.out.println("Thread " + Thread.currentThread().getName()
                + " process image in " + processImage.getFilePath() + "(" + getTime() + " ms)");
    }

    public Long getTime() {
        return processImage.getTime();
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}
