package com.bbamsch.simpleserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Stores information about an HTTP Request
 */
public class HttpRequest {
    private static final String REQUEST_DETAILS_DELIMITER = " ";
    private static final String HEADER_DELIMITER = ":";

    private HttpMethod method;
    private String path;
    private HttpProtocol protocol;
    private Map<String, String> headers = new HashMap<>();
    private InputStream body;

    /**
     * Builds an HttpRequest from a given Socket
     *
     * @param socket connection socket object
     * @throws IOException if error encountered during HttpRequest Processing
     */
    public HttpRequest(Socket socket) throws IOException {
        Scanner scanner = new Scanner(socket.getInputStream());
        processRequestDetails(scanner);
        processHeaders(scanner);
        this.body = socket.getInputStream();
    }

    private void processRequestDetails(Scanner scanner) {
        String[] requestDetails = scanner.nextLine().split(REQUEST_DETAILS_DELIMITER);
        method = HttpMethod.valueOf(requestDetails[0]);
        path = requestDetails[1];
        protocol = HttpProtocol.getByName(requestDetails[2]);
    }

    private void processHeaders(Scanner scanner) {
        if (scanner.hasNextLine()) {
            String line;
            do {
                line = scanner.nextLine();
                addHeader(line);
            } while (!line.isEmpty() && scanner.hasNextLine());
        }
    }

    private void addHeader(String header) {
        int headerDelimiterIndex = header.indexOf(HEADER_DELIMITER);
        if (headerDelimiterIndex != -1) {
            String headerKey = header.substring(0, headerDelimiterIndex).trim();
            String headerValue = header.substring(headerDelimiterIndex + 1).trim();
            headers.put(headerKey, headerValue);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getBody() {
        return body;
    }
}
