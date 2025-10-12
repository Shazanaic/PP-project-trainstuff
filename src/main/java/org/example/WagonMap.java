package org.example;

import java.util.*;

public class WagonMap extends AbstractStorage {
    private final Map<Integer, Wagon> map = new TreeMap<>();

    @Override
    public boolean add(AbstractItem item) {
        if (!(item instanceof Wagon)) return false;
        Wagon w = (Wagon) item;
        if (map.containsKey(w.getId())) return false;
        map.put(w.getId(), w);
        return true;
    }

    @Override
    public boolean update(int id, AbstractItem newItem) {
        if (!(newItem instanceof Wagon)) return false;
        if (!map.containsKey(id)) return false;
        map.put(id, (Wagon) newItem);
        return true;
    }

    @Override
    public boolean remove(int id) { return map.remove(id) != null; }

    @Override
    public List<AbstractItem> getAll() { return new ArrayList<>(map.values()); }

    @Override
    public void clear() { map.clear(); }

    @Override
    public AbstractItem findById(int id) { return map.get(id); }

    public List<Wagon> toList() { return new ArrayList<>(map.values()); }
}
