package com.bbamsch.simpleserver.task;

import com.bbamsch.simpleserver.Server;
import com.bbamsch.simpleserver.http.HttpRequest;
import com.bbamsch.simpleserver.http.HttpResponse;
import com.bbamsch.simpleserver.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Socket Handler to take control of a accepted connection from Server and proceed
 */
public class SocketHandlerTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(SocketHandlerTask.class.getName());

    private final Server server;
    private final Socket socket;

    public SocketHandlerTask(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            HttpRequest httpRequest = new HttpRequest(socket);
            HttpResponse httpResponse = new HttpResponse(httpRequest);
            OutputStream outputStream = socket.getOutputStream();

            String webRoot = String.valueOf(server.getConfiguration().getOrDefault("content_root", "webroot/"));
            String urlPath = httpRequest.getPath().substring(1);

            File file = new File(webRoot, urlPath);
            if (file.isDirectory()) {
                file = new File(file, "index.html");
            }
            if (file.exists()) {
                Path filePath = FileSystems.getDefault().getPath(file.getPath());
                httpResponse.setFilePath(filePath);
                httpResponse.setStatus(HttpStatus.OK);
            } else {
                httpResponse.setStatus(HttpStatus.NOT_FOUND);
            }
            outputStream.write(httpResponse.asBytes());

            socket.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while running SocketHandlerTask", e);
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Socket cleanup is best-effort
            }
        }
    }
}
