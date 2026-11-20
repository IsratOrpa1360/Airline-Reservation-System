package GUI;

import Entities_All.Flight;
import FileManagement.FlightManagement;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

public class FlightOperationsGUI extends JFrame {

    private JTextField txtFlightNum, txtOrigin, txtDest, txtDate, txtDep, txtArr, txtDur;
    private JTextField txtTotalSeats, txtEconPrice, txtBizPrice, txtFirstPrice, txtAirline;
    private JLabel lblAvail;
    private DefaultTableModel model;
    private JTable table;
    private final FlightManagement fm = new FlightManagement();

    public FlightOperationsGUI() {
        setTitle("Flight Operations");
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.add(SimpleTheme.createHeader("Flight Operations", e -> dispose()), BorderLayout.NORTH);

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
        JPanel card = SimpleTheme.sectionPanel("Flight Details");

        JPanel fields = new JPanel(new GridLayout(3, 4, 8, 8));
        fields.setBackground(Color.WHITE);

        txtFlightNum  = SimpleTheme.createField(); txtFlightNum.setToolTipText("e.g. BS201");
        txtOrigin     = SimpleTheme.createField(); txtOrigin.setToolTipText("e.g. Dhaka");
        txtDest       = SimpleTheme.createField(); txtDest.setToolTipText("e.g. Dubai");
        txtDate       = SimpleTheme.createField(); txtDate.setToolTipText("YYYY-MM-DD");
        txtDep        = SimpleTheme.createField(); txtDep.setToolTipText("HH:MM");
        txtArr        = SimpleTheme.createField(); txtArr.setToolTipText("HH:MM");
        txtDur        = SimpleTheme.createField(); txtDur.setToolTipText("e.g. 5h 30m");
        txtTotalSeats = SimpleTheme.createField(); txtTotalSeats.setToolTipText("Integer");
        txtEconPrice  = SimpleTheme.createField(); txtEconPrice.setToolTipText("Economy price BDT");
        txtBizPrice   = SimpleTheme.createField(); txtBizPrice.setToolTipText("Business price BDT");
        txtFirstPrice = SimpleTheme.createField(); txtFirstPrice.setToolTipText("First class price BDT");
        txtAirline    = SimpleTheme.createField(); txtAirline.setToolTipText("Airline name");

        String[] lbl = {
            "Flight Number *", "Origin *", "Destination *", "Date (YYYY-MM-DD) *",
            "Dep. Time (HH:MM)", "Arr. Time (HH:MM)", "Duration", "Total Seats *",
            "Economy Price (BDT) *", "Business Price (BDT)", "First Class Price", "Airline Name"
        };
        JComponent[] comps = {txtFlightNum, txtOrigin, txtDest, txtDate,
            txtDep, txtArr, txtDur, txtTotalSeats,
            txtEconPrice, txtBizPrice, txtFirstPrice, txtAirline};

        for (int i = 0; i < 12; i++) {
            JPanel cell = new JPanel(new GridLayout(2, 1, 0, 2));
            cell.setBackground(Color.WHITE);
            cell.add(SimpleTheme.createLabel(lbl[i]));
            cell.add(comps[i]);
            fields.add(cell);
        }
        card.add(fields, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        btnRow.setBackground(Color.WHITE);
        JTextField srchField = SimpleTheme.createField();
        srchField.setPreferredSize(new Dimension(160, 28));
        srchField.setToolTipText("Flight number / origin / destination");

        JButton btnAdd    = SimpleTheme.createButton("Add Flight");
        JButton btnUpd    = SimpleTheme.createButton("Update");
        JButton btnDel    = SimpleTheme.createDangerButton("Delete");
        JButton btnSrch   = SimpleTheme.createButton("Search");
        JButton btnAll    = SimpleTheme.createButton("View All");
        JButton btnClear  = SimpleTheme.createButton("Clear Form");
        for (JButton b : new JButton[]{btnAdd, btnUpd, btnDel, btnSrch, btnAll, btnClear})
            b.setPreferredSize(new Dimension(100, 28));

        btnAdd  .addActionListener(e -> handleAdd());
        btnUpd  .addActionListener(e -> handleUpdate());
        btnDel  .addActionListener(e -> handleDelete());

        btnSrch .addActionListener(e -> handleSearch(srchField.getText().trim()));
        btnAll  .addActionListener(e -> loadAll());
        btnClear.addActionListener(e -> clearForm());

        btnRow.add(SimpleTheme.createLabel("Search:"));
        btnRow.add(srchField);
        btnRow.add(btnAdd); btnRow.add(btnUpd); btnRow.add(btnDel);
        btnRow.add(btnSrch); btnRow.add(btnAll); btnRow.add(btnClear);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTable() {
        String[] cols = {"Flight No","Origin","Destination","Date","Dep","Arr","Duration",
            "Total","Booked","Avail","Economy","Business","First","Airline"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        SimpleTheme.styleTable(table);
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
        p.add(SimpleTheme.createBoldLabel("Flight Records"), BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void handleAdd() {
        if (!validateForm()) return;
        Flight f = buildFlight();
        if (fm.findByNumber(f.getFlightNumber()) != null) {
            SimpleTheme.showError(this, "Flight " + f.getFlightNumber() + " already exists."); return;
        }
        fm.saveFlight(f);
        loadAll(); clearForm();
        SimpleTheme.showInfo(this, "Flight " + f.getFlightNumber() + " added.");
    }

    private void handleUpdate() {
        String fn = txtFlightNum.getText().trim().toUpperCase();
        if (fn.isEmpty()) { SimpleTheme.showError(this, "Enter a Flight Number to update."); return; }
        if (!validateForm()) return;
        List<Flight> all = fm.getAllFlights();
        Flight updated = buildFlight();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getFlightNumber().equalsIgnoreCase(fn)) { all.set(i, updated); found = true; break; }
        }
        if (!found) { SimpleTheme.showError(this, "Flight not found."); return; }
        saveAll(all);
        loadAll(); clearForm();
        SimpleTheme.showInfo(this, "Flight " + fn + " updated.");
    }

    private void handleDelete() {
        String fn = txtFlightNum.getText().trim().toUpperCase();
        if (fn.isEmpty()) { SimpleTheme.showError(this, "Enter a Flight Number to delete."); return; }
        if (!SimpleTheme.confirm(this, "Delete flight " + fn + "?")) return;
        List<Flight> all = fm.getAllFlights();
        all.removeIf(f -> f.getFlightNumber().equalsIgnoreCase(fn));
        saveAll(all);
        loadAll(); clearForm();
        SimpleTheme.showInfo(this, "Flight " + fn + " deleted.");
    }

    private void handleSearch(String term) {
        if (term.isEmpty()) { loadAll(); return; }
        model.setRowCount(0);
        for (Flight f : fm.getAllFlights()) {
            if (f.getFlightNumber().equalsIgnoreCase(term)
                || f.getOrigin().toLowerCase().contains(term.toLowerCase())
                || f.getDestination().toLowerCase().contains(term.toLowerCase()))
                addRow(f);
        }
    }

    private void loadAll() {
        model.setRowCount(0);
        for (Flight f : fm.getAllFlights()) addRow(f);
    }

    private void addRow(Flight f) {
        model.addRow(new Object[]{
            f.getFlightNumber(), f.getOrigin(), f.getDestination(),
            f.getDepartureDate(), f.getDepartureTime(), f.getArrivalTime(), f.getDuration(),
            f.getTotalSeats(), f.getBookedSeats(), f.getAvailableSeats(),
            String.format("%.0f", f.getEconomyPrice()),
            String.format("%.0f", f.getBusinessPrice()),
            String.format("%.0f", f.getFirstClassPrice()),
            f.getAirline()
        });
    }

    private void fillFromRow(int r) {
        txtFlightNum .setText((String) model.getValueAt(r, 0));
        txtOrigin    .setText((String) model.getValueAt(r, 1));
        txtDest      .setText((String) model.getValueAt(r, 2));
        txtDate      .setText((String) model.getValueAt(r, 3));
        txtDep       .setText((String) model.getValueAt(r, 4));
        txtArr       .setText((String) model.getValueAt(r, 5));
        txtDur       .setText((String) model.getValueAt(r, 6));
        txtTotalSeats.setText(model.getValueAt(r, 7).toString());
        txtEconPrice .setText(model.getValueAt(r, 10).toString());
        txtBizPrice  .setText(model.getValueAt(r, 11).toString());
        txtFirstPrice.setText(model.getValueAt(r, 12).toString());
        txtAirline   .setText((String) model.getValueAt(r, 13));
    }

    private Flight buildFlight() {
        String fn  = txtFlightNum.getText().trim().toUpperCase();
        double ep  = parseDouble(txtEconPrice.getText());
        double bp  = parseDouble(txtBizPrice.getText());  if (bp == 0) bp = ep * 2.5;
        double fp  = parseDouble(txtFirstPrice.getText()); if (fp == 0) fp = ep * 4;
        int seats  = (int) parseDouble(txtTotalSeats.getText());
        String air = txtAirline.getText().trim(); if (air.isEmpty()) air = "SkyLink";
        return new Flight(fn, txtOrigin.getText().trim(), txtDest.getText().trim(),
            txtDate.getText().trim(), txtDep.getText().trim(), txtArr.getText().trim(),
            txtDur.getText().trim(), ep, bp, fp, seats, 0, air);
    }

    private boolean validateForm() {
        if (txtFlightNum.getText().trim().isEmpty() || txtOrigin.getText().trim().isEmpty()
            || txtDest.getText().trim().isEmpty() || txtDate.getText().trim().isEmpty()
            || txtTotalSeats.getText().trim().isEmpty() || txtEconPrice.getText().trim().isEmpty()) {
            SimpleTheme.showError(this, "Please fill all required (*) fields."); return false;
        }
        try { int s = Integer.parseInt(txtTotalSeats.getText().trim()); if (s <= 0) throw new Exception(); }
        catch (Exception ex) { SimpleTheme.showError(this, "Total Seats must be a positive integer."); return false; }
        try { double p = Double.parseDouble(txtEconPrice.getText().trim()); if (p <= 0) throw new Exception(); }
        catch (Exception ex) { SimpleTheme.showError(this, "Economy Price must be a positive number."); return false; }
        return true;
    }

    private void clearForm() {
        for (JTextField f : new JTextField[]{txtFlightNum, txtOrigin, txtDest, txtDate, txtDep,
            txtArr, txtDur, txtTotalSeats, txtEconPrice, txtBizPrice, txtFirstPrice, txtAirline})
            f.setText("");
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; }
    }

    private void saveAll(List<Flight> all) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("DataFiles/flights.txt"))) {
            for (Flight f : all) pw.println(f.toFileLine());
        } catch (Exception ex) { SimpleTheme.showError(this, "Save error: " + ex.getMessage()); }
    }
}
