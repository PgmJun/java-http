package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HttpResponse {
    //TODO: http상태코드 관리
    private final String httpStatus;
    private final String mimeType;
    private final int contentLength;
    private final String body;

    public HttpResponse(String httpStatus, File file) throws IOException {
        this.httpStatus = httpStatus;
        this.mimeType = Files.probeContentType(file.toPath());
        this.body = Files.readString(file.toPath());
        this.contentLength = Files.readString(file.toPath()).getBytes().length;
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
