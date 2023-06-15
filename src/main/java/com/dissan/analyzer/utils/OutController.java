package com.dissan.analyzer.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class OutController {
    private final BufferedWriter out;

    public OutController(int size) {
        this.out = new BufferedWriter(new OutputStreamWriter(new
                FileOutputStream(FileDescriptor.out), StandardCharsets.US_ASCII), size);
    }

    public OutController() {
        this(512);
    }

    public void print(String m){
        try {
            this.out.write(m);
            this.out.flush();
        } catch (IOException e) {
            e.getMessage();
            System.exit(1);
        }
    }

    public void println(String m){
        this.print(m + '\n');
    }

    public void close() {
        try {
            this.out.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }
}
