package full.server;

import full.interfaces.HttpRequest;
import full.interfaces.HttpResponse;
import full.service.CurrentTime;
import full.service.Hello;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Misson6And7 {

    private Map<String, ServerConfig.HostConfig> virtualHosts;
    private String indexFileName;

    @BeforeEach
    public void setUp() {
        virtualHosts = new HashMap<>();
        
        ServerConfig.HostConfig defaultHostConfig = new ServerConfig.HostConfig();
        defaultHostConfig.rootDirectory = "src/main/resources/a_com_root";
        virtualHosts.put("localhost", defaultHostConfig);
        virtualHosts.put("default", defaultHostConfig);
        
        indexFileName = "index.html";
    }

    @Test
    public void testHelloServiceWithPathMapping() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            testServletResponse(serverSocket, "/Hello", "Hello, world!");
            testServletResponse(serverSocket, "/service.Hello", "Hello, world!");
        }
    }

    @Test
    public void testCurrentTimeServiceWithPathMapping() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            testServletResponse(serverSocket, "/CurrentTime", "Current time:");
            testServletResponse(serverSocket, "/service.CurrentTime", "Current time:");
        }
    }

    private void testServletResponse(ServerSocket serverSocket, String requestPath, String expectedContent) throws IOException {
        // Start a thread to handle the request
        Thread serverThread = new Thread(() -> {
            try {
                Socket clientSocket = serverSocket.accept();
                RequestProcessor processor = new RequestProcessor(virtualHosts, indexFileName, clientSocket);
                processor.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (Socket clientSocket = new Socket("localhost", serverSocket.getLocalPort())) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            writer.write("GET " + requestPath + " HTTP/1.1\r\n");
            writer.write("Host: localhost\r\n");
            writer.write("\r\n");
            writer.flush();

            String responseLine = reader.readLine();
            assertNotNull(responseLine);
            assertTrue(responseLine.startsWith("HTTP/1.1 200 OK"));

            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                responseBody.append(line).append("\n");
            }

            assertTrue(responseBody.toString().contains(expectedContent));
        }
    }
}