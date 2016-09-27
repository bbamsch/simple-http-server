package com.bbamsch.simpleserver;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tests the Server HTTP Responses
 */
public class ServerTest {
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
        Server server = new Server();
        AsyncServerExecutor asyncServerExecutor = new AsyncServerExecutor(server);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(asyncServerExecutor);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        try {
            Assert.assertEquals("HTTP/1.1 200 OK", httpResponse.getStatusLine());
            HttpEntity httpEntity = httpResponse.getEntity();
            EntityUtils.consume(httpEntity);
        } catch (Exception e) {
            Assert.fail("Exception thrown");
        } finally {
            httpResponse.close();
        }

        server.shutdown();
    }
}