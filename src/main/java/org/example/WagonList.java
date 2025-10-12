package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WagonList extends AbstractStorage {
    private final List<Wagon> list = new ArrayList<>();

    @Override
    public boolean add(AbstractItem item) {
        if (!(item instanceof Wagon)) return false;
        Wagon w = (Wagon) item;
        if (findById(w.getId()) != null) return false;
        list.add(w);
        return true;
    }

    @Override
    public boolean update(int id, AbstractItem newItem) {
        if (!(newItem instanceof Wagon)) return false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                list.set(i, (Wagon) newItem);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(int id) {
        Iterator<Wagon> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().getId() == id) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AbstractItem> getAll() {
        List<AbstractItem> res = new ArrayList<>(list.size());
        res.addAll(list);
        return res;
    }

    @Override
    public void clear() { list.clear(); }

    @Override
    public AbstractItem findById(int id) {
        for (Wagon w : list) if (w.getId() == id) return w;
        return null;
    }

    public List<Wagon> getWagonList() { return list; }
}
