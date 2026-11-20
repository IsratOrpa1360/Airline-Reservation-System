package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private MainFrame frame;
    private JPanel statsPanel;
    private JPanel recentPanel;
    private JLabel greetLabel;

    public DashboardPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BLUE);
        header.setBorder(new EmptyBorder(24, 30, 24, 30));

        greetLabel = UITheme.label("Welcome!", UITheme.F_H1, Color.WHITE);
        JLabel sub = UITheme.label("Where would you like to fly today?", new Font("Segoe UI", Font.ITALIC, 15), new Color(200, 225, 255));

        JButton searchBtn = UITheme.primaryButton("🔍  Search Flights");
        searchBtn.setBackground(UITheme.GOLD);
        searchBtn.setForeground(UITheme.NAVY);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setPreferredSize(new Dimension(180, 44));
        searchBtn.addActionListener(e -> frame.gotoSearch());

        JPanel leftHdr = new JPanel(new GridLayout(2, 1, 0, 6));
        leftHdr.setOpaque(false);
        leftHdr.add(greetLabel);
        leftHdr.add(sub);

        header.add(leftHdr, BorderLayout.WEST);
        header.add(searchBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Main body
        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setBackground(UITheme.SURFACE);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));

        statsPanel  = new JPanel(new GridLayout(1, 4, 12, 0));
        statsPanel.setOpaque(false);
        body.add(statsPanel, BorderLayout.NORTH);

        recentPanel = new JPanel(new BorderLayout());
        recentPanel.setOpaque(false);
        body.add(recentPanel, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
    }

    public void refresh() {
        AppSession ses = AppSession.getInstance();
        greetLabel.setText("Welcome back, " + ses.getDisplayName() + "!");

        // Stats cards
        statsPanel.removeAll();
        BookingManagement bm = new BookingManagement();
        FlightManagement  fm = new FlightManagement();

        List<Booking> myBookings   = bm.getBookingsForUser(ses.getBookingUsername());
        long confirmed = myBookings.stream().filter(b -> "Confirmed".equals(b.getBookingStatus())).count();
        int totalFlights = fm.getAllFlights().size();

        int pts = 0;
        String tier = "—";
        if (!ses.isGuest() && ses.getCurrentUser() != null) {
            pts  = ses.getCurrentUser().getLoyaltyPoints();
            tier = ses.getCurrentUser().getMembershipTier();
        }

        statsPanel.add(statCard("✈",  "Available Flights",  String.valueOf(totalFlights),  UITheme.SKY));
        statsPanel.add(statCard("📋", "My Bookings",         String.valueOf(myBookings.size()), UITheme.BLUE));
        statsPanel.add(statCard("✅", "Confirmed",           String.valueOf(confirmed),       UITheme.SUCCESS));
        statsPanel.add(statCard("⭐", "Loyalty Points",      pts + " (" + tier + ")",         new Color(200, 130, 0)));

        // Recent bookings
        recentPanel.removeAll();
        JLabel rtitle = UITheme.label("Recent Bookings", UITheme.F_H3, UITheme.NAVY);
        rtitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        recentPanel.add(rtitle, BorderLayout.NORTH);

        if (myBookings.isEmpty()) {
            JLabel empty = UITheme.label("No bookings yet. Search for a flight to get started!", UITheme.F_BODY, UITheme.TEXT_MUTED);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            recentPanel.add(empty, BorderLayout.CENTER);
        } else {
            String[] cols = {"Booking ID", "Flight", "Seat", "Class", "Passenger", "Total (৳)", "Status", "Date"};
            Object[][] data = new Object[Math.min(myBookings.size(), 10)][8];
            List<Booking> recent = myBookings.subList(Math.max(0, myBookings.size() - 10), myBookings.size());
            for (int i = 0; i < recent.size(); i++) {
                Booking b = recent.get(i);
                data[i] = new Object[]{b.getBookingId(), b.getFlightNumber(), b.getSeatNumber(),
                        b.getSeatClass(), b.getPassengerName(),
                        String.format("%.2f", b.getTotalCost()), b.getBookingStatus(), b.getBookingDate()};
            }
            JTable t = buildTable(cols, data);
            JScrollPane sp = new JScrollPane(t);
            sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
            recentPanel.add(sp, BorderLayout.CENTER);
        }

        // Quick actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.setOpaque(false);

        JButton bSearch = UITheme.primaryButton("Search Flights");
        bSearch.addActionListener(e -> frame.gotoSearch());
        JButton bHistory = UITheme.outlineButton("View All Bookings");
        bHistory.addActionListener(e -> frame.gotoHistory());
        JButton bMap = UITheme.outlineButton("Route Map");
        bMap.addActionListener(e -> frame.showPanel("routemap"));

        actions.add(bSearch); actions.add(bHistory); actions.add(bMap);
        recentPanel.add(actions, BorderLayout.SOUTH);

        revalidate(); repaint();
    }

    private JPanel statCard(String icon, String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(UITheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            new EmptyBorder(16, 16, 16, 16)));

        JLabel ico = UITheme.label(icon + "  " + label, UITheme.F_SMALL, UITheme.TEXT_MUTED);
        JLabel val = UITheme.label(value, UITheme.F_H2, UITheme.TEXT_DARK);

        card.add(ico, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private JTable buildTable(String[] cols, Object[][] data) {
        JTable t = new JTable(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        t.setFont(UITheme.F_BODY);
        t.setRowHeight(28);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(UITheme.LIGHT_BLUE);
        t.getTableHeader().setFont(UITheme.F_TH);
        t.getTableHeader().setBackground(new Color(240, 244, 255));
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        return t;
    }
}
