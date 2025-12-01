package org.example;

import java.util.List;

public class JsonDecorator extends OutputWriterDecorator {
    private final String filename;

    public JsonDecorator(OutputWriter inner, String filename) {
        super(inner);
        this.filename = filename;
    }

    @Override
    public void write(List<? extends AbstractItem> items) throws Exception {
        super.write(items);
        JsonHandler.saveToJSON(items, filename);
    }
}
