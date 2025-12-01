package org.example;

import javax.crypto.SecretKey;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.example.CryptoUtils.*;
import static org.example.ZipUtils.*;

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
            System.out.println("13) Encrypt/Decrypt data file");
            System.out.println("14) Archive data file");
            System.out.println("0) Exit");
            System.out.print("Choose: ");
            String choice = br.readLine();

            switch (choice) {
                case "1" -> showList();
                case "2" -> showMap();
                case "3" -> addFromConsole();
                case "4" -> updateFromConsole();
                case "5" -> deleteFromConsole();
                case "6" -> saveSubmenu();
                case "7" -> loadSubmenu();
                case "8" -> sortListByPrice();
                case "9" -> sortListByDate();
                case "10" -> sortListByIds();
                case "11" -> clearAll();
                case "12" -> addFromFile();
                case "13" -> encryptionMenu();
                case "14" -> zipMenu();
                case "0" -> {
                    exitProcedure();
                    exit = true;
                }
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private void saveSubmenu() throws IOException {
        System.out.println("Save options (multiple formats allowed):");
        System.out.println("1) Text file");
        System.out.println("2) XML");
        System.out.println("3) JSON");
        System.out.println("0) Cancel");

        boolean wantTxt = false;
        boolean wantXml = false;
        boolean wantJson = false;

        while (true) {
            System.out.print("Choose formats (example '1 2 3'): ");
            String line = br.readLine().trim();

            if (line.equals("0")) return;

            String[] parts = line.split("\\s+");
            boolean ok = true;

            wantTxt = wantXml = wantJson = false;

            for (String p : parts) {
                switch (p) {
                    case "1" -> wantTxt = true;
                    case "2" -> wantXml = true;
                    case "3" -> wantJson = true;
                    default -> ok = false;
                }
            }

            if (!ok) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            if (!wantTxt && !wantXml && !wantJson) {
                System.out.println("No formats selected.");
                continue;
            }

            break;
        }

        List<AbstractItem> items = listStorage.getAll();
        if (items.isEmpty()) {
            System.out.println("List is empty — nothing to save.");
            return;
        }

        String txtFile = null;
        String xmlFile = null;
        String jsonFile = null;

        if (wantTxt) {
            txtFile = askFileName("Enter TXT filename", "wagons.txt");
        }
        if (wantXml) {
            xmlFile = askFileName("Enter XML filename", "wagons.xml");
        }
        if (wantJson) {
            jsonFile = askFileName("Enter JSON filename", "wagons.json");
        }

        OutputWriter writer;

        if (wantTxt) {
            writer = new FileWriterAdapter(new File(txtFile));
        } else if (wantXml) {
            writer = new XmlWriterAdapter(xmlFile);
        } else {
            writer = new JsonWriterAdapter(jsonFile);
        }

        if (wantTxt && !(writer instanceof FileWriterAdapter)) {
            writer = new FileDecorator(writer, new File(txtFile));
        }
        if (wantXml && !(writer instanceof XmlWriterAdapter)) {
            writer = new XmlDecorator(writer, xmlFile);
        }

        if (wantJson && !(writer instanceof JsonWriterAdapter)) {
            writer = new JsonDecorator(writer, jsonFile);
        }

        try {
            writer.write(items);

            System.out.println("Saved successfully in selected format(s).");

            manualSaveDone = true;
            dataChanged = false;
            if (wantTxt) {
                currentFile = new File(txtFile);
            }

        } catch (SecurityException se) {
            System.out.println("Permission denied: " + se.getMessage());
        } catch (FileNotFoundException fnfe) {
            System.out.println("File not found or cannot create: " + fnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println("I/O error: " + ioe.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }


    private String askFileName(String prompt, String defaultName) throws IOException {
        while (true) {
            System.out.print(prompt + " (default: " + defaultName + "): ");
            String name = br.readLine().trim();

            if (name.isEmpty()) name = defaultName;

            File f = new File(name);

            try {
                if (f.exists()) {
                    if (!f.canWrite()) {
                        System.out.println("Cannot write to file: permission denied.");
                        continue;
                    }
                } else {
                    try {
                        if (!f.createNewFile()) {
                            System.out.println("Cannot create file: unknown reason.");
                            continue;
                        }
                        f.delete();
                    } catch (IOException e) {
                        System.out.println("Cannot create file: " + e.getMessage());
                        continue;
                    }
                }
            } catch (SecurityException se) {
                System.out.println("Access error: " + se.getMessage());
                continue;
            }

            return name;
        }
    }


    private void loadSubmenu() throws IOException {
        System.out.println("Load options:");
        System.out.println("1) Text (default)");
        System.out.println("2) XML");
        System.out.println("3) JSON");
        System.out.print("Choose: ");
        String ch = br.readLine().trim();
        switch (ch) {
            case "1" -> loadFromFileReplace();
            case "2" -> {
                System.out.print("Enter XML file name to load: ");
                String xmlFile = br.readLine().trim();
                File file = new File(xmlFile);
                if (!file.exists()) {
                    System.out.println("File not found: " + xmlFile);
                    return;
                }

                try {
                    List<Wagon> loaded = XmlHandler.loadFromXML(xmlFile);
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
                    System.out.println("Loaded " + addedCount + " wagons from XML: " + xmlFile);
                } catch (Exception e) {
                    System.out.println("Failed to load XML: " + e.getMessage());
                }
            }
            case "3" -> {
                System.out.print("Enter JSON file name to load: ");
                String jsonFile = br.readLine().trim();
                File file = new File(jsonFile);
                if (!file.exists()) {
                    System.out.println("File not found: " + jsonFile);
                    return;
                }

                try {
                    List<Wagon> loaded = JsonHandler.loadFromJSON(jsonFile);
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
                    System.out.println("Loaded " + addedCount + " wagons from JSON: " + jsonFile);
                } catch (Exception e) {
                    System.out.println("Failed to load JSON: " + e.getMessage());
                }
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void encryptionMenu() throws IOException {
        System.out.println("Encryption options:");
        System.out.println("1) Encrypt file");
        System.out.println("2) Decrypt file");
        System.out.println("3) Back");
        System.out.print("Choose: ");
        String ch = br.readLine();
        switch (ch) {
            case "1" -> encryptAction();
            case "2" -> decryptAction();
            case "3" -> {}
            default -> System.out.println("Invalid choice.");
        }
    }

    private void encryptAction() throws IOException {
        System.out.print("Enter source file to encrypt: ");
        String srcPath = br.readLine().trim();
        File src = new File(srcPath);
        if (!src.exists()) {
            System.out.println("File not found.");
            return;
        }

        System.out.print("Enter output file name for encrypted data: ");
        String dstPath = br.readLine().trim();

        try {
            CryptoUtils.encryptFile(srcPath, dstPath);
        } catch (Exception e) {
            System.out.println("Encryption failed: " + e.getMessage());
        }
    }

    private void decryptAction() throws IOException {
        System.out.print("Enter encrypted file name: ");
        String srcPath = br.readLine().trim();
        File src = new File(srcPath);
        if (!src.exists()) {
            System.out.println("File not found.");
            return;
        }

        System.out.print("Enter output file name (decrypted): ");
        String dstPath = br.readLine().trim();

        try {
            CryptoUtils.decryptFile(srcPath, dstPath);
        } catch (Exception e) {
            System.out.println("Decryption failed: " + e.getMessage());
        }
    }

    private void zipMenu() throws IOException {
        System.out.println("Archive options:");
        System.out.println("1) Create zip (single file)");
        System.out.println("2) Back");
        System.out.print("Choose: ");
        String ch = br.readLine();
        switch (ch) {
            case "1" -> createZipAction();
            case "2" -> {}
            default -> System.out.println("Invalid choice.");
        }
    }

    private void createZipAction() throws IOException {
        System.out.print("Enter filename to archive: ");
        String input = br.readLine().trim();
        File file = new File(input);
        if (!file.exists()) {
            System.out.println("File not found.");
            return;
        }

        System.out.print("Enter name for ZIP archive (or leave empty for default): ");
        String zipName = br.readLine().trim();
        if (zipName.isEmpty()) zipName = file.getName() + ".zip";

        try {
            zipFile(file, new File(zipName));
        } catch (IOException e) {
            System.out.println("Error creating archive: " + e.getMessage());
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
            Wagon w = new WagonBuilder()
                    .setId(id)
                    .setType(type)
                    .setModel(model)
                    .setCapacity(cap)
                    .setSeats(seats)
                    .setReleaseDate(d)
                    .setPrice(price)
                    .build();
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
            Wagon w = new WagonBuilder()
                    .setId(id)
                    .setType(type)
                    .setModel(model)
                    .setCapacity(cap)
                    .setSeats(seats)
                    .setReleaseDate(d)
                    .setPrice(price)
                    .build();
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
            wl.getWagonList().sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
            System.out.println("Sorted list by price.");
            manualSaveDone = false;
            dataChanged = true;
        }
    }

    private void sortListByIds() {
        if (listStorage instanceof WagonList wl) {
            wl.getWagonList().sort((a, b) -> Integer.compare(a.getId(), b.getId()));
            System.out.println("Sorted list by Ids.");
            manualSaveDone = false;
            dataChanged = true;
        }
    }

    private void sortListByDate() {
        if (listStorage instanceof WagonList wl) {
            wl.getWagonList().sort((a, b) -> {
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
            while (!good) {
                System.out.print("No save file. Save before exit? (y/n): ");
                String ans = br.readLine().trim().toLowerCase();
                switch (ans) {
                    case "y" -> {
                        savePrompt();
                        good = true;
                    }
                    case "n" -> {
                        return;
                    }
                    default -> System.out.println("Invalid answer.");
                }
            }
        }
    }
}
