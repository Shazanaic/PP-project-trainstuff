package org.example.gui;

import org.example.Wagon;
import org.example.WagonList;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class WagonTableModel extends AbstractTableModel {

    private final String[] columns = {
            "ID", "Type", "Model", "Capacity",
            "Seats", "Release date", "Price"
    };

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("dd:MM:yy", Locale.US);

    private final WagonList storage;

    public WagonTableModel(WagonList storage) {
        this.storage = storage;
    }

    @Override
    public int getRowCount() {
        return storage.getWagonList().size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Wagon w = storage.getWagonList().get(row);

        return switch (col) {
            case 0 -> w.getId();
            case 1 -> w.getType();
            case 2 -> w.getModel();
            case 3 -> w.getCapacity();
            case 4 -> w.getSeats();
            case 5 -> (w.getReleaseDate() == null ? "" : DATE_FMT.format(w.getReleaseDate()));
            case 6 -> w.getPrice();
            default -> "";
        };
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
