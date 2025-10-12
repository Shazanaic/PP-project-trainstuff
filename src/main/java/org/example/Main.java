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

    private static void applySort(WagonList list, String field) {
        switch (field.toLowerCase(Locale.ROOT)) {
            case "price":
                list.getWagonList().sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                break;
            case "date":
                list.getWagonList().sort((a, b) -> {
                    if (a.getReleaseDate() == null && b.getReleaseDate() == null) return 0;
                    if (a.getReleaseDate() == null) return 1;
                    if (b.getReleaseDate() == null) return -1;
                    return a.getReleaseDate().compareTo(b.getReleaseDate());
                });
                break;
            case "id":
                list.getWagonList().sort((a, b) -> Integer.compare(a.getId(), b.getId()));
                break;
            default:
                System.out.println("Unknown sort field: " + field);
        }
    }
}
