package GUI;

import FileManagement.BookingManagement;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Standalone seat map dialog. Returns selected seat via callback.
 * Rows 1-3 = First Class (A,B,C,D), 4-8 = Business (A,B,C,D), 9-28 = Economy (A,B,C,D,E,F).
 */
public class SeatSelectGUI extends JDialog {

    private String selectedSeat = null;
    private JButton lastSelected = null;
    private JLabel lblInfo;

    private static final Color AVAIL_COLOR  = new Color(76, 175, 80);
    private static final Color SEL_COLOR    = new Color(255, 152, 0);
    private static final Color BOOKED_COLOR = new Color(211, 47, 47);

    public SeatSelectGUI(String flightNumber, String flightClass, java.awt.Frame parent) {
        super(parent, "Seat Selection — " + (flightNumber != null ? flightNumber : ""), true);
        setSize(580, 700);
        setLocationRelativeTo(parent);

        BookingManagement bm = new BookingManagement();
        List<String> booked = (flightNumber != null)
            ? bm.getBookedSeatsForFlight(flightNumber) : java.util.Collections.emptyList();

        setLayout(new BorderLayout());
        add(buildHeader(flightNumber, flightClass), BorderLayout.NORTH);
        add(buildSeatPanel(booked, flightClass), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildHeader(String fn, String cls) {
        JPanel h = new JPanel(new GridLayout(2, 1, 0, 2));
        h.setBackground(SimpleTheme.HEADER_BG);
        h.setPreferredSize(new Dimension(0, 55));
        h.setBorder(new EmptyBorder(5, 16, 5, 16));
        JLabel t = new JLabel("Select Your Seat");
        t.setFont(SimpleTheme.TITLE_FONT);
        t.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Flight: " + (fn != null ? fn : "—") + "   Class: " + (cls != null ? cls : "All"));
        sub.setFont(SimpleTheme.SMALL_FONT);
        sub.setForeground(new Color(180, 210, 255));
        h.add(t); h.add(sub);
        return h;
    }

    private JScrollPane buildSeatPanel(List<String> booked, String cls) {
        JPanel map = new JPanel(null);
        map.setBackground(new Color(248, 249, 252));

        int totalHeight = 30 + 30 + 3*36 + 20 + 30 + 5*36 + 20 + 30 + 20*36 + 30;
        map.setPreferredSize(new Dimension(520, totalHeight));

        int startX = 90;
        int y = 20;

        y = addSectionLabel(map, "FIRST CLASS  (Rows 1-3)", y, new Color(156, 39, 176));
        for (int row = 1; row <= 3; row++) {
            y = addRow(map, row, new char[]{'A','B',' ','C','D'}, "First", booked, cls, y, startX);
        }
        y += 10; addDivider(map, y, startX); y += 16;

        y = addSectionLabel(map, "BUSINESS CLASS  (Rows 4-8)", y, new Color(25, 118, 210));
        for (int row = 4; row <= 8; row++) {
            y = addRow(map, row, new char[]{'A','B',' ','C','D'}, "Business", booked, cls, y, startX);
        }
        y += 10; addDivider(map, y, startX); y += 16;

        y = addSectionLabel(map, "ECONOMY CLASS  (Rows 9-28)", y, new Color(46, 125, 50));
        for (int row = 9; row <= 28; row++) {
            y = addRow(map, row, new char[]{'A','B','C',' ','D','E','F'}, "Economy", booked, cls, y, startX);
        }

        map.setPreferredSize(new Dimension(520, y + 20));
        JScrollPane sp = new JScrollPane(map);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private int addSectionLabel(JPanel p, String text, int y, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(color);
        l.setBackground(new Color(240, 244, 255));
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(2, 6, 2, 6));
        l.setBounds(30, y, 440, 22);
        p.add(l);
        return y + 28;
    }

    private int addRow(JPanel panel, int row, char[] cols, String rowClass,
                       List<String> booked, String filterClass, int y, int startX) {
        boolean classMatch = filterClass == null || filterClass.isEmpty() || rowClass.equals(filterClass);

        JLabel num = new JLabel(String.format("%2d", row));
        num.setFont(new Font("Arial", Font.BOLD, 11));
        num.setForeground(SimpleTheme.TEXT_MED);
        num.setBounds(startX - 35, y, 30, 28);
        panel.add(num);

        int x = startX;
        for (char col : cols) {
            if (col == ' ') { x += 14; continue; }
            String seat = row + "" + col;
            boolean isBooked = booked.contains(seat);
            JButton btn = new JButton(seat);
            btn.setFont(new Font("Arial", Font.BOLD, 9));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBounds(x, y, 40, 26);

            if (isBooked || !classMatch) {
                btn.setBackground(BOOKED_COLOR);
                btn.setForeground(Color.WHITE);
                btn.setEnabled(false);
                btn.setToolTipText(seat + " — " + (isBooked ? "Booked" : "Wrong class"));
            } else {
                btn.setBackground(AVAIL_COLOR);
                btn.setForeground(Color.WHITE);
                btn.setToolTipText(seat + " — " + rowClass + " — Available");
                btn.addActionListener(ev -> selectSeat(btn, seat, rowClass));
            }
            panel.add(btn);
            x += 46;
        }
        return y + 32;
    }

    private void addDivider(JPanel p, int y, int startX) {
        JSeparator sep = new JSeparator();
        sep.setForeground(SimpleTheme.BORDER_COL);
        sep.setBounds(startX - 40, y, 420, 2);
        p.add(sep);
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(0, 4));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(8, 16, 10, 16));

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        legend.setBackground(Color.WHITE);
        legend.add(legendChip(AVAIL_COLOR, "Available"));
        legend.add(legendChip(SEL_COLOR, "Selected"));
        legend.add(legendChip(BOOKED_COLOR, "Booked/Unavail"));
        footer.add(legend, BorderLayout.NORTH);

        lblInfo = new JLabel("No seat selected.", SwingConstants.CENTER);
        lblInfo.setFont(SimpleTheme.BOLD_FONT);
        lblInfo.setForeground(SimpleTheme.HEADER_BG);
        footer.add(lblInfo, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btns.setBackground(Color.WHITE);
        JButton btnConfirm = SimpleTheme.createButton("Confirm Seat");
        JButton btnCancel  = SimpleTheme.createButton("Cancel");
        btnCancel.setBackground(SimpleTheme.DANGER_BG);
        btnConfirm.addActionListener(e -> {
            if (selectedSeat == null) {
                SimpleTheme.showError(this, "Please select a seat first."); return;
            }
            dispose();
        });
        btnCancel.addActionListener(e -> { selectedSeat = null; dispose(); });
        btns.add(btnConfirm); btns.add(btnCancel);
        footer.add(btns, BorderLayout.SOUTH);
        return footer;
    }

    private JPanel legendChip(Color c, String lbl) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(Color.WHITE);
        JPanel chip = new JPanel();
        chip.setBackground(c);
        chip.setPreferredSize(new Dimension(14, 14));
        chip.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        p.add(chip);
        JLabel l = new JLabel(lbl);
        l.setFont(SimpleTheme.SMALL_FONT);
        p.add(l);
        return p;
    }

    private void selectSeat(JButton btn, String seat, String cls) {
        if (lastSelected != null) lastSelected.setBackground(AVAIL_COLOR);
        lastSelected = btn;
        btn.setBackground(SEL_COLOR);
        selectedSeat = seat;
        lblInfo.setText("Selected: " + seat + "  (" + cls + ")   Click 'Confirm Seat' to proceed.");
    }

    public String getSelectedSeat() {
        return selectedSeat;
    }
}
