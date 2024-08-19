package ru.qimix;

import java.io.BufferedOutputStream;
import java.io.IOException;

public abstract class Handler {
    public abstract void handle(Request request, BufferedOutputStream responseStream) throws IOException;
}
