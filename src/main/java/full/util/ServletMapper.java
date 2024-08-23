package full.util;

import java.util.HashMap;
import java.util.Map;

public class ServletMapper {

    private static final Map<String, String> urlToServletMap = new HashMap<>();

    public static void addMapping(String urlPattern, String servletClassName) {
        urlToServletMap.put(urlPattern, servletClassName);
    }

    public static String mapUrlToClass(String urlPattern) {
        return urlToServletMap.get(urlPattern);
    }

    public static void clearMappings() {
        urlToServletMap.clear();
    }

    public static String mapUrlToClassLegacy(String path) {
        if (path.matches("^/([a-zA-Z_][\\w\\.]*)$")) {    
            String inputUrl = path.substring(1);

            if (!inputUrl.startsWith("service.")) {
                return "full.service." + inputUrl.replace("/", ".");
            } else {
                return "full." + inputUrl.replace("/", ".");
            }
        } else {
            return null;
        }
    }
}