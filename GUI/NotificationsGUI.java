package GUI;

import Entities_All.User;
import FileManagement.BookingManagement;
import Entities_All.Booking;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NotificationsGUI extends JFrame {

    private DefaultTableModel model;
    private final User currentUser;

    public NotificationsGUI(User user) {
        this.currentUser = user;
        setTitle("Notifications");
        setSize(820, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.add(SimpleTheme.createHeader("Notifications", e -> dispose()), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createLineBorder(SimpleTheme.BORDER_COL));
        JButton btnRefresh = SimpleTheme.createButton("Refresh");
        JButton btnBooking = SimpleTheme.createButton("Booking Confirmations");
        JButton btnSystem  = SimpleTheme.createButton("System Messages");
        btnRefresh.setPreferredSize(new Dimension(90, 28));
        btnBooking.setPreferredSize(new Dimension(190, 28));
        btnSystem .setPreferredSize(new Dimension(160, 28));
        btnRefresh.addActionListener(e -> loadNotifications());
        btnBooking.addActionListener(e -> loadBookingNotifications());
        btnSystem .addActionListener(e -> loadSystemMessages());
        toolbar.add(btnRefresh); toolbar.add(btnBooking); toolbar.add(btnSystem);
        content.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Type", "Message", "Date/Time"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        SimpleTheme.styleTable(table);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(500);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(SimpleTheme.BORDER_COL));

        JPanel tablePanel = new JPanel(new BorderLayout(0, 4));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(SimpleTheme.createBoldLabel("Recent Notifications"), BorderLayout.NORTH);
        tablePanel.add(sp, BorderLayout.CENTER);
        content.add(tablePanel, BorderLayout.CENTER);

        main.add(content, BorderLayout.CENTER);
        add(main);
        loadNotifications();
        setVisible(true);
    }

    private void loadNotifications() {
        model.setRowCount(0);
        loadBookingNotifications();
        loadSystemMessages();
    }

    private void loadBookingNotifications() {
        model.setRowCount(0);
        if (currentUser == null) return;
        List<Booking> bookings = new BookingManagement().getBookingsForUser(currentUser.getUsername());
        for (Booking b : bookings) {
            String msg, type;
            if ("Confirmed".equals(b.getBookingStatus())) {
                type = "Booking Confirmed";
                msg  = "Booking " + b.getBookingId() + " confirmed for flight " +
                       b.getFlightNumber() + ", seat " + b.getSeatNumber() +
                       ". Total: BDT " + String.format("%,.2f", b.getTotalCost());
            } else {
                type = "Booking Cancelled";
                msg  = "Booking " + b.getBookingId() + " for flight " +
                       b.getFlightNumber() + " has been cancelled.";
            }
            model.addRow(new Object[]{type, msg, b.getBookingDate()});
        }
        if (bookings.isEmpty()) {
            model.addRow(new Object[]{"Info", "No booking notifications found.", "—"});
        }
    }

    private void loadSystemMessages() {
        String user = currentUser != null ? currentUser.getUsername() : "Guest";
        String tier = currentUser != null ? currentUser.getMembershipTier() : "Guest";
        int pts     = currentUser != null ? currentUser.getLoyaltyPoints()  : 0;

        model.addRow(new Object[]{"Welcome", "Logged in as: " + user + "   Tier: " + tier + "   Points: " + pts, "System"});
        if (pts > 0) {
            model.addRow(new Object[]{"Loyalty", "You have " + pts + " loyalty points.", "System"});
        }
    }
}
