package org.example;

import java.util.Date;

public class WagonDirector {

    public Wagon createStandardWagon(AbstractWagonBuilder builder) {
        return builder
                .setId(0)
                .setType("Default")
                .setModel("Standard")
                .setCapacity(10000)
                .setSeats(40)
                .setReleaseDate(new Date())
                .setPrice(5000)
                .build();
    }

    public Wagon createCustomWagon(
            AbstractWagonBuilder builder,
            int id,
            String type,
            String model,
            double capacity,
            int seats,
            Date date,
            double price
    ) {
        return builder
                .setId(id)
                .setType(type)
                .setModel(model)
                .setCapacity(capacity)
                .setSeats(seats)
                .setReleaseDate(date)
                .setPrice(price)
                .build();
    }
}
