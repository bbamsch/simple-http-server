package com.bbamsch.simpleserver;

import com.bbamsch.simpleserver.task.SocketHandlerTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple HTTP Fileserver Proof of Concept
 * <p>
 * Provides very basic HTTP Server functionality
 * - Serve up a file given HTTP Request Details
 * <p>
 * Assumptions:
 * - All requests made to server will be HTTP/1.0 or HTTP/1.1 Requests
 */
public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    // Server Configuration File Name
    private static final String SERVER_PROPERTIES_FILE = "server.properties";

    private boolean serverOn = true;
    private Properties configuration;

    Server() throws IOException {
        // Load Configuration from Server Configuration File
        Properties configuration = new Properties();
        try {
            configuration.load(new FileInputStream(SERVER_PROPERTIES_FILE));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "Server configuration file missing: {0}", SERVER_PROPERTIES_FILE);
        } catch (IOException | SecurityException e) {
            LOGGER.log(Level.WARNING, "Error while reading configuration file: {0}", SERVER_PROPERTIES_FILE);
        }
        this.configuration = configuration;
    }

    Server(Properties configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }

    void run() {
        try {
            // Set up Server Socket based on Server Configuration Values
            InetAddress inetAddress = InetAddress.getByName(
                    configuration.getProperty("ip", "0.0.0.0"));
            Integer queueLength = Integer.parseInt(
                    configuration.getProperty("queue_length", "50"));
            Integer port = Integer.parseInt(
                    configuration.getProperty("port", "8080"));
            ServerSocket serverSocket = new ServerSocket(port, queueLength, inetAddress);

            Integer numThreads = Integer.parseInt(
                    configuration.getProperty("num_threads", "1"));

            // Begin Server Loop
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            while (serverShouldContinue()) {
                Socket socket = serverSocket.accept();
                executorService.submit(new SocketHandlerTask(this, socket));
            }
            executorService.shutdown();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception encountered in Top-Level Dispatcher", e);
        }
    }

    private boolean serverShouldContinue() {
        return serverOn;
    }

    public void shutdown() {
        serverOn = false;
    }

    public Properties getConfiguration() {
        return configuration;
    }
}
