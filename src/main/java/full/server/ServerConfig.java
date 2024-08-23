package full.server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
    private int port;
    private Map<String, HostConfig> hosts = new HashMap<>();

    public static class HostConfig {
        public String rootDirectory;
        public Map<Integer, String> errorPages = new HashMap<>();
    }

    public ServerConfig(String configFilePath) throws IOException {
        parseConfig(configFilePath);
    }

    private void parseConfig(String configFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonString = sb.toString();

            parseJSON(jsonString);
        }
    }

    private void parseJSON(String json) {
        json = json.trim();

        int portStartIndex = json.indexOf("\"port\":") + 7;
        int portEndIndex = json.indexOf(",", portStartIndex);
        this.port = Integer.parseInt(json.substring(portStartIndex, portEndIndex).trim());

        int hostsStartIndex = json.indexOf("\"hosts\":") + 8;
        String hostsString = json.substring(hostsStartIndex).trim();
        hostsString = hostsString.substring(1, hostsString.lastIndexOf("}")).trim();

        String[] hostEntries = hostsString.split("},");
        for (String hostEntry : hostEntries) {
            String hostName = hostEntry.substring(1, hostEntry.indexOf("\":")).trim().replace("\"", "");
            String hostConfigString = hostEntry.substring(hostEntry.indexOf("{") + 1).trim();

            HostConfig hostConfig = new HostConfig();

            int rootDirStartIndex = hostConfigString.indexOf("\"rootDirectory\":") + 16;
            int rootDirEndIndex = hostConfigString.indexOf(",", rootDirStartIndex);
            hostConfig.rootDirectory = hostConfigString.substring(rootDirStartIndex, rootDirEndIndex).trim().replace("\"", "");

            int errorPagesStartIndex = hostConfigString.indexOf("\"errorPages\":") + 13;
            String errorPagesString = hostConfigString.substring(errorPagesStartIndex).trim();
            errorPagesString = errorPagesString.substring(1, errorPagesString.lastIndexOf("}")).trim();

            String[] errorPageEntries = errorPagesString.split(",");
            for (String errorPageEntry : errorPageEntries) {
                String[] errorPageKeyValue = errorPageEntry.split(":");
                int errorCode = Integer.parseInt(errorPageKeyValue[0].trim().replace("\"", ""));
                String errorPageFile = errorPageKeyValue[1].trim().replace("\"", "").replace("}", "").replace("]", "").replace(" ", "");
                hostConfig.errorPages.put(errorCode, errorPageFile);
            }

            hosts.put(hostName, hostConfig);
        }
    }

    public int getPort() {
        return port;
    }

    public Map<String, HostConfig> getHosts() {
        return hosts;
    }

    public HostConfig getHostConfig(String host) {
        return hosts.getOrDefault(host, hosts.get("default"));
    }
}