package utils;

/**
 * Utility class for generating safe and consistent image file names.
 */
public class FileNameUtils {

    /**
     * Generates a safe file name based on the given image index.
     * <p>
     * The method appends an index to avoid duplicate file names
     * and uses the <code>.jpg</code> extension by default.
     * </p>
     *
     * @param index the image index (used to avoid duplicate names)
     * @return the generated file name with the proper extension
     */
    public static String getSafeFileName(int index) {
        return "image" + index + ".jpg";
    }
}
