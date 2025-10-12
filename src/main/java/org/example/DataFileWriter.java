package org.example;

import java.io.*;
import java.util.List;

public class DataFileWriter {
    private final File file;

    public DataFileWriter(File file) { this.file = file; }

    public void writeAll(List<? extends AbstractItem> items) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (AbstractItem it : items) {
                bw.write(it.toDataString());
                bw.newLine();
            }
            bw.flush();
        }
    }
}
