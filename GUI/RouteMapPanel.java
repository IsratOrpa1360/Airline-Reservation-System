package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class RouteMapPanel extends JPanel {

    private MainFrame frame;
    private MapCanvas mapCanvas;

    // Approximate (x,y) positions as % of 800×420 canvas for each city
    private static final Map<String, int[]> CITY_POS = new LinkedHashMap<>();
    static {
        CITY_POS.put("Dhaka",     new int[]{530, 215});
        CITY_POS.put("Dubai",     new int[]{430, 210});
        CITY_POS.put("London",    new int[]{300, 130});
        CITY_POS.put("New York",  new int[]{155, 145});
        CITY_POS.put("Tokyo",     new int[]{650, 165});
        CITY_POS.put("Singapore", new int[]{590, 265});
        CITY_POS.put("Paris",     new int[]{308, 120});
        CITY_POS.put("Sydney",    new int[]{660, 335});
        CITY_POS.put("Kuala Lumpur", new int[]{580, 260});
        CITY_POS.put("Bangkok",   new int[]{580, 230});
        CITY_POS.put("Istanbul",  new int[]{390, 160});
        CITY_POS.put("Toronto",   new int[]{155, 145});
        CITY_POS.put("Riyadh",    new int[]{420, 215});
    }

    public RouteMapPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        add(pageHeader("🗺  Flight Route Map", "Visual overview of all available flight routes"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(UITheme.SURFACE);
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        mapCanvas = new MapCanvas();
        JPanel mapCard = UITheme.card();
        mapCard.setLayout(new BorderLayout());
        mapCard.add(mapCanvas, BorderLayout.CENTER);

        // Legend panel
        JPanel legend = buildLegend();
        mapCard.add(legend, BorderLayout.SOUTH);

        body.add(mapCard, BorderLayout.CENTER);
        body.add(buildInstructions(), BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        p.setBackground(new Color(248, 250, 255));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        p.add(UITheme.label("Legend:", UITheme.F_H4, UITheme.TEXT_MED));
        p.add(legendDot(UITheme.GOLD));
        p.add(UITheme.label("City / Airport", UITheme.F_SMALL, UITheme.TEXT_MED));
        p.add(legendDot(new Color(30, 144, 255, 160)));
        p.add(UITheme.label("Flight Route", UITheme.F_SMALL, UITheme.TEXT_MED));
        p.add(legendDot(UITheme.SKY));
        p.add(UITheme.label("Direct Connection", UITheme.F_SMALL, UITheme.TEXT_MED));

        JButton refreshBtn = UITheme.outlineButton("Refresh Routes");
        refreshBtn.setPreferredSize(new Dimension(130, 28));
        refreshBtn.addActionListener(e -> refresh());
        p.add(Box.createHorizontalStrut(20));
        p.add(refreshBtn);

        return p;
    }

    private JPanel legendDot(Color c) {
        return new JPanel() {
            public void paintComponent(Graphics g) {
                g.setColor(c);
                g.fillOval(0, 2, 12, 12);
            }
            public Dimension getPreferredSize() { return new Dimension(14, 16); }
        };
    }

    private JPanel buildInstructions() {
        JPanel p = UITheme.card();
        p.setLayout(new GridLayout(1, 3, 20, 0));
        p.setBorder(new EmptyBorder(12, 16, 12, 16));

        p.add(infoChip("✈  Real-time routes", "Routes shown are based on currently available flights in the system."));
        p.add(infoChip("📍  17 Cities Connected", "Flights connect 17 major airports across Asia, Europe and the Americas."));
        p.add(infoChip("🔍  Search Flights", "Use Flight Search to book any available route shown on this map."));
        return p;
    }

    private JPanel infoChip(String title, String desc) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(UITheme.label(title, UITheme.F_H4, UITheme.NAVY), BorderLayout.NORTH);
        JLabel d = UITheme.label("<html>" + desc + "</html>", UITheme.F_SMALL, UITheme.TEXT_MUTED);
        p.add(d, BorderLayout.CENTER);
        return p;
    }

    public void refresh() {
        FlightManagement fm = new FlightManagement();
        List<Flight> flights = fm.getAllFlights();
        mapCanvas.setFlights(flights);
        mapCanvas.repaint();
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

    static class MapCanvas extends JPanel {

        private List<Flight> flights = new ArrayList<>();

        MapCanvas() {
            setBackground(new Color(10, 25, 60));
            setPreferredSize(new Dimension(800, 420));
        }

        void setFlights(List<Flight> f) { this.flights = f; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int W = getWidth(), H = getHeight();
            double scaleX = W / 800.0, scaleY = H / 420.0;

            drawGlobe(g2, W, H);
            drawGrid(g2, W, H);
            drawRoutes(g2, scaleX, scaleY);
            drawCities(g2, scaleX, scaleY);

            g2.dispose();
        }

        private void drawGlobe(Graphics2D g2, int W, int H) {
            //Gradient background
            GradientPaint bg = new GradientPaint(0, 0, new Color(8, 20, 55), 0, H, new Color(15, 40, 90));
            g2.setPaint(bg);
            g2.fillRect(0, 0, W, H);

            // Subtle
            g2.setColor(new Color(30, 70, 130, 60));
            g2.fillRect((int)(W*0.35), (int)(H*0.18), (int)(W*0.25), (int)(H*0.45)); // Europe+Africa
            g2.fillRect((int)(W*0.60), (int)(H*0.15), (int)(W*0.25), (int)(H*0.40)); // Asia
            g2.fillRect((int)(W*0.10), (int)(H*0.15), (int)(W*0.22), (int)(H*0.38)); // Americas
            g2.fillRect((int)(W*0.65), (int)(H*0.60), (int)(W*0.20), (int)(H*0.30)); // Australia
        }

        private void drawGrid(Graphics2D g2, int W, int H) {
            g2.setColor(new Color(255, 255, 255, 18));
            g2.setStroke(new BasicStroke(0.5f));
            // Vertical lines
            for (int x = 0; x <= W; x += W / 12) g2.drawLine(x, 0, x, H);
            // Horizontal lines
            for (int y = 0; y <= H; y += H / 6) g2.drawLine(0, y, W, y);
        }

        private void drawRoutes(Graphics2D g2, double sx, double sy) {
            if (flights == null) return;
            Set<String> drawnRoutes = new HashSet<>();

            for (Flight f : flights) {
                String key1 = f.getOrigin() + "|" + f.getDestination();
                String key2 = f.getDestination() + "|" + f.getOrigin();
                if (drawnRoutes.contains(key1) || drawnRoutes.contains(key2)) continue;
                drawnRoutes.add(key1);

                int[] a = CITY_POS.get(f.getOrigin());
                int[] b = CITY_POS.get(f.getDestination());
                if (a == null || b == null) continue;

                int x1 = (int)(a[0] * sx), y1 = (int)(a[1] * sy);
                int x2 = (int)(b[0] * sx), y2 = (int)(b[1] * sy);

                //Dashed arc-like line
                float[] dash = {10f, 6f};
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dash, 0f));
                g2.setColor(new Color(30, 144, 255, 130));

                //Draw curved arc via quadratic bezier
                int cx = (x1 + x2) / 2;
                int cy = Math.min(y1, y2) - 40;
                java.awt.geom.QuadCurve2D curve = new java.awt.geom.QuadCurve2D.Float(x1, y1, cx, cy, x2, y2);
                g2.draw(curve);

                //Draw small arrow mid-point
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(100, 180, 255, 160));
                int mx = (x1 + 2*cx + x2) / 4;
                int my = (y1 + 2*cy + y2) / 4;
                g2.fillOval(mx - 2, my - 2, 5, 5);
            }
        }

        private void drawCities(Graphics2D g2, double sx, double sy) {
            //Collect which cities have active flights
            Set<String> activeCities = new HashSet<>();
            if (flights != null) {
                for (Flight f : flights) {
                    activeCities.add(f.getOrigin());
                    activeCities.add(f.getDestination());
                }
            }

            for (Map.Entry<String, int[]> e : CITY_POS.entrySet()) {
                String city = e.getKey();
                int[] pos = e.getValue();
                int cx = (int)(pos[0] * sx);
                int cy = (int)(pos[1] * sy);

                boolean active = activeCities.contains(city);
                Color dotColor = active ? UITheme.GOLD : new Color(120, 140, 170);
                int dotSize = active ? 10 : 7;

                //Glow ring for active cities
                if (active) {
                    g2.setColor(new Color(255, 200, 50, 40));
                    g2.fillOval(cx - 9, cy - 9, 20, 20);
                    g2.setColor(new Color(255, 200, 50, 80));
                    g2.fillOval(cx - 6, cy - 6, 14, 14);
                }

                g2.setColor(dotColor);
                g2.fillOval(cx - dotSize/2, cy - dotSize/2, dotSize, dotSize);

                //City label
                g2.setColor(active ? Color.WHITE : new Color(160, 180, 200));
                g2.setFont(active ? new Font("Segoe UI", Font.BOLD, 11)
                                  : new Font("Segoe UI", Font.PLAIN, 9));
                g2.drawString(city, cx + 7, cy + 4);
            }
        }
    }
}
