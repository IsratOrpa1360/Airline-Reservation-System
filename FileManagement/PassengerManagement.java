package FileManagement;

import Entities_All.Passenger;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class PassengerManagement {
    private final String filePath;

    public PassengerManagement() {
        filePath = "DataFiles/passenger.txt";
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            File dir = new File("DataFiles");
            if (!dir.exists()) dir.mkdirs();
            File f = new File(filePath);
            if (!f.exists()) f.createNewFile();
        } catch (Exception ex) {
            System.err.println("Could not create data file: " + ex.getMessage());
        }
    }

    public void writePassenger(Passenger p, boolean append) {
        try (FileWriter writer = new FileWriter(new File(filePath), append)) {
            writer.write(p.getFileWriteFormat());
            writer.flush();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error saving passenger: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Passenger[] getAllPassengers() {
        File f = new File(filePath);
        if (!f.exists() || f.length() == 0) return new Passenger[0];

        ArrayList<Passenger> list = new ArrayList<>();
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(";", -1);
                if (data.length < 12) continue;
                try {
                    Passenger p = new Passenger(data[0], data[1], data[2], data[3],
                            data[4], data[5], data[6], data[7], data[8], data[9], data[10], data[11]);
                    list.add(p);
                } catch (NumberFormatException ex) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (Exception ex) {
            System.err.println("Error reading passengers: " + ex.getMessage());
        }
        return list.toArray(new Passenger[0]);
    }

    public Passenger searchPassenger(int pl) {
        for (Passenger p : getAllPassengers()) {
            if (p.getPl() == pl) return p;
        }
        return null;
    }

    public void deletePassenger(int pl) {
        Passenger[] passengers = getAllPassengers();
        boolean first = true;
        try (FileWriter writer = new FileWriter(new File(filePath), false)) {
            for (Passenger p : passengers) {
                if (p.getPl() != pl) {
                    writer.write(p.getFileWriteFormat());
                }
            }
            writer.flush();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error deleting passenger: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updatePassenger(Passenger updated) {
        Passenger[] passengers = getAllPassengers();
        if (passengers.length == 0) {
            JOptionPane.showMessageDialog(null, "No passengers found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean found = false;
        try (FileWriter writer = new FileWriter(new File(filePath), false)) {
            for (Passenger p : passengers) {
                if (p.getPl() == updated.getPl()) {
                    writer.write(updated.getFileWriteFormat());
                    found = true;
                } else {
                    writer.write(p.getFileWriteFormat());
                }
            }
            writer.flush();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error updating passenger: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
        if (!found) {
            JOptionPane.showMessageDialog(null, "Passenger with PL " + updated.getPl() + " not found.",
                    "Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }
}
