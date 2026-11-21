package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FlightSearchPanel extends JPanel {

    private MainFrame   frame;
    private JComboBox<String> cmbOrigin, cmbDest;
    private JTextField  txtDate, txtPax;
    private JComboBox<String> cmbClass;
    private JPanel      resultsPanel;
    private JLabel      resultCountLabel;

    public FlightSearchPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        //Page header
        JPanel hdr = pageHeader("🔍  Flight Search", "Find available flights and book your seat");
        add(hdr, BorderLayout.NORTH);

        //Search form card
        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout(0, 14));
        formCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel fl = UITheme.label("Search for Flights", UITheme.F_H3, UITheme.NAVY);
        fl.setBorder(new EmptyBorder(0, 0, 8, 0));
        formCard.add(fl, BorderLayout.NORTH);

        //Search fields row
        JPanel fields = new JPanel(new GridLayout(1, 5, 12, 0));
        fields.setOpaque(false);

        FlightManagement fm = new FlightManagement();
        List<String> origins = fm.getDistinctOrigins();
        List<String> dests   = fm.getDistinctDestinations();
        origins.add(0, "Any Origin");
        dests.add(0, "Any Destination");

        cmbOrigin = new JComboBox<>(origins.toArray(new String[0]));
        cmbDest   = new JComboBox<>(dests.toArray(new String[0]));
        styleCombo(cmbOrigin); styleCombo(cmbDest);

        txtDate  = UITheme.styledField(); txtDate.setPreferredSize(new Dimension(0, 38));
        txtDate.setText("YYYY-MM-DD");
        txtDate.setForeground(UITheme.TEXT_MUTED);
        txtDate.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtDate.getText().equals("YYYY-MM-DD")) { txtDate.setText(""); txtDate.setForeground(UITheme.TEXT_DARK); }
            }
            public void focusLost(FocusEvent e) {
                if (txtDate.getText().isEmpty()) { txtDate.setText("YYYY-MM-DD"); txtDate.setForeground(UITheme.TEXT_MUTED); }
            }
        });

        cmbClass = new JComboBox<>(new String[]{"All Classes", "Economy", "Business", "First"});
        styleCombo(cmbClass);

        JButton searchBtn = UITheme.primaryButton("🔍  Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setPreferredSize(new Dimension(0, 38));
        searchBtn.addActionListener(e -> performSearch());

        JPanel fieldWrapper = new JPanel(new GridLayout(2, 5, 12, 6));
        fieldWrapper.setOpaque(false);
        fieldWrapper.add(UITheme.label("From", UITheme.F_H4, UITheme.TEXT_MED));
        fieldWrapper.add(UITheme.label("To", UITheme.F_H4, UITheme.TEXT_MED));
        fieldWrapper.add(UITheme.label("Departure Date", UITheme.F_H4, UITheme.TEXT_MED));
        fieldWrapper.add(UITheme.label("Class", UITheme.F_H4, UITheme.TEXT_MED));
        fieldWrapper.add(UITheme.label("", UITheme.F_H4, UITheme.TEXT_MED));
        fieldWrapper.add(cmbOrigin);
        fieldWrapper.add(cmbDest);
        fieldWrapper.add(txtDate);
        fieldWrapper.add(cmbClass);
        fieldWrapper.add(searchBtn);
        formCard.add(fieldWrapper, BorderLayout.CENTER);

        // Results area
        resultsPanel = new JPanel();
        resultsPanel.setBackground(UITheme.SURFACE);
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        resultCountLabel = UITheme.label("Enter search criteria above and click Search.", UITheme.F_BODY, UITheme.TEXT_MUTED);
        resultCountLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        resultCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultsPanel.add(resultCountLabel);

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(UITheme.SURFACE);
        body.setBorder(new EmptyBorder(12, 14, 14, 14));
        body.add(formCard, BorderLayout.NORTH);
        body.add(scroll,   BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        //Kick off with all flights
        performSearch();
    }

    private void performSearch() {
        String origin = cmbOrigin.getSelectedIndex() == 0 ? "" : (String) cmbOrigin.getSelectedItem();
        String dest   = cmbDest.getSelectedIndex()   == 0 ? "" : (String) cmbDest.getSelectedItem();
        String date   = txtDate.getText().trim().equals("YYYY-MM-DD") ? "" : txtDate.getText().trim();
        String cls    = cmbClass.getSelectedIndex()  == 0 ? "" : (String) cmbClass.getSelectedItem();

        FlightManagement fm = new FlightManagement();
        List<Flight> flights = fm.searchFlights(origin, dest, date);

        resultsPanel.removeAll();

        if (flights.isEmpty()) {
            JLabel noResult = UITheme.label("No flights found matching your search criteria.", UITheme.F_BODY, UITheme.TEXT_MUTED);
            noResult.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultsPanel.add(noResult);
        } else {
            resultCountLabel = UITheme.label(flights.size() + " flight(s) found", UITheme.F_H4, UITheme.SKY);
            resultCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultCountLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
            resultsPanel.add(resultCountLabel);

            for (Flight f : flights) {
                resultsPanel.add(buildFlightCard(f, cls));
                resultsPanel.add(Box.createVerticalStrut(10));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel buildFlightCard(Flight f, String filterClass) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(UITheme.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(14, 18, 14, 18)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left: route + times
        JPanel left = new JPanel(new GridLayout(3, 1, 0, 3));
        left.setOpaque(false);

        JLabel route = UITheme.label(f.getOrigin() + "  →  " + f.getDestination(), UITheme.F_H3, UITheme.NAVY);
        JLabel times = UITheme.label(f.getDepartureTime() + " – " + f.getArrivalTime() +
                "   |  " + f.getDuration() + "   |  " + f.getDepartureDate(), UITheme.F_BODY, UITheme.TEXT_MED);
        JLabel info  = UITheme.label(f.getAirline() + "  ·  Flight " + f.getFlightNumber() +
                "  ·  " + f.getAvailableSeats() + "/" + f.getTotalSeats() + " seats", UITheme.F_SMALL, UITheme.TEXT_MUTED);

        left.add(route); left.add(times); left.add(info);

        //Center: prices
        JPanel prices = new JPanel(new GridLayout(3, 1, 0, 4));
        prices.setOpaque(false);
        prices.setPreferredSize(new Dimension(280, 0));
        prices.add(priceChip("Economy",  f.getDynamicPrice("Economy"),  "Economy",  f, "Economy"));
        prices.add(priceChip("Business", f.getDynamicPrice("Business"), "Business", f, "Business"));
        prices.add(priceChip("First",    f.getDynamicPrice("First"),    "First",    f, "First"));

        //Right: availability badge
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(100, 0));

        boolean avail = f.checkSeatAvailability();
        JLabel badge = UITheme.label(avail ? "Available" : "Full", UITheme.F_H4,
                avail ? UITheme.SUCCESS : UITheme.DANGER);
        badge.setOpaque(true);
        badge.setBackground(avail ? UITheme.SUCCESS_BG : UITheme.DANGER_BG);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        right.add(badge);

        card.add(left,   BorderLayout.CENTER);
        card.add(prices, BorderLayout.EAST);

        return card;
    }

    private JPanel priceChip(String cls, double price, String selClass, Flight flight, String flightClass) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JLabel lbl   = UITheme.label(cls, UITheme.F_SMALL, UITheme.TEXT_MUTED);
        JLabel pLabel = UITheme.label(String.format("৳ %,.0f", price), UITheme.F_H4, UITheme.TEXT_DARK);

        JButton btn = new JButton("Select");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(UITheme.WHITE);
        btn.setBackground(UITheme.SKY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(65, 24));
        btn.addActionListener(e -> selectFlight(flight, flightClass, price));

        if (!flight.checkSeatAvailability()) {
            btn.setEnabled(false);
            btn.setBackground(UITheme.SEAT_BLOCK);
        }

        row.add(lbl,    BorderLayout.WEST);
        row.add(pLabel, BorderLayout.CENTER);
        row.add(btn,    BorderLayout.EAST);
        return row;
    }

    private void selectFlight(Flight flight, String flightClass, double basePrice) {
        AppSession ses = AppSession.getInstance();
        ses.clearBookingFlow();
        ses.setSelectedFlight(flight);
        ses.setSelectedClass(flightClass);
        ses.setBasePrice(basePrice);
        frame.gotoSeatSelection();
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setFont(UITheme.F_INPUT);
        cb.setBackground(UITheme.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        cb.setPreferredSize(new Dimension(0, 38));
    }

    private JPanel pageHeader(String title, String sub) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.NAVY);
        h.setBorder(new EmptyBorder(16, 24, 16, 24));
        JPanel txt = new JPanel(new GridLayout(2, 1, 0, 4));
        txt.setOpaque(false);
        txt.add(UITheme.label(title, UITheme.F_H2, Color.WHITE));
        txt.add(UITheme.label(sub, UITheme.F_BODY, new Color(180, 210, 255)));
        h.add(txt, BorderLayout.WEST);
        JButton back = new JButton("← Back");
        back.setFont(UITheme.F_BTN);
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(30, 60, 130));
        back.setBorderPainted(false);
        back.setFocusPainted(false);
        back.setOpaque(true);
        back.setPreferredSize(new Dimension(120, 36));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> frame.gotoDashboard());
        h.add(back, BorderLayout.EAST);
        return h;
    }
}
