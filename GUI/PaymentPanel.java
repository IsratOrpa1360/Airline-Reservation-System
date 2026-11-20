package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PaymentPanel extends JPanel {

    private MainFrame frame;

    private JLabel   fareLbl, surchargeLbl, demandLbl, taxesLbl, serviceLbl, totalLbl;
    private JLabel   summaryLbl;
    private JComboBox<String> cmbMethod;
    private JTextField txtCardNumber, txtCardHolder, txtExpiry, txtCvv;
    private JPanel   cardFieldsPanel;

    public PaymentPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        add(pageHeader("💳  Payment", "Complete your booking with a simulated payment"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setBackground(UITheme.SURFACE);
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        body.add(buildPriceBreakdown());
        body.add(buildPaymentForm());

        add(body, BorderLayout.CENTER);
    }

    private JPanel buildPriceBreakdown() {
        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        card.add(UITheme.label("Price Breakdown", UITheme.F_H3, UITheme.NAVY));
        card.add(Box.createVerticalStrut(16));

        summaryLbl = new JLabel();
        summaryLbl.setFont(UITheme.F_SMALL);
        summaryLbl.setForeground(UITheme.TEXT_MUTED);
        summaryLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(summaryLbl);
        card.add(Box.createVerticalStrut(14));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        card.add(sep);
        card.add(Box.createVerticalStrut(10));

        fareLbl      = priceRow(card, "Base Fare");
        surchargeLbl = priceRow(card, "Class Surcharge");
        demandLbl    = priceRow(card, "Demand Adjustment");
        taxesLbl     = priceRow(card, "Taxes (15%)");
        serviceLbl   = priceRow(card, "Service Charges");

        card.add(Box.createVerticalStrut(8));
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(UITheme.NAVY);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        card.add(sep2);
        card.add(Box.createVerticalStrut(8));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        totalRow.add(UITheme.label("TOTAL", new Font("Segoe UI", Font.BOLD, 16), UITheme.NAVY), BorderLayout.WEST);
        totalLbl = UITheme.label("৳ 0.00", new Font("Segoe UI", Font.BOLD, 20), UITheme.SKY);
        totalRow.add(totalLbl, BorderLayout.EAST);
        card.add(totalRow);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JLabel priceRow(JPanel parent, String label) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.add(UITheme.label(label, UITheme.F_BODY, UITheme.TEXT_MED), BorderLayout.WEST);
        JLabel val = UITheme.label("৳ 0.00", UITheme.F_BODY, UITheme.TEXT_DARK);
        row.add(val, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(4));
        return val;
    }

    private JPanel buildPaymentForm() {
        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        card.add(UITheme.label("Payment Method", UITheme.F_H3, UITheme.NAVY));
        card.add(Box.createVerticalStrut(12));

        cmbMethod = new JComboBox<>(new String[]{
                "Credit Card", "Debit Card", "Mobile Banking (bKash/Nagad)", "Cash / Offline Payment"});
        cmbMethod.setFont(UITheme.F_INPUT);
        cmbMethod.setPreferredSize(new Dimension(0, 38));
        cmbMethod.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbMethod.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbMethod.addActionListener(e -> toggleCardFields());
        card.add(cmbMethod);

        card.add(Box.createVerticalStrut(16));

        // Card fields (shown for Card payments)
        cardFieldsPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        cardFieldsPanel.setOpaque(false);
        cardFieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtCardHolder = addCField(cardFieldsPanel, "Cardholder Name");
        txtCardNumber = addCField(cardFieldsPanel, "Card Number (16 digits)");
        txtExpiry     = addCField(cardFieldsPanel, "Expiry (MM/YY)");
        txtCvv        = addCField(cardFieldsPanel, "CVV (3 digits)");
        card.add(cardFieldsPanel);

        card.add(Box.createVerticalStrut(16));
        card.add(Box.createVerticalGlue());

        JLabel disclaimer = UITheme.label("⚠  SIMULATION ONLY — No real payment is processed. Do not enter real card details.",
                UITheme.F_SMALL, UITheme.WARNING);
        disclaimer.setAlignmentX(Component.LEFT_ALIGNMENT);
        disclaimer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.WARNING_BG),
                new EmptyBorder(8, 10, 8, 10)));
        disclaimer.setBackground(UITheme.WARNING_BG);
        disclaimer.setOpaque(true);
        card.add(disclaimer);
        card.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backBtn = UITheme.outlineButton("← Back");
        backBtn.addActionListener(e -> frame.gotoPassengerInfo());

        JButton payBtn = UITheme.successButton("✅  Confirm & Pay");
        payBtn.addActionListener(e -> handlePayment());

        btnRow.add(backBtn);
        btnRow.add(payBtn);
        card.add(btnRow);

        return card;
    }

    private JTextField addCField(JPanel panel, String label) {
        panel.add(UITheme.label(label, UITheme.F_H4, UITheme.TEXT_MED));
        JTextField f = UITheme.styledField();
        f.setPreferredSize(new Dimension(0, 36));
        panel.add(f);
        return f;
    }

    private void toggleCardFields() {
        String method = (String) cmbMethod.getSelectedItem();
        boolean showCard = method != null &&
                (method.startsWith("Credit") || method.startsWith("Debit"));
        cardFieldsPanel.setVisible(showCard);
        revalidate();
    }

    public void refresh() {
        AppSession ses = AppSession.getInstance();
        Flight f = ses.getSelectedFlight();
        if (f == null) return;

        String cls = ses.getSelectedClass();
        double base = f.getDynamicPrice(cls != null ? cls : "Economy");

        // Compute components
        double clsSurcharge = 0;
        double demand = 0;
        double fill = f.getTotalSeats() > 0 ? (double) f.getBookedSeats() / f.getTotalSeats() : 0;
        if (fill >= 0.90)      demand = base * 0.20;
        else if (fill >= 0.70) demand = base * 0.10;

        Payment pay = new Payment(base, clsSurcharge, demand, "");
        ses.setTotalPrice(pay.calculateTotal());

        // Booking summary
        Passenger p = ses.getBookingPassenger();
        summaryLbl.setText("<html><b>Flight:</b> " + f.getFlightNumber() + "  " +
                f.getOrigin() + " → " + f.getDestination() + "<br>" +
                "<b>Date:</b> " + f.getDepartureDate() + "  |  " +
                "<b>Seat:</b> " + ses.getSelectedSeat() + "  |  " +
                "<b>Class:</b> " + cls + "<br>" +
                (p != null ? "<b>Passenger:</b> " + p.getName() : "") + "</html>");

        fareLbl.setText(String.format("৳ %,.2f", base));
        surchargeLbl.setText(String.format("৳ %,.2f", clsSurcharge));
        demandLbl.setText(String.format("৳ %,.2f", demand));
        taxesLbl.setText(String.format("৳ %,.2f", pay.getTaxes()));
        serviceLbl.setText(String.format("৳ %,.2f", pay.getServiceCharges()));
        totalLbl.setText(String.format("৳ %,.2f", pay.calculateTotal()));

        toggleCardFields();
    }

    private void handlePayment() {
        AppSession ses = AppSession.getInstance();
        Flight f = ses.getSelectedFlight();
        Passenger p = ses.getBookingPassenger();

        if (f == null || p == null || ses.getSelectedSeat() == null) {
            JOptionPane.showMessageDialog(this, "Booking data incomplete. Please restart the booking flow.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String method = (String) cmbMethod.getSelectedItem();

        // Validate card details if needed
        if (method != null && (method.startsWith("Credit") || method.startsWith("Debit"))) {
            if (txtCardNumber.getText().trim().length() < 12 ||
                txtExpiry.getText().trim().isEmpty() ||
                txtCvv.getText().trim().length() < 3) {
                JOptionPane.showMessageDialog(this, "Please fill in all card details.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Prevent duplicate seat booking
        BookingManagement bm = new BookingManagement();
        if (bm.isSeatBooked(f.getFlightNumber(), ses.getSelectedSeat())) {
            JOptionPane.showMessageDialog(this, "Seat " + ses.getSelectedSeat() + " was just booked by someone else. Please select another seat.",
                    "Seat Unavailable", JOptionPane.WARNING_MESSAGE);
            frame.gotoSeatSelection();
            return;
        }

        // Build payment
        String cls = ses.getSelectedClass();
        double base = f.getDynamicPrice(cls != null ? cls : "Economy");
        double fill = f.getTotalSeats() > 0 ? (double) f.getBookedSeats() / f.getTotalSeats() : 0;
        double demand = fill >= 0.90 ? base * 0.20 : fill >= 0.70 ? base * 0.10 : 0;
        Payment pay = new Payment(base, 0, demand, method);
        pay.processPayment();
        ses.setCurrentPayment(pay);

        // Create booking
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Booking booking = new Booking(
            bm.generateBookingId(), ses.getBookingUsername(), f.getFlightNumber(),
            ses.getSelectedSeat(), cls,
            p.getName(), p.getPassportNumber(), p.getNationality(),
            p.getPhoneNumber(), p.getEmail(), p.getEmergencyContact(),
            p.getPreferredMeal(), p.getDob(),
            pay.calculateTotal(), true, "Confirmed", today);
        bm.saveBooking(booking);

        // Update flight seat count
        new FlightManagement().incrementBooked(f.getFlightNumber());

        // Award loyalty points
        if (!ses.isGuest() && ses.getCurrentUser() != null) {
            User user = ses.getCurrentUser();
            LoyaltyProgram lp = new LoyaltyProgram(user.getUsername(), user.getLoyaltyPoints());
            lp.earnPoints(pay.calculateTotal(), "Booking " + booking.getBookingId());
            user.setLoyaltyPoints(lp.getTotalPoints());
            user.updateTier();
            new FileManagement.UserManagement().updateUser(user);
        }

        Notification.bookingConfirmed(booking.getBookingId(), f.getFlightNumber()).show(this);
        Notification.paymentSuccess(pay.calculateTotal(), pay.getTransactionId()).show(this);

        frame.gotoConfirmation();
    }

    private JPanel pageHeader(String title, String sub) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.NAVY);
        h.setBorder(new EmptyBorder(14, 24, 14, 24));
        JPanel txt = new JPanel(new GridLayout(2, 1, 0, 4));
        txt.setOpaque(false);
        txt.add(UITheme.label(title, UITheme.F_H2, Color.WHITE));
        txt.add(UITheme.label(sub, UITheme.F_BODY, new Color(180, 210, 255)));
        h.add(txt, BorderLayout.WEST);
        return h;
    }
}
