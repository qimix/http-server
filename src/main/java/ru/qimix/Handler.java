package ru.qimix;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

public abstract class Handler {
    public abstract void handle(Request request, BufferedOutputStream responseStream);
}
