package GUI;

import FileManagement.FlightManagement;
import Entities_All.Flight;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * macOS-style spotlight search overlay for quick flight lookup.
 * Triggered from MainDashboard; dims the parent and floats a search bar.
 */
public class SpotlightSearchGUI extends JDialog {

    private JTextField searchField;
    private JPanel resultsPanel;
    private final FlightManagement fm = new FlightManagement();

    public SpotlightSearchGUI(Frame parent) {
        super(parent, true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // Full-screen glass overlay
        setSize(parent.getSize());
        setLocationRelativeTo(parent);

        JPanel overlay = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);

        // Spotlight card — centered, fixed width
        int cardW = 620, cardH = 60;
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(new Color(248, 248, 248));
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel icon = new JLabel("🔍");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        icon.setBorder(new EmptyBorder(0, 0, 0, 10));

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 18));
        searchField.setBorder(null);
        searchField.setBackground(new Color(248, 248, 248));
        searchField.setForeground(new Color(30, 30, 30));

        card.add(icon, BorderLayout.WEST);
        card.add(searchField, BorderLayout.CENTER);

        // Results dropdown panel
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(new MatteBorder(0, 1, 1, 1, new Color(200, 200, 200)));
        resultsPanel.setVisible(false);

        // Container to stack card + results
        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setOpaque(false);
        card.setMaximumSize(new Dimension(cardW, cardH));
        card.setPreferredSize(new Dimension(cardW, cardH));
        stack.add(card);
        stack.add(resultsPanel);

        // Position stack at center-top (40% from top)
        int x = (getWidth() - cardW) / 2;
        int y = (int)(getHeight() * 0.28);
        stack.setBounds(x, y, cardW, 400);
        overlay.add(stack);

        // ESC closes
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        overlay.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "close");
        overlay.getActionMap().put("close", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });

        // Click outside closes
        overlay.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!stack.getBounds().contains(e.getPoint())) dispose();
            }
        });

        // Live search
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { dispose(); return; }
                doSearch(searchField.getText().trim());
            }
        });

        setContentPane(overlay);
        SwingUtilities.invokeLater(() -> searchField.requestFocusInWindow());
    }

    private void doSearch(String term) {
        resultsPanel.removeAll();
        if (term.isEmpty()) { resultsPanel.setVisible(false); repack(); return; }

        List<Flight> all = fm.getAllFlights();
        int count = 0;
        for (Flight f : all) {
            if (f.getFlightNumber().toLowerCase().contains(term.toLowerCase())
                || f.getOrigin().toLowerCase().contains(term.toLowerCase())
                || f.getDestination().toLowerCase().contains(term.toLowerCase())
                || f.getAirline().toLowerCase().contains(term.toLowerCase())) {

                resultsPanel.add(buildRow(f));
                if (++count == 6) break;
            }
        }

        if (count == 0) {
            JLabel none = new JLabel("  No flights found for \"" + term + "\"");
            none.setFont(new Font("Arial", Font.ITALIC, 13));
            none.setForeground(new Color(140, 140, 140));
            none.setBorder(new EmptyBorder(10, 12, 10, 12));
            none.setAlignmentX(0);
            resultsPanel.add(none);
        }

        resultsPanel.setVisible(true);
        repack();
    }

    private JPanel buildRow(Flight f) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
            new EmptyBorder(9, 14, 9, 14)
        ));
        row.setMaximumSize(new Dimension(620, 50));

        JLabel left = new JLabel("<html><b style='font-size:12px'>" + f.getFlightNumber()
            + "</b> &nbsp; " + f.getOrigin() + " → " + f.getDestination() + "</html>");
        left.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel right = new JLabel("BDT " + String.format("%,.0f", f.getEconomyPrice())
            + "   " + f.getDepartureDate());
        right.setFont(new Font("Arial", Font.PLAIN, 12));
        right.setForeground(new Color(100, 100, 100));

        row.add(left, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        row.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { row.setBackground(new Color(232, 240, 253)); }
            public void mouseExited(MouseEvent e)  { row.setBackground(Color.WHITE); }
        });
        return row;
    }

    private void repack() {
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public static void show(Frame parent) {
        new SpotlightSearchGUI(parent).setVisible(true);
    }
}
