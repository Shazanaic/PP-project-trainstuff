package org.example;

import java.util.List;

public class JsonWriterAdapter implements OutputWriter {
    private final String filename;

    public JsonWriterAdapter(String filename) {
        this.filename = filename;
    }

    @Override
    public void write(List<? extends AbstractItem> items) throws Exception {
        JsonHandler.saveToJSON(items, filename);
    }
}
