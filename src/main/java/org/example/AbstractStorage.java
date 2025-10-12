package org.example;

import java.util.List;

public abstract class AbstractStorage {
    public abstract boolean add(AbstractItem item);
    public abstract boolean update(int id, AbstractItem newItem);
    public abstract boolean remove(int id);
    public abstract List<AbstractItem> getAll();
    public abstract void clear();
    public abstract AbstractItem findById(int id);
}
