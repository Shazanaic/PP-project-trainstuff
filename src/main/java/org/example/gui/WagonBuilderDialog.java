package org.example.gui;

import org.example.*;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WagonBuilderDialog {

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("dd:MM:yy", Locale.US);

    public static Wagon show(JFrame parent) {
        JTextField id = new JTextField();
        JTextField type = new JTextField();
        JTextField model = new JTextField();
        JTextField capacity = new JTextField();
        JTextField seats = new JTextField();
        JTextField releaseDate = new JTextField();
        JTextField price = new JTextField();

        Object[] fields = {
                "ID:", id,
                "Type:", type,
                "Model:", model,
                "Capacity:", capacity,
                "Seats:", seats,
                "Release date (dd:MM:yy):", releaseDate,
                "Price:", price
        };

        int result = JOptionPane.showConfirmDialog(
                parent, fields, "Wagon builder",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION)
            return null;

        Date date = null;
        try {
            if (!releaseDate.getText().isBlank())
                date = DATE_FMT.parse(releaseDate.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                    "Format is dd:MM:yy",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            return new WagonBuilder()
                    .setId(Integer.parseInt(id.getText()))
                    .setType(type.getText())
                    .setModel(model.getText())
                    .setCapacity(Double.parseDouble(capacity.getText()))
                    .setSeats(Integer.parseInt(seats.getText()))
                    .setReleaseDate(date)
                    .setPrice(Double.parseDouble(price.getText()))
                    .build();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                    "Invalid input: " + ex.getMessage(),
                    "U are invalid", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
