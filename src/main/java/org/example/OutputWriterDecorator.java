package org.example;

import java.util.List;

public abstract class OutputWriterDecorator implements OutputWriter {
    protected final OutputWriter inner;

    public OutputWriterDecorator(OutputWriter inner) {
        this.inner = inner;
    }

    @Override
    public void write(List<? extends AbstractItem> items) throws Exception {
        if (inner != null)
            inner.write(items);
    }
}
