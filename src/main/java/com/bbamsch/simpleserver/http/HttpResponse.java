package com.bbamsch.simpleserver.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Response Object to hold all necessary details to construct a server response
 */
public class HttpResponse {
    private final HttpProtocol protocol;
    private final Map<String, String> headers = new HashMap<>();
    private HttpStatus status;
    private Path filePath;

    public HttpResponse(HttpRequest request) {
        this.protocol = request.getProtocol();
        this.status = HttpStatus.OK;
        this.filePath = null;
    }

    public byte[] asBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write((protocol.getName() + " " + status.getCode() + " " + status.getMessage() + '\n').getBytes());
        for (String headerKey : headers.keySet()) {
            byteArrayOutputStream.write((headerKey + ":" + headers.get(headerKey) + '\n').getBytes());
        }
        byteArrayOutputStream.write('\n');
        if (filePath != null) {
            Files.copy(filePath, byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
}
