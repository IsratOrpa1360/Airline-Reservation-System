package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingMgmtGUI extends JFrame {

    // Booking form
    private JTextField   txtFlightNum, txtSeat, txtPassName, txtPassport;
    private JTextField   txtNat, txtPhone, txtEmail, txtEmerg, txtDob;
    private JComboBox<String> cmbClass, cmbMeal;
    private JLabel       lblPrice;
    // Search / history
    private JTextField   txtSearchUser, txtSearchFlight;
    private DefaultTableModel model;
    private JTable       table;

    private final User currentUser;
    private final BookingManagement bm = new BookingManagement();
    private final FlightManagement  fm = new FlightManagement();

    public BookingMgmtGUI(User user) {
        this.currentUser = user;
        setTitle("Booking Management");
        setSize(1080, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.add(SimpleTheme.createHeader("Booking Management", e -> dispose()), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(SimpleTheme.BOLD_FONT);
        tabs.addTab("New Booking", buildNewBookingTab());
        tabs.addTab("Booking History", buildHistoryTab());
        main.add(tabs, BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    // ── New Booking Tab ────────────────────────────────────────────────────────
    private JPanel buildNewBookingTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 8));
        tab.setBackground(Color.WHITE);
        tab.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Row 1: Flight & Seat selection
        JPanel flightRow = SimpleTheme.sectionPanel("Flight & Seat");
        JPanel fFields = new JPanel(new GridLayout(1, 4, 10, 0));
        fFields.setBackground(Color.WHITE);

        txtFlightNum = SimpleTheme.createField(); txtFlightNum.setToolTipText("e.g. BS201");
        cmbClass     = SimpleTheme.createCombo(new String[]{"Economy","Business","First"});
        txtSeat      = SimpleTheme.createField(); txtSeat.setToolTipText("e.g. 12A");
        lblPrice     = new JLabel("---");
        lblPrice.setFont(SimpleTheme.BOLD_FONT);
        lblPrice.setForeground(SimpleTheme.HEADER_BG);

        for (String[] pair : new String[][]{{"Flight Number *",null},{"Class *",null},
            {"Seat Number *",null},{"Base Fare (BDT)",null}}) {
            JPanel cell = new JPanel(new GridLayout(2, 1, 0, 2));
            cell.setBackground(Color.WHITE);
            cell.add(SimpleTheme.createLabel(pair[0]));
            if (pair[0].startsWith("Flight")) cell.add(txtFlightNum);
            else if (pair[0].startsWith("Class")) cell.add(cmbClass);
            else if (pair[0].startsWith("Seat")) cell.add(txtSeat);
            else cell.add(lblPrice);
            fFields.add(cell);
        }
        flightRow.add(fFields, BorderLayout.CENTER);

        JPanel flightBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        flightBtns.setBackground(Color.WHITE);
        JButton btnSelectSeat = SimpleTheme.createButton("Choose Seat");
        JButton btnCheckFlight = SimpleTheme.createButton("Check Flight");
        btnSelectSeat .setPreferredSize(new Dimension(120, 28));
        btnCheckFlight.setPreferredSize(new Dimension(120, 28));
        btnSelectSeat .addActionListener(e -> openSeatSelector());
        btnCheckFlight.addActionListener(e -> checkFlight());
        flightBtns.add(btnSelectSeat); flightBtns.add(btnCheckFlight);
        flightRow.add(flightBtns, BorderLayout.SOUTH);

        // Row 2: Passenger info
        JPanel passRow = SimpleTheme.sectionPanel("Passenger Information");
        JPanel pFields = new JPanel(new GridLayout(2, 4, 8, 8));
        pFields.setBackground(Color.WHITE);

        txtPassName = SimpleTheme.createField();
        txtPassport = SimpleTheme.createField();
        txtNat      = SimpleTheme.createField();
        txtPhone    = SimpleTheme.createField();
        txtEmail    = SimpleTheme.createField();
        txtEmerg    = SimpleTheme.createField();
        txtDob      = SimpleTheme.createField(); txtDob.setToolTipText("YYYY-MM-DD");
        cmbMeal     = SimpleTheme.createCombo(new String[]{
            "Standard","Vegetarian","Vegan","Halal","Kosher","Gluten-Free","Diabetic","Low Sodium"});

        String[] pLbls = {"Passenger Name *","Passport Number *","Nationality","Phone Number",
            "Email Address","Emergency Contact","Date of Birth","Meal Preference"};
        JComponent[] pComps = {txtPassName, txtPassport, txtNat, txtPhone,
            txtEmail, txtEmerg, txtDob, cmbMeal};
        for (int i = 0; i < pLbls.length; i++) {
            JPanel cell = new JPanel(new GridLayout(2, 1, 0, 2));
            cell.setBackground(Color.WHITE);
            cell.add(SimpleTheme.createLabel(pLbls[i]));
            cell.add(pComps[i]);
            pFields.add(cell);
        }
        passRow.add(pFields, BorderLayout.CENTER);

        // Row 3: Buttons
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        actionRow.setBackground(Color.WHITE);
        actionRow.setBorder(BorderFactory.createLineBorder(SimpleTheme.BORDER_COL));
        JButton btnBook    = SimpleTheme.createButton("Process Booking & Pay");
        JButton btnClear   = SimpleTheme.createButton("Clear Form");
        btnBook.setPreferredSize(new Dimension(180, 32));
        btnClear.setPreferredSize(new Dimension(120, 32));
        btnBook .addActionListener(e -> handleBooking());
        btnClear.addActionListener(e -> clearNewBookingForm());
        actionRow.add(btnBook); actionRow.add(btnClear);

        JPanel top = new JPanel(new GridLayout(3, 1, 0, 8));
        top.setBackground(Color.WHITE);
        top.add(flightRow); top.add(passRow); top.add(actionRow);
        tab.add(top, BorderLayout.CENTER);

        // Pre-fill username if logged in
        if (currentUser != null && !currentUser.getMembershipTier().equals("Guest")) {
            txtEmail.setText(currentUser.getEmail());
        }

        return tab;
    }

    // ── History Tab ────────────────────────────────────────────────────────────
    private JPanel buildHistoryTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 8));
        tab.setBackground(Color.WHITE);
        tab.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        searchRow.setBackground(Color.WHITE);
        searchRow.setBorder(BorderFactory.createLineBorder(SimpleTheme.BORDER_COL));
        txtSearchUser   = SimpleTheme.createField(); txtSearchUser.setPreferredSize(new Dimension(150, 28));
        txtSearchFlight = SimpleTheme.createField(); txtSearchFlight.setPreferredSize(new Dimension(120, 28));
        JButton btnMine   = SimpleTheme.createButton("My Bookings");
        JButton btnByUser = SimpleTheme.createButton("By Username");
        JButton btnByFlt  = SimpleTheme.createButton("By Flight");
        JButton btnAll    = SimpleTheme.createButton("All Bookings");
        JButton btnCancel = SimpleTheme.createDangerButton("Cancel Booking");
        for (JButton b : new JButton[]{btnMine, btnByUser, btnByFlt, btnAll, btnCancel})
            b.setPreferredSize(new Dimension(120, 28));

        btnMine  .addActionListener(e -> loadForUser(currentUser != null ? currentUser.getUsername() : ""));
        btnByUser.addActionListener(e -> loadForUser(txtSearchUser.getText().trim()));
        btnByFlt .addActionListener(e -> loadForFlight(txtSearchFlight.getText().trim()));
        btnAll   .addActionListener(e -> loadAllBookings());
        btnCancel.addActionListener(e -> handleCancel());

        searchRow.add(SimpleTheme.createLabel("Username:"));
        searchRow.add(txtSearchUser);
        searchRow.add(SimpleTheme.createLabel("Flight:"));
        searchRow.add(txtSearchFlight);
        searchRow.add(btnMine);
        searchRow.add(btnByUser); searchRow.add(btnByFlt); searchRow.add(btnAll); searchRow.add(btnCancel);
        tab.add(searchRow, BorderLayout.NORTH);

        String[] cols = {"Booking ID","Username","Flight","Seat","Class","Passenger","Total (BDT)","Status","Date"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        SimpleTheme.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(SimpleTheme.BORDER_COL));

        JPanel tablePanel = new JPanel(new BorderLayout(0, 4));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(SimpleTheme.createBoldLabel("Booking Records"), BorderLayout.NORTH);
        tablePanel.add(sp, BorderLayout.CENTER);
        tab.add(tablePanel, BorderLayout.CENTER);

        loadForUser(currentUser != null ? currentUser.getUsername() : "");
        return tab;
    }

    // ── Actions ────────────────────────────────────────────────────────────────
    private void openSeatSelector() {
        String fn  = txtFlightNum.getText().trim().toUpperCase();
        String cls = (String) cmbClass.getSelectedItem();
        SeatSelectGUI dlg = new SeatSelectGUI(fn.isEmpty() ? null : fn, cls, this);
        String seat = dlg.getSelectedSeat();
        if (seat != null) {
            txtSeat.setText(seat);
            checkFlight();
        }
    }

    private void checkFlight() {
        String fn  = txtFlightNum.getText().trim().toUpperCase();
        if (fn.isEmpty()) { lblPrice.setText("Enter flight number first"); return; }
        Flight f = fm.findByNumber(fn);
        if (f == null) { lblPrice.setText("Flight not found"); return; }
        String cls = (String) cmbClass.getSelectedItem();
        double price = f.getDynamicPrice(cls);
        lblPrice.setText(String.format("BDT %,.0f  (base)", price));
    }

    private void handleBooking() {
        String fn = txtFlightNum.getText().trim().toUpperCase();
        String seat = txtSeat.getText().trim().toUpperCase();
        String name = txtPassName.getText().trim();
        String pass = txtPassport.getText().trim();
        String cls  = (String) cmbClass.getSelectedItem();

        if (fn.isEmpty() || seat.isEmpty() || name.isEmpty() || pass.isEmpty()) {
            SimpleTheme.showError(this, "Flight Number, Seat, Passenger Name and Passport are required."); return;
        }
        Flight flight = fm.findByNumber(fn);
        if (flight == null) { SimpleTheme.showError(this, "Flight " + fn + " not found."); return; }
        if (flight.getAvailableSeats() <= 0) { SimpleTheme.showError(this, "No seats available on this flight."); return; }
        if (bm.isSeatBooked(fn, seat)) {
            SimpleTheme.showError(this, "Seat " + seat + " is already booked."); return;
        }

        // Calculate price
        double baseFare   = flight.getDynamicPrice(cls);
        double surcharge  = cls.equals("Business") ? baseFare * 0.10 : cls.equals("First") ? baseFare * 0.20 : 0;
        Payment pay = new Payment(baseFare, surcharge, 0, "");
        double total = pay.calculateTotal();

        // Show payment dialog
        String method = (String) JOptionPane.showInputDialog(this,
            String.format("Total Amount: BDT %,.2f\n\nSelect Payment Method:", total),
            "Payment", JOptionPane.QUESTION_MESSAGE, null,
            new String[]{"Credit Card","Debit Card","Mobile Banking","Cash/Offline"},
            "Credit Card");
        if (method == null) return;

        pay.setPaymentMethod(method);
        pay.processPayment();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String user  = currentUser != null ? currentUser.getUsername() : "guest";
        Booking booking = new Booking(
            bm.generateBookingId(), user, fn, seat, cls,
            name, pass,
            txtNat.getText().trim(), txtPhone.getText().trim(), txtEmail.getText().trim(),
            txtEmerg.getText().trim(), (String) cmbMeal.getSelectedItem(), txtDob.getText().trim(),
            pay.calculateTotal(), true, "Confirmed", today);

        bm.saveBooking(booking);
        fm.incrementBooked(fn);

        // Award loyalty points
        if (currentUser != null && !currentUser.getMembershipTier().equals("Guest")) {
            int pts = (int)(total / 1000.0 * LoyaltyProgram.POINTS_PER_1000);
            currentUser.addLoyaltyPoints(pts);
            new UserManagement().updateUser(currentUser);
        }

        SimpleTheme.showInfo(this, "Booking Confirmed!\n\n"
            + "Booking ID : " + booking.getBookingId() + "\n"
            + "Flight     : " + fn + "   Seat: " + seat + "   Class: " + cls + "\n"
            + "Passenger  : " + name + "\n"
            + "Txn ID     : " + pay.getTransactionId() + "\n"
            + String.format("Total Paid : BDT %,.2f", pay.calculateTotal()));

        clearNewBookingForm();
        loadForUser(user);
    }

    private void handleCancel() {
        int row = table.getSelectedRow();
        if (row < 0) { SimpleTheme.showError(this, "Select a booking to cancel."); return; }
        String bid    = (String) model.getValueAt(row, 0);
        String status = (String) model.getValueAt(row, 7);
        if ("Cancelled".equals(status)) { SimpleTheme.showError(this, "Booking is already cancelled."); return; }
        if (!SimpleTheme.confirm(this, "Cancel booking " + bid + "?")) return;
        Booking b = bm.findById(bid);
        if (b == null) { SimpleTheme.showError(this, "Booking not found."); return; }
        bm.cancelBooking(bid);
        fm.decrementBooked(b.getFlightNumber());
        SimpleTheme.showInfo(this, "Booking " + bid + " cancelled.");
        loadAllBookings();
    }

    private void loadForUser(String username) {
        model.setRowCount(0);
        if (username.isEmpty()) return;
        for (Booking b : bm.getBookingsForUser(username)) addBookingRow(b);
    }

    private void loadForFlight(String fn) {
        model.setRowCount(0);
        if (fn.isEmpty()) return;
        for (Booking b : bm.getBookingsForFlight(fn)) addBookingRow(b);
    }

    private void loadAllBookings() {
        model.setRowCount(0);
        for (Booking b : bm.getAllBookings()) addBookingRow(b);
    }

    private void addBookingRow(Booking b) {
        model.addRow(new Object[]{
            b.getBookingId(), b.getUsername(), b.getFlightNumber(),
            b.getSeatNumber(), b.getSeatClass(), b.getPassengerName(),
            String.format("%.2f", b.getTotalCost()), b.getBookingStatus(), b.getBookingDate()
        });
    }

    private void clearNewBookingForm() {
        txtFlightNum.setText(""); txtSeat.setText("");
        txtPassName.setText(""); txtPassport.setText("");
        txtNat.setText(""); txtPhone.setText("");
        txtEmerg.setText(""); txtDob.setText("");
        cmbClass.setSelectedIndex(0); cmbMeal.setSelectedIndex(0);
        lblPrice.setText("---");
        if (currentUser != null && !currentUser.getMembershipTier().equals("Guest"))
            txtEmail.setText(currentUser.getEmail());
        else txtEmail.setText("");
    }
}
