package com.zutubi.services.mail.rest.client;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 */
public class ConnectionConfig {

    private String protocol;
    private String baseUri;
    private Map<String, String> params;

    public static ConnectionConfig parse(String connectionUrl) {
        if (!connectionUrl.contains("://")) {
            throw new IllegalArgumentException("Invalid connection string. Missing ':'");
        }

        String protocol;
        String baseUri;
        Map<String, String> params = new HashMap<>();

        int startIndex = 0;
        String remainder = connectionUrl;

        int endIndex = remainder.indexOf("://");
        protocol = remainder.substring(startIndex, endIndex);

        startIndex = endIndex + 3;
        remainder = remainder.substring(startIndex);

        endIndex = remainder.indexOf("?");
        if (endIndex == -1) {
            baseUri = remainder;
        } else {
            baseUri = remainder.substring(startIndex, endIndex);
        }

        if (endIndex != -1) {
            startIndex = endIndex + 1;
            remainder = remainder.substring(startIndex);

            StringTokenizer tokens = new StringTokenizer(remainder, "&", false);
            while (tokens.hasMoreElements()) {
                String token = tokens.nextToken();
                if (token.contains("=")) {
                    int index = token.indexOf("=");
                    params.put(token.substring(0, index), token.substring(index + 1));
                } else {
                    params.put(token, "");
                }
            }
        }

        return new ConnectionConfig(protocol, baseUri, params);
    }

    private ConnectionConfig(String protocol, String baseUri, Map<String, String> params) {
        this.protocol = protocol;
        this.baseUri = baseUri;
        this.params = params;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getBaseURI() {
        return baseUri;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
