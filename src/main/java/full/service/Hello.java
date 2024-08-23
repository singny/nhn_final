package full.service;

import full.interfaces.HttpRequest;
import full.interfaces.HttpResponse;
import full.interfaces.SimpleServlet;

import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Hello implements SimpleServlet {

    @Override
    public void service(HttpRequest req, HttpResponse res) {
        try {
            Writer writer = res.getWriter();
            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
            writer.write("\r\n");

            writer.write("Hello, ");
            String name = req.getParameter("name");
            if (name != null) {
                String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
                writer.write(decodedName);
            } else {
                writer.write("world");
            }
            writer.write("!");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}