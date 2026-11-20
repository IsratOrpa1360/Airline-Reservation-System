package FileManagement;

import Entities_All.Flight;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FlightManagement {
    private static final String FILE = "DataFiles/flights.txt";

    public FlightManagement() { ensureFile(); }

    private void ensureFile() {
        try {
            new File("DataFiles").mkdirs();
            File f = new File(FILE);
            if (!f.exists()) f.createNewFile();
        } catch (Exception ignored) {}
    }

    public List<Flight> getAllFlights() {
        List<Flight> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists() || f.length() == 0) return list;
        try (java.util.Scanner sc = new java.util.Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                Flight fl = Flight.fromFileLine(line);
                if (fl != null) list.add(fl);
            }
        } catch (Exception e) { System.err.println("FlightManagement read: " + e.getMessage()); }
        return list;
    }

    public Flight findByNumber(String flightNumber) {
        for (Flight f : getAllFlights())
            if (f.getFlightNumber().equalsIgnoreCase(flightNumber)) return f;
        return null;
    }

    public List<Flight> searchFlights(String origin, String destination, String date) {
        List<Flight> result = new ArrayList<>();
        for (Flight f : getAllFlights()) {
            boolean matchOrigin = origin == null || origin.isEmpty() ||
                    f.getOrigin().equalsIgnoreCase(origin);
            boolean matchDest = destination == null || destination.isEmpty() ||
                    f.getDestination().equalsIgnoreCase(destination);
            boolean matchDate = date == null || date.isEmpty() ||
                    f.getDepartureDate().equals(date);
            if (matchOrigin && matchDest && matchDate) result.add(f);
        }
        return result;
    }

    public void saveFlight(Flight flight) {
        List<Flight> all = getAllFlights();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getFlightNumber().equals(flight.getFlightNumber())) {
                all.set(i, flight);
                found = true; break;
            }
        }
        if (!found) all.add(flight);
        writeAll(all);
    }

    public void writeAll(List<Flight> flights) {
        try (FileWriter fw = new FileWriter(FILE, false)) {
            for (Flight f : flights) fw.write(f.toFileLine() + "\n");
        } catch (Exception e) { System.err.println("FlightManagement write: " + e.getMessage()); }
    }

    /** Increment booked seat count and persist */
    public void incrementBooked(String flightNumber) {
        Flight f = findByNumber(flightNumber);
        if (f != null) { f.bookSeat(); saveFlight(f); }
    }

    /** Decrement booked seat count and persist */
    public void decrementBooked(String flightNumber) {
        Flight f = findByNumber(flightNumber);
        if (f != null) { f.releaseSeat(); saveFlight(f); }
    }

    public List<String> getDistinctOrigins() {
        List<String> origins = new ArrayList<>();
        for (Flight f : getAllFlights())
            if (!origins.contains(f.getOrigin())) origins.add(f.getOrigin());
        return origins;
    }

    public List<String> getDistinctDestinations() {
        List<String> dests = new ArrayList<>();
        for (Flight f : getAllFlights())
            if (!dests.contains(f.getDestination())) dests.add(f.getDestination());
        return dests;
    }
}
