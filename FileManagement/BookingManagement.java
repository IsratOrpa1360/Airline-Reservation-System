package FileManagement;

import Entities_All.Booking;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookingManagement {
    private static final String FILE = "DataFiles/bookings.txt";

    public BookingManagement() { ensureFile(); }

    private void ensureFile() {
        try {
            new File("DataFiles").mkdirs();
            File f = new File(FILE);
            if (!f.exists()) f.createNewFile();
        } catch (Exception ignored) {}
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists() || f.length() == 0) return list;
        try (java.util.Scanner sc = new java.util.Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                Booking b = Booking.fromFileLine(line);
                if (b != null) list.add(b);
            }
        } catch (Exception e) { System.err.println("BookingManagement read: " + e.getMessage()); }
        return list;
    }

    public Booking findById(String bookingId) {
        for (Booking b : getAllBookings())
            if (b.getBookingId().equalsIgnoreCase(bookingId)) return b;
        return null;
    }

    public List<Booking> getBookingsForUser(String username) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : getAllBookings())
            if (b.getUsername().equalsIgnoreCase(username)) result.add(b);
        return result;
    }

    public List<Booking> getBookingsForFlight(String flightNumber) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : getAllBookings())
            if (b.getFlightNumber().equalsIgnoreCase(flightNumber) && !b.isCancelled())
                result.add(b);
        return result;
    }

    public boolean isSeatBooked(String flightNumber, String seatNumber) {
        for (Booking b : getBookingsForFlight(flightNumber))
            if (b.getSeatNumber().equalsIgnoreCase(seatNumber)) return true;
        return false;
    }

    public void saveBooking(Booking booking) {
        List<Booking> all = getAllBookings();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getBookingId().equals(booking.getBookingId())) {
                all.set(i, booking); found = true; break;
            }
        }
        if (!found) all.add(booking);
        writeAll(all);
    }

    private void writeAll(List<Booking> bookings) {
        try (FileWriter fw = new FileWriter(FILE, false)) {
            for (Booking b : bookings) fw.write(b.toFileLine() + "\n");
        } catch (Exception e) { System.err.println("BookingManagement write: " + e.getMessage()); }
    }

    public boolean cancelBooking(String bookingId) {
        Booking b = findById(bookingId);
        if (b == null || b.isCancelled()) return false;
        b.cancelBooking();
        saveBooking(b);
        return true;
    }

    public String generateBookingId() {
        return "BK" + String.format("%06d", getAllBookings().size() + 1)
                + (System.currentTimeMillis() % 1000);
    }

    public List<String> getBookedSeatsForFlight(String flightNumber) {
        List<String> seats = new ArrayList<>();
        for (Booking b : getBookingsForFlight(flightNumber))
            seats.add(b.getSeatNumber());
        return seats;
    }
}
