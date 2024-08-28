package apitest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class APITest {

    private static final String API_URL = "https://bfhldevapigw.healthrx.co.in/automation-campus/create/user";

    public static void main(String[] args) {
        // Example test cases
        testCreateUser("5", "Eve", "Adams", "1234567890", "eve.adams@test.com"); // Valid case
        testCreateUser("6", "Frank", "Miller", "1234567890", "frank.miller@test.com"); // Duplicate phone number
        testCreateUser("7", "Grace", "Lee", "9876543210", "grace.lee@test.com"); // Valid case with different phone number
        testCreateUser("8", "Heidi", "White", "9999999999", "heidi.white@test.com"); // Duplicate phone number
        testCreateUser("9", "Ivan", "Moore", "0000000000", "ivan.moore@test.com"); // Valid case with edge phone number
        testCreateUser("10", "Judy", "King", null, "judy.king@test.com");            // Missing phone number
        testCreateUser("11", null, "Lynn", "1111111111", "lynne.king@test.com");   // Missing first name
        testCreateUser(null, null, "Martin", "2222222222", "martin.jones@test.com"); // Missing roll-number and first name
        testCreateUser("13", "Nina", "Smith", "3333333333", null);                 // Missing email ID
        testCreateUser("34", "Raj", "Chopra", "2222222222", "raj.chopra@test.com"); // Valid case
        testCreateUser("35", "Sanya", "Bhatia", "3333333333", "sanya.bhatia@test.com"); // Valid case
        testCreateUser("36", "Tanishq", "Rao", "4444444444", "tanishq.rao@test.com"); // Valid case
        testCreateUser("37", "Usha", "Nair", "5555555555", "usha.nair@test.com");    // Valid case
        testCreateUser("38", "Vikram", "Arora", "6666666666", "vikram.arora@test.com"); // Valid case
        testCreateUser("39", "Yash", "Jain", "7777777777", "yash.jain@test.com");    // Valid case
        testCreateUser("40", "Zara", "Siddiqui", "8888888888", "zara.siddiqui@test.com"); // Valid case
    }

    private static void testCreateUser(String rollNumber, String firstName, String lastName, String phoneNumber, String emailId) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            if (rollNumber != null) {
                conn.setRequestProperty("roll-number", rollNumber);
            }
            conn.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"firstName\": %s, \"lastName\": %s, \"phoneNumber\": %s, \"emailId\": %s}",
                toJsonValue(firstName), toJsonValue(lastName), toJsonValue(phoneNumber), toJsonValue(emailId)
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            System.out.println("Response Code: " + code);
            
            // Read the response body
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println("Response Body: " + response.toString());
            }

            if (code == HttpURLConnection.HTTP_OK) {
                System.out.println("Success: " + conn.getResponseMessage());
            } else {
                System.out.println("Error: " + conn.getResponseMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String toJsonValue(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }
}
