package org.example;

import java.util.List;

public interface OutputWriter {
    void write(List<? extends AbstractItem> items) throws Exception;
}

