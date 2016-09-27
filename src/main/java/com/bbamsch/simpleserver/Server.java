package com.bbamsch.simpleserver;

import com.bbamsch.simpleserver.http.HttpRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple HTTP Fileserver Proof of Concept
 * <p>
 * Provides very basic HTTP Server functionality
 * - Serve up a file given HTTP Request Details
 */
public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    // Server Configuration File Name
    private static final String SERVER_PROPERTIES_FILE = "server.properties";

    public static void main(String[] args) {
        // Load Configuration from Server Configuration File
        Properties configuration = new Properties();
        try {
            configuration.load(new FileInputStream(SERVER_PROPERTIES_FILE));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "Server configuration file missing: {0}", SERVER_PROPERTIES_FILE);
        } catch (IOException | SecurityException e) {
            LOGGER.log(Level.WARNING, "Error while reading configuration file: {0}", SERVER_PROPERTIES_FILE);
        }

        try {
            // Set up Server Socket based on Server Configuration Values
            InetAddress inetAddress = InetAddress.getByName(
                    String.valueOf(configuration.getOrDefault("ip", "0.0.0.0")));
            Integer queueLength = Integer.parseInt(
                    String.valueOf(configuration.getOrDefault("queue_length", "50")));
            Integer port = Integer.parseInt(
                    String.valueOf(configuration.getOrDefault("port", "8080")));
            ServerSocket serverSocket = new ServerSocket(port, queueLength, inetAddress);

            // Begin Server Loop
            while (true) {
                Socket socket = serverSocket.accept();

                OutputStream outputStream = socket.getOutputStream();
                outputStream.close();

                socket.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unhandled exception at top level", e);
        }
    }
}
