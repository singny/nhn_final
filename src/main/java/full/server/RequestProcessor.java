package full.server;

import full.interfaces.SimpleServlet;
import full.util.ServletMapper;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestProcessor implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);
    private final Map<String, ServerConfig.HostConfig> virtualHosts;
    private final String indexFileName;
    private final Socket socket;

    public RequestProcessor(Map<String, ServerConfig.HostConfig> virtualHosts, String indexFileName, Socket socket) {
        this.virtualHosts = virtualHosts;
        this.indexFileName = indexFileName;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (OutputStream rawOut = socket.getOutputStream()) {	
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            HttpRequestImpl request = new HttpRequestImpl(reader);
            HttpResponseImpl response = new HttpResponseImpl(rawOut);

            String host = request.getHeaders().get("Host");
            
            if (host == null) {
                logger.error("Host header is missing");
                sendErrorPage(response, 400, "HTTP/1.1 400 Bad Request", "Bad Request");
                return;
            }
            if (host.contains(":")) {
                host = host.split(":")[0];
            }

            ServerConfig.HostConfig hostConfig = virtualHosts.getOrDefault(host, virtualHosts.get("default"));
            if (hostConfig == null) {
                logger.error("No configuration found for host: {}", host);
                sendErrorPage(response, 500, "HTTP/1.1 500 Internal Server Error", "Internal Server Error");
                return;
            }
            
            File rootDirectory = new File(hostConfig.rootDirectory);
            String path = request.getPath();
            File requestedFile = new File(rootDirectory, path.substring(1));
            
            // 보안 규칙: 상위 디렉터리 접근 및 .exe 파일 요청 차단
            if (path.contains("..") || path.endsWith(".exe")) {
                logger.warn("Forbidden request: {}", path);
                sendErrorPage(response, 403, "HTTP/1.1 403 Forbidden", hostConfig.errorPages.get(403));
                return;
            }

            // URL 서블릿 매핑
            String className = ServletMapper.mapUrlToClass(path);
            if (className != null) {
                try {
                    SimpleServlet servlet = (SimpleServlet) Class.forName(className).getDeclaredConstructor().newInstance();
                    servlet.service(request, response);
                    return;
                } catch (ClassNotFoundException e) {
                    logger.error("Servlet class not found: {}", className, e);
                    sendErrorPage(response, 404, "HTTP/1.1 404 Not Found", hostConfig.errorPages.get(404));
                } catch (Exception e) {
                    logger.error("Error processing servlet: {}", className, e);
                    sendErrorPage(response, 500, "HTTP/1.1 500 Internal Server Error", hostConfig.errorPages.get(500));
                }
            }

            if (requestedFile.isDirectory()) {
                requestedFile = new File(requestedFile, indexFileName);
            }
            
            if (!requestedFile.exists() || !requestedFile.canRead()) {
                logger.warn("File not found or not readable: {}", requestedFile.getPath());
                sendErrorPage(response, 404, "HTTP/1.1 404 Not Found", hostConfig.errorPages.get(404));
                return;
            }

            String contentType = URLConnection.getFileNameMap().getContentTypeFor(requestedFile.getName());
            byte[] fileData = Files.readAllBytes(requestedFile.toPath());

            response.setStatus(200);
            response.setHeader("Content-Type", contentType);
            response.setHeader("Content-Length", String.valueOf(fileData.length));
            response.htmlWrite(fileData);
            response.sendResponse();
        } catch (IOException ex) {
        	logger.error("Error processing request", ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
            	logger.error("Error closing socket", ex);
            }
        }
    }

    private void sendErrorPage(HttpResponseImpl response, int errorCode, String statusLine, String errorPagePath) throws IOException {
        File errorPage = new File(errorPagePath);
        if (errorPage.exists() && errorPage.canRead()) {
            byte[] errorData = Files.readAllBytes(errorPage.toPath());
            response.setStatus(errorCode);
            response.setHeader("Content-Type", "text/html");
            response.setHeader("Content-Length", String.valueOf(errorData.length));
            response.htmlWrite(errorData);
        } else {
            response.setStatus(errorCode);
            response.getWriter().write("<html><body><h1>" + errorCode + " Error</h1></body></html>");
        }
        response.sendResponse();
    }
}