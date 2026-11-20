package Entities_All;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification {
    public enum Type {
        BOOKING_CONFIRMED, PAYMENT_SUCCESS, BOOKING_CANCELLED,
        FLIGHT_REMINDER, DISCOUNT_OFFER, REGISTRATION_SUCCESS, GENERAL
    }

    private Type   type;
    private String title;
    private String message;
    private String timestamp;
    private boolean isRead;

    public Notification(Type type, String title, String message) {
        this.type      = type;
        this.title     = title;
        this.message   = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
        this.isRead    = false;
    }

    public Type    getType()      { return type; }
    public String  getTitle()     { return title; }
    public String  getMessage()   { return message; }
    public String  getTimestamp() { return timestamp; }
    public boolean isRead()       { return isRead; }
    public void    markRead()     { isRead = true; }

    // ── Static factory methods ─────────────────────────────────────────────────
    public static Notification bookingConfirmed(String bookingId, String flight) {
        return new Notification(Type.BOOKING_CONFIRMED,
            "Booking Confirmed!",
            "Your booking " + bookingId + " for flight " + flight + " is confirmed. Have a great journey!");
    }

    public static Notification paymentSuccess(double amount, String txnId) {
        return new Notification(Type.PAYMENT_SUCCESS,
            "Payment Successful",
            String.format("Payment of ৳%.2f completed. Transaction ID: %s", amount, txnId));
    }

    public static Notification bookingCancelled(String bookingId) {
        return new Notification(Type.BOOKING_CANCELLED,
            "Booking Cancelled",
            "Your booking " + bookingId + " has been cancelled. Refund (if applicable) will be processed.");
    }

    public static Notification registrationSuccess(String username) {
        return new Notification(Type.REGISTRATION_SUCCESS,
            "Welcome Aboard!",
            "Account created for " + username + ". Start earning loyalty points with your first booking!");
    }

    public static Notification discountOffer(String message) {
        return new Notification(Type.DISCOUNT_OFFER, "Special Offer!", message);
    }

    /** Show notification using JOptionPane */
    public void show(java.awt.Component parent) {
        String icon;
        int messageType;
        switch (type) {
            case BOOKING_CONFIRMED:
            case PAYMENT_SUCCESS:
            case REGISTRATION_SUCCESS: icon = "✅  "; messageType = javax.swing.JOptionPane.INFORMATION_MESSAGE; break;
            case BOOKING_CANCELLED:    icon = "❌  "; messageType = javax.swing.JOptionPane.WARNING_MESSAGE;     break;
            case DISCOUNT_OFFER:       icon = "🎁  "; messageType = javax.swing.JOptionPane.INFORMATION_MESSAGE; break;
            default:                   icon = "ℹ  ";  messageType = javax.swing.JOptionPane.INFORMATION_MESSAGE; break;
        }
        javax.swing.JOptionPane.showMessageDialog(parent,
            icon + message, icon + title, messageType);
        markRead();
    }

    @Override
    public String toString() { return "[" + timestamp + "] " + title + ": " + message; }
}
