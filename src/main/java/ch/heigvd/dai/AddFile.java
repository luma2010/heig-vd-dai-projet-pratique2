package ch.heigvd.dai;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AddFile {
    public static void addFile(String path, String name) {
        try(OutputStream os = new FileOutputStream(path);
                Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
