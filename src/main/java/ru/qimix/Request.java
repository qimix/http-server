package ru.qimix;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class Request {
    String requestMethod;
    String requestHeader;
    String requestBody;
    Socket socket;
    Path filePath;
    String mimeType;

    public Request(String requestMethod,
                   String requestHeader,
                   Path filePath,
                   String mimeType,
                   Socket socket) {
        requestMethod = requestMethod;
        requestHeader = requestHeader;
        filePath = filePath;
        mimeType = mimeType;
        socket = socket;
    }
    public Request(String requestMethod,
                   String requestHeader,
                   String requestBody,
                   Path filePath,
                   Files mimeType,
                   Socket socket) {
        requestMethod = requestMethod;
        requestHeader = requestHeader;
        requestBody = requestBody;
        mimeType = mimeType;
        socket = socket;
    }
}
