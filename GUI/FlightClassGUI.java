package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FlightClassGUI extends JFrame {

    public FlightClassGUI() {
        setTitle("Flight Class Comparison");
        setSize(860, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(0, 8));
        main.setBackground(Color.WHITE);
        main.add(SimpleTheme.createHeader("Flight Class Comparison", e -> dispose()), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel info = SimpleTheme.createLabel("Feature comparison across all three flight classes.");
        info.setBorder(new EmptyBorder(0, 0, 6, 0));
        content.add(info, BorderLayout.NORTH);

        // Comparison table
        String[] cols = {"Feature", "Economy Class", "Business Class", "First Class"};
        Object[][] rows = {
            {"Seat Width",        "17-18 inches",         "21-23 inches",         "25-34 inches"},
            {"Legroom",           "30-32 inches",         "38-60 inches",         "Private suite / pod"},
            {"Checked Baggage",   "20 kg",                "40 kg",                "60 kg"},
            {"Carry-on",          "7 kg",                 "14 kg",                "20 kg"},
            {"Meals",             "Standard meal",        "Premium multi-course", "Chef-curated dining"},
            {"Recline",           "3-4 inches",           "Full flat-bed",        "Private lie-flat"},
            {"Entertainment",     "Personal screen",      "Large personal screen","Ultra HD suite"},
            {"Power Outlet",      "USB only",             "AC + USB",             "AC + USB + kit"},
            {"Priority Boarding", "No",                   "Yes",                  "Dedicated concierge"},
            {"Lounge Access",     "No",                   "Yes (Business lounge)","Yes (First lounge)"},
            {"Fare Multiplier",   "1x (base price)",      "~2.5x base",           "~4x base"},
            {"Min Price (BDT)",   "5,000",                "15,000",               "25,000"},
            {"Seat Rows",         "9-28",                 "4-8",                  "1-3"},
            {"Columns per Row",   "A,B,C,D,E,F (6 seats)","A,B,C,D (4 seats)",   "A,B,C,D (4 seats)"},
            {"Points Multiplier", "1x",                   "2x loyalty points",    "3x loyalty points"},
        };

        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        SimpleTheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.setRowHeight(28);

        // Color alternating rows with class tints
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            Color[] tints = {Color.WHITE, new Color(235, 245, 255), new Color(255, 252, 235)};
            public java.awt.Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) setBackground(col == 0 ? new Color(240, 244, 255) : tints[col > 0 ? col-1 : 0]);
                setFont(col == 0 ? SimpleTheme.BOLD_FONT : SimpleTheme.TABLE_FONT);
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(SimpleTheme.BORDER_COL));
        content.add(sp, BorderLayout.CENTER);


        main.add(content, BorderLayout.CENTER);
        add(main);
        setVisible(true);
    }
}
