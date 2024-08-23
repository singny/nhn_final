package full.server;

import org.junit.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class Misson1 {

    @Test
    public void testAComResponse() throws Exception {
        String url = "http://a.com";
        String expectedResponse = "This is a.com";

        String actualResponse = sendHttpGetRequest(url);

        assertEquals("Response from a.com did not match expected output", expectedResponse, actualResponse.trim());
    }

    @Test
    public void testBComResponse() throws Exception {
        String url = "http://b.com";
        String expectedResponse = "This is b.com";

        String actualResponse = sendHttpGetRequest(url);

        assertEquals("Response from b.com did not match expected output", expectedResponse, actualResponse.trim());
    }

    private String sendHttpGetRequest(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        } finally {
            connection.disconnect();
        }
    }
}