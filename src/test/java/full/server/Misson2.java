package full.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Misson2 {

    private static final String CONFIG_FILE = "src/test/resources/config.json";

    @Test
    public void testConfigParsing() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(CONFIG_FILE)));

        int port = extractPort(content);
        assertEquals(9090, port);

        Map<String, HostConfig> hosts = extractHostConfigs(content);

        // a.com 호스트
        HostConfig aComConfig = hosts.get("a.com");
        assertEquals("src/main/resources/a_com_root", aComConfig.rootDirectory);
        assertEquals("src/main/resources/error_pages/403.html", aComConfig.errorPages.get("403"));
        assertEquals("src/main/resources/error_pages/404.html", aComConfig.errorPages.get("404"));
        assertEquals("src/main/resources/error_pages/500.html", aComConfig.errorPages.get("500"));

        // b.com 호스트
        HostConfig bComConfig = hosts.get("b.com");
        assertEquals("src/main/resources/b_com_root", bComConfig.rootDirectory);
        assertEquals("src/main/resources/error_pages/403.html", bComConfig.errorPages.get("403"));
        assertEquals("src/main/resources/error_pages/404.html", bComConfig.errorPages.get("404"));
        assertEquals("src/main/resources/error_pages/500.html", bComConfig.errorPages.get("500"));

        HostConfig defaultConfig = hosts.get("default");
        assertEquals("src/main/resources/a_com_root", defaultConfig.rootDirectory);
        assertEquals("src/main/resources/error_pages/403.html", defaultConfig.errorPages.get("403"));
        assertEquals("src/main/resources/error_pages/404.html", defaultConfig.errorPages.get("404"));
        assertEquals("src/main/resources/error_pages/500.html", defaultConfig.errorPages.get("500"));
    }

    private int extractPort(String content) {
        // 포트 번호 추출
        String portPrefix = "\"port\":";
        int portStart = content.indexOf(portPrefix) + portPrefix.length();
        int portEnd = content.indexOf(",", portStart);
        if (portEnd == -1) {
            portEnd = content.indexOf("}", portStart);
        }
        String portString = content.substring(portStart, portEnd).trim();
        return Integer.parseInt(portString);
    }

    private Map<String, HostConfig> extractHostConfigs(String content) {
        Map<String, HostConfig> hosts = new HashMap<>();
        String hostsPrefix = "\"hosts\":";
        int hostsStart = content.indexOf(hostsPrefix) + hostsPrefix.length();
        int hostsEnd = content.indexOf("}", hostsStart);
        if (hostsEnd == -1) {
            hostsEnd = content.indexOf("}", hostsStart + 1);
        }
        String hostsString = content.substring(hostsStart, hostsEnd + 1);

        String[] hostEntries = hostsString.split("\\},\\{");
        for (String hostEntry : hostEntries) {
            String hostName = extractHostName(hostEntry);
            HostConfig config = extractHostConfig(hostEntry);
            hosts.put(hostName, config);
        }

        return hosts;
    }

    private String extractHostName(String hostEntry) {
        String hostNamePrefix = "\"rootDirectory\":";
        int hostNameStart = hostEntry.indexOf("\"") + 1;
        int hostNameEnd = hostEntry.indexOf("\"", hostNameStart);
        return hostEntry.substring(hostNameStart, hostNameEnd);
    }

    private HostConfig extractHostConfig(String hostEntry) {
        HostConfig config = new HostConfig();

        // 루트 디렉토리 추출
        String rootDirPrefix = "\"rootDirectory\":";
        int rootDirStart = hostEntry.indexOf(rootDirPrefix) + rootDirPrefix.length();
        int rootDirEnd = hostEntry.indexOf("\"", rootDirStart + 1);
        config.rootDirectory = hostEntry.substring(rootDirStart + 1, rootDirEnd);

        // 오류 페이지 추출
        config.errorPages = new HashMap<>();
        String errorPagesPrefix = "\"errorPages\":";
        int errorPagesStart = hostEntry.indexOf(errorPagesPrefix) + errorPagesPrefix.length();
        int errorPagesEnd = hostEntry.indexOf("}", errorPagesStart);
        if (errorPagesEnd == -1) {
            errorPagesEnd = hostEntry.indexOf("}", errorPagesStart + 1);
        }
        String errorPagesString = hostEntry.substring(errorPagesStart, errorPagesEnd + 1);

        // 각 오류 페이지 추출
        String[] errorEntries = errorPagesString.split("\\},\\{");
        for (String errorEntry : errorEntries) {
            String[] parts = errorEntry.split(":");
            String errorCode = parts[0].replaceAll("[\"{}]", "").trim();
            String errorFile = parts[1].replaceAll("[\"{}]", "").trim();
            config.errorPages.put(errorCode, errorFile);
        }

        return config;
    }

    static class HostConfig {
        String rootDirectory;
        Map<String, String> errorPages;
    }
}