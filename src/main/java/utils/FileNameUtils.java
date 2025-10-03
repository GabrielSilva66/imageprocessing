package utils;
import java.io.File;

public class FileNameUtils {

    /**
     * Gera um nome de arquivo seguro a partir de uma URL.
     * @param url URL da imagem
     * @param outputDir Diretório onde o arquivo será salvo
     * @param index Índice da imagem (para evitar nomes duplicados)
     * @return Caminho completo do arquivo com extensão correta
     */
    public static String getSafeFileName(String url, String outputDir, int index) {
        int queryIndex = url.indexOf('?');
        String cleanUrl = queryIndex != -1 ? url.substring(0, queryIndex) : url;

        int lastDot = cleanUrl.lastIndexOf('.');
        String extension = lastDot != -1 ? cleanUrl.substring(lastDot) : ".jpg"; // padrão jpg se não houver

        return outputDir + File.separator + "image" + index + extension;
    }
}
