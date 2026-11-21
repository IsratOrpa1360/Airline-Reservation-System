package GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class MainFrame extends JFrame {

    private static final String P_DASHBOARD   = "dashboard";
    private static final String P_SEARCH      = "search";
    private static final String P_SEAT        = "seat";
    private static final String P_PASSENGER   = "passenger";
    private static final String P_CLASS       = "class";
    private static final String P_PAYMENT     = "payment";
    private static final String P_CONFIRM     = "confirm";
    private static final String P_HISTORY     = "history";
    private static final String P_PROFILE     = "profile";
    private static final String P_LOYALTY     = "loyalty";
    private static final String P_ROUTEMAP    = "routemap";
    private static final String P_PAXMGR      = "paxmgr";

    private CardLayout cardLayout;
    private JPanel     contentArea;
    private JLabel     userLabel;
    private JButton    activeNavBtn;

    //Panel references
    private DashboardPanel       dashboardPanel;
    private FlightSearchPanel    searchPanel;
    private SeatSelectionPanel   seatPanel;
    private PassengerInfoPanel   passengerPanel;
    private FlightClassPanel     classPanel;
    private PaymentPanel         paymentPanel;
    private BookingConfirmPanel  confirmPanel;
    private BookingHistoryPanel  historyPanel;
    private ProfilePanel         profilePanel;
    private LoyaltyPanel         loyaltyPanel;
    private RouteMapPanel        routePanel;
    private PassengerRegGUI      paxMgrPanel;

    //Static reference for panels to call navigation
    private static MainFrame instance;

    public MainFrame() {
        instance = this;
        AppSession ses = AppSession.getInstance();
        setTitle("SkyLink Airways — " + ses.getDisplayName());
        setSize(1350, 800);
        setMinimumSize(new Dimension(1100, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildSidebar(),  BorderLayout.WEST);
        add(buildContent(),  BorderLayout.CENTER);

        setVisible(true);
        showPanel(P_DASHBOARD);
    }

    public static MainFrame get() { return instance; }

    //Top bar
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.NAVY);
        bar.setBorder(new EmptyBorder(0, 20, 0, 20));
        bar.setPreferredSize(new Dimension(0, 54));

        JLabel logo = UITheme.label("✈  SkyLink Airways", UITheme.F_H3, Color.WHITE);
        bar.add(logo, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        userLabel = UITheme.label("👤  " + AppSession.getInstance().getDisplayName(), UITheme.F_BODY, new Color(190, 215, 255));
        right.add(userLabel);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UITheme.F_BTN);
        logoutBtn.setForeground(UITheme.WHITE);
        logoutBtn.setBackground(new Color(183, 28, 28));
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            AppSession.getInstance().logout();
            new LoginGUI();
            dispose();
        });
        right.add(logoutBtn);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    //Sidebar
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(12, 34, 88));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(8, 0, 8, 0));

        addNavSection(sidebar, "BOOKING");
        addNavBtn(sidebar, "🏠  Dashboard",     P_DASHBOARD);
        addNavBtn(sidebar, "🔍  Search Flights", P_SEARCH);
        addNavBtn(sidebar, "🗺  Route Map",       P_ROUTEMAP);
        addNavBtn(sidebar, "💺  Flight Classes",  P_CLASS);

        addNavSection(sidebar, "MY ACCOUNT");
        boolean isGuest = AppSession.getInstance().isGuest();
        addNavBtn(sidebar, "📋  My Bookings",     P_HISTORY);
        if (!isGuest) {
            addNavBtn(sidebar, "⭐  Loyalty Points", P_LOYALTY);
            addNavBtn(sidebar, "👤  My Profile",     P_PROFILE);
        }

        addNavSection(sidebar, "ADMIN");
        addNavBtn(sidebar, "👥  Passengers",      P_PAXMGR);

        sidebar.add(Box.createVerticalGlue());

        //Version label
        JLabel ver = UITheme.label("  v2.0 — SkyLink ARS", UITheme.F_SMALL, new Color(80, 110, 170));
        ver.setBorder(new EmptyBorder(8, 12, 4, 0));
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(ver);

        return sidebar;
    }

    private void addNavSection(JPanel sidebar, String text) {
        JLabel l = UITheme.label("  " + text, new Font("Segoe UI", Font.BOLD, 10), new Color(100, 140, 200));
        l.setBorder(new EmptyBorder(14, 8, 4, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(l);
    }

    private void addNavBtn(JPanel sidebar, String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.F_NAV);
        btn.setForeground(new Color(200, 215, 245));
        btn.setBackground(new Color(12, 34, 88));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 38));
        btn.setPreferredSize(new Dimension(200, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(0, 16, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(new Color(25, 55, 120));
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(new Color(12, 34, 88));
            }
        });
        btn.addActionListener(e -> showPanel(panelName));
        sidebar.add(btn);
    }

    //Content area
    private JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(UITheme.SURFACE);

        //Lazy-init all panels
        dashboardPanel  = new DashboardPanel(this);
        searchPanel     = new FlightSearchPanel(this);
        seatPanel       = new SeatSelectionPanel(this);
        passengerPanel  = new PassengerInfoPanel(this);
        classPanel      = new FlightClassPanel(this);
        paymentPanel    = new PaymentPanel(this);
        confirmPanel    = new BookingConfirmPanel(this);
        historyPanel    = new BookingHistoryPanel(this);
        profilePanel    = new ProfilePanel(this);
        loyaltyPanel    = new LoyaltyPanel(this);
        routePanel      = new RouteMapPanel(this);
        paxMgrPanel     = new PassengerRegGUI();

        contentArea.add(dashboardPanel,    P_DASHBOARD);
        contentArea.add(searchPanel,       P_SEARCH);
        contentArea.add(seatPanel,         P_SEAT);
        contentArea.add(passengerPanel,    P_PASSENGER);
        contentArea.add(classPanel,        P_CLASS);
        contentArea.add(paymentPanel,      P_PAYMENT);
        contentArea.add(confirmPanel,      P_CONFIRM);
        contentArea.add(historyPanel,      P_HISTORY);
        contentArea.add(profilePanel,      P_PROFILE);
        contentArea.add(loyaltyPanel,      P_LOYALTY);
        contentArea.add(routePanel,        P_ROUTEMAP);
        contentArea.add(paxMgrPanel,       P_PAXMGR);

        return contentArea;
    }

    //Navigation
    public void showPanel(String name) {
        // Refresh panels that need up-to-date data
        if (P_DASHBOARD.equals(name)) dashboardPanel.refresh();
        if (P_HISTORY.equals(name))   historyPanel.refresh();
        if (P_LOYALTY.equals(name))   loyaltyPanel.refresh();
        if (P_PROFILE.equals(name))   profilePanel.refresh();
        if (P_SEAT.equals(name))      seatPanel.refresh();
        if (P_PAYMENT.equals(name))   paymentPanel.refresh();
        if (P_CONFIRM.equals(name))   confirmPanel.refresh();

        cardLayout.show(contentArea, name);

        //Highlight active nav button
        updateNavHighlight(name);
    }

    private void updateNavHighlight(String panelName) {
        //Reset all sidebar buttons
        Component[] sidebar = ((JPanel) getContentPane().getComponent(1)).getComponents();
        for (Component c : sidebar) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                b.setBackground(new Color(12, 34, 88));
                b.setForeground(new Color(200, 215, 245));
                activeNavBtn = null;
            }
        }
        //Activate matching button by its action command 
    }

    public void gotoSearch()          { showPanel(P_SEARCH); }
    public void gotoSeatSelection()   { seatPanel.refresh(); showPanel(P_SEAT); }
    public void gotoPassengerInfo()   { showPanel(P_PASSENGER); }
    public void gotoPayment()         { paymentPanel.refresh(); showPanel(P_PAYMENT); }
    public void gotoConfirmation()    { confirmPanel.refresh(); showPanel(P_CONFIRM); }
    public void gotoHistory()         { historyPanel.refresh(); showPanel(P_HISTORY); }
    public void gotoDashboard()       { dashboardPanel.refresh(); showPanel(P_DASHBOARD); }

    public void refreshUserLabel() {
        userLabel.setText("👤  " + AppSession.getInstance().getDisplayName());
    }
}
