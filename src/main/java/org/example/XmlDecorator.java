package org.example;

import java.util.List;

public class XmlDecorator extends OutputWriterDecorator {
    private final String filename;

    public XmlDecorator(OutputWriter inner, String filename) {
        super(inner);
        this.filename = filename;
    }

    @Override
    public void write(List<? extends AbstractItem> items) throws Exception {
        super.write(items);
        XmlHandler.saveToXML(items, filename);
    }
}
