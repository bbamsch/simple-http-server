package com.bbamsch.simpleserver.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for HTTP Protocol values
 */
public enum HttpProtocol {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private static Map<String, HttpProtocol> nameLookup = new HashMap<>();
    private String name;

    static {
        for (HttpProtocol httpProtocol : HttpProtocol.values()) {
            nameLookup.put(httpProtocol.getName(), httpProtocol);
        }
    }

    HttpProtocol(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

    public static HttpProtocol getByName(String name) throws IllegalArgumentException {
        if (nameLookup.containsKey(name)) {
            return nameLookup.get(name);
        }
        throw new IllegalArgumentException("HttpProtocol does not exist or not supported: " + name);
    }
}
