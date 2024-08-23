package full.server;

import full.interfaces.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseImpl implements HttpResponse {
    private final Writer writer;
    private final OutputStream outputStream;
    private final Map<String, String> headers = new HashMap<>();
    private int statusCode = 200;
    private byte[] htmlData = null;

    public HttpResponseImpl(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.writer = new OutputStreamWriter(outputStream);
    }

    @Override
    public Writer getWriter() {
        return writer;
    }

    @Override
    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
    
    public void htmlWrite(byte[] htmlData) {
    	this.htmlData = htmlData;
    }

    public void sendResponse() throws IOException {
        // 상태 라인 작성
        writer.write("HTTP/1.1 " + statusCode + " \r\n");

        // 헤더 작성
        for (Map.Entry<String, String> header : headers.entrySet()) {
            writer.write(header.getKey() + ": " + header.getValue() + "\r\n");
        }
        writer.write("\r\n");
        writer.flush();

        // errorData가 존재하는 경우 OutputStream으로 전송
        if (this.htmlData != null) {
            outputStream.write(this.htmlData);
            outputStream.flush();
        }
    }
}