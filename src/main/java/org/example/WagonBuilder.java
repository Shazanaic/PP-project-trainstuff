package org.example;

import java.util.Date;

public class WagonBuilder extends AbstractWagonBuilder {

    @Override
    public WagonBuilder setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public WagonBuilder setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public WagonBuilder setModel(String model) {
        this.model = model;
        return this;
    }

    @Override
    public WagonBuilder setCapacity(double capacity) {
        this.capacity = capacity;
        return this;
    }

    @Override
    public WagonBuilder setSeats(int seats) {
        this.seats = seats;
        return this;
    }

    @Override
    public WagonBuilder setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    @Override
    public WagonBuilder setPrice(double price) {
        this.price = price;
        return this;
    }

    @Override
    public Wagon build() {
        return new Wagon(id, type, model, capacity, seats, releaseDate, price);
    }
}
