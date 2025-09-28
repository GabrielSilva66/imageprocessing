import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.types.GenerateContentResponse;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to generate image URLs.
 * This implementation utilizes generative AI, more specifically
 * <a href="https://gemini.google.com/">Google Gemini</a>, to generate image URLs.
 *
 * @author <a href="mailto:everton.cavalcante@ufrn.br">Everton Cavalcante</a>
 */
public class URLGenerator {
    /** Generative AI model to use */
    private static final String GENAI_MODEL = "gemini-2.5-flash-lite";

    /** Client object to interact with Google Gemini API */
    private static final Client client;

    /*
     Interacting with the Gemini API requires an API key. In this implementation,
     the API key is explicitly provided from a file.
     */
    static {
        String apiKey = "";
        try {
            apiKey = Files.readString(Paths.get("googleai.key")).trim();
        } catch (IOException e) {
            System.err.println("Failed to read API key from googleai.key: " + e.getMessage());
            System.exit(1);
        }

        client = Client.builder().apiKey(apiKey).build();
    }

    /**
     * Generates a list of public domain image URL from public domain image repositories
     * on the Web. The generated URLs should directly point to a valid image file in
     * either JPEG or PNG format.
     *
     * @param numimages Number of image URLs to generate
     * @return List of image URLs
     */
    public static List<String> generateImageURLs(int numimages) {
        List<String> imageUrls = new ArrayList<>();

        try {
            while (imageUrls.size() < numimages) {
                // Prompt to generate image URLs
                String generationPrompt = "Generate " + numimages + " public domain image URLs " +
                        "(either JPEG or PNG format) from trusted public domain image repositories. " +
                        "The URLs must directly point to a valid image file ending with .jpg or .png " +
                        "and the file size must be less than 200 KB. Provide the final image URLs " +
                        "in plain text.";
                GenerateContentResponse generationResponse = client.models
                        .generateContent(GENAI_MODEL, generationPrompt, null);

                // Prompt to extract the URLs from the output of the previous prompt
                String extractionPrompt = "Extract all URLs from the following contents into a " +
                        "plain text list. Each URL must be on a new line. These are the contents:\n" +
                        generationResponse.text();
                GenerateContentResponse extractionResponse = client.models
                        .generateContent(GENAI_MODEL, extractionPrompt, null);

                String[] urls = Objects.requireNonNull(extractionResponse.text()).split("\n");
                for (String url : urls) {
                    if (isAccessible(url)) imageUrls.add(url);
                    else break;
                }
            }
        } catch (ApiException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return imageUrls;
    }

    /**
     * Check if a URL is accessible by making an HTTP GET request to it
     *
     * @param url URL to check
     * @return True, if the request is successful, false otherwise
     */
    private static boolean isAccessible(String url) {
        try {
            URL urlObject = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // Set timeout to avoid long waits
            connection.setReadTimeout(5000);
            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (URISyntaxException | IOException e) {
            return false;
        }
    }
}
