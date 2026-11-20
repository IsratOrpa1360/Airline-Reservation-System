import FileManagement.FlightManagement;
import FileManagement.UserManagement;
import Entities_All.Flight;
import Entities_All.User;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Seeds the application with demo flights and a demo user on first run.
 * Safe to call every launch — skips seeding if data already exists.
 */
public class DataInitializer {

    public static void initialize() {
        seedFlights();
        seedUsers();
    }

    private static void seedFlights() {
        FlightManagement fm = new FlightManagement();
        if (!fm.getAllFlights().isEmpty()) return; // already seeded

        // Build dates relative to today
        String d0 = dateFrom(3),  d1 = dateFrom(5),  d2 = dateFrom(7),
               d3 = dateFrom(10), d4 = dateFrom(12), d5 = dateFrom(14),
               d6 = dateFrom(17), d7 = dateFrom(20), d8 = dateFrom(22),
               d9 = dateFrom(25);

        Flight[] flights = {
            // flightNo, origin, destination, date, dep, arr, duration, eco, biz, first, total, booked, airline
            new Flight("BS201","Dhaka","Dubai",          d0,"10:00","14:30","4h 30m", 15000, 45000,  80000, 152, 0,"Biman Bangladesh"),
            new Flight("BS202","Dubai","Dhaka",          d1,"16:00","22:45","4h 45m", 14500, 44000,  78000, 152, 0,"Biman Bangladesh"),
            new Flight("BG301","Dhaka","London",         d0,"22:00","08:30","10h 30m",25000, 70000, 120000, 180, 0,"Biman Bangladesh"),
            new Flight("EK401","Dubai","London",         d1,"09:00","14:00","7h 00m", 20000, 60000, 100000, 200, 0,"Emirates"),
            new Flight("EK402","London","New York",      d2,"11:00","14:00","9h 00m", 30000, 80000, 130000, 250, 0,"Emirates"),
            new Flight("SQ501","Dhaka","Singapore",      d3,"14:00","21:00","5h 00m", 18000, 52000,  90000, 160, 0,"Singapore Airlines"),
            new Flight("SQ502","Singapore","Tokyo",      d4,"08:00","15:30","7h 30m", 22000, 65000, 110000, 180, 0,"Singapore Airlines"),
            new Flight("AI601","Dhaka","Kolkata",        d0,"08:00","09:30","1h 30m",  5000, 15000,  25000, 120, 0,"Air India"),
            new Flight("AI602","Kolkata","Delhi",        d1,"12:00","14:00","2h 00m",  7000, 21000,  38000, 140, 0,"Air India"),
            new Flight("QR701","Dhaka","Doha",           d2,"01:30","05:00","5h 30m", 17000, 48000,  85000, 170, 0,"Qatar Airways"),
            new Flight("QR702","Doha","Paris",           d3,"08:00","13:30","7h 30m", 28000, 75000, 125000, 200, 0,"Qatar Airways"),
            new Flight("TG801","Dhaka","Bangkok",        d4,"06:00","12:00","4h 00m", 12000, 36000,  62000, 140, 0,"Thai Airways"),
            new Flight("TG802","Bangkok","Kuala Lumpur", d5,"14:00","17:00","3h 00m",  9000, 28000,  50000, 150, 0,"Thai Airways"),
            new Flight("MH901","Dhaka","Kuala Lumpur",   d6,"23:00","07:30","5h 30m", 16000, 46000,  82000, 160, 0,"Malaysia Airlines"),
            new Flight("CX101","Hong Kong","Sydney",     d7,"10:30","22:00","9h 30m", 32000, 85000, 140000, 220, 0,"Cathay Pacific"),
            new Flight("LH201","Frankfurt","New York",   d8,"13:00","16:00","9h 00m", 29000, 78000, 128000, 240, 0,"Lufthansa"),
            new Flight("BA301","London","Dhaka",         d9,"21:00","13:30","9h 30m", 24000, 68000, 115000, 190, 0,"British Airways"),
        };

        for (Flight f : flights) fm.saveFlight(f);
        System.out.println("DataInitializer: Seeded " + flights.length + " flights.");
    }

    private static void seedUsers() {
        UserManagement um = new UserManagement();
        if (um.findByUsername("demo") != null) return; // already seeded

        User orpa = new User("Orpa", "orpa@airline.com", "orpa123",
                15000, "Gold",
                LocalDate.now().minusMonths(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        um.registerUser(orpa);

        User demo = new User("demo", "demo@airline.com", "demo123",
                2500, "Bronze",
                LocalDate.now().minusMonths(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        um.registerUser(demo);

        User admin = new User("admin", "admin@airline.com", "admin123",
                50000, "Platinum",
                LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        um.registerUser(admin);
        System.out.println("DataInitializer: Seeded demo users (Orpa/orpa123, demo/demo123, admin/admin123).");
    }

    private static String dateFrom(int daysAhead) {
        return LocalDate.now().plusDays(daysAhead)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
