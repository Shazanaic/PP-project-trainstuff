package org.example;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataFileReader {
    private final File file;
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd:MM:yy", Locale.US);

    public DataFileReader(File file) {
        this.file = file;
        fmt.setLenient(false);
    }

    public List<Wagon> readAll() throws IOException {
        List<Wagon> result = new ArrayList<>();
        if (!file.exists()) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(file));
             BufferedWriter log = new BufferedWriter(new FileWriter("errors.log", true))) {
            String line;
            int lineNo = 0;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length != 7) {
                    log.write(String.format("Line %d: wrong number of fields -> %s%n", lineNo, line));
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String type = parts[1].trim();
                    String model = parts[2].trim();
                    double capacity = Double.parseDouble(parts[3].trim());
                    int seats = Integer.parseInt(parts[4].trim());
                    Date d = fmt.parse(parts[5].trim());
                    double price = Double.parseDouble(parts[6].trim());

                    Wagon w = new WagonBuilder()
                            .setId(id)
                            .setType(type)
                            .setModel(model)
                            .setCapacity(capacity)
                            .setSeats(seats)
                            .setReleaseDate(d)
                            .setPrice(price)
                            .build();

                    result.add(w);
                } catch (NumberFormatException | ParseException ex) {
                    log.write(String.format("Line %d: invalid data -> %s ; cause: %s%n", lineNo, line, ex.getMessage()));
                }
            }
        }
        return result;
    }
}
