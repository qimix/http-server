package ru.qimix;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    protected final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    protected Map<Map<String, String>, Handler> handlerMap = Collections.synchronizedMap(new HashMap<Map<String, String>, Handler>());
    protected List<String> messages = Collections.synchronizedList(new ArrayList<>());
    protected int serverPort = 9999;
    protected int counter = 0;

    public synchronized int getCounter() {
        return counter;
    }

    public synchronized void setCounter() {
        this.counter = counter++;
    }

    public void listen(int serverPort) {
        this.serverPort = serverPort;
    }

    protected Handler handler = new Handler() {
        @Override
        public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
            System.out.println("Запрос обработан: " + request.requestMethod + " - " + request.requestPath);
            final var filePath = Path.of(".", "public", request.requestPath);
            final var mimeType = Files.probeContentType(filePath);
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            responseStream.write(content);
            responseStream.flush();
            responseStream.close();
        }
    };


    protected void fillHandlerMap() {
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/index.html"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/classic.html"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/spring.svg"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/spring.png"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/resources.html"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/styles.css"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/app.js"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/links.html"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/forms.html"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/events.html"), handler);
        handlerMap.put((Map<String, String>) new HashMap<>().put("GET", "/events.js"), handler);
    }

    public synchronized void addHandler(String requestMethod, String requestPath, Handler handler) {
        System.out.println("Добавлен хендлер: " + requestMethod + " - " + requestPath);
        synchronized (handlerMap) {
            handlerMap.put((Map<String, String>) new HashMap<>().put(requestMethod, requestPath), handler);
        }
    }

    protected synchronized String getQueryParam(String site) {
        NameValuePair value = URLEncodedUtils.parse(URI.create(site), "UTF-8").get(0);
        return value.getValue();
    }

    protected synchronized void getContent(String[] parts, BufferedOutputStream out) throws IOException {
        Request request = new Request(parts[0], parts[1], parts[2]);
        Handler handler = handlerMap.get((Map<String, String>) new HashMap<>().put(parts[0], parts[1]));
        handler.handle(request, out);
    }

    public void startServer() {
        System.out.println("Server started on port: " + serverPort);
        fillHandlerMap();
        try (var serverSocket = new ServerSocket(serverPort)) {
            while (true) {
                try {
                    var socket = serverSocket.accept();
                    threadPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                final var out = new BufferedOutputStream(socket.getOutputStream());
                                final var requestLine = in.readLine();
                                final String[] parts = requestLine.split(" ");
                                String site = "http://localhost:" + serverPort + parts[1];

                                if (parts.length != 3) {
                                    return;
                                }

                                if (parts[1].contains("?")) {
                                    String params = URLEncodedUtils.parse(URI.create(site), "UTF-8").get(0).toString();
                                    StringBuilder stringBuilder = new StringBuilder(parts[1]);
                                    String reqPath = stringBuilder.delete(parts[1].length() - params.length() - 1, parts[1].length()).toString();
                                    parts[1] = reqPath;
                                }

                                final var path = parts[1];
                                if (!handlerMap.containsKey((Map<String, String>) new HashMap<>().put(parts[0], parts[1]))) {
                                    out.write((
                                            "HTTP/1.1 404 Not Found\r\n" +
                                                    "Content-Length: 0\r\n" +
                                                    "Connection: close\r\n" +
                                                    "\r\n"
                                    ).getBytes());
                                    out.flush();
                                    return;
                                } else if (parts[0].equals("POST") && parts[1].equals("/messages")) {
                                    int param = Integer.parseInt(getQueryParam(site));
                                    if (messages.contains(param)) {
                                        messages.set(param, "new message");
                                    } else {
                                        setCounter();
                                        messages.add(getCounter(), "new message");
                                    }
                                } else {
                                    getContent(parts, out);
                                }
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
}
