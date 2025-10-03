package handleimages;

import tasks.DownloadImage;
import tasks.DownloadImageThread;
import tasks.ProcessImage;
import tasks.ProcessImageThread;
import utils.FileNameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public record ImageProcessingBenchmark(List<String> imgUrls) {
    private static final String FILE_NAME = "execution_times.csv";

    public void run() {
        int[] sizes = {10, 25, 50, 100, 250, 500, 1000, 2500};

        // Dados do CSV
        List<String> results = new ArrayList<>();
        results.add("images,sequential,concurrent");

        for (int size : sizes) {
            Long sequentiallyTime = runSequentially(size);
            System.out.println("Sequentially execution with " + size
                    + " images finished in " + sequentiallyTime + "ms");

            Long concurrentlyTime = runConcurrently(size);
            System.out.println("Concurrently execution with " + size
                    + " images finished in " + concurrentlyTime + "ms");

            String line = String.format("%d,%d,%d", size, sequentiallyTime, concurrentlyTime);
            results.add(line);
        }

        try {
            writeResultsToFile(results);
            System.out.println("\nResultados gravados em " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Erro ao gravar no arquivo: " + e.getMessage());
        }
    }

    private void writeResultsToFile(List<String> lines) throws IOException {
        Path filePath = Path.of(FILE_NAME);

        Files.write(
                filePath,
                lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public Long runSequentially(int numImages) {
        Long totalFilterTime = 0L;

        for (int i = 0; i < numImages; i++) {
            String url = imgUrls.get(i);
            String fileName = FileNameUtils.getSafeFileName(url, i);

            DownloadImage downloader = new DownloadImage(url, fileName);
            downloader.run();

            ProcessImage processImage = new ProcessImage(fileName);
            processImage.run(downloader.getFilePath());

            totalFilterTime += processImage.getTime();
        }

        return totalFilterTime;
    }

    public Long runConcurrently(int numImages) {
        List<DownloadImageThread> downloadThreads = new ArrayList<>();
        List<ProcessImageThread> processThreads = new ArrayList<>();

        for (int i = 0; i < numImages; i++) {
            String url = imgUrls.get(i);
            String fileName = FileNameUtils.getSafeFileName(url, i);

            String baseThreadName = "Thread " + i;
            String downloadThreadName = "Download " + baseThreadName;
            String processThreadName = "Process " + baseThreadName;

            ProcessImageThread processThread = new ProcessImageThread(processThreadName, fileName);
            DownloadImageThread downloadThread = new DownloadImageThread(
                    downloadThreadName,
                    url,
                    fileName,
                    processThread
            );

            downloadThread.start();

            downloadThreads.add(downloadThread);
            processThreads.add(processThread);
        }

        for (int i = 0; i < numImages; i++) {
            try {
                downloadThreads.get(i).join();
                processThreads.get(i).join();
            } catch (InterruptedException e) {
                System.err.println("Interrupted error in Threads " + i);
            }
        }

        Long totalFilterTime = 0L;

        for (int i = 0; i < numImages; i++) {
            totalFilterTime += processThreads.get(i).getTime();
        }

        return totalFilterTime;
    }
}
