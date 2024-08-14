package ru.qimix;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.net.ServerSocket;

public class Request {
    String method;
    String path;
    String protocol;
    ServerSocket socket;
    BufferedReader in;
    BufferedOutputStream out;

    public Request(String method,
                   String path,
                   String protocol,
                   ServerSocket socket,
                   BufferedReader bufferedReader,
                   BufferedOutputStream bufferedOutputStream){
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.socket = socket;
        this.in = bufferedReader;
        this.out = bufferedOutputStream;
    }
}
