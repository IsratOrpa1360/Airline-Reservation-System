package GUI;

import Entities_All.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class PassengerInfoPanel extends JPanel {

    private MainFrame frame;

    private JTextField txtName, txtPassport, txtNationality, txtPhone;
    private JTextField txtEmail, txtEmergency, txtDob, txtFreqFlyer;
    private JComboBox<String> cmbMeal;
    private JLabel flightSummaryLabel;

    public PassengerInfoPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        add(pageHeader("👤  Passenger Information", "Enter your travel details"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setBackground(UITheme.SURFACE);
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Left: form
        JPanel formCard = UITheme.card();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = UITheme.label("Passenger Details", UITheme.F_H3, UITheme.NAVY);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(title);

        JPanel grid = new JPanel(new GridLayout(0, 2, 14, 8));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtName     = addField(grid, "Full Name *");
        txtPassport = addField(grid, "Passport Number *");
        txtNationality = addField(grid, "Nationality *");
        txtPhone    = addField(grid, "Phone Number *");
        txtEmail    = addField(grid, "Email Address *");
        txtEmergency = addField(grid, "Emergency Contact *");
        txtDob      = addField(grid, "Date of Birth  (DD/MM/YYYY)");
        txtFreqFlyer = addField(grid, "Frequent Flyer No. (optional)");

        formCard.add(grid);
        formCard.add(Box.createVerticalStrut(12));

        // Meal row
        JPanel mealRow = new JPanel(new BorderLayout(10, 0));
        mealRow.setOpaque(false);
        mealRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        mealRow.add(UITheme.label("Preferred Meal", UITheme.F_H4, UITheme.TEXT_MED), BorderLayout.WEST);
        cmbMeal = new JComboBox<>(MealPreference.getMealTypeNames());
        cmbMeal.setFont(UITheme.F_INPUT);
        cmbMeal.setPreferredSize(new Dimension(220, 36));
        mealRow.add(cmbMeal, BorderLayout.CENTER);
        formCard.add(mealRow);

        formCard.add(Box.createVerticalStrut(18));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton continueBtn = UITheme.primaryButton("Proceed to Payment →");
        continueBtn.setPreferredSize(new Dimension(220, 40));
        continueBtn.addActionListener(e -> handleContinue());

        JButton backBtn = UITheme.outlineButton("← Back to Seats");
        backBtn.setPreferredSize(new Dimension(160, 40));
        backBtn.addActionListener(e -> frame.gotoSeatSelection());

        btnRow.add(backBtn);
        btnRow.add(continueBtn);
        formCard.add(btnRow);

        // Right: booking summary
        JPanel summaryCard = buildSummaryCard();
        summaryCard.setPreferredSize(new Dimension(280, 0));

        JScrollPane formScroll = new JScrollPane(formCard);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);

        body.add(formScroll,    BorderLayout.CENTER);
        body.add(summaryCard,   BorderLayout.EAST);
        add(body, BorderLayout.CENTER);
    }

    private JTextField addField(JPanel grid, String label) {
        grid.add(UITheme.label(label, UITheme.F_H4, UITheme.TEXT_MED));
        JTextField f = UITheme.styledField();
        f.setPreferredSize(new Dimension(0, 36));
        grid.add(f);
        return f;
    }

    private JPanel buildSummaryCard() {
        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 16, 18, 16));

        card.add(UITheme.label("Booking Summary", UITheme.F_H4, UITheme.NAVY));
        card.add(Box.createVerticalStrut(12));

        flightSummaryLabel = new JLabel();
        flightSummaryLabel.setFont(UITheme.F_BODY);
        flightSummaryLabel.setForeground(UITheme.TEXT_MED);
        flightSummaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(flightSummaryLabel);

        card.add(Box.createVerticalStrut(16));
        card.add(UITheme.label("Meal Options", UITheme.F_H4, UITheme.NAVY));
        card.add(Box.createVerticalStrut(8));

        for (MealPreference m : MealPreference.ALL_MEALS) {
            if (m.getCalories() == 0) continue;
            JLabel ml = UITheme.label("• " + m.getMealType() + " (" + m.getCalories() + " kcal)", UITheme.F_SMALL, UITheme.TEXT_MED);
            ml.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(ml);
            card.add(Box.createVerticalStrut(2));
        }
        return card;
    }

    private void handleContinue() {
        String name = txtName.getText().trim();
        String pp   = txtPassport.getText().trim();
        String nat  = txtNationality.getText().trim();
        String ph   = txtPhone.getText().trim();
        String em   = txtEmail.getText().trim();
        String ec   = txtEmergency.getText().trim();
        String dob  = txtDob.getText().trim();
        String ff   = txtFreqFlyer.getText().trim();
        String meal = (String) cmbMeal.getSelectedItem();

        if (name.isEmpty() || pp.isEmpty() || nat.isEmpty() || ph.isEmpty() || em.isEmpty() || ec.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required (*) fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!em.contains("@")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!pp.matches("[A-Za-z0-9]{6,20}")) {
            JOptionPane.showMessageDialog(this, "Passport number must be 6-20 alphanumeric characters.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AppSession ses = AppSession.getInstance();
        Passenger p = new Passenger(0, name, ses.getBookingUsername(), "Unknown",
                em, ph, pp, ses.getSelectedFlight() != null ? ses.getSelectedFlight().getFlightNumber() : "",
                nat, ec, meal, dob.isEmpty() ? "N/A" : dob);
        ses.setBookingPassenger(p);
        frame.gotoPayment();
    }

    // Called before showing this panel
    public void refresh() {
        AppSession ses = AppSession.getInstance();
        Flight f = ses.getSelectedFlight();
        if (f == null) return;

        String info = "<html><b>Flight:</b> " + f.getFlightNumber() + "<br>" +
                "<b>Route:</b> " + f.getOrigin() + " → " + f.getDestination() + "<br>" +
                "<b>Date:</b> " + f.getDepartureDate() + "<br>" +
                "<b>Seat:</b> " + ses.getSelectedSeat() + "<br>" +
                "<b>Class:</b> " + ses.getSelectedClass() + "<br>" +
                "<b>Est. Total:</b> ৳" + String.format("%,.2f", ses.getTotalPrice()) + "</html>";
        flightSummaryLabel.setText(info);

        // Pre-fill email from session
        if (!ses.isGuest() && ses.getCurrentUser() != null && txtEmail.getText().isEmpty()) {
            txtEmail.setText(ses.getCurrentUser().getEmail());
        } else if (ses.isGuest() && txtEmail.getText().isEmpty()) {
            txtEmail.setText(ses.getGuestEmail());
            txtName.setText(ses.getGuestName());
        }
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
