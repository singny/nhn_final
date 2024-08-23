package full.interfaces;

import java.util.Map;

public interface HttpRequest {
    String getParameter(String name);
    String getMethod();
    String getPath();
    Map<String, String> getHeaders();
}
