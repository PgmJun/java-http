package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


//TODO: getAccept에서 text/html 등 리턴하도록 만들
public class HttpRequest {
    private HttpMethod method;
    private HttpProtocol protocol;
    private String httpVersion;
    private String path;
    private Map<String, String> queries;
    private Map<String, String> headers;

    public static HttpRequest from(BufferedReader request) {
        try {
            String[] requestLine = request.readLine().split(" ");
            HttpMethod method = HttpMethod.findByValue(requestLine[0]);
            String httpVersion = requestLine[2];
            Map<String, String> queries = getQueries(requestLine);
            String path = requestLine[1];
            String protocol = requestLine[2].split("/")[0];
            String version = requestLine[2].split("/")[1];
            HttpProtocol httpProtocol = new HttpProtocol(protocol, version);
            Map<String, String> headers = getHttpHeaders(request);

            return new HttpRequest(method, httpProtocol, httpVersion, path, queries, headers);

        } catch (IOException e) {
            throw new IllegalArgumentException("HTTP 요청 정보를 파싱하는 중에 에러가 발생했습니다.", e);
        }
    }

    private static Map<String, String> getQueries(String[] requestFirstLine) {
        Map<String, String> requestQueries = new HashMap<>();
        if (requestFirstLine[1].contains("?")) {
            int queryStartIndex = requestFirstLine[1].indexOf("?") + 1;
            String requestQueryString = requestFirstLine[1].substring(queryStartIndex);
            String[] queries = requestQueryString.split("&");

            for (String query : queries) {
                String[] q = query.split("=");
                String key = q[0];
                String value = q[1];
                requestQueries.put(key, value);
            }
            deleteQueryInPath(requestFirstLine);
        }
        return requestQueries;
    }

    private static void deleteQueryInPath(String[] requestFirstLine) {
        int queryIndex = requestFirstLine[1].indexOf("?");
        requestFirstLine[1] = requestFirstLine[1].substring(0, queryIndex);
    }

    private static Map<String, String> getHttpHeaders(BufferedReader input) throws IOException {
        Map<String, String> httpHeader = new HashMap<>();

        String line = input.readLine();
        while (!line.isEmpty()) {
            String[] header = line.split(": ");
            httpHeader.put(header[0], header[1]);

            line = input.readLine();
        }

        return httpHeader;
    }

    public HttpRequest(HttpMethod method, HttpProtocol protocol, String httpVersion, String path, Map<String, String> queries,
                       Map<String, String> headers) {
        this.method = method;
        this.protocol = protocol;
        this.httpVersion = httpVersion;
        this.path = path;
        this.queries = queries;
        this.headers = headers;
    }

    public boolean hasFilePath() {
        if (path.contains(".")) {
            int extensionIndex = path.lastIndexOf(".");
            if (path.substring(extensionIndex).length() > 1) {
                return true;
            }
        }

        return false;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public String getHeader(String header) {
        try {
            return headers.get(header);
        } catch (Exception e) {
            throw new IllegalArgumentException("'%s' Header가 존재하지 않습니다.".formatted(header));
        }
    }
}
