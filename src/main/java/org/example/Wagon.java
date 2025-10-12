package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Wagon extends AbstractItem {
    private int id;
    private String type;
    private String model;
    private double capacity;
    private int seats;
    private Date releaseDate;
    private double price;

    private static final SimpleDateFormat DISPLAY_FMT = new SimpleDateFormat("dd:MM:yy", Locale.US);

    public Wagon() {}

    public Wagon(int id, String type, String model, double capacity, int seats, Date releaseDate, double price) {
        this.id = id;
        this.type = type;
        this.model = model;
        this.capacity = capacity;
        this.seats = seats;
        this.releaseDate = releaseDate;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getCapacity() { return capacity; }
    public void setCapacity(double capacity) { this.capacity = capacity; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toDataString() {
        String dateStr = (releaseDate == null) ? "" : DISPLAY_FMT.format(releaseDate);
        return String.format(Locale.US,"%d;%s;%s;%.2f;%d;%s;%.2f", id, safe(type), safe(model), capacity, seats, dateStr, price);
    }

    @Override
    public String toString() {
        String dateStr = (releaseDate == null) ? "N/A" : DISPLAY_FMT.format(releaseDate);
        return String.format("Wagon{id=%d, type=%s, model=%s, capacity=%.2f, seats=%d, release=%s, price=%.2f}",
                id, safe(type), safe(model), capacity, seats, dateStr, price);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Wagon)) return false;
        return ((Wagon) obj).id == this.id;
    }
}
