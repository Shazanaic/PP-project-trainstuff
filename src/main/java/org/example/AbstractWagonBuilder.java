package org.example;

import java.util.Date;

public abstract class AbstractWagonBuilder {

    protected int id;
    protected String type;
    protected String model;
    protected double capacity;
    protected int seats;
    protected Date releaseDate;
    protected double price;

    public abstract AbstractWagonBuilder setId(int id);
    public abstract AbstractWagonBuilder setType(String type);
    public abstract AbstractWagonBuilder setModel(String model);
    public abstract AbstractWagonBuilder setCapacity(double capacity);
    public abstract AbstractWagonBuilder setSeats(int seats);
    public abstract AbstractWagonBuilder setReleaseDate(Date date);
    public abstract AbstractWagonBuilder setPrice(double price);

    public abstract Wagon build();
}
