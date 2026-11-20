package GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class FlightClassPanel extends JPanel {

    private MainFrame frame;

    public FlightClassPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        add(pageHeader("💎  Flight Class Comparison", "Choose the experience that suits you best"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(UITheme.SURFACE);
        body.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel sub = UITheme.label(
            "All prices are approximate base fares. Final pricing depends on demand and availability.",
            UITheme.F_SMALL, UITheme.TEXT_MUTED);
        sub.setBorder(new EmptyBorder(0, 0, 10, 0));
        body.add(sub, BorderLayout.NORTH);

        // Three class cards
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.add(buildClassCard("Economy", "✈",
            new String[][]{
                {"Seat Width",       "17 – 18 inches"},
                {"Legroom",          "30 – 32 inches"},
                {"Baggage",          "20 kg checked"},
                {"Carry-on",         "7 kg"},
                {"Meals",            "Standard in-flight meal"},
                {"Entertainment",    "Personal screen"},
                {"Price Range",      "৳ 5,000 – 35,000"},
                {"Recline",          "3 – 4 inches"},
                {"Power Outlet",     "USB only"},
                {"Priority Boarding","No"},
            },
            UITheme.SKY, new Color(230, 244, 255)));

        cards.add(buildClassCard("Business", "🌟",
            new String[][]{
                {"Seat Width",       "21 – 23 inches"},
                {"Legroom",          "38 – 60 inches"},
                {"Baggage",          "40 kg checked"},
                {"Carry-on",         "14 kg"},
                {"Meals",            "Premium multi-course meal"},
                {"Entertainment",    "Large personal screen"},
                {"Price Range",      "৳ 15,000 – 90,000"},
                {"Recline",          "Full flat-bed"},
                {"Power Outlet",     "AC + USB"},
                {"Priority Boarding","Yes"},
            },
            UITheme.BLUE, new Color(225, 235, 255)));

        cards.add(buildClassCard("First Class", "👑",
            new String[][]{
                {"Seat Width",       "25 – 34 inches"},
                {"Legroom",          "Full suite / private pod"},
                {"Baggage",          "60 kg checked"},
                {"Carry-on",         "20 kg"},
                {"Meals",            "Chef-curated gourmet dining"},
                {"Entertainment",    "Ultra HD suite screen"},
                {"Price Range",      "৳ 25,000 – 1,50,000"},
                {"Recline",          "Private suite / lie-flat"},
                {"Power Outlet",     "AC + USB + amenity kit"},
                {"Priority Boarding","Dedicated concierge"},
            },
            UITheme.GOLD, new Color(255, 250, 225)));

        body.add(cards, BorderLayout.CENTER);

        // Bottom CTA
        JPanel cta = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        cta.setOpaque(false);
        JLabel ctaTxt = UITheme.label("Ready to book? Search for available flights now.", UITheme.F_BODY, UITheme.TEXT_MED);
        JButton searchBtn = UITheme.primaryButton("Search Flights →");
        searchBtn.addActionListener(e -> frame.gotoSearch());
        cta.add(ctaTxt);
        cta.add(searchBtn);
        body.add(cta, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildClassCard(String title, String icon, String[][] features,
                                   Color accentColor, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(UITheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(4, 0, 0, 0, accentColor),
            BorderFactory.createLineBorder(UITheme.BORDER, 1)));

        // Header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(bgColor);
        hdr.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel ico = UITheme.label(icon, new Font("Segoe UI", Font.PLAIN, 36), accentColor);
        JLabel ttl = UITheme.label(title, UITheme.F_H2, UITheme.NAVY);

        JPanel hdrTxt = new JPanel(new GridLayout(2, 1, 0, 4));
        hdrTxt.setOpaque(false);
        hdrTxt.add(ico);
        hdrTxt.add(ttl);
        hdr.add(hdrTxt, BorderLayout.CENTER);
        card.add(hdr, BorderLayout.NORTH);

        // Features table
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(12, 14, 16, 14));
        body.setBackground(UITheme.WHITE);

        for (int i = 0; i < features.length; i++) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(i % 2 == 0 ? UITheme.WHITE : new Color(248, 250, 255));
            row.setBorder(new EmptyBorder(7, 8, 7, 8));

            JLabel keyLbl = UITheme.label(features[i][0], UITheme.F_SMALL, UITheme.TEXT_MUTED);
            JLabel valLbl = UITheme.label(features[i][1], UITheme.F_BODY, UITheme.TEXT_DARK);
            valLbl.setHorizontalAlignment(SwingConstants.RIGHT);

            row.add(keyLbl, BorderLayout.WEST);
            row.add(valLbl, BorderLayout.EAST);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            body.add(row);
        }
        card.add(body, BorderLayout.CENTER);

        return card;
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
