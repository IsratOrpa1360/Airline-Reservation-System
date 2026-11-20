package GUI;

import java.awt.*;
import javax.swing.*;

public class UITheme {
    // ── Palette ────────────────────────────────────────────────────────────────
    public static final Color NAVY        = new Color(8,   28,  72);
    public static final Color BLUE        = new Color(21,  101, 192);
    public static final Color SKY         = new Color(30,  136, 229);
    public static final Color LIGHT_BLUE  = new Color(227, 242, 253);
    public static final Color GOLD        = new Color(255, 179,   0);
    public static final Color WHITE       = Color.WHITE;
    public static final Color OFF_WHITE   = new Color(248, 250, 255);
    public static final Color SURFACE     = new Color(236, 241, 250);
    public static final Color BORDER      = new Color(207, 216, 230);
    public static final Color TEXT_DARK   = new Color(13,  27,  62);
    public static final Color TEXT_MED    = new Color(55,  71,  79);
    public static final Color TEXT_MUTED  = new Color(120, 144, 156);
    public static final Color SUCCESS     = new Color(27,  120,  35);
    public static final Color SUCCESS_BG  = new Color(232, 245, 233);
    public static final Color DANGER      = new Color(183,  28,  28);
    public static final Color DANGER_BG   = new Color(255, 235, 238);
    public static final Color WARNING     = new Color(230, 100,   0);
    public static final Color WARNING_BG  = new Color(255, 243, 224);
    public static final Color SEAT_AVAIL  = new Color(30,  136, 229);
    public static final Color SEAT_SEL    = new Color(27,  120,  35);
    public static final Color SEAT_BOOKED = new Color(183,  28,  28);
    public static final Color SEAT_BLOCK  = new Color(158, 158, 158);

    // ── Typography ─────────────────────────────────────────────────────────────
    public static final Font F_H1       = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font F_H2       = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font F_H3       = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font F_H4       = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font F_INPUT    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_BTN      = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_NAV      = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font F_TH       = new Font("Segoe UI", Font.BOLD,  11);
    public static final Font F_MONO     = new Font("Consolas",  Font.PLAIN, 12);

    // ── Shared helpers ─────────────────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        return styledButton(text, SKY, WHITE);
    }
    public static JButton successButton(String text) {
        return styledButton(text, SUCCESS, WHITE);
    }
    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER, WHITE);
    }
    public static JButton secondaryButton(String text) {
        return styledButton(text, new Color(96, 125, 139), WHITE);
    }
    public static JButton outlineButton(String text) {
        javax.swing.JButton b = new javax.swing.JButton(text);
        b.setFont(F_BTN);
        b.setForeground(SKY);
        b.setBackground(WHITE);
        b.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(SKY, 1),
            new javax.swing.border.EmptyBorder(6, 18, 6, 18)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hoverBg = LIGHT_BLUE;
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hoverBg); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(WHITE); }
        });
        return b;
    }

    private static javax.swing.JButton styledButton(String text, Color bg, Color fg) {
        javax.swing.JButton b = new javax.swing.JButton(text);
        b.setFont(F_BTN);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new javax.swing.border.EmptyBorder(8, 20, 8, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hover = bg.darker();
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    public static javax.swing.JTextField styledField() {
        javax.swing.JTextField f = new javax.swing.JTextField();
        f.setFont(F_INPUT);
        f.setForeground(TEXT_DARK);
        f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER),
            new javax.swing.border.EmptyBorder(6, 10, 6, 10)));
        f.setBackground(WHITE);
        return f;
    }

    public static javax.swing.JLabel label(String text, Font font, Color color) {
        javax.swing.JLabel l = new javax.swing.JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static javax.swing.JPanel card() {
        javax.swing.JPanel p = new javax.swing.JPanel();
        p.setBackground(WHITE);
        p.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER, 1),
            new javax.swing.border.EmptyBorder(16, 16, 16, 16)));
        return p;
    }
}
