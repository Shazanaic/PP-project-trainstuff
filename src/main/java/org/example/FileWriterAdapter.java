package org.example;

import java.io.File;
import java.util.List;

public class FileWriterAdapter implements OutputWriter {
    private final DataFileWriter writer;

    public FileWriterAdapter(File file) {
        this.writer = new DataFileWriter(file);
    }

    @Override
    public void write(List<? extends AbstractItem> items) throws Exception {
        writer.writeAll(items);
    }
}
