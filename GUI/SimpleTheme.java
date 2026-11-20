package GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleTheme {

    public static final Color HEADER_BG   = new Color(21, 101, 192);
    public static final Color BUTTON_BG   = new Color(25, 118, 210);
    public static final Color BUTTON_HOVER = new Color(13, 71, 161);
    public static final Color DANGER_BG   = new Color(198, 40, 40);
    public static final Color SUCCESS_BG  = new Color(46, 125, 50);
    public static final Color TABLE_HDR   = new Color(227, 242, 253);
    public static final Color BORDER_COL  = new Color(189, 189, 189);
    public static final Color BG_WHITE    = Color.WHITE;
    public static final Color TEXT_DARK   = new Color(33, 33, 33);
    public static final Color TEXT_MED    = new Color(97, 97, 97);

    public static final Font TITLE_FONT  = new Font("Arial", Font.BOLD, 16);
    public static final Font LABEL_FONT  = new Font("Arial", Font.PLAIN, 13);
    public static final Font BOLD_FONT   = new Font("Arial", Font.BOLD, 13);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);
    public static final Font INPUT_FONT  = new Font("Arial", Font.PLAIN, 13);
    public static final Font TABLE_FONT  = new Font("Arial", Font.PLAIN, 12);
    public static final Font TABLE_H_FNT = new Font("Arial", Font.BOLD, 12);
    public static final Font SMALL_FONT  = new Font("Arial", Font.PLAIN, 11);

    public static JPanel createHeader(String title, ActionListener closeAction) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(HEADER_BG);
        h.setPreferredSize(new Dimension(0, 50));
        h.setBorder(new EmptyBorder(0, 16, 0, 16));
        JLabel lbl = new JLabel(title);
        lbl.setFont(TITLE_FONT);
        lbl.setForeground(Color.WHITE);
        h.add(lbl, BorderLayout.WEST);
        if (closeAction != null) {
            JButton close = makeCloseBtn("Close");
            close.addActionListener(closeAction);
            h.add(close, BorderLayout.EAST);
        }
        return h;
    }

    public static JPanel createHeader(String title) {
        return createHeader(title, null);
    }

    private static JButton makeCloseBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(BUTTON_FONT);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(13, 71, 161));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(80, 30));
        return b;
    }

    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(BUTTON_BG);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(130, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(BUTTON_HOVER); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BUTTON_BG); }
        });
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = createButton(text);
        btn.setBackground(DANGER_BG);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(183, 28, 28)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(DANGER_BG); }
        });
        return btn;
    }

    public static JTextField createField() {
        JTextField f = new JTextField();
        f.setFont(INPUT_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        return f;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(INPUT_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        return f;
    }

    public static JComboBox<String> createCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(INPUT_FONT);
        c.setBackground(Color.WHITE);
        return c;
    }

    public static JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(TEXT_DARK);
        return l;
    }

    public static JLabel createBoldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BOLD_FONT);
        l.setForeground(HEADER_BG);
        return l;
    }

    public static void styleTable(JTable t) {
        t.setFont(TABLE_FONT);
        t.setRowHeight(24);
        t.getTableHeader().setFont(TABLE_H_FNT);
        t.getTableHeader().setBackground(TABLE_HDR);
        t.getTableHeader().setForeground(TEXT_DARK);
        t.setSelectionBackground(new Color(187, 222, 251));
        t.setGridColor(new Color(220, 220, 220));
        t.setShowGrid(true);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    public static void showError(Component p, String msg) {
        JOptionPane.showMessageDialog(p, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component p, String msg) {
        JOptionPane.showMessageDialog(p, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component p, String msg) {
        return JOptionPane.showConfirmDialog(p, msg, "Confirm",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static JPanel sectionPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(BG_WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL),
            new EmptyBorder(8, 10, 8, 10)));
        JLabel h = new JLabel(title);
        h.setFont(BOLD_FONT);
        h.setForeground(HEADER_BG);
        h.setBorder(new EmptyBorder(0, 0, 6, 0));
        p.add(h, BorderLayout.NORTH);
        return p;
    }
}
