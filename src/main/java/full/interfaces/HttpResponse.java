package full.interfaces;

import java.io.Writer;

public interface HttpResponse {
    Writer getWriter();
    void setStatus(int statusCode);
    void setHeader(String name, String value);
}
