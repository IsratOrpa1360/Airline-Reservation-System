package GUI;

import Entities_All.Passenger;
import FileManagement.PassengerManagement;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PassengerMgmtGUI extends JFrame {

    private JTextField txtPl, txtName, txtPid, txtPassport, txtNat, txtPhone, txtEmail;
    private JTextField txtEmerg, txtDob, txtMeal, txtFlight;
    private JComboBox<String> cmbGender;
    private DefaultTableModel model;
    private JTable table;
    private final PassengerManagement pm = new PassengerManagement();

    public PassengerMgmtGUI() {
        setTitle("Passenger Management");
        setSize(1080, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.add(SimpleTheme.createHeader("Passenger Management", e -> dispose()), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(Color.WHITE);
        center.setBorder(new EmptyBorder(8, 10, 8, 10));
        center.add(buildForm(), BorderLayout.NORTH);
        center.add(buildTable(), BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);

        add(main);
        loadAll();
        setVisible(true);
    }

    private JPanel buildForm() {
        JPanel card = SimpleTheme.sectionPanel("Passenger Details");

        JPanel fields = new JPanel(new GridLayout(3, 4, 8, 8));
        fields.setBackground(Color.WHITE);

        txtPl       = SimpleTheme.createField(); txtPl.setToolTipText("Auto-assigned for new; must match for update/delete");
        txtName     = SimpleTheme.createField();
        txtPid      = SimpleTheme.createField(); txtPid.setToolTipText("Optional passenger ID / loyalty ID");
        cmbGender   = SimpleTheme.createCombo(new String[]{"Male","Female","Other"});
        txtEmail    = SimpleTheme.createField();
        txtPhone    = SimpleTheme.createField();
        txtPassport = SimpleTheme.createField();
        txtFlight   = SimpleTheme.createField(); txtFlight.setToolTipText("e.g. BS201");
        txtNat      = SimpleTheme.createField();
        txtEmerg    = SimpleTheme.createField();
        txtMeal     = SimpleTheme.createField(); txtMeal.setToolTipText("e.g. Vegetarian, Halal");
        txtDob      = SimpleTheme.createField(); txtDob.setToolTipText("YYYY-MM-DD");

        String[] lbl = {"PL (ID)","Full Name *","Passenger ID","Gender",
            "Email Address","Phone Number","Passport Number *","Flight Number",
            "Nationality","Emergency Contact","Meal Preference","Date of Birth"};
        JComponent[] comps = {txtPl, txtName, txtPid, cmbGender,
            txtEmail, txtPhone, txtPassport, txtFlight,
            txtNat, txtEmerg, txtMeal, txtDob};

        for (int i = 0; i < lbl.length; i++) {
            JPanel cell = new JPanel(new GridLayout(2, 1, 0, 2));
            cell.setBackground(Color.WHITE);
            cell.add(SimpleTheme.createLabel(lbl[i]));
            cell.add(comps[i]);
            fields.add(cell);
        }
        card.add(fields, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        btnRow.setBackground(Color.WHITE);
        JTextField srch = SimpleTheme.createField();
        srch.setPreferredSize(new Dimension(160, 28));
        srch.setToolTipText("Search by name or passport number");

        JButton btnAdd   = SimpleTheme.createButton("Add Passenger");
        JButton btnUpd   = SimpleTheme.createButton("Update");
        JButton btnDel   = SimpleTheme.createDangerButton("Delete");
        JButton btnSrch  = SimpleTheme.createButton("Search");
        JButton btnAll   = SimpleTheme.createButton("View All");
        JButton btnClear = SimpleTheme.createButton("Clear Form");
        for (JButton b : new JButton[]{btnAdd, btnUpd, btnDel, btnSrch, btnAll, btnClear})
            b.setPreferredSize(new Dimension(112, 28));

        btnAdd  .addActionListener(e -> handleAdd());
        btnUpd  .addActionListener(e -> handleUpdate());
        btnDel  .addActionListener(e -> handleDelete());
        btnSrch .addActionListener(e -> handleSearch(srch.getText().trim()));
        btnAll  .addActionListener(e -> loadAll());
        btnClear.addActionListener(e -> clearForm());

        btnRow.add(SimpleTheme.createLabel("Search:"));
        btnRow.add(srch);
        btnRow.add(btnAdd); btnRow.add(btnUpd); btnRow.add(btnDel);
        btnRow.add(btnSrch); btnRow.add(btnAll); btnRow.add(btnClear);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTable() {
        String[] cols = {"PL","Name","Gender","Email","Phone","Passport","Flight","Nationality","Meal","DOB"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        SimpleTheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent ev) {
                int r = table.getSelectedRow();
                if (r >= 0) fillFromRow(r);
            }
        });
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(SimpleTheme.BORDER_COL));

        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.add(SimpleTheme.createBoldLabel("Passenger Records"), BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void handleAdd() {
        if (!validateForm()) return;
        // Auto-assign next PL
        int nextPl = pm.getAllPassengers().length + 1;
        if (!txtPl.getText().trim().isEmpty()) {
            try { nextPl = Integer.parseInt(txtPl.getText().trim()); } catch (Exception ignored) {}
        }
        Passenger pa = buildPassenger(nextPl);
        pm.writePassenger(pa, true);
        loadAll(); clearForm();
        SimpleTheme.showInfo(this, "Passenger \"" + pa.getName() + "\" added (PL=" + nextPl + ").");
    }

    private void handleUpdate() {
        String plStr = txtPl.getText().trim();
        if (plStr.isEmpty()) { SimpleTheme.showError(this, "PL (ID) is required for update."); return; }
        if (!validateForm()) return;
        int pl;
        try { pl = Integer.parseInt(plStr); } catch (Exception ex) {
            SimpleTheme.showError(this, "PL must be a number."); return;
        }
        pm.updatePassenger(buildPassenger(pl));
        loadAll(); clearForm();
        SimpleTheme.showInfo(this, "Passenger updated.");
    }

    private void handleDelete() {
        String plStr = txtPl.getText().trim();
        if (plStr.isEmpty()) { SimpleTheme.showError(this, "PL (ID) is required for delete."); return; }
        int pl;
        try { pl = Integer.parseInt(plStr); } catch (Exception ex) {
            SimpleTheme.showError(this, "PL must be a number."); return;
        }
        if (!SimpleTheme.confirm(this, "Delete passenger with PL=" + pl + "?")) return;
        pm.deletePassenger(pl);
        loadAll(); clearForm();
        SimpleTheme.showInfo(this, "Passenger deleted.");
    }

    private void handleSearch(String term) {
        if (term.isEmpty()) { loadAll(); return; }
        model.setRowCount(0);
        for (Passenger p : pm.getAllPassengers()) {
            if (p.getName().toLowerCase().contains(term.toLowerCase())
                || p.getPassportNumber().toLowerCase().contains(term.toLowerCase())
                || p.getEmail().toLowerCase().contains(term.toLowerCase()))
                addRow(p);
        }
    }

    private void loadAll() {
        model.setRowCount(0);
        for (Passenger p : pm.getAllPassengers()) addRow(p);
    }

    private void addRow(Passenger p) {
        model.addRow(new Object[]{
            p.getPl(), p.getName(), p.getGender(), p.getEmail(),
            p.getPhoneNumber(), p.getPassportNumber(), p.getFlightNumber(),
            p.getNationality(), p.getPreferredMeal(), p.getDob()
        });
    }

    private void fillFromRow(int r) {
        txtPl      .setText(model.getValueAt(r, 0).toString());
        txtName    .setText((String) model.getValueAt(r, 1));
        cmbGender  .setSelectedItem(model.getValueAt(r, 2));
        txtEmail   .setText((String) model.getValueAt(r, 3));
        txtPhone   .setText((String) model.getValueAt(r, 4));
        txtPassport.setText((String) model.getValueAt(r, 5));
        txtFlight  .setText((String) model.getValueAt(r, 6));
        txtNat     .setText((String) model.getValueAt(r, 7));
        txtMeal    .setText((String) model.getValueAt(r, 8));
        txtDob     .setText((String) model.getValueAt(r, 9));
    }

    private Passenger buildPassenger(int pl) {
        return new Passenger(
            pl,
            txtName.getText().trim(),
            txtPid.getText().trim(),
            (String) cmbGender.getSelectedItem(),
            txtEmail.getText().trim(),
            txtPhone.getText().trim(),
            txtPassport.getText().trim(),
            txtFlight.getText().trim(),
            txtNat.getText().trim(),
            txtEmerg.getText().trim(),
            txtMeal.getText().trim(),
            txtDob.getText().trim()
        );
    }

    private boolean validateForm() {
        if (txtName.getText().trim().isEmpty()) {
            SimpleTheme.showError(this, "Full Name is required."); return false;
        }
        if (txtPassport.getText().trim().isEmpty()) {
            SimpleTheme.showError(this, "Passport Number is required."); return false;
        }
        return true;
    }

    private void clearForm() {
        for (JTextField f : new JTextField[]{txtPl, txtName, txtPid, txtPassport, txtNat,
            txtPhone, txtEmail, txtEmerg, txtDob, txtMeal, txtFlight}) f.setText("");
        cmbGender.setSelectedIndex(0);
    }
}
