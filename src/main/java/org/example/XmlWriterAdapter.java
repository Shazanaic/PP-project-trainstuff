package org.example;

import java.util.List;

public class XmlWriterAdapter implements OutputWriter {
    private final String filename;

    public XmlWriterAdapter(String filename) {
        this.filename = filename;
    }

    @Override
    public void write(List<? extends AbstractItem> items) throws Exception {
        XmlHandler.saveToXML(items, filename);
    }
}
