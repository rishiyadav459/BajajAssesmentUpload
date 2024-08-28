import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN_Number> <JSON_File_Path>");
            return;
        }

        String prnNumber = args[0].toLowerCase().replace(" ", "");
        String jsonFilePath = args[1];

        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(jsonFilePath));
            String destinationValue = findDestinationValue(jsonElement);

            if (destinationValue != null) {
                String randomString = generateRandomString(8);
                String concatenatedString = prnNumber + destinationValue + randomString;
                String md5Hash = generateMD5Hash(concatenatedString);

                System.out.println(md5Hash + ";" + randomString);
            } else {
                System.out.println("Key 'destination' not found in the JSON file.");
            }

        } catch (FileNotFoundException e) {
            System.out.println("JSON file not found: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                if (entry.getKey().equals("destination")) {
                    return entry.getValue().getAsString();
                } else {
                    String value = findDestinationValue(entry.getValue());
                    if (value != null) {
                        return value;
                    }
                }
            }
        } else if (jsonElement.isJsonArray()) {
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                String value = findDestinationValue(element);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return stringBuilder.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}