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

public record ImageProcessingBenchmark(List<String> imgUrls) {
    public void run() {
        int[] sizes = {10, 25, 50, 100, 250, 500, 1000, 2500};

        // Nome do arquivo
        final String FILE_NAME = "resultados.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // Cabeçalho
            writer.write("images,sequential,concurrent,limited-concurrent-4,limited-concurrent-8,limited-concurrent-16");
            writer.newLine();

            for (int size : sizes) {
                Long sequentiallyTime = runSequentially(size);
                System.out.println("Sequentially execution with " + size + " images finished in " +
                        sequentiallyTime + "ms");

                Long concurrentlyTime = runConcurrently(size);
                System.out.println("Concurrently execution with " + size + " images finished in " +
                        concurrentlyTime + "ms");

                Long limited4ConcurrentlyTime = runLimitedConcurrently(size, 4);
                System.out.println("Limited (4) concurrently execution with " + size +
                        " images finished in " + limited4ConcurrentlyTime + "ms");

                Long limited8ConcurrentlyTime = runLimitedConcurrently(size, 8);
                System.out.println("Limited (8) concurrently execution with " + size +
                        " images finished in " + limited8ConcurrentlyTime + "ms");

                Long limited16ConcurrentlyTime = runLimitedConcurrently(size, 16);
                System.out.println("Limited (16) concurrently execution with " + size +
                        " images finished in " + limited16ConcurrentlyTime + "ms");

                String line = String.format("%d,%d,%d,%d,%d,%d", size, sequentiallyTime, concurrentlyTime,
                        limited4ConcurrentlyTime, limited8ConcurrentlyTime, limited16ConcurrentlyTime);
                writer.write(line);
                writer.newLine();
                writer.flush();
            }

            System.out.println("\nResultados gravados em " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Erro ao gravar no arquivo: " + e.getMessage());
        }
    }

    public Long runSequentially(int numImages) {
        // List of fileName and filePath
        List<Pair<String, String>> files = new ArrayList<>();

        // Download
        for (int i = 0; i < numImages; i++) {
            String url = imgUrls.get(i);
            String fileName = FileNameUtils.getSafeFileName(url, i);

            DownloadImage downloader = new DownloadImage(url, fileName);
            downloader.run();

            files.add(new Pair<>(fileName, downloader.getFilePath()));
        }

        // Initialize time
        Long startTime = System.currentTimeMillis();

        // Process
        for (int i = 0; i < numImages; i++) {
            Pair<String, String> file = files.get(i);
            String fileName = file.getFirst();
            String filePath = file.getSecond();

            ProcessImage processImage = new ProcessImage(fileName);
            processImage.run(filePath);
        }

        // Ended time
        Long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    public Long runConcurrently(int numImages) {
        List<DownloadImageThread> downloadThreads = new ArrayList<>();
        List<Pair<String, String>> files = new ArrayList<>(); // <fileName, filePath>

        // Create download threads
        for (int i = 0; i < numImages; i++) {
            String url = imgUrls.get(i);
            String fileName = FileNameUtils.getSafeFileName(url, i);
            String downloadThreadName = "Download Thread " + i;

            DownloadImageThread downloadThread = new DownloadImageThread(downloadThreadName, url, fileName);
            downloadThread.start();
            downloadThreads.add(downloadThread);
        }

        // Wait download threads and save filename and filepath
        for (int i = 0; i < numImages; i++) {
            try {
                DownloadImageThread thread = downloadThreads.get(i);
                thread.join();
                String filePath = thread.getFilePath();
                String fileName = thread.getFileName();
                files.add(new Pair<>(fileName, filePath));
            } catch (InterruptedException e) {
                System.err.println("Interrupted error in Download Thread " + i);
            }
        }

        // Initialize time
        long startTime = System.currentTimeMillis();

        // Start process threads
        List<ProcessImageThread> processThreads = getProcessImageThreads(numImages, files);

        // Wait process threads finished
        for (int i = 0; i < numImages; i++) {
            try {
                processThreads.get(i).join();
            } catch (InterruptedException e) {
                System.err.println("Interrupted error in Process Thread " + i);
            }
        }

        // Ended time
        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    @NotNull
    private static List<ProcessImageThread> getProcessImageThreads(int numImages, List<Pair<String, String>> files) {
        List<ProcessImageThread> processThreads = new ArrayList<>();

        for (int i = 0; i < numImages; i++) {
            Pair<String, String> file = files.get(i);
            String fileName = file.getFirst();
            String downloadPath = file.getSecond();

            String processThreadName = "Process Thread " + i;
            ProcessImageThread processThread = new ProcessImageThread(processThreadName, fileName);
            processThread.setDownloadPath(downloadPath);

            processThread.start();
            processThreads.add(processThread);
        }

        return processThreads;
    }

    public Long runLimitedConcurrently(int numImages, int threadsLimit) {
        List<Pair<String, String>> files = new ArrayList<>(); // <fileName, filePath>

        // Download threads
        for (int i = 0; i < numImages; i += threadsLimit) {
            List<DownloadImageThread> downloadThreads = new ArrayList<>();
            int upperBound = Math.min(i + threadsLimit, numImages);

            // Initialize download threads
            for (int j = i; j < upperBound; j++) {
                String url = imgUrls.get(j);
                String fileName = FileNameUtils.getSafeFileName(url, j);
                String downloadThreadName = "Download Thread " + j;

                DownloadImageThread downloadThread = new DownloadImageThread(downloadThreadName, url, fileName);
                downloadThread.start();
                downloadThreads.add(downloadThread);
            }

            // Wait download threads
            for (int j = i; j < upperBound; j++) {
                try {
                    DownloadImageThread thread = downloadThreads.get(j - i);
                    thread.join();
                    String filePath = thread.getFilePath();
                    String fileName = thread.getFileName();
                    files.add(new Pair<>(fileName, filePath));
                } catch (InterruptedException e) {
                    System.err.println("Interrupted error in Download Thread " + j);
                }
            }
        }

        // Initilize time
        long startTime = System.currentTimeMillis();

        // Process images
        for (int i = 0; i < numImages; i += threadsLimit) {
            List<ProcessImageThread> processThreads = new ArrayList<>();
            int upperBound = Math.min(i + threadsLimit, numImages);

            // Started process threads
            for (int j = i; j < upperBound; j++) {
                Pair<String, String> file = files.get(j);
                String fileName = file.getFirst();
                String downloadPath = file.getSecond();
                String processThreadName = "Process Thread " + j;

                ProcessImageThread processThread = new ProcessImageThread(processThreadName, fileName);
                processThread.setDownloadPath(downloadPath);
                processThread.start(); // supondo que start() internamente chame run(filePath)
                processThreads.add(processThread);
            }

            // Wait process threads
            for (int j = 0; j < processThreads.size(); j++) {
                try {
                    processThreads.get(j).join();
                } catch (InterruptedException e) {
                    System.err.println("Interrupted error in Process Thread " + (i + j));
                }
            }
        }

        // Ended time
        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }
}
