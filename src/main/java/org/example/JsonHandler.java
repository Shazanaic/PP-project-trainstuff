package org.example;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonHandler {
    private static final SimpleDateFormat fmt = new SimpleDateFormat("dd:MM:yy");

    public static void saveToJSON(List<? extends AbstractItem> items, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println("{");
            writer.println("  \"wagons\": [");

            for (int i = 0; i < items.size(); i++) {
                AbstractItem it = items.get(i);
                if (!(it instanceof Wagon)) continue;
                Wagon w = (Wagon) it;

                writer.println("    {");
                writer.println("      \"id\": " + w.getId() + ",");
                writer.println("      \"type\": \"" + escape(w.getType()) + "\",");
                writer.println("      \"model\": \"" + escape(w.getModel()) + "\",");
                writer.println("      \"capacity\": " + w.getCapacity() + ",");
                writer.println("      \"seats\": " + w.getSeats() + ",");

                String dstr = w.getReleaseDate() == null ? "" : fmt.format(w.getReleaseDate());
                writer.println("      \"releaseDate\": \"" + dstr + "\",");

                writer.println("      \"price\": " + w.getPrice());
                writer.print("    }");
                if (i < items.size() - 1) writer.print(",");
                writer.println();
            }

            writer.println("  ]");
            writer.println("}");
            System.out.println("JSON saved successfully: " + filename);
        }
    }

    public static List<Wagon> loadFromJSON(String filename) throws IOException {
        List<Wagon> list = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) throw new IllegalArgumentException("File not found: " + filename);

        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line.trim());
            }
        }

        String content = json.toString();
        int start = content.indexOf('[');
        int end = content.lastIndexOf(']');
        if (start == -1 || end == -1) return list;
        String array = content.substring(start + 1, end);

        String[] objects = array.split("\\},\\s*\\{");
        for (String obj : objects) {
            obj = obj.replace("{", "").replace("}", "").trim();
            Map<String, String> map = new HashMap<>();

            String[] pairs = obj.split(",\\s*\"");
            for (String pair : pairs) {
                pair = pair.replaceAll("^\"|\"$", ""); // убираем кавычки по краям
                String[] kv = pair.split("\":\\s*", 2);
                if (kv.length == 2) {
                    String key = kv[0].replace("\"", "").trim();
                    String value = kv[1].replace("\"", "").trim();
                    map.put(key, value);
                }
            }

            try {
                int id = Integer.parseInt(map.getOrDefault("id", "0"));
                String type = map.getOrDefault("type", "");
                String model = map.getOrDefault("model", "");
                double capacity = Double.parseDouble(map.getOrDefault("capacity", "0"));
                int seats = Integer.parseInt(map.getOrDefault("seats", "0"));
                String ds = map.getOrDefault("releaseDate", "");
                Date d = ds.isEmpty() ? null : fmt.parse(ds);
                double price = Double.parseDouble(map.getOrDefault("price", "0"));
                list.add(new WagonBuilder()
                        .setId(id)
                        .setType(type)
                        .setModel(model)
                        .setCapacity(capacity)
                        .setSeats(seats)
                        .setReleaseDate(d)
                        .setPrice(price)
                        .build()
);
            } catch (NumberFormatException | ParseException e) {
                System.out.println("Skipping invalid JSON entry: " + e.getMessage());
            }
        }
        System.out.println("JSON loaded successfully: " + filename);
        return list;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
