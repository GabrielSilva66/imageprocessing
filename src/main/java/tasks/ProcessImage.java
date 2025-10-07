package tasks;

import handleimages.ImageProcessor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProcessImage {
    protected String fileName;
    protected static final String PROCESS_DIR = "gs-images";

    static {
        try {
            var dirPath = Paths.get(PROCESS_DIR);

            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("Diretório '" + PROCESS_DIR + "' foi criado com sucesso.");
            }
        } catch (IOException e) {
            System.err.println("Falha ao criar o diretório: " + PROCESS_DIR);
            throw new RuntimeException("Não foi possível criar o diretório de processamento.", e);
        }
    }

    public ProcessImage(String fileName) {
        this.fileName = fileName;
    }

    public void run(String imagePath) {
        ImageProcessor.toGrayscale(imagePath, getFilePath());
    }

    public String getFilePath() {
        return PROCESS_DIR + File.separator + fileName;
    }
}


