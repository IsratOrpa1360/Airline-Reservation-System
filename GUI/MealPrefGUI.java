package GUI;

import Entities_All.MealPreference;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MealPrefGUI extends JFrame {

    public MealPrefGUI() {
        setTitle("Meal Preferences");
        setSize(720, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.add(SimpleTheme.createHeader("Meal Preferences", e -> dispose()), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel info = SimpleTheme.createLabel("Available in-flight meal options.");
        info.setBorder(new EmptyBorder(0, 0, 6, 0));
        content.add(info, BorderLayout.NORTH);

        String[] cols = {"Meal Type", "Description", "Suitable For"};
        Object[][] data = {
            {"Standard",        "Regular mixed meal with meat and vegetables",      "All passengers"},
            {"Vegetarian",      "Meat-free meal with vegetables and dairy",          "Vegetarians"},
            {"Vegan",           "Plant-based meal, no animal products",              "Vegans"},
            {"Halal",           "Prepared according to Islamic dietary laws",        "Muslim passengers"},
            {"Kosher",          "Prepared according to Jewish dietary laws",         "Jewish passengers"},
            {"Gluten-Free",     "Prepared without gluten-containing ingredients",    "Celiac / gluten intolerant"},
            {"Diabetic",        "Low-sugar, controlled-carbohydrate meal",           "Diabetic passengers"},
            {"Low Sodium",      "Reduced salt content",                             "Hypertension patients"},
            {"Child Meal",      "Kid-friendly meal with simple flavors",             "Children under 12"},
            {"Seafood",         "Seafood-based meal option",                         "Seafood lovers"},
        };

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        SimpleTheme.styleTable(table);
        table.setRowHeight(28);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(SimpleTheme.BORDER_COL));
        content.add(sp, BorderLayout.CENTER);


        main.add(content, BorderLayout.CENTER);
        add(main);
        setVisible(true);
    }
}
