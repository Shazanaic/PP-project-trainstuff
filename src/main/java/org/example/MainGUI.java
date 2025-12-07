package org.example;

import org.example.gui.MenuGUI;
import java.io.File;

public class MainGUI {
    public static void main(String[] args) {
        try {
            File f = new File("wagons.txt");

            WagonList list = new WagonList();

            if (f.exists()) {
                DataFileReader r = new DataFileReader(f);
                for (Wagon w : r.readAll()) list.add(w);
            }

            new MenuGUI(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
