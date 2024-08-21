package ru.qimix;

public class Request {
    String requestMethod;
    String requestPath;
    String requestHeader;
    String requestBody;

    public Request(String requestMethod, String requestPath, String requestHeader) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestHeader = requestHeader;
    }

    public Request(String requestMethod, String requestPath, String requestHeader, String requestBody) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }
}
