package ru.qimix;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class Request {
    String requestMethod;
    String requestHeader;
    String requestBody;
    Path filePath;
    String mimeType;

    public Request(String requestMethod,
                   String requestHeader,
                   Path filePath,
                   String mimeType
            ) {
        requestMethod = requestMethod;
        requestHeader = requestHeader;
        filePath = filePath;
        mimeType = mimeType;
    }
    public Request(String requestMethod,
                   String requestHeader,
                   String requestBody,
                   Path filePath,
                   Files mimeType
         ) {
        requestMethod = requestMethod;
        requestHeader = requestHeader;
        requestBody = requestBody;
        mimeType = mimeType;
    }
}
