package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class SeatSelectionPanel extends JPanel {

    private MainFrame frame;
    private JPanel    seatMapPanel;
    private JLabel    selectionInfoLabel;
    private JLabel    flightInfoLabel;
    private JButton   continueBtn;
    private JButton   selectedBtn;

    // 1-3 = First(A,B,_,C,D), 4-8 = Business(A,B,_,C,D), 9-28 = Economy(A,B,C,_,D,E,F)
    private static final int FIRST_ROWS    = 3;
    private static final int BUSINESS_ROWS = 5;
    private static final int ECONOMY_ROWS  = 20;

    public SeatSelectionPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        add(pageHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setBackground(UITheme.SURFACE);
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        //Left: seat map
        seatMapPanel = new JPanel();
        seatMapPanel.setBackground(UITheme.WHITE);
        seatMapPanel.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JScrollPane seatScroll = new JScrollPane(seatMapPanel);
        seatScroll.getVerticalScrollBar().setUnitIncrement(16);
        seatScroll.setBorder(null);
        seatScroll.setPreferredSize(new Dimension(520, 0));
        body.add(seatScroll, BorderLayout.CENTER);

        //Right: info + legend
        JPanel right = new JPanel();
        right.setBackground(UITheme.SURFACE);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(280, 0));

        right.add(buildLegend());
        right.add(Box.createVerticalStrut(14));
        right.add(buildSelectionInfo());
        body.add(right, BorderLayout.EAST);

        add(body, BorderLayout.CENTER);
    }

    public void refresh() {
        AppSession ses = AppSession.getInstance();
        Flight f = ses.getSelectedFlight();
        if (f == null) return;

        flightInfoLabel.setText(f.getFlightNumber() + "  " + f.getOrigin() + " → " + f.getDestination()
                + "  |  " + ses.getSelectedClass() + "  |  " + f.getDepartureDate());

        BookingManagement bm = new BookingManagement();
        List<String> bookedSeats = bm.getBookedSeatsForFlight(f.getFlightNumber());

        buildSeatMap(bookedSeats);
        selectedBtn = null;
        ses.setSelectedSeat(null);
        selectionInfoLabel.setText("No seat selected.");
        continueBtn.setEnabled(false);
    }

    private void buildSeatMap(List<String> booked) {
        seatMapPanel.removeAll();
        seatMapPanel.setLayout(null);
        seatMapPanel.setPreferredSize(new Dimension(480, 1100));

        int y = 20;
        int startX = 50;

        //Nose
        drawLabel(seatMapPanel, "✈ FRONT", startX + 160, y, UITheme.NAVY, UITheme.F_H4); y += 30;

        //FIRST CLASS
        drawSectionLabel(seatMapPanel, "FIRST CLASS", y); y += 30;
        for (int row = 1; row <= FIRST_ROWS; row++) {
            y = drawRow(seatMapPanel, row, new char[]{'A','B',' ','C','D'}, "First", booked, y, startX);
        }
        drawDivider(seatMapPanel, y); y += 20;

        //BUSINESS CLASS
        drawSectionLabel(seatMapPanel, "BUSINESS CLASS", y); y += 30;
        for (int row = FIRST_ROWS + 1; row <= FIRST_ROWS + BUSINESS_ROWS; row++) {
            y = drawRow(seatMapPanel, row, new char[]{'A','B',' ','C','D'}, "Business", booked, y, startX);
        }
        drawDivider(seatMapPanel, y); y += 20;

        //ECONOMY CLASS
        drawSectionLabel(seatMapPanel, "ECONOMY CLASS", y); y += 30;
        for (int row = FIRST_ROWS + BUSINESS_ROWS + 1; row <= FIRST_ROWS + BUSINESS_ROWS + ECONOMY_ROWS; row++) {
            y = drawRow(seatMapPanel, row, new char[]{'A','B','C',' ','D','E','F'}, "Economy", booked, y, startX);
        }

        seatMapPanel.setPreferredSize(new Dimension(480, y + 30));
        seatMapPanel.revalidate();
        seatMapPanel.repaint();
    }

    private int drawRow(JPanel panel, int row, char[] cols, String cls, List<String> booked, int y, int startX) {
        //Row number
        JLabel rowNum = new JLabel(String.format("%2d", row));
        rowNum.setFont(UITheme.F_SMALL);
        rowNum.setForeground(UITheme.TEXT_MUTED);
        rowNum.setBounds(startX - 28, y, 25, 28);
        panel.add(rowNum);

        int x = startX;
        for (char col : cols) {
            if (col == ' ') { x += 16; continue; } // aisle gap
            String seatId = row + "" + col;
            boolean isBooked = booked.contains(seatId);
            JButton btn = createSeatBtn(seatId, cls, isBooked);
            btn.setBounds(x, y, 44, 28);
            panel.add(btn);
            x += 50;
        }
        return y + 36;
    }

    private JButton createSeatBtn(String seatId, String cls, boolean isBooked) {
        JButton btn = new JButton(seatId);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 9));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);

        if (isBooked) {
            btn.setBackground(UITheme.SEAT_BOOKED);
            btn.setForeground(Color.WHITE);
            btn.setEnabled(false);
            btn.setToolTipText(seatId + " — Booked");
        } else {
            btn.setBackground(UITheme.SEAT_AVAIL);
            btn.setForeground(Color.WHITE);
            btn.setToolTipText(seatId + " — " + cls + " — Available");
            btn.addActionListener(e -> selectSeat(btn, seatId, cls));
        }
        return btn;
    }

    private void selectSeat(JButton btn, String seatId, String cls) {
        AppSession ses = AppSession.getInstance();
        String required = ses.getSelectedClass();

        //Enforce class restriction
        if (required != null && !required.isEmpty() && !cls.equals(required)) {
            JOptionPane.showMessageDialog(this,
                "You selected " + required + " class. Please choose a " + required + " seat.",
                "Class Mismatch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Deselect previous
        if (selectedBtn != null) {
            selectedBtn.setBackground(UITheme.SEAT_AVAIL);
        }

        selectedBtn = btn;
        btn.setBackground(UITheme.SEAT_SEL);

        ses.setSelectedSeat(seatId);
        double price = ses.getSelectedFlight().getDynamicPrice(cls);
        ses.setBasePrice(price);

        //Compute total with taxes/service
        Payment pay = new Payment(price, 0, 0, "");
        ses.setTotalPrice(pay.calculateTotal());

        selectionInfoLabel.setText("<html>Selected: <b>" + seatId + "</b>  (" + cls + ")<br>" +
                "Base Fare: ৳" + String.format("%,.0f", price) + "<br>" +
                "Est. Total: ৳" + String.format("%,.0f", pay.calculateTotal()) + "</html>");
        continueBtn.setEnabled(true);
    }

    private JPanel buildLegend() {
        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        card.add(UITheme.label("Seat Legend", UITheme.F_H4, UITheme.NAVY));
        card.add(Box.createVerticalStrut(10));
        card.add(legendRow(UITheme.SEAT_AVAIL,  "Available"));
        card.add(legendRow(UITheme.SEAT_SEL,    "Selected"));
        card.add(legendRow(UITheme.SEAT_BOOKED, "Booked"));
        card.add(legendRow(UITheme.SEAT_BLOCK,  "Blocked"));
        return card;
    }

    private JPanel legendRow(Color color, String label) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        row.setOpaque(false);
        JPanel swatch = new JPanel();
        swatch.setBackground(color);
        swatch.setPreferredSize(new Dimension(20, 14));
        swatch.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        row.add(swatch);
        row.add(UITheme.label(label, UITheme.F_BODY, UITheme.TEXT_MED));
        return row;
    }

    private JPanel buildSelectionInfo() {
        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(UITheme.label("Selection", UITheme.F_H4, UITheme.NAVY));
        card.add(Box.createVerticalStrut(10));

        selectionInfoLabel = UITheme.label("No seat selected.", UITheme.F_BODY, UITheme.TEXT_MED);
        selectionInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(selectionInfoLabel);

        card.add(Box.createVerticalStrut(14));

        continueBtn = UITheme.primaryButton("Continue →");
        continueBtn.setEnabled(false);
        continueBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        continueBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        continueBtn.addActionListener(e -> frame.gotoPassengerInfo());
        card.add(continueBtn);

        JButton backBtn = UITheme.outlineButton("← Back to Search");
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        backBtn.addActionListener(e -> frame.gotoSearch());
        card.add(Box.createVerticalStrut(8));
        card.add(backBtn);

        return card;
    }

    private JPanel pageHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.NAVY);
        h.setBorder(new EmptyBorder(14, 24, 14, 24));
        JPanel txt = new JPanel(new GridLayout(2, 1, 0, 4));
        txt.setOpaque(false);
        txt.add(UITheme.label("💺  Seat Selection", UITheme.F_H2, Color.WHITE));
        flightInfoLabel = UITheme.label("Select your flight first.", UITheme.F_BODY, new Color(180, 210, 255));
        txt.add(flightInfoLabel);
        h.add(txt, BorderLayout.WEST);
        JButton back = new JButton("← Back to Search");
        back.setFont(UITheme.F_BTN);
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(30, 60, 130));
        back.setBorderPainted(false);
        back.setFocusPainted(false);
        back.setOpaque(true);
        back.setPreferredSize(new Dimension(150, 36));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> frame.gotoSearch());
        h.add(back, BorderLayout.EAST);
        return h;
    }

    private void drawSectionLabel(JPanel panel, String text, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(UITheme.TEXT_MUTED);
        l.setBounds(24, y, 400, 20);
        l.setBackground(new Color(240, 244, 255));
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(0, 6, 0, 0));
        panel.add(l);
    }

    private void drawLabel(JPanel panel, String text, int x, int y, Color fg, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(fg);
        l.setBounds(x, y, 200, 24);
        panel.add(l);
    }

    private void drawDivider(JPanel panel, int y) {
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setBounds(24, y + 4, 430, 2);
        panel.add(sep);
    }
}
