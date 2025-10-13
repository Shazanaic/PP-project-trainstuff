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
    private boolean dataChanged = false;

    public Menu(AbstractStorage listStorage, AbstractStorage mapStorage) {
        this.listStorage = listStorage;
        this.mapStorage = mapStorage;
        fmt.setLenient(false);
    }

    public void showAndHandle() throws IOException {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- Menu ---");
            System.out.println("Current file: " + (currentFile != null ? currentFile.getName() : "(none)") +
                    (dataChanged ? " *unsaved changes*" : ""));
            System.out.println("1) Show all (List)");
            System.out.println("2) Show all (Map)");
            System.out.println("3) Add wagon");
            System.out.println("4) Update wagon (by id)");
            System.out.println("5) Delete wagon (by id)");
            System.out.println("6) Save to file");
            System.out.println("7) Load from file (replace list)");
            System.out.println("8) Sort List by price");
            System.out.println("9) Sort List by release date");
            System.out.println("10) Sort List by Id`s");
            System.out.println("11) Clear all");
            System.out.println("12) Add wagons from file (append to list)");
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
                case "10": sortListByIds(); break;
                case "11": clearAll(); break;
                case "12": addFromFile(); break;
                case "0": exitProcedure(); exit = true; break;
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
            dataChanged = true;
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
            manualSaveDone = false;
            dataChanged = true;
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
            if (addedList && addedMap) {
                System.out.println("Added successfully.");
                manualSaveDone = false;
                dataChanged = true;
            } else System.out.println("Not added (duplicate id?).");
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
            if (u1 && u2) {
                System.out.println("Updated.");
                manualSaveDone = false;
                dataChanged = true;
            } else System.out.println("Update failed.");
        } catch (Exception ex) {
            System.out.println("Invalid input: " + ex.getMessage());
        }
    }

    private void deleteFromConsole() throws IOException {
        System.out.print("Enter id to delete: ");
        int id = Integer.parseInt(br.readLine().trim());
        boolean r1 = listStorage.remove(id);
        boolean r2 = mapStorage.remove(id);
        if (r1 || r2) {
            System.out.println("Deleted.");
            manualSaveDone = false;
            dataChanged = true;
        } else System.out.println("Not found.");
    }

    private void savePrompt() throws IOException {
        System.out.print("Enter filename to save (or empty to overwrite current): ");
        String fname = br.readLine().trim();
        File file = fname.isEmpty() && currentFile != null ? currentFile : new File(fname.isEmpty() ? "wagons.txt" : fname);
        DataFileWriter writer = new DataFileWriter(file);
        try {
            writer.writeAll(listStorage.getAll());
            currentFile = file;
            manualSaveDone = true;
            dataChanged = false;
            System.out.println("Saved to " + file.getName());
        } catch (Exception ex) {
            System.out.println("Error saving: " + ex.getMessage());
        }
    }

    private void sortListByPrice() {
        if (listStorage instanceof WagonList wl) {
            wl.getWagonList().sort((a,b) -> Double.compare(a.getPrice(), b.getPrice()));
            System.out.println("Sorted list by price.");
            manualSaveDone = false;
            dataChanged = true;
        }
    }

    private void sortListByIds() {
        if (listStorage instanceof WagonList wl) {
            wl.getWagonList().sort((a,b) -> Integer.compare(a.getId(), b.getId()));
            System.out.println("Sorted list by Ids.");
            manualSaveDone = false;
            dataChanged = true;
        }
    }

    private void sortListByDate() {
        if (listStorage instanceof WagonList wl) {
            wl.getWagonList().sort((a,b) -> {
                if (a.getReleaseDate() == null && b.getReleaseDate() == null) return 0;
                if (a.getReleaseDate() == null) return 1;
                if (b.getReleaseDate() == null) return -1;
                return a.getReleaseDate().compareTo(b.getReleaseDate());
            });
            System.out.println("Sorted list by date.");
            manualSaveDone = false;
            dataChanged = true;
        }
    }

    private void clearAll() {
        listStorage.clear();
        mapStorage.clear();
        manualSaveDone = false;
        dataChanged = true;
        System.out.println("Cleared.");
    }

    private void exitProcedure() throws IOException {
        if (!dataChanged) {
            System.out.println("No unsaved changes. Exiting...");
            return;
        }
        if (manualSaveDone) {
            System.out.println("Exiting...");
            return;
        }

        if (currentFile != null) {
            System.out.print("Save changes to " + currentFile.getName() + "? (y/n): ");
            String ans = br.readLine().trim().toLowerCase();
            if (ans.equals("y")) {
                new DataFileWriter(currentFile).writeAll(listStorage.getAll());
                System.out.println("Saved to " + currentFile.getName());
            }
        } else {
            boolean good = false;
            while(!good) {
            System.out.print("No save file. Save before exit? (y/n): ");
            String ans = br.readLine().trim().toLowerCase();
                switch (ans) {
                    case ("y"):
                        savePrompt(); good = true; break;
                    case("n") : return;
                    default: System.out.println("Invalid answer eblan");
                }
            }
        }
    }
}
