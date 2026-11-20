package Entities_All;

public class Flight {
    private String flightNumber;
    private String origin;
    private String destination;
    private String departureDate;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private double economyPrice;
    private double businessPrice;
    private double firstClassPrice;
    private int    totalSeats;
    private int    bookedSeats;
    private String airline;

    public Flight() {}

    public Flight(String flightNumber, String origin, String destination,
                  String departureDate, String departureTime, String arrivalTime,
                  String duration, double economyPrice, double businessPrice,
                  double firstClassPrice, int totalSeats, int bookedSeats, String airline) {
        this.flightNumber   = flightNumber;
        this.origin         = origin;
        this.destination    = destination;
        this.departureDate  = departureDate;
        this.departureTime  = departureTime;
        this.arrivalTime    = arrivalTime;
        this.duration       = duration;
        this.economyPrice   = economyPrice;
        this.businessPrice  = businessPrice;
        this.firstClassPrice = firstClassPrice;
        this.totalSeats     = totalSeats;
        this.bookedSeats    = bookedSeats;
        this.airline        = airline;
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public String getFlightNumber()   { return flightNumber; }
    public String getOrigin()         { return origin; }
    public String getDestination()    { return destination; }
    public String getDepartureDate()  { return departureDate; }
    public String getDepartureTime()  { return departureTime; }
    public String getArrivalTime()    { return arrivalTime; }
    public String getDuration()       { return duration; }
    public double getEconomyPrice()   { return economyPrice; }
    public double getBusinessPrice()  { return businessPrice; }
    public double getFirstClassPrice(){ return firstClassPrice; }
    public int    getTotalSeats()     { return totalSeats; }
    public int    getBookedSeats()    { return bookedSeats; }
    public String getAirline()        { return airline; }
    public int    getAvailableSeats() { return totalSeats - bookedSeats; }

    // ── Setters ────────────────────────────────────────────────────────────────
    public void setFlightNumber(String n)    { flightNumber = n; }
    public void setOrigin(String o)          { origin = o; }
    public void setDestination(String d)     { destination = d; }
    public void setDepartureDate(String d)   { departureDate = d; }
    public void setDepartureTime(String t)   { departureTime = t; }
    public void setArrivalTime(String t)     { arrivalTime = t; }
    public void setDuration(String d)        { duration = d; }
    public void setEconomyPrice(double p)    { economyPrice = p; }
    public void setBusinessPrice(double p)   { businessPrice = p; }
    public void setFirstClassPrice(double p) { firstClassPrice = p; }
    public void setTotalSeats(int n)         { totalSeats = n; }
    public void setBookedSeats(int n)        { bookedSeats = n; }
    public void setAirline(String a)         { airline = a; }

    // ── Business methods ───────────────────────────────────────────────────────
    public boolean checkSeatAvailability() {
        return getAvailableSeats() > 0;
    }

    public void bookSeat() {
        if (checkSeatAvailability()) bookedSeats++;
    }

    public void releaseSeat() {
        if (bookedSeats > 0) bookedSeats--;
    }

    /** Dynamic price for a given class based on demand */
    public double getDynamicPrice(String flightClass) {
        double base;
        switch (flightClass) {
            case "Business":   base = businessPrice;   break;
            case "First":      base = firstClassPrice; break;
            default:           base = economyPrice;    break;
        }
        double fillRatio = totalSeats > 0 ? (double) bookedSeats / totalSeats : 0;
        if      (fillRatio >= 0.90) base *= 1.20;
        else if (fillRatio >= 0.70) base *= 1.10;
        return base;
    }

    public String getFlightDetails() {
        return String.format("%s | %s → %s | %s %s – %s (%s) | Seats: %d/%d",
                flightNumber, origin, destination, departureDate,
                departureTime, arrivalTime, duration,
                getAvailableSeats(), totalSeats);
    }

    /** Format for file persistence */
    public String toFileLine() {
        return flightNumber + ";" + origin + ";" + destination + ";" +
               departureDate + ";" + departureTime + ";" + arrivalTime + ";" +
               duration + ";" + economyPrice + ";" + businessPrice + ";" +
               firstClassPrice + ";" + totalSeats + ";" + bookedSeats + ";" + airline;
    }

    public static Flight fromFileLine(String line) {
        String[] d = line.split(";", -1);
        if (d.length < 13) return null;
        try {
            return new Flight(d[0], d[1], d[2], d[3], d[4], d[5], d[6],
                    Double.parseDouble(d[7]), Double.parseDouble(d[8]),
                    Double.parseDouble(d[9]), Integer.parseInt(d[10]),
                    Integer.parseInt(d[11]), d[12]);
        } catch (Exception ex) { return null; }
    }

    @Override
    public String toString() {
        return flightNumber + " | " + origin + " → " + destination + " | " + departureDate;
    }
}
