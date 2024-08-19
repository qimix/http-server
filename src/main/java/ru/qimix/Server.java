package ru.qimix;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    protected final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    protected final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    protected Map<Map<String,String>, Handler> handlerMap = new HashMap<Map<String,String>, Handler>();

    public void fillHandlerMap(){
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET","/classic.html"), new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                    final var template = Files.readString(request.filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + request.mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.write(content);
                    responseStream.flush();
            }
        });
    }

    public void startServer() {
        System.out.println("Server started on port: 9999");
        fillHandlerMap();
        try (final var serverSocket = new ServerSocket(9999)) {
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    threadPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                connectionProcess(socket);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connectionProcess(Socket socket) throws IOException {
        final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        final var out = new BufferedOutputStream(socket.getOutputStream());
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");

        if (parts.length != 3) {
            return;
        }

        final var path = parts[1];
        if (!validPaths.contains(path)) {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
            return;
        }

        final var filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

        // special case for classic
        Request request = new Request(parts[0], parts[1], filePath, mimeType);
        Handler handler = handlerMap.get((Map<String, String>) new HashMap<>().put("GET","/classic.html"));
        handler.handle(request,new BufferedOutputStream(socket.getOutputStream()));

        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }
}
