package Entities_All;

public class Booking {
    private String  bookingId;
    private String  username;
    private String  flightNumber;
    private String  seatNumber;
    private String  seatClass;
    // Embedded passenger snapshot (avoids separate lookup)
    private String  passengerName;
    private String  passportNumber;
    private String  nationality;
    private String  phone;
    private String  email;
    private String  emergencyContact;
    private String  preferredMeal;
    private String  dob;
    // Booking financials
    private double  totalCost;
    private boolean isPaymentComplete;
    private String  bookingStatus;   // Confirmed / Cancelled / Pending
    private String  bookingDate;

    public Booking() {}

    public Booking(String bookingId, String username, String flightNumber,
                   String seatNumber, String seatClass,
                   String passengerName, String passportNumber, String nationality,
                   String phone, String email, String emergencyContact,
                   String preferredMeal, String dob,
                   double totalCost, boolean isPaymentComplete,
                   String bookingStatus, String bookingDate) {
        this.bookingId         = bookingId;
        this.username          = username;
        this.flightNumber      = flightNumber;
        this.seatNumber        = seatNumber;
        this.seatClass         = seatClass;
        this.passengerName     = passengerName;
        this.passportNumber    = passportNumber;
        this.nationality       = nationality;
        this.phone             = phone;
        this.email             = email;
        this.emergencyContact  = emergencyContact;
        this.preferredMeal     = preferredMeal;
        this.dob               = dob;
        this.totalCost         = totalCost;
        this.isPaymentComplete = isPaymentComplete;
        this.bookingStatus     = bookingStatus;
        this.bookingDate       = bookingDate;
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public String  getBookingId()        { return bookingId; }
    public String  getUsername()         { return username; }
    public String  getFlightNumber()     { return flightNumber; }
    public String  getSeatNumber()       { return seatNumber; }
    public String  getSeatClass()        { return seatClass; }
    public String  getPassengerName()    { return passengerName; }
    public String  getPassportNumber()   { return passportNumber; }
    public String  getNationality()      { return nationality; }
    public String  getPhone()            { return phone; }
    public String  getEmail()            { return email; }
    public String  getEmergencyContact() { return emergencyContact; }
    public String  getPreferredMeal()    { return preferredMeal; }
    public String  getDob()              { return dob; }
    public double  getTotalCost()        { return totalCost; }
    public boolean isPaymentComplete()   { return isPaymentComplete; }
    public String  getBookingStatus()    { return bookingStatus; }
    public String  getBookingDate()      { return bookingDate; }

    // ── Setters ────────────────────────────────────────────────────────────────
    public void setBookingId(String id)              { bookingId = id; }
    public void setUsername(String u)                { username = u; }
    public void setFlightNumber(String fn)           { flightNumber = fn; }
    public void setSeatNumber(String sn)             { seatNumber = sn; }
    public void setSeatClass(String sc)              { seatClass = sc; }
    public void setPassengerName(String n)           { passengerName = n; }
    public void setPassportNumber(String pp)         { passportNumber = pp; }
    public void setNationality(String n)             { nationality = n; }
    public void setPhone(String p)                   { phone = p; }
    public void setEmail(String e)                   { email = e; }
    public void setEmergencyContact(String ec)       { emergencyContact = ec; }
    public void setPreferredMeal(String m)           { preferredMeal = m; }
    public void setDob(String d)                     { dob = d; }
    public void setTotalCost(double c)               { totalCost = c; }
    public void setPaymentComplete(boolean pc)       { isPaymentComplete = pc; }
    public void setBookingStatus(String s)           { bookingStatus = s; }
    public void setBookingDate(String d)             { bookingDate = d; }

    // ── Business methods ───────────────────────────────────────────────────────
    public void processBooking() {
        this.bookingStatus     = "Confirmed";
        this.isPaymentComplete = true;
    }

    public void cancelBooking() {
        this.bookingStatus     = "Cancelled";
        this.isPaymentComplete = false;
    }

    public String getBookingSummary() {
        return String.format(
            "Booking ID: %s\nFlight: %s | Seat: %s (%s)\nPassenger: %s\nTotal: ৳%.2f\nStatus: %s",
            bookingId, flightNumber, seatNumber, seatClass,
            passengerName, totalCost, bookingStatus);
    }

    public boolean isCancelled() { return "Cancelled".equals(bookingStatus); }

    /** Format for file persistence — uses | as secondary delimiter for safety */
    public String toFileLine() {
        return String.join(";",
            bookingId, username, flightNumber, seatNumber, seatClass,
            passengerName, passportNumber, nationality, phone, email,
            emergencyContact, preferredMeal, dob,
            String.valueOf(totalCost), String.valueOf(isPaymentComplete),
            bookingStatus, bookingDate);
    }

    public static Booking fromFileLine(String line) {
        String[] d = line.split(";", -1);
        if (d.length < 17) return null;
        try {
            return new Booking(d[0], d[1], d[2], d[3], d[4],
                d[5], d[6], d[7], d[8], d[9], d[10], d[11], d[12],
                Double.parseDouble(d[13]), Boolean.parseBoolean(d[14]),
                d[15], d[16]);
        } catch (Exception ex) { return null; }
    }

    @Override
    public String toString() {
        return bookingId + " | " + flightNumber + " | " + passengerName + " | " + bookingStatus;
    }
}
