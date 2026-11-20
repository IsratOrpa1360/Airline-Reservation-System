package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class BookingHistoryPanel extends JPanel {

    private MainFrame frame;
    private DefaultTableModel tableModel;
    private JTable    table;
    private JLabel    statusLabel;
    private List<Booking> currentBookings;

    public BookingHistoryPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        add(pageHeader("📋  My Bookings", "View and manage your flight bookings"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(UITheme.SURFACE);
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Table card
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Table header row
        JPanel tHdr = new JPanel(new BorderLayout());
        tHdr.setBackground(UITheme.BLUE);
        tHdr.setBorder(new EmptyBorder(10, 16, 10, 16));
        tHdr.add(UITheme.label("Booking History", UITheme.F_H4, Color.WHITE), BorderLayout.WEST);
        statusLabel = UITheme.label("0 bookings", UITheme.F_SMALL, new Color(200, 225, 255));
        tHdr.add(statusLabel, BorderLayout.EAST);
        card.add(tHdr, BorderLayout.NORTH);

        // Table
        String[] cols = {"Booking ID", "Flight", "Route", "Date", "Seat", "Class",
                         "Passenger", "Total (৳)", "Status", "Booked On"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(UITheme.F_BODY);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(UITheme.LIGHT_BLUE);
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setFont(UITheme.F_TH);
        th.setBackground(new Color(245, 248, 255));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        th.setPreferredSize(new Dimension(0, 32));

        // Color status column
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!sel) setBackground(row % 2 == 0 ? UITheme.WHITE : UITheme.SURFACE);
                if (col == 8 && val != null) {
                    if ("Confirmed".equals(val.toString())) { setForeground(UITheme.SUCCESS); }
                    else if ("Cancelled".equals(val.toString())) { setForeground(UITheme.DANGER); }
                    else setForeground(UITheme.WARNING);
                } else {
                    setForeground(sel ? UITheme.TEXT_DARK : UITheme.TEXT_DARK);
                }
                return this;
            }
        });

        int[] widths = {100, 75, 140, 100, 65, 85, 130, 90, 90, 100};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        card.add(sp, BorderLayout.CENTER);

        // Action panel
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.setBackground(new Color(248, 250, 255));
        actions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        JButton cancelBtn = UITheme.dangerButton("Cancel Selected Booking");
        cancelBtn.addActionListener(e -> cancelSelected());

        JButton refreshBtn = UITheme.outlineButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());

        JButton newBooking = UITheme.primaryButton("Book a New Flight");
        newBooking.addActionListener(e -> { AppSession.getInstance().clearBookingFlow(); frame.gotoSearch(); });

        actions.add(cancelBtn);
        actions.add(refreshBtn);
        actions.add(newBooking);

        JLabel hint = UITheme.label("Select a row and click Cancel to cancel a confirmed booking.", UITheme.F_SMALL, UITheme.TEXT_MUTED);
        actions.add(hint);

        card.add(actions, BorderLayout.SOUTH);
        body.add(card, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    public void refresh() {
        AppSession ses = AppSession.getInstance();
        BookingManagement bm = new BookingManagement();
        FlightManagement  fm = new FlightManagement();

        currentBookings = bm.getBookingsForUser(ses.getBookingUsername());
        tableModel.setRowCount(0);

        for (Booking b : currentBookings) {
            Flight f = fm.findByNumber(b.getFlightNumber());
            String route = f != null ? f.getOrigin() + " → " + f.getDestination() : b.getFlightNumber();
            String date  = f != null ? f.getDepartureDate() : "—";
            tableModel.addRow(new Object[]{
                b.getBookingId(), b.getFlightNumber(), route, date,
                b.getSeatNumber(), b.getSeatClass(), b.getPassengerName(),
                String.format("%.2f", b.getTotalCost()), b.getBookingStatus(), b.getBookingDate()
            });
        }

        long confirmed = currentBookings.stream().filter(b -> "Confirmed".equals(b.getBookingStatus())).count();
        statusLabel.setText(currentBookings.size() + " booking(s)  |  " + confirmed + " confirmed");
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String bookingId = (String) tableModel.getValueAt(row, 0);
        String status    = (String) tableModel.getValueAt(row, 8);

        if ("Cancelled".equals(status)) {
            JOptionPane.showMessageDialog(this, "This booking is already cancelled.", "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Cancel booking " + bookingId + "?\nThis action cannot be undone.",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        BookingManagement bm = new BookingManagement();
        Booking b = bm.findById(bookingId);
        if (b != null) {
            bm.cancelBooking(bookingId);
            new FlightManagement().decrementBooked(b.getFlightNumber());
            Notification.bookingCancelled(bookingId).show(this);
            refresh();
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
