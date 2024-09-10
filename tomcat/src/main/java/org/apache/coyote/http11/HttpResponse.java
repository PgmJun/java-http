package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HttpResponse {
    //TODO: http상태코드 관리
    private String httpStatus;
    private String mimeType;
    private int contentLength;
    private String body;

    public HttpResponse() {
    }

    public void setResponse(String httpStatus, File file) throws IOException {
        this.httpStatus = httpStatus;
        this.mimeType = Files.probeContentType(file.toPath());
        this.body = new String(Files.readAllBytes(file.toPath()));
        this.contentLength = Files.readAllBytes(file.toPath()).length;
    }

    public void setResponse(String httpStatus, String body) {
        this.httpStatus = httpStatus;
        this.mimeType = "text/html";
        this.body = body;
        this.contentLength = body.getBytes().length;
    }

    public String toMessage() {
        final var response = String.join("\r\n",
                "HTTP/1.1 %s ".formatted(httpStatus),
                "Content-Type: %s;charset=utf-8 ".formatted(mimeType),
                "Content-Length: " + contentLength + " ",
                "",
                body);

        return response;
    }
}
