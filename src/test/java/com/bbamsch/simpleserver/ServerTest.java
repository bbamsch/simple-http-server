package com.bbamsch.simpleserver;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tests the Server HTTP Responses
 */
public class ServerTest {
    /**
     * Asyncronous Executor for our HTTP Server
     * (So we can run GET Requests with our main Thread)
     */
    class AsyncServerExecutor implements Runnable {
        private Server server;

        public AsyncServerExecutor(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            server.run();
        }
    }

    @Test
    public void main() throws Exception {
        // Boot up our Server Asyncronously
        Server server = new Server();
        AsyncServerExecutor asyncServerExecutor = new AsyncServerExecutor(server);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(asyncServerExecutor);

        // Execute GET to Server w/ Valid Resource Path
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

        // Verify Response
        Assert.assertEquals("HTTP/1.1 200 OK", httpResponse.getStatusLine().toString());
        HttpEntity httpEntity = httpResponse.getEntity();
        InputStream httpContent = httpEntity.getContent();
        FileInputStream fileContent = new FileInputStream(new File("webroot/index.html"));
        Assert.assertTrue("Content Served Inaccurate", IOUtils.contentEquals(httpContent, fileContent));
        EntityUtils.consume(httpEntity);
        httpResponse.close();

        // Execute GET to Server w/ Invalid Resource Path
        httpGet = new HttpGet("http://localhost:8080/does-not.exist");
        httpResponse = httpClient.execute(httpGet);

        // Verify Response
        Assert.assertEquals("HTTP/1.1 404 Not Found", httpResponse.getStatusLine().toString());
        httpResponse.close();

        // Shutdown the Server
        server.shutdown();
        executorService.shutdown();
    }
}