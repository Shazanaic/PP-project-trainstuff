package org.example;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        try {
            File inputFile = new File("wagons.txt");
            String mode = "interactive";

            WagonList list = new WagonList();
            WagonMap map = new WagonMap();
            if (inputFile.exists()) {
                DataFileReader reader = new DataFileReader(inputFile);
                List<Wagon> loaded = reader.readAll();
                for (Wagon w : loaded) {
                    list.add(w);
                    map.add(w);
                }
                System.out.println("Loaded " + loaded.size() + " wagons from " + inputFile.getName());
            } else {
                System.out.println(inputFile.getName() + " not found. Starting with empty list.");
            }

            if ("interactive".equalsIgnoreCase(mode)) {
                Menu menu = new Menu(list, map);
                menu.showAndHandle();
            } else {
                System.out.println("Only interactive mode is supported now.");
            }

        } catch (Exception ex) {
            System.err.println("Fatal error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}