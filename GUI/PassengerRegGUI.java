package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import Entities_All.Passenger;
import FileManagement.PassengerManagement;

public class PassengerRegGUI extends JPanel implements ActionListener {

    //Color palette
    private static final Color C_HEADER      = new Color(15,  40,  80);
    private static final Color C_PRIMARY     = new Color(25,  70, 145);
    private static final Color C_ACCENT      = new Color(41, 121, 255);
    private static final Color C_SUCCESS     = new Color(34, 139,  34);
    private static final Color C_DANGER      = new Color(200,  40,  40);
    private static final Color C_WARNING     = new Color(200, 130,   0);
    private static final Color C_BG          = new Color(236, 241, 248);
    private static final Color C_CARD        = Color.WHITE;
    private static final Color C_TXT_MAIN    = new Color(20,  30,  50);
    private static final Color C_TXT_MUTED   = new Color(100, 116, 139);
    private static final Color C_BORDER      = new Color(210, 220, 235);
    private static final Color C_ROW_ALT     = new Color(246, 249, 253);
    private static final Color C_SEL_ROW     = new Color(210, 228, 255);

    //Fonts
    private static final Font F_APP_TITLE    = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_APP_SUB      = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_SECTION      = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_LABEL        = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_INPUT        = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BTN          = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_TH           = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font F_TD           = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_STATUS       = new Font("Segoe UI", Font.PLAIN, 11);

    //Form widgets
    private JTextField txtPl, txtName, txtPassengerId, txtDob;
    private JTextField txtEmail, txtPhoneNumber, txtEmergencyContact;
    private JTextField txtPassportNumber, txtFlightNumber, txtNationality;
    private JTextField txtSearch;
    private JRadioButton rbMale, rbFemale;
    private ButtonGroup genderGroup;
    private JComboBox<String> cmbMeal;

    //Buttons
    private JButton btnRegister, btnUpdate, btnDelete, btnClear, btnSearch;

    //Table 
    private DefaultTableModel tModel;
    private JTable table;
    private JLabel statusLabel;
    private JLabel countLabel;


    public PassengerRegGUI() {
        applyLookAndFeel();
        setLayout(new BorderLayout(0, 0));
        setBackground(C_BG);
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildMain(),      BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
        loadAllPassengers();
    }

