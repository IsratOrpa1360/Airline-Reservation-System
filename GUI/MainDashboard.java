package GUI;

import Entities_All.User;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    private final User currentUser;

    public MainDashboard(User user) {
        this.currentUser = user;
        setTitle("Airline Reservation System - Dashboard");
        setSize(720, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);

        //Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SimpleTheme.HEADER_BG);
        header.setPreferredSize(new Dimension(0, 62));
        header.setBorder(new EmptyBorder(6, 20, 6, 20));
        JPanel hLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        hLeft.setOpaque(false);
        JLabel title = new JLabel("Airline Reservation System");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        JLabel welcome = new JLabel("Welcome, " + user.getUsername() +
            "  |  Tier: " + user.getMembershipTier() +
            "  |  Points: " + user.getLoyaltyPoints());
        welcome.setFont(new Font("Arial", Font.PLAIN, 12));
        welcome.setForeground(new Color(180, 210, 255));
        hLeft.add(title);
        hLeft.add(welcome);
        header.add(hLeft, BorderLayout.WEST);
        main.add(header, BorderLayout.NORTH);

        //Navigation buttons
        JPanel navArea = new JPanel(new BorderLayout(0, 8));
        navArea.setBackground(Color.WHITE);
        navArea.setBorder(new EmptyBorder(20, 30, 10, 30));

        JLabel navTitle = new JLabel("Main Menu", SwingConstants.CENTER);
        navTitle.setFont(new Font("Arial", Font.BOLD, 13));
        navTitle.setForeground(SimpleTheme.TEXT_MED);
        navArea.add(navTitle, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 4, 10, 10));
        grid.setBackground(Color.WHITE);

        addNavBtn(grid, "Flight Operations",     () -> new FlightOperationsGUI());
        addNavBtn(grid, "Passenger Management",  () -> new PassengerMgmtGUI());
        addNavBtn(grid, "Booking Management",    () -> new BookingMgmtGUI(currentUser));
        addNavBtn(grid, "Seat Selection",        () -> openSeatSelect());
        addNavBtn(grid, "Payment Management",    () -> new PaymentMgmtGUI(null, currentUser));
        addNavBtn(grid, "Flight Class Info",     () -> new FlightClassGUI());
        addNavBtn(grid, "Meal Preferences",      () -> new MealPrefGUI());
        addNavBtn(grid, "Notifications",         () -> new NotificationsGUI(currentUser));
        addNavBtn(grid, "Update Profile",        () -> new ProfileGUI(currentUser, this));
        addNavBtn(grid, "Logout",                () -> handleLogout());
        addNavBtn(grid, "🔍 Search Flights",     () -> SpotlightSearchGUI.show(this));
        grid.add(new JLabel());

        navArea.add(grid, BorderLayout.CENTER);
        main.add(navArea, BorderLayout.CENTER);


        add(main);


        KeyStroke ks = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE,
            java.awt.event.InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "spotlight");
        getRootPane().getActionMap().put("spotlight",
            new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    SpotlightSearchGUI.show(MainDashboard.this);
                }
            });

        setVisible(true);
    }

    private void addNavBtn(JPanel panel, String label, Runnable action) {
        JButton btn = SimpleTheme.createButton(label);
        btn.setPreferredSize(new Dimension(160, 50));
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.addActionListener(e -> action.run());
        panel.add(btn);
    }

    private void openSeatSelect() {
        String fn = JOptionPane.showInputDialog(this,
            "Enter Flight Number to view seats:", "Seat Selection",
            JOptionPane.QUESTION_MESSAGE);
        if (fn == null || fn.trim().isEmpty()) return;
        new SeatSelectGUI(fn.trim().toUpperCase(), null, this);
    }

    private void handleLogout() {
        if (SimpleTheme.confirm(this, "Are you sure you want to logout?")) {
            dispose();
            new LoginGUI();
        }
    }

    public void refreshWelcome(User updatedUser) {
        dispose();
        new MainDashboard(updatedUser);
    }
}
