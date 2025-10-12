package org.example;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Menu {
    private final AbstractStorage listStorage;
    private final AbstractStorage mapStorage;
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd:MM:yy");

    private File currentFile = null;
    private boolean manualSaveDone = false;

    public Menu(AbstractStorage listStorage, AbstractStorage mapStorage) {
        this.listStorage = listStorage;
        this.mapStorage = mapStorage;
        fmt.setLenient(false);
    }

    public void showAndHandle() throws IOException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- Menu ---");
            System.out.println("1) Show all (List)");
            System.out.println("2) Show all (Map)");
            System.out.println("3) Add wagon");
            System.out.println("4) Update wagon (by id)");
            System.out.println("5) Delete wagon (by id)");
            System.out.println("6) Save to file");
            System.out.println("7) Load from file (replace list)");
            System.out.println("8) Sort List by price");
            System.out.println("9) Sort List by release date");
            System.out.println("10) Clear all");
            System.out.println("11) Add wagons from file (append to list)");
            System.out.println("0) Exit");
            System.out.print("Choose: ");
            String choice = br.readLine();
            switch (choice) {
                case "1": showList(); break;
                case "2": showMap(); break;
                case "3": addFromConsole(); break;
                case "4": updateFromConsole(); break;
                case "5": deleteFromConsole(); break;
                case "6": savePrompt(); break;
                case "7": loadFromFileReplace(); break;
                case "8": sortListByPrice(); break;
                case "9": sortListByDate(); break;
                case "10": clearAll(); break;
                case "11": addFromFile(); break;
                case "0":
                    exitProcedure();
                    exit = true;
                    break;
                default: System.out.println("Unknown option.");
            }
        }
    }

    private void loadFromFileReplace() throws IOException {
        System.out.print("Enter filename to load and replace current list: ");
        String fname = br.readLine().trim();
        File file = new File(fname);
        if (!file.exists()) {
            System.out.println("File not found: " + fname);
            return;
        }

        DataFileReader reader = new DataFileReader(file);
        try {
            List<Wagon> loaded = reader.readAll();
            listStorage.clear();
            mapStorage.clear();
            int addedCount = 0;
            for (Wagon w : loaded) {
                boolean addedList = listStorage.add(w);
                boolean addedMap = mapStorage.add(w);
                if (addedList && addedMap) addedCount++;
            }
            currentFile = file;
            manualSaveDone = false;
            System.out.println("Loaded " + addedCount + " wagons from " + fname);
        } catch (IOException ex) {
            System.out.println("Error reading file: " + ex.getMessage());
        }
    }

    private void addFromFile() throws IOException {
        System.out.print("Enter filename to add wagons to current list: ");
        String fname = br.readLine().trim();
        File file = new File(fname);
        if (!file.exists()) {
            System.out.println("File not found: " + fname);
            return;
        }

        DataFileReader reader = new DataFileReader(file);
        try {
            List<Wagon> loaded = reader.readAll();
            int addedCount = 0;
            for (Wagon w : loaded) {
                boolean addedList = listStorage.add(w);
                boolean addedMap = mapStorage.add(w);
                if (addedList && addedMap) addedCount++;
            }
            System.out.println("Added " + addedCount + " wagons from " + fname);
        } catch (IOException ex) {
            System.out.println("Error reading file: " + ex.getMessage());
        }
    }

    private void showList() {
        List<AbstractItem> all = listStorage.getAll();
        if (all.isEmpty()) System.out.println("(list is empty)");
        for (AbstractItem it : all) System.out.println(it);
    }

    private void showMap() {
        List<AbstractItem> all = mapStorage.getAll();
        if (all.isEmpty()) System.out.println("(map is empty)");
        for (AbstractItem it : all) System.out.println(it);
    }

    private void addFromConsole() throws IOException {
        System.out.println("Enter fields: id;type;model;capacity;seats;dd:MM:yy;price");
        String line = br.readLine();
        try {
            String[] p = line.split(";");
            int id = Integer.parseInt(p[0].trim());
            String type = p[1].trim();
            String model = p[2].trim();
            double cap = Double.parseDouble(p[3].trim());
            int seats = Integer.parseInt(p[4].trim());
            Date d = fmt.parse(p[5].trim());
            double price = Double.parseDouble(p[6].trim());
            Wagon w = new Wagon(id, type, model, cap, seats, d, price);
            boolean addedList = listStorage.add(w);
            boolean addedMap = mapStorage.add(w);
            if (addedList && addedMap) System.out.println("Added successfully.");
            else System.out.println("Not added (duplicate id?).");
        } catch (Exception ex) {
            System.out.println("Invalid input: " + ex.getMessage());
        }
    }

    private void updateFromConsole() throws IOException {
        System.out.print("Enter id to update: ");
        int id = Integer.parseInt(br.readLine().trim());
        System.out.println("Enter new fields: id;type;model;capacity;seats;dd:MM:yy;price");
        String line = br.readLine();
        try {
            String[] p = line.split(";");
            int nid = Integer.parseInt(p[0].trim());
            String type = p[1].trim();
            String model = p[2].trim();
            double cap = Double.parseDouble(p[3].trim());
            int seats = Integer.parseInt(p[4].trim());
            Date d = fmt.parse(p[5].trim());
            double price = Double.parseDouble(p[6].trim());
            Wagon w = new Wagon(nid, type, model, cap, seats, d, price);
            boolean u1 = listStorage.update(id, w);
            boolean u2 = mapStorage.update(id, w);
            System.out.println((u1 && u2) ? "Updated." : "Update failed.");
        } catch (Exception ex) {
            System.out.println("Invalid input: " + ex.getMessage());
        }
    }

    private void deleteFromConsole() throws IOException {
        System.out.print("Enter id to delete: ");
        int id = Integer.parseInt(br.readLine().trim());
        boolean r1 = listStorage.remove(id);
        boolean r2 = mapStorage.remove(id);
        System.out.println((r1 || r2) ? "Deleted." : "Not found.");
    }

    private void savePrompt() throws IOException {
        System.out.print("Enter filename to save (e.g. wagons_out.txt): ");
        String fname = br.readLine().trim();
        File file = new File(fname);
        DataFileWriter writer = new DataFileWriter(file);
        try {
            writer.writeAll(listStorage.getAll());
            currentFile = file;
            manualSaveDone = true;
            System.out.println("Saved to " + fname);
        } catch (Exception ex) {
            System.out.println("Error saving: " + ex.getMessage());
        }
    }

    private void sortListByPrice() {
        if (listStorage instanceof WagonList) {
            WagonList wl = (WagonList) listStorage;
            wl.getWagonList().sort((a,b) -> Double.compare(a.getPrice(), b.getPrice()));
            System.out.println("Sorted list by price.");
        } else System.out.println("List storage not available.");
    }

    private void sortListByDate() {
        if (listStorage instanceof WagonList) {
            WagonList wl = (WagonList) listStorage;
            wl.getWagonList().sort((a,b) -> {
                if (a.getReleaseDate() == null && b.getReleaseDate() == null) return 0;
                if (a.getReleaseDate() == null) return 1;
                if (b.getReleaseDate() == null) return -1;
                return a.getReleaseDate().compareTo(b.getReleaseDate());
            });
            System.out.println("Sorted list by date.");
        } else System.out.println("List storage not available.");
    }

    private void clearAll() {
        listStorage.clear();
        mapStorage.clear();
        System.out.println("Cleared.");
    }

    private void exitProcedure() throws IOException {
        if (manualSaveDone) {
            System.out.println("Exiting (manual save already done)...");
            return;
        }

        if (currentFile != null) {
            DataFileWriter writer = new DataFileWriter(currentFile);
            writer.writeAll(listStorage.getAll());
            System.out.println("Data automatically saved to " + currentFile.getName());
        } else if (!listStorage.getAll().isEmpty()) {
            boolean good = false;
            while(!good) {
                System.out.print("No save file associated. Do you want to save ts? (y/n): ");
                String pinos = br.readLine();
                switch (pinos) {
                    case "y":
                        System.out.print("Enter savefile name: ");
                        String fname = br.readLine().trim();
                        if (!fname.isEmpty()) {
                            DataFileWriter writer = new DataFileWriter(new File(fname));
                            writer.writeAll(listStorage.getAll());
                            System.out.println("Data saved to " + fname);
                        }
                        good = true;
                        break;
                    case "n":
                        return;
                    default:
                        System.out.println("Invalid answer eblan");
                }
            }
        } else {
            System.out.println("List empty, nothing to save.");
        }
    }
}
