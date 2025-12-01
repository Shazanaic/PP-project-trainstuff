package org.example;

import java.io.File;
import java.util.List;

public class FileDecorator extends OutputWriterDecorator {
    private final File file;

    public FileDecorator(OutputWriter inner, File file) {
        super(inner);
        this.file = file;
    }

    @Override
    public void write(List<? extends AbstractItem> items) throws Exception {
        super.write(items);
        new DataFileWriter(file).writeAll(items);
    }
}