    //Look-and-feel
    private void applyLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        UIManager.put("OptionPane.messageFont",  F_INPUT);
        UIManager.put("OptionPane.buttonFont",   F_BTN);
        UIManager.put("Button.font",             F_BTN);
    }

    //Header bar
    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(C_HEADER);
        hdr.setBorder(new EmptyBorder(14, 28, 14, 28));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);

        JLabel title = new JLabel("✈  Airline Reservation System");
        title.setFont(F_APP_TITLE);
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Passenger Registration & Management");
        sub.setFont(F_APP_SUB);
        sub.setForeground(new Color(170, 195, 230));

        left.add(title);
        left.add(sub);
        hdr.add(left, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        JLabel version = new JLabel("v2.0  ");
        version.setFont(F_APP_SUB);
        version.setForeground(new Color(130, 160, 200));
        JButton back = new JButton("← Back to Dashboard");
        back.setFont(F_BTN);
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(30, 60, 130));
        back.setBorderPainted(false);
        back.setFocusPainted(false);
        back.setOpaque(true);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> {
            if (MainFrame.get() != null) MainFrame.get().gotoDashboard();
        });
        rightPanel.add(version);
        rightPanel.add(back);
        hdr.add(rightPanel, BorderLayout.EAST);

        return hdr;
    }

    //Main content (form + table)
    private JSplitPane buildMain() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildFormPanel(), buildTablePanel());
        split.setDividerSize(6);
        split.setDividerLocation(390);
        split.setResizeWeight(0.0);
        split.setBorder(new EmptyBorder(12, 12, 12, 12));
        split.setBackground(C_BG);
        return split;
    }

    //Left: form card
    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(C_BG);
        wrapper.setPreferredSize(new Dimension(372, 0));

        JPanel card = new JPanel();
        card.setBackground(C_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1),
                new EmptyBorder(18, 18, 18, 18)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Passenger Details");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(C_PRIMARY);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        heading.setBorder(new EmptyBorder(0, 0, 12, 0));
        card.add(heading);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(C_CARD);
        form.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(3, 0, 3, 0);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 1; g.weightx = 1;

        addSection(form, g, "▸  Personal Information");
        txtPl            = addField(form, g, "PL Number *");
        txtName          = addField(form, g, "Full Name *");
        txtPassengerId   = addField(form, g, "Passenger ID *");
        txtDob           = addField(form, g, "Date of Birth  (DD/MM/YYYY)");
        addGenderRow(form, g);

        addSection(form, g, "▸  Contact Information");
        txtEmail             = addField(form, g, "Email Address *");
        txtPhoneNumber       = addField(form, g, "Phone Number *");
        txtEmergencyContact  = addField(form, g, "Emergency Contact *");

        addSection(form, g, "▸  Travel Information");
        txtPassportNumber = addField(form, g, "Passport Number *");
        txtFlightNumber   = addField(form, g, "Flight Number *");
        txtNationality    = addField(form, g, "Nationality *");
        addMealRow(form, g);

        card.add(form);
        card.add(Box.createVerticalStrut(16));
        card.add(buildButtonBar());

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private void addSection(JPanel p, GridBagConstraints g, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(F_SECTION);
        lbl.setForeground(C_PRIMARY);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 215, 240)),
                new EmptyBorder(9, 0, 5, 0)));
        p.add(lbl, g);
        g.gridy++;
    }

    private JTextField addField(JPanel p, GridBagConstraints g, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL);
        lbl.setForeground(C_TXT_MUTED);
        lbl.setBorder(new EmptyBorder(2, 0, 1, 0));
        p.add(lbl, g);
        g.gridy++;

        JTextField tf = new JTextField();
        tf.setFont(F_INPUT);
        tf.setForeground(C_TXT_MAIN);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                new EmptyBorder(5, 9, 5, 9)));
        tf.setPreferredSize(new Dimension(0, 34));
        p.add(tf, g);
        g.gridy++;
        return tf;
    }

    private void addGenderRow(JPanel p, GridBagConstraints g) {
        JLabel lbl = new JLabel("Gender *");
        lbl.setFont(F_LABEL);
        lbl.setForeground(C_TXT_MUTED);
        lbl.setBorder(new EmptyBorder(2, 0, 1, 0));
        p.add(lbl, g);
        g.gridy++;

        rbMale   = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        rbMale.setFont(F_LABEL);
        rbFemale.setFont(F_LABEL);
        rbMale.setBackground(C_CARD);
        rbFemale.setBackground(C_CARD);
        rbMale.setForeground(C_TXT_MAIN);
        rbFemale.setForeground(C_TXT_MAIN);
        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(C_CARD);
        row.add(rbMale);
        row.add(Box.createHorizontalStrut(18));
        row.add(rbFemale);
        p.add(row, g);
        g.gridy++;
    }

    private void addMealRow(JPanel p, GridBagConstraints g) {
        JLabel lbl = new JLabel("Preferred Meal");
        lbl.setFont(F_LABEL);
        lbl.setForeground(C_TXT_MUTED);
        lbl.setBorder(new EmptyBorder(2, 0, 1, 0));
        p.add(lbl, g);
        g.gridy++;

        cmbMeal = new JComboBox<>(new String[]
                {"-- Select --", "Vegetarian", "Non-Vegetarian", "Vegan", "Gluten-Free", "Halal", "Kosher"});
        cmbMeal.setFont(F_INPUT);
        cmbMeal.setBackground(C_CARD);
        cmbMeal.setPreferredSize(new Dimension(0, 34));
        p.add(cmbMeal, g);
        g.gridy++;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new GridLayout(2, 2, 8, 8));
        bar.setBackground(C_CARD);
        bar.setAlignmentX(LEFT_ALIGNMENT);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));

        btnRegister = makeBtn("Register",  C_ACCENT,   Color.WHITE);
        btnUpdate   = makeBtn("Update",    C_SUCCESS,  Color.WHITE);
        btnDelete   = makeBtn("Delete",    C_DANGER,   Color.WHITE);
        btnClear    = makeBtn("Clear",     new Color(140, 152, 170), Color.WHITE);

        bar.add(btnRegister);
        bar.add(btnUpdate);
        bar.add(btnDelete);
        bar.add(btnClear);
        return bar;
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(F_BTN);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(this);

        Color hover = bg.darker();
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    //Right: table panel
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(C_BG);

        // Search bar
        JPanel searchCard = new JPanel(new BorderLayout(10, 0));
        searchCard.setBackground(C_CARD);
        searchCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                new EmptyBorder(10, 14, 10, 14)));

        JLabel searchLbl = new JLabel("Search by PL Number:");
        searchLbl.setFont(F_SECTION);
        searchLbl.setForeground(C_TXT_MAIN);

        txtSearch = new JTextField();
        txtSearch.setFont(F_INPUT);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                new EmptyBorder(5, 9, 5, 9)));
        txtSearch.setPreferredSize(new Dimension(180, 34));
        txtSearch.addActionListener(e -> handleSearch());

        btnSearch = makeBtn("Search", C_WARNING, Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(90, 34));

        JButton btnShowAll = makeBtn("Show All", C_PRIMARY, Color.WHITE);
        btnShowAll.setPreferredSize(new Dimension(90, 34));
        btnShowAll.addActionListener(e -> { txtSearch.setText(""); loadAllPassengers(); });

        JPanel searchBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchBtns.setBackground(C_CARD);
        searchBtns.add(txtSearch);
        searchBtns.add(btnSearch);
        searchBtns.add(btnShowAll);

        searchCard.add(searchLbl, BorderLayout.WEST);
        searchCard.add(searchBtns, BorderLayout.EAST);
        panel.add(searchCard, BorderLayout.NORTH);

        //Table card
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(C_CARD);
        tableCard.setBorder(BorderFactory.createLineBorder(C_BORDER));

        //Table header bar
        JPanel tableHdr = new JPanel(new BorderLayout());
        tableHdr.setBackground(C_PRIMARY);
        tableHdr.setBorder(new EmptyBorder(9, 14, 9, 14));
        JLabel tableTitle = new JLabel("Registered Passengers");
        tableTitle.setFont(F_SECTION);
        tableTitle.setForeground(Color.WHITE);
        countLabel = new JLabel("0 records");
        countLabel.setFont(F_APP_SUB);
        countLabel.setForeground(new Color(170, 195, 230));
        tableHdr.add(tableTitle, BorderLayout.WEST);
        tableHdr.add(countLabel, BorderLayout.EAST);
        tableCard.add(tableHdr, BorderLayout.NORTH);

        //Table
        String[] cols = {"PL", "Name", "Passenger ID", "Gender", "Email",
                "Phone", "Passport No.", "Flight No.", "Nationality", "Emergency Contact", "Meal", "DOB"};
        tModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tModel);
        table.setFont(F_TD);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(C_SEL_ROW);
        table.setSelectionForeground(C_TXT_MAIN);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader th = table.getTableHeader();
        th.setFont(F_TH);
        th.setBackground(new Color(244, 247, 252));
        th.setForeground(C_TXT_MAIN);
        th.setReorderingAllowed(false);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDER));
        th.setPreferredSize(new Dimension(0, 34));

        //Striped row renderer
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!sel) setBackground(row % 2 == 0 ? C_CARD : C_ROW_ALT);
                setForeground(sel ? C_TXT_MAIN : C_TXT_MAIN);
                return this;
            }
        };
        for (int i = 0; i < cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);

        //Column widths
        int[] widths = {50, 130, 110, 72, 170, 115, 115, 90, 105, 135, 105, 95};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        //Row click - populate form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) populateFormFromTableRow(row);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JLabel hint = new JLabel("  💡 Click any row to load passenger details into the form");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(C_TXT_MUTED);
        hint.setBorder(new EmptyBorder(5, 8, 5, 8));
        hint.setBackground(new Color(250, 252, 255));
        hint.setOpaque(true);
        tableCard.add(hint, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    //Status bar
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_HEADER);
        bar.setBorder(new EmptyBorder(5, 20, 5, 20));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(F_STATUS);
        statusLabel.setForeground(new Color(170, 195, 230));

        JLabel copy = new JLabel("Airline Reservation System © 2024");
        copy.setFont(F_STATUS);
        copy.setForeground(new Color(100, 130, 170));

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(copy, BorderLayout.EAST);
        return bar;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if      (src == btnRegister) handleRegister();
        else if (src == btnUpdate)   handleUpdate();
        else if (src == btnDelete)   handleDelete();
        else if (src == btnSearch)   handleSearch();
        else if (src == btnClear)    clearForm();
    }

    //Register 
    public void registerClicked() { handleRegister(); }
    private void handleRegister() {
        if (!validateForm(true)) return;

        String plStr = txtPl.getText().trim();
        int pl;
        try { pl = Integer.parseInt(plStr); }
        catch (NumberFormatException ex) { showError("PL Number must be a whole number (e.g. 1001)."); return; }

        //Duplicate PL check
        PassengerManagement pm = new PassengerManagement();
        if (pm.searchPassenger(pl) != null) {
            showError("A passenger with PL " + pl + " already exists. Use Update to modify.");
            return;
        }

        Passenger p = buildPassengerFromForm(pl);
        pm.writePassenger(p, true);
        refreshTable();
        showSuccess("Passenger \"" + p.getName() + "\" registered successfully.");
        clearForm();
    }

    //Update
    public void updateClicked() { handleUpdate(); }
    private void handleUpdate() {
        if (!validateForm(true)) return;
        int pl;
        try { pl = Integer.parseInt(txtPl.getText().trim()); }
        catch (NumberFormatException ex) { showError("PL Number must be a whole number."); return; }

        Passenger p = buildPassengerFromForm(pl);
        PassengerManagement pm = new PassengerManagement();
        pm.updatePassenger(p);
        refreshTable();
        showSuccess("Passenger PL " + pl + " updated successfully.");
    }

    //Delete 
    public void deleteClicked() { handleDelete(); }
    private void handleDelete() {
        String plStr = txtPl.getText().trim();
        if (plStr.isEmpty()) { showError("Enter the PL Number of the passenger to delete."); return; }
        int pl;
        try { pl = Integer.parseInt(plStr); }
        catch (NumberFormatException ex) { showError("PL Number must be a whole number."); return; }

        PassengerManagement pm = new PassengerManagement();
        if (pm.searchPassenger(pl) == null) {
            showError("No passenger found with PL " + pl + "."); return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete passenger with PL " + pl + "?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        pm.deletePassenger(pl);
        refreshTable();
        setStatus("Passenger PL " + pl + " deleted.", C_DANGER);
        clearForm();
    }

    //Search 
    public void searchClicked() { handleSearch(); }
    private void handleSearch() {
        String plStr = txtSearch.getText().trim();
        if (plStr.isEmpty()) { loadAllPassengers(); return; }

        int pl;
        try { pl = Integer.parseInt(plStr); }
        catch (NumberFormatException ex) { showError("PL must be a valid number."); return; }

        PassengerManagement pm = new PassengerManagement();
        Passenger p = pm.searchPassenger(pl);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "No passenger found with PL: " + pl,
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
            setStatus("No passenger found with PL " + pl + ".", C_WARNING);
            return;
        }

        tModel.setRowCount(0);
        tModel.addRow(p.getTableRow());
        updateCount();
        populateFormFromPassenger(p);
        setStatus("Found: " + p.getName() + " (PL " + pl + ")", C_SUCCESS);
    }

    //Clear form
    private void clearForm() {
        for (JTextField f : new JTextField[]{txtPl, txtName, txtPassengerId, txtDob,
                txtEmail, txtPhoneNumber, txtEmergencyContact,
                txtPassportNumber, txtFlightNumber, txtNationality}) {
            f.setText("");
            f.setForeground(C_TXT_MAIN);
        }
        genderGroup.clearSelection();
        cmbMeal.setSelectedIndex(0);
        txtSearch.setText("");
        setStatus("Form cleared — ready for new entry.", C_TXT_MUTED);
    }

    //Data helpers 
    private void loadAllPassengers() {
        PassengerManagement pm = new PassengerManagement();
        Passenger[] list = pm.getAllPassengers();
        tModel.setRowCount(0);
        if (list != null) {
            for (Passenger p : list) {
                if (p != null) tModel.addRow(p.getTableRow());
            }
        }
        updateCount();
        setStatus("Loaded " + tModel.getRowCount() + " passenger(s).", C_ACCENT);
    }

    public void refreshTable() { loadAllPassengers(); }

    private void updateCount() {
        int n = tModel.getRowCount();
        countLabel.setText(n + " record" + (n != 1 ? "s" : ""));
    }

    private void populateFormFromTableRow(int row) {
        txtPl.setText(tModel.getValueAt(row, 0).toString());
        txtName.setText(tModel.getValueAt(row, 1).toString());
        txtPassengerId.setText(tModel.getValueAt(row, 2).toString());
        String gender = tModel.getValueAt(row, 3).toString();
        if ("Male".equalsIgnoreCase(gender)) rbMale.setSelected(true);
        else if ("Female".equalsIgnoreCase(gender)) rbFemale.setSelected(true);
        txtEmail.setText(tModel.getValueAt(row, 4).toString());
        txtPhoneNumber.setText(tModel.getValueAt(row, 5).toString());
        txtPassportNumber.setText(tModel.getValueAt(row, 6).toString());
        txtFlightNumber.setText(tModel.getValueAt(row, 7).toString());
        txtNationality.setText(tModel.getValueAt(row, 8).toString());
        txtEmergencyContact.setText(tModel.getValueAt(row, 9).toString());
        cmbMeal.setSelectedItem(tModel.getValueAt(row, 10));
        txtDob.setText(tModel.getValueAt(row, 11).toString());
        setStatus("Loaded: " + tModel.getValueAt(row, 1) + " — edit fields and click Update.", C_ACCENT);
    }

    private void populateFormFromPassenger(Passenger p) {
        txtPl.setText(String.valueOf(p.getPl()));
        txtName.setText(p.getName());
        txtPassengerId.setText(p.getPassengerId());
        if ("Male".equalsIgnoreCase(p.getGender())) rbMale.setSelected(true);
        else if ("Female".equalsIgnoreCase(p.getGender())) rbFemale.setSelected(true);
        txtEmail.setText(p.getEmail());
        txtPhoneNumber.setText(p.getPhoneNumber());
        txtPassportNumber.setText(p.getPassportNumber());
        txtFlightNumber.setText(p.getFlightNumber());
        txtNationality.setText(p.getNationality());
        txtEmergencyContact.setText(p.getEmergencyContact());
        cmbMeal.setSelectedItem(p.getPreferredMeal());
        txtDob.setText(p.getDob());
    }

    private Passenger buildPassengerFromForm(int pl) {
        String gender = rbMale.isSelected() ? "Male" : rbFemale.isSelected() ? "Female" : "Not Specified";
        String meal   = cmbMeal.getSelectedIndex() == 0 ? "" : (String) cmbMeal.getSelectedItem();
        return new Passenger(pl,
                txtName.getText().trim(), txtPassengerId.getText().trim(), gender,
                txtEmail.getText().trim(), txtPhoneNumber.getText().trim(),
                txtPassportNumber.getText().trim(), txtFlightNumber.getText().trim(),
                txtNationality.getText().trim(), txtEmergencyContact.getText().trim(),
                meal, txtDob.getText().trim());
    }

    //Validation
    private boolean validateForm(boolean needPl) {
        if (needPl && txtPl.getText().trim().isEmpty()) {
            showError("PL Number is required."); return false;
        }
        if (txtName.getText().trim().isEmpty()) {
            showError("Full Name is required."); return false;
        }
        if (txtPassengerId.getText().trim().isEmpty()) {
            showError("Passenger ID is required."); return false;
        }
        return true;
    }

    //Status helpers 
    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
        setStatus("⚠  " + msg, C_DANGER);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
        setStatus("✔  " + msg, C_SUCCESS);
    }
}
