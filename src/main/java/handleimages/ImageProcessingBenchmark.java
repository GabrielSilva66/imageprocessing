package handleimages;

import utils.FileDownloader;
import utils.FileNameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageProcessingBenchmark {

    /** Directory to store downloaded images */
    private final static String IMAGES_DIR = "images";

    /** Directory to store processed images */
    private final static String OUTPUT_DIR = "gs-images";

    private final List<String> imgUrls;

    public ImageProcessingBenchmark(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public void processSequentially(int numImages) {
        long totalFilterTime = 0;

        for (int i = 0; i < numImages; i++) {
            String url = imgUrls.get(i);
            totalFilterTime += processImage(url, i);
        }

        System.out.println("Tempo total SEQUENCIAL aplicando filtro em "
                + numImages + " imagens: " + totalFilterTime + " ms");
    }


    public void processInConcurrency(int numImages) {
        List<Thread> workers = new ArrayList<>();
        List<Long> times = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < numImages; i++) {
            final int index = i;
            final String url = imgUrls.get(i);

            Thread t = new Thread(() -> {
                long filterTime = processImage(url, index);
                times.add(filterTime);
            });
            workers.add(t);
            t.start();
        }

        for (Thread t : workers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        long totalFilterTime = times.stream().mapToLong(Long::longValue).sum();

        System.out.println("Tempo total" + numImages + " imagens: " + totalFilterTime + " ms");
    }

    private static long processImage(String url, int index) {
        try {
            String imageFile = FileNameUtils.getSafeFileName(url, IMAGES_DIR, index);
            String grayFile  = FileNameUtils.getSafeFileName(url, OUTPUT_DIR, index);

            FileDownloader.downloadImage2(url, imageFile);

            long startTime = System.currentTimeMillis();

            ImageProcessor.toGrayscale(imageFile, grayFile);

            long endTime = System.currentTimeMillis();
            long filterTime = endTime - startTime;

            System.out.println("Thread " + Thread.currentThread().getName() +
                    " aplicou filtro em " + grayFile + " (" + filterTime + " ms)");

            return filterTime;
        } catch (Exception e) {
            System.err.println("Erro na thread " + Thread.currentThread().getName() + ": " + e.getMessage());
            return 0;
        }
    }




}
