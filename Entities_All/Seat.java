package Entities_All;

public class Seat {
    public enum Status { AVAILABLE, SELECTED, BOOKED, BLOCKED }

    private String seatNumber;   // e.g. "12A"
    private String seatClass;    // Economy / Business / First
    private Status status;

    public Seat(String seatNumber, String seatClass) {
        this.seatNumber = seatNumber;
        this.seatClass  = seatClass;
        this.status     = Status.AVAILABLE;
    }

    public String getSeatNumber() { return seatNumber; }
    public String getSeatClass()  { return seatClass; }
    public Status getStatus()     { return status; }

    public void setStatus(Status s) { status = s; }

    public boolean isAvailable() { return status == Status.AVAILABLE; }
    public boolean isBooked()    { return status == Status.BOOKED; }
    public boolean isSelected()  { return status == Status.SELECTED; }
    public boolean isBlocked()   { return status == Status.BLOCKED; }

    public void select()   { if (isAvailable()) status = Status.SELECTED; }
    public void deselect() { if (isSelected())  status = Status.AVAILABLE; }
    public void book()     { status = Status.BOOKED; }
    public void block()    { status = Status.BLOCKED; }
    public void release()  { status = Status.AVAILABLE; }

    @Override
    public String toString() {
        return seatNumber + " [" + seatClass + "] - " + status;
    }
}
