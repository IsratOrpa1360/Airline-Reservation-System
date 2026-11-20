package GUI;

import Entities_All.*;

/**
 * Singleton session holder – carries the logged-in user and current booking
 * state across all GUI panels without passing parameters everywhere.
 */
public class AppSession {

    private static AppSession instance;

    // ── Auth ───────────────────────────────────────────────────────────────────
    private User    currentUser;
    private boolean isGuest;
    private String  guestName;
    private String  guestEmail;

    // ── Active booking flow ────────────────────────────────────────────────────
    private Flight   selectedFlight;
    private String   selectedSeatNumber;
    private String   selectedClass;        // Economy / Business / First
    private double   calculatedBasePrice;
    private double   calculatedTotalPrice;
    private Passenger bookingPassenger;    // passenger details for current booking
    private Payment   currentPayment;

    private AppSession() {}

    public static AppSession getInstance() {
        if (instance == null) instance = new AppSession();
        return instance;
    }

    // ── Auth accessors ─────────────────────────────────────────────────────────
    public User    getCurrentUser()  { return currentUser; }
    public boolean isGuest()         { return isGuest; }
    public String  getGuestName()    { return guestName; }
    public String  getGuestEmail()   { return guestEmail; }

    public void loginUser(User u)    { currentUser = u; isGuest = false; }
    public void loginGuest(String name, String email) {
        isGuest = true; guestName = name; guestEmail = email; currentUser = null;
    }
    public void logout() {
        currentUser = null; isGuest = false; guestName = null; guestEmail = null;
        clearBookingFlow();
    }

    public String getDisplayName() {
        if (isGuest) return guestName + " (Guest)";
        if (currentUser != null) return currentUser.getUsername();
        return "Not logged in";
    }

    public boolean isLoggedIn() { return currentUser != null || isGuest; }

    // ── Booking flow accessors ─────────────────────────────────────────────────
    public Flight   getSelectedFlight()    { return selectedFlight; }
    public String   getSelectedSeat()      { return selectedSeatNumber; }
    public String   getSelectedClass()     { return selectedClass; }
    public double   getBasePrice()         { return calculatedBasePrice; }
    public double   getTotalPrice()        { return calculatedTotalPrice; }
    public Passenger getBookingPassenger() { return bookingPassenger; }
    public Payment  getCurrentPayment()    { return currentPayment; }

    public void setSelectedFlight(Flight f)          { selectedFlight = f; }
    public void setSelectedSeat(String s)            { selectedSeatNumber = s; }
    public void setSelectedClass(String c)           { selectedClass = c; }
    public void setBasePrice(double p)               { calculatedBasePrice = p; }
    public void setTotalPrice(double p)              { calculatedTotalPrice = p; }
    public void setBookingPassenger(Passenger p)     { bookingPassenger = p; }
    public void setCurrentPayment(Payment pay)       { currentPayment = pay; }

    public void clearBookingFlow() {
        selectedFlight = null; selectedSeatNumber = null; selectedClass = null;
        calculatedBasePrice = 0; calculatedTotalPrice = 0;
        bookingPassenger = null; currentPayment = null;
    }

    // ── Convenience ───────────────────────────────────────────────────────────
    public String getBookingUsername() {
        return isGuest ? (guestName + "_guest") : (currentUser != null ? currentUser.getUsername() : "unknown");
    }
}
