package handleimages;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import tasks.DownloadImage;
import tasks.DownloadImageThread;
import tasks.ProcessImage;
import tasks.ProcessImageThread;
import utils.FileNameUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Benchmark class for testing image download and processing performance.
 * <p>
 * This class supports multiple execution modes:
 * <ul>
 *     <li>Sequential execution</li>
 *     <li>Concurrent execution with unlimited threads</li>
 *     <li>Concurrent execution with a limited number of threads (4, 8, 16, etc.)</li>
 * </ul>
 * The results are saved into a CSV file ("resultados.csv") for analysis.
 * </p>
 */
public record ImageProcessingBenchmark(List<String> imgUrls) {

    /**
     * Runs the benchmark for multiple image batch sizes and writes the results to a CSV file.
     */
    public void run() {
        int[] sizes = {10, 25, 50, 100, 250, 500, 1000, 2500};
        final String FILE_NAME = "resultados.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("images,sequential,concurrent,limited-concurrent-4,limited-concurrent-8,limited-concurrent-16");
            writer.newLine();

            for (int size : sizes) {
                Long sequentiallyTime = runSequentially(size);
                System.out.println("Sequentially execution with " + size + " images finished in " + sequentiallyTime + "ms");

                Long concurrentlyTime = runConcurrently(size);
                System.out.println("Concurrently execution with " + size + " images finished in " + concurrentlyTime + "ms");

                Long limited4ConcurrentlyTime = runLimitedConcurrently(size, 4);
                System.out.println("Limited (4) concurrently execution with " + size + " images finished in " + limited4ConcurrentlyTime + "ms");

                Long limited8ConcurrentlyTime = runLimitedConcurrently(size, 8);
                System.out.println("Limited (8) concurrently execution with " + size + " images finished in " + limited8ConcurrentlyTime + "ms");

                Long limited16ConcurrentlyTime = runLimitedConcurrently(size, 16);
                System.out.println("Limited (16) concurrently execution with " + size + " images finished in " + limited16ConcurrentlyTime + "ms");

                String line = String.format("%d,%d,%d,%d,%d,%d", size, sequentiallyTime, concurrentlyTime,
                        limited4ConcurrentlyTime, limited8ConcurrentlyTime, limited16ConcurrentlyTime);
                writer.write(line);
                writer.newLine();
                writer.flush();
            }

            System.out.println("\nResults saved in " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    /**
     * Runs the download and processing sequentially for the given number of images.
     *
     * @param numImages number of images to process
     * @return execution time in milliseconds
     */
    public Long runSequentially(int numImages) {
        List<Pair<String, String>> files = new ArrayList<>();

        // Download images
        for (int i = 0; i < numImages; i++) {
            String url = imgUrls.get(i);
            String fileName = FileNameUtils.getSafeFileName(i);
            DownloadImage downloader = new DownloadImage(url, fileName);
            downloader.run();
            files.add(new Pair<>(fileName, downloader.getFilePath()));
        }

        long startTime = System.currentTimeMillis();

        // Process images
        for (Pair<String, String> file : files) {
            ProcessImage processImage = new ProcessImage(file.getFirst());
            processImage.run(file.getSecond());
        }

        return System.currentTimeMillis() - startTime;
    }

    /**
     * Runs download and processing concurrently with unlimited threads.
     *
     * @param numImages number of images to process
     * @return execution time in milliseconds
     */
    public Long runConcurrently(int numImages) {
        List<DownloadImageThread> downloadThreads = new ArrayList<>();
        List<Pair<String, String>> files = new ArrayList<>();

        for (int i = 0; i < numImages; i++) {
            String url = imgUrls.get(i);
            String fileName = FileNameUtils.getSafeFileName(i);
            DownloadImageThread downloadThread = new DownloadImageThread("Download Thread " + i, url, fileName);
            downloadThread.start();
            downloadThreads.add(downloadThread);
        }

        for (int i = 0; i < numImages; i++) {
            try {
                DownloadImageThread thread = downloadThreads.get(i);
                thread.join();
                files.add(new Pair<>(thread.getFileName(), thread.getFilePath()));
            } catch (InterruptedException e) {
                System.err.println("Interrupted error in Download Thread " + i);
            }
        }

        long startTime = System.currentTimeMillis();
        List<ProcessImageThread> processThreads = getProcessImageThreads(numImages, files);

        for (ProcessImageThread thread : processThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Interrupted error in Process Thread");
            }
        }

        return System.currentTimeMillis() - startTime;
    }

    /**
     * Creates and starts threads for image processing.
     *
     * @param numImages number of images
     * @param files     list of pairs containing file name and file path
     * @return list of started process threads
     */
    @NotNull
    private static List<ProcessImageThread> getProcessImageThreads(int numImages, List<Pair<String, String>> files) {
        List<ProcessImageThread> processThreads = new ArrayList<>();
        for (int i = 0; i < numImages; i++) {
            Pair<String, String> file = files.get(i);
            ProcessImageThread thread = new ProcessImageThread("Process Thread " + i, file.getFirst());
            thread.setDownloadPath(file.getSecond());
            thread.start();
            processThreads.add(thread);
        }
        return processThreads;
    }

    /**
     * Runs download and processing concurrently with a limited number of threads.
     *
     * @param numImages   number of images
     * @param threadsLimit maximum number of concurrent threads
     * @return execution time in milliseconds
     */
    public Long runLimitedConcurrently(int numImages, int threadsLimit) {
        List<Pair<String, String>> files = new ArrayList<>();

        // Download in blocks
        for (int i = 0; i < numImages; i += threadsLimit) {
             List<DownloadImageThread> downloadThreads = getDownloadImageThreads(numImages, threadsLimit, i);

            for (int j = 0; j < downloadThreads.size(); j++) {
                try {
                    DownloadImageThread thread = downloadThreads.get(j);
                    thread.join();
                    files.add(new Pair<>(thread.getFileName(), thread.getFilePath()));
                } catch (InterruptedException e) {
                    System.err.println("Interrupted error in Download Thread " + (i + j));
                }
            }
        }

        long startTime = System.currentTimeMillis();

        // Process in blocks
        for (int i = 0; i < numImages; i += threadsLimit) {
            List<ProcessImageThread> processThreads = new ArrayList<>();
            int upperBound = Math.min(i + threadsLimit, numImages);
            for (int j = i; j < upperBound; j++) {
                Pair<String, String> file = files.get(j);
                ProcessImageThread thread = new ProcessImageThread("Process Thread " + j, file.getFirst());
                thread.setDownloadPath(file.getSecond());
                thread.start();
                processThreads.add(thread);
            }

            for (ProcessImageThread thread : processThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.err.println("Interrupted error in Process Thread");
                }
            }
        }

        return System.currentTimeMillis() - startTime;
    }

    /**
     * Creates and starts a list of download threads for a subset of images.
     *
     * @param numImages    total number of images
     * @param threadsLimit maximum number of concurrent threads for this batch
     * @param i   starting index of the images for this batch
     * @return a list of started {@link DownloadImageThread} objects
     */
    @NotNull
    private List<DownloadImageThread> getDownloadImageThreads(int numImages, int threadsLimit, int i) {
        List<DownloadImageThread> downloadThreads = new ArrayList<>();
        int upperBound = Math.min(i + threadsLimit, numImages);
        for (int j = i; j < upperBound; j++) {
            String url = imgUrls.get(j);
            String fileName = FileNameUtils.getSafeFileName(j);
            DownloadImageThread downloadThread = new DownloadImageThread("Download Thread " + j, url, fileName);
            downloadThread.start();
            downloadThreads.add(downloadThread);
        }
        return downloadThreads;
    }
}
