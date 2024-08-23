package full.service;

import full.interfaces.HttpRequest;
import full.interfaces.HttpResponse;
import full.interfaces.SimpleServlet;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CurrentTime implements SimpleServlet {
    @Override
    public void service(HttpRequest req, HttpResponse res) {
        try {
	    	Writer writer = res.getWriter();
	        writer.write("HTTP/1.1 200 OK\r\n");
	        writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
	        writer.write("\r\n");
	        
	        LocalDateTime now = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String currentTime = now.format(formatter);

	        writer.write("Current time is: " + currentTime);
	        writer.flush();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
}