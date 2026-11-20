package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PaymentMgmtGUI extends JFrame {

    private JTextField txtFare, txtSurcharge, txtDemand;
    private JComboBox<String> cmbMethod;
    private JLabel lblTaxes, lblService, lblTotal;
    private JTextField txtLookupId;
    private DefaultTableModel histModel;
    private JTable histTable;

    private final User currentUser;

    public PaymentMgmtGUI(Booking existingBooking, User user) {
        this.currentUser = user;
        setTitle("Payment Management");
        setSize(960, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.add(SimpleTheme.createHeader("Payment Management", e -> dispose()), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(SimpleTheme.BOLD_FONT);
        tabs.addTab("Calculate & Process Payment", buildCalcTab());
        tabs.addTab("Payment History", buildHistoryTab());
        main.add(tabs, BorderLayout.CENTER);

        add(main);

        if (existingBooking != null) prefillFromBooking(existingBooking);

        setVisible(true);
    }

    private JPanel buildCalcTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 10));
        tab.setBackground(Color.WHITE);
        tab.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel left = new JPanel(new BorderLayout(0, 8));
        left.setBackground(Color.WHITE);

        // Input panel
        JPanel inputCard = SimpleTheme.sectionPanel("Fare Components");
        JPanel grid = new JPanel(new GridLayout(4, 2, 8, 8));
        grid.setBackground(Color.WHITE);

        txtFare      = SimpleTheme.createField(); txtFare.setToolTipText("Base flight fare in BDT");
        txtSurcharge = SimpleTheme.createField(); txtSurcharge.setToolTipText("Class surcharge (0 for Economy)");
        txtDemand    = SimpleTheme.createField(); txtDemand.setToolTipText("Demand adjustment (0 if none)");
        cmbMethod    = SimpleTheme.createCombo(new String[]{"Credit Card","Debit Card","Mobile Banking","Cash/Offline"});

        txtFare.setText("0"); txtSurcharge.setText("0"); txtDemand.setText("0");

        for (String[] p : new String[][]{{"Base Fare (BDT) *",null},{"Class Surcharge (BDT)",null},
            {"Demand Adjustment",null},{"Payment Method *",null}}) {
            JPanel cell = new JPanel(new GridLayout(2, 1, 0, 2));
            cell.setBackground(Color.WHITE);
            cell.add(SimpleTheme.createLabel(p[0]));
            if (p[0].startsWith("Base")) cell.add(txtFare);
            else if (p[0].startsWith("Class")) cell.add(txtSurcharge);
            else if (p[0].startsWith("Demand")) cell.add(txtDemand);
            else cell.add(cmbMethod);
            grid.add(cell);
        }
        inputCard.add(grid, BorderLayout.CENTER);

        JPanel calcBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        calcBtnRow.setBackground(Color.WHITE);
        JButton btnCalc = SimpleTheme.createButton("Calculate Total");
        btnCalc.setPreferredSize(new Dimension(140, 28));
        btnCalc.addActionListener(e -> calculateTotal());
        calcBtnRow.add(btnCalc);
        inputCard.add(calcBtnRow, BorderLayout.SOUTH);
        left.add(inputCard, BorderLayout.NORTH);

        // Summary panel
        JPanel sumCard = SimpleTheme.sectionPanel("Payment Summary");
        JPanel sumGrid = new JPanel(new GridLayout(5, 2, 8, 8));
        sumGrid.setBackground(Color.WHITE);
        JLabel[] keys = {
            SimpleTheme.createLabel("Taxes (15%):"),
            SimpleTheme.createLabel("Service Charges:"),
            SimpleTheme.createLabel(""),
            SimpleTheme.createLabel(""),
            SimpleTheme.createBoldLabel("TOTAL AMOUNT:"),
        };
        lblTaxes   = new JLabel("---"); lblTaxes.setFont(SimpleTheme.LABEL_FONT);
        lblService = new JLabel("BDT 500.00 (flat)"); lblService.setFont(SimpleTheme.LABEL_FONT);
        JLabel spacer1 = new JLabel(""); JLabel spacer2 = new JLabel("");
        lblTotal   = new JLabel("---");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(SimpleTheme.HEADER_BG);
        JLabel[] vals = {lblTaxes, lblService, spacer1, spacer2, lblTotal};
        for (int i = 0; i < 5; i++) { sumGrid.add(keys[i]); sumGrid.add(vals[i]); }
        sumCard.add(sumGrid, BorderLayout.CENTER);

        JPanel payBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        payBtnRow.setBackground(Color.WHITE);
        JButton btnPay = SimpleTheme.createButton("Process Payment");
        btnPay.setPreferredSize(new Dimension(140, 28));
        btnPay.addActionListener(e -> processPayment());
        payBtnRow.add(btnPay);
        sumCard.add(payBtnRow, BorderLayout.SOUTH);
        left.add(sumCard, BorderLayout.CENTER);

        tab.add(left, BorderLayout.CENTER);
        return tab;
    }

    private JPanel buildHistoryTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 8));
        tab.setBackground(Color.WHITE);
        tab.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        searchRow.setBackground(Color.WHITE);
        searchRow.setBorder(BorderFactory.createLineBorder(SimpleTheme.BORDER_COL));
        txtLookupId = SimpleTheme.createField();
        txtLookupId.setPreferredSize(new Dimension(160, 28));
        txtLookupId.setToolTipText("Booking ID");
        JButton btnMine = SimpleTheme.createButton("My Bookings");
        JButton btnAll  = SimpleTheme.createButton("All Records");
        JButton btnFind = SimpleTheme.createButton("Find by ID");
        for (JButton b : new JButton[]{btnMine, btnAll, btnFind}) b.setPreferredSize(new Dimension(110, 28));
        btnMine.addActionListener(e -> loadPayHistForUser(currentUser != null ? currentUser.getUsername() : ""));
        btnAll .addActionListener(e -> loadAllPayHist());
        btnFind.addActionListener(e -> findById(txtLookupId.getText().trim()));
        searchRow.add(SimpleTheme.createLabel("Booking ID:"));
        searchRow.add(txtLookupId);
        searchRow.add(btnMine); searchRow.add(btnAll); searchRow.add(btnFind);
        tab.add(searchRow, BorderLayout.NORTH);

        String[] cols = {"Booking ID","Username","Flight","Seat","Class","Passenger","Total (BDT)","Paid","Status"};
        histModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        histTable = new JTable(histModel);
        SimpleTheme.styleTable(histTable);
        tab.add(new JScrollPane(histTable), BorderLayout.CENTER);

        loadPayHistForUser(currentUser != null ? currentUser.getUsername() : "");
        return tab;
    }

    private void calculateTotal() {
        try {
            double fare  = Double.parseDouble(txtFare.getText().trim());
            double sur   = Double.parseDouble(txtSurcharge.getText().trim());
            double dem   = Double.parseDouble(txtDemand.getText().trim());
            Payment p = new Payment(fare, sur, dem, (String) cmbMethod.getSelectedItem());
            lblTaxes.setText(String.format("BDT %,.2f", p.getTaxes()));
            lblTotal.setText(String.format("BDT %,.2f", p.calculateTotal()));
        } catch (NumberFormatException ex) {
            SimpleTheme.showError(this, "Fare fields must be numbers.");
        }
    }

    private void processPayment() {
        try {
            double fare = Double.parseDouble(txtFare.getText().trim());
            double sur  = Double.parseDouble(txtSurcharge.getText().trim());
            double dem  = Double.parseDouble(txtDemand.getText().trim());
            Payment p = new Payment(fare, sur, dem, (String) cmbMethod.getSelectedItem());
            p.processPayment();
            SimpleTheme.showInfo(this,
                "Payment Successful!\n\n"
                + "Transaction ID : " + p.getTransactionId() + "\n"
                + String.format("Total Amount   : BDT %,.2f\n", p.calculateTotal())
                + "Method         : " + p.getPaymentMethod() + "\n"
                + "Status         : " + p.getPaymentStatus());
        } catch (NumberFormatException ex) {
            SimpleTheme.showError(this, "Fare fields must be numbers.");
        }
    }

    private void prefillFromBooking(Booking b) {
        txtFare.setText(String.valueOf(b.getTotalCost()));
    }

    private void loadPayHistForUser(String username) {
        histModel.setRowCount(0);
        if (username.isEmpty()) return;
        for (Booking b : new BookingManagement().getBookingsForUser(username)) addHistRow(b);
    }

    private void loadAllPayHist() {
        histModel.setRowCount(0);
        for (Booking b : new BookingManagement().getAllBookings()) addHistRow(b);
    }

    private void findById(String id) {
        histModel.setRowCount(0);
        if (id.isEmpty()) return;
        Booking b = new BookingManagement().findById(id);
        if (b != null) addHistRow(b);
        else SimpleTheme.showError(this, "Booking ID not found.");
    }

    private void addHistRow(Booking b) {
        histModel.addRow(new Object[]{
            b.getBookingId(), b.getUsername(), b.getFlightNumber(),
            b.getSeatNumber(), b.getSeatClass(), b.getPassengerName(),
            String.format("%.2f", b.getTotalCost()), b.isPaymentComplete() ? "Yes" : "No",
            b.getBookingStatus()
        });
    }
}
