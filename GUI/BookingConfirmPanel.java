package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class BookingConfirmPanel extends JPanel {

    private MainFrame frame;
    private JPanel    contentPanel;

    public BookingConfirmPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        add(pageHeader("✅  Booking Confirmed!", "Your flight is booked. Safe travels!"), BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setBackground(UITheme.SURFACE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 60, 20, 60));

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        contentPanel.removeAll();
        AppSession ses = AppSession.getInstance();
        Flight f   = ses.getSelectedFlight();
        Passenger p = ses.getBookingPassenger();
        Payment pay = ses.getCurrentPayment();
        if (f == null || p == null) return;

        // Fetch the latest booking for this user+flight
        BookingManagement bm = new BookingManagement();
        List<Booking> bookings = bm.getBookingsForUser(ses.getBookingUsername());
        Booking latest = bookings.isEmpty() ? null : bookings.get(bookings.size() - 1);

        // Confirmation ticket card
        JPanel ticket = new JPanel(new BorderLayout(0, 0));
        ticket.setBackground(UITheme.WHITE);
        ticket.setMaximumSize(new Dimension(900, Integer.MAX_VALUE));
        ticket.setAlignmentX(Component.CENTER_ALIGNMENT);
        ticket.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            new EmptyBorder(0, 0, 0, 0)));

        // Ticket header
        JPanel ticketHdr = new JPanel(new BorderLayout(20, 0));
        ticketHdr.setBackground(UITheme.BLUE);
        ticketHdr.setBorder(new EmptyBorder(20, 28, 20, 28));

        JPanel routePanel = new JPanel(new GridLayout(1, 3, 0, 0));
        routePanel.setOpaque(false);

        JLabel orig = bigCity(f.getOrigin());
        JLabel arrow = UITheme.label("✈  ──────────", new Font("Segoe UI", Font.PLAIN, 20), new Color(180, 220, 255));
        JLabel dest = bigCity(f.getDestination());
        arrow.setHorizontalAlignment(SwingConstants.CENTER);
        routePanel.add(orig); routePanel.add(arrow); routePanel.add(dest);

        ticketHdr.add(routePanel, BorderLayout.CENTER);

        JPanel flightMeta = new JPanel(new GridLayout(2, 1, 0, 3));
        flightMeta.setOpaque(false);
        flightMeta.add(UITheme.label(f.getAirline() + "  ·  Flight " + f.getFlightNumber(), UITheme.F_BODY, new Color(200, 230, 255)));
        flightMeta.add(UITheme.label(f.getDepartureDate() + "  ·  " + f.getDepartureTime() + " – " + f.getArrivalTime(), UITheme.F_SMALL, new Color(180, 210, 255)));
        ticketHdr.add(flightMeta, BorderLayout.EAST);
        ticket.add(ticketHdr, BorderLayout.NORTH);

        // Dashed divider
        ticket.add(buildDashedSeparator(), BorderLayout.CENTER);

        // Ticket body
        JPanel ticketBody = new JPanel(new GridLayout(1, 3, 0, 0));
        ticketBody.setBackground(UITheme.WHITE);
        ticketBody.setBorder(new EmptyBorder(18, 28, 18, 28));

        ticketBody.add(infoSection("PASSENGER",
            p.getName(), p.getNationality(), p.getPassportNumber(), p.getEmail()));

        ticketBody.add(infoSection("SEAT",
            ses.getSelectedSeat() + "  (" + ses.getSelectedClass() + ")",
            "Meal: " + p.getPreferredMeal(),
            "Departure: " + f.getDepartureTime(),
            "Arrival: " + f.getArrivalTime() + "  (" + f.getDuration() + ")"));

        String bid = latest != null ? latest.getBookingId() : "—";
        String txn = pay != null ? pay.getTransactionId() : "—";
        String total = pay != null ? String.format("৳ %,.2f", pay.getTotalAmount()) : "—";
        ticketBody.add(infoSection("BOOKING",
            "ID: " + bid, "Txn: " + txn, "Total Paid: " + total, "Status: Confirmed ✅"));

        ticket.add(ticketBody, BorderLayout.SOUTH);

        contentPanel.add(ticket);
        contentPanel.add(Box.createVerticalStrut(24));

        // Action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton dashBtn = UITheme.primaryButton("Go to Dashboard");
        dashBtn.setPreferredSize(new Dimension(180, 42));
        dashBtn.addActionListener(e -> frame.gotoDashboard());

        JButton histBtn = UITheme.outlineButton("View All Bookings");
        histBtn.setPreferredSize(new Dimension(180, 42));
        histBtn.addActionListener(e -> frame.gotoHistory());

        JButton newBtn = UITheme.outlineButton("Book Another Flight");
        newBtn.setPreferredSize(new Dimension(180, 42));
        newBtn.addActionListener(e -> { ses.clearBookingFlow(); frame.gotoSearch(); });

        btnPanel.add(dashBtn);
        btnPanel.add(histBtn);
        btnPanel.add(newBtn);

        contentPanel.add(btnPanel);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JLabel bigCity(String city) {
        JLabel l = new JLabel(city);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JPanel infoSection(String heading, String... lines) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(UITheme.label(heading, new Font("Segoe UI", Font.BOLD, 10), UITheme.TEXT_MUTED));
        p.add(Box.createVerticalStrut(6));
        for (String line : lines) {
            JLabel l = UITheme.label(line, UITheme.F_BODY, UITheme.TEXT_DARK);
            l.setBorder(new EmptyBorder(2, 0, 2, 0));
            p.add(l);
        }
        return p;
    }

    private Component buildDashedSeparator() {
        return new JComponent() {
            protected void paintComponent(Graphics g) {
                g.setColor(UITheme.BORDER);
                Graphics2D g2 = (Graphics2D) g;
                float[] dash = {8f, 4f};
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, dash, 0));
                g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
            }
            public Dimension getPreferredSize() { return new Dimension(0, 12); }
        };
    }

    private JPanel pageHeader(String title, String sub) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.SUCCESS);
        h.setBorder(new EmptyBorder(16, 24, 16, 24));
        JPanel txt = new JPanel(new GridLayout(2, 1, 0, 4));
        txt.setOpaque(false);
        txt.add(UITheme.label(title, UITheme.F_H2, Color.WHITE));
        txt.add(UITheme.label(sub, UITheme.F_BODY, new Color(200, 240, 200)));
        h.add(txt, BorderLayout.WEST);
        return h;
    }
}
