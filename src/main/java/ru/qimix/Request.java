package ru.qimix;

import java.nio.file.Files;
import java.nio.file.Path;

public class Request {
    String requestMethod;
    String requestPath;
    String requestHeader;
    String requestBody;

    public Request(String requestMethod,
                   String requestPath,
                   String requestHeader
            ) {
        requestMethod = requestMethod;
        requestPath = requestPath;
        requestHeader = requestHeader;
    }
    public Request(String requestMethod,
                   String requestPath,
                   String requestHeader,
                   String requestBody
         ) {
        requestMethod = requestMethod;
        requestPath = requestPath;
        requestHeader = requestHeader;
        requestBody = requestBody;
    }
}
