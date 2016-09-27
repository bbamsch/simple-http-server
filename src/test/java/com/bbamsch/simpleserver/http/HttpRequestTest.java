package com.bbamsch.simpleserver.http;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.net.Socket;

/**
 * Created by Brian on 9/26/2016.
 */
public class HttpRequestTest {
    private static final String BASIC_HTTP_REQUEST_FILE = "src/test/resources/basic-http-request.txt";

    Socket socket;

    @Before
    public void setUp() throws Exception {
        socket = Mockito.mock(Socket.class);
        Mockito.doReturn(new FileInputStream(BASIC_HTTP_REQUEST_FILE)).when(socket).getInputStream();
    }

    @Test
    public void testConstruct() throws Exception {
        HttpRequest httpRequest = new HttpRequest(socket);
        Assert.assertEquals(HttpMethod.GET, httpRequest.getMethod());
        Assert.assertEquals("/", httpRequest.getPath());
        Assert.assertEquals(HttpProtocol.HTTP_1_1, httpRequest.getProtocol());
        Assert.assertEquals("localhost:8080", httpRequest.getHeaders().get("Host"));
    }
}