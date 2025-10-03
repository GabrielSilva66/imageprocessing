package tasks;

import handleimages.ImageProcessor;

import java.io.File;

public class ProcessImage {
    protected String fileName;
    protected static String PROCESS_DIR = "gs-images";
    protected Long time;

    public ProcessImage(String fileName) {
        this.fileName = fileName;
    }

    public void run(String imagePath) {
        Long startTime = System.currentTimeMillis();
        ImageProcessor.toGrayscale(imagePath, getFilePath());
        Long endTime = System.currentTimeMillis();

        time = endTime - startTime;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return PROCESS_DIR + File.separator + fileName;
    }

    public Long getTime() {
        return time;
    }
}


