package GUI;

import Entities_All.*;
import FileManagement.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private MainFrame frame;
    private JTextField txtUsername, txtEmail;
    private JPasswordField txtCurrentPwd, txtNewPwd, txtConfirmPwd;
    private JLabel tierLabel, pointsLabel, memberSinceLabel;

    public ProfilePanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        add(pageHeader("👤  My Profile", "Manage your account information"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setBackground(UITheme.SURFACE);
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        body.add(buildProfileCard());
        body.add(buildPasswordCard());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildProfileCard() {
        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        card.add(UITheme.label("Account Information", UITheme.F_H3, UITheme.NAVY));
        card.add(Box.createVerticalStrut(16));

        // Avatar circle
        JPanel avatar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BLUE);
                g2.fillOval(0, 0, 80, 80);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                String initial = AppSession.getInstance().getDisplayName().substring(0, 1).toUpperCase();
                g2.drawString(initial, (80 - fm.stringWidth(initial)) / 2, 55);
            }
        };
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setMaximumSize(new Dimension(80, 80));
        avatar.setOpaque(false);
        avatar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(avatar);
        card.add(Box.createVerticalStrut(12));

        tierLabel     = UITheme.label("", UITheme.F_H4, UITheme.GOLD);
        pointsLabel   = UITheme.label("", UITheme.F_BODY, UITheme.TEXT_MED);
        memberSinceLabel = UITheme.label("", UITheme.F_SMALL, UITheme.TEXT_MUTED);
        tierLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pointsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        memberSinceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tierLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(pointsLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(memberSinceLabel);
        card.add(Box.createVerticalStrut(18));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));

        // Form fields
        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 8));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(UITheme.label("Username", UITheme.F_H4, UITheme.TEXT_MED));
        txtUsername = UITheme.styledField();
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(245, 247, 252));
        grid.add(txtUsername);

        grid.add(UITheme.label("Email Address", UITheme.F_H4, UITheme.TEXT_MED));
        txtEmail = UITheme.styledField();
        grid.add(txtEmail);

        card.add(grid);
        card.add(Box.createVerticalStrut(16));

        JButton saveBtn = UITheme.primaryButton("Save Profile Changes");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> handleProfileSave());
        card.add(saveBtn);

        return card;
    }

    private JPanel buildPasswordCard() {
        JPanel card = UITheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        card.add(UITheme.label("Change Password", UITheme.F_H3, UITheme.NAVY));
        card.add(Box.createVerticalStrut(16));

        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 8));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(UITheme.label("Current Password", UITheme.F_H4, UITheme.TEXT_MED));
        txtCurrentPwd = styledPassword();
        grid.add(txtCurrentPwd);

        grid.add(UITheme.label("New Password", UITheme.F_H4, UITheme.TEXT_MED));
        txtNewPwd = styledPassword();
        grid.add(txtNewPwd);

        grid.add(UITheme.label("Confirm New Password", UITheme.F_H4, UITheme.TEXT_MED));
        txtConfirmPwd = styledPassword();
        grid.add(txtConfirmPwd);

        card.add(grid);
        card.add(Box.createVerticalStrut(16));

        JButton pwdBtn = UITheme.outlineButton("Update Password");
        pwdBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pwdBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pwdBtn.addActionListener(e -> handlePasswordChange());
        card.add(pwdBtn);

        card.add(Box.createVerticalStrut(24));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));

        card.add(UITheme.label("Account Security", UITheme.F_H4, UITheme.NAVY));
        card.add(Box.createVerticalStrut(8));
        String[] tips = {
            "✔  Use a strong password with 8+ characters",
            "✔  Mix letters, numbers, and symbols",
            "✔  Do not share your credentials",
            "✔  Log out when using shared devices"
        };
        for (String t : tips) {
            JLabel l = UITheme.label(t, UITheme.F_SMALL, UITheme.TEXT_MED);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            l.setBorder(new EmptyBorder(2, 0, 2, 0));
            card.add(l);
        }

        return card;
    }

    public void refresh() {
        AppSession ses = AppSession.getInstance();
        if (ses.isGuest() || ses.getCurrentUser() == null) {
            tierLabel.setText("Guest Account");
            pointsLabel.setText("Register to earn loyalty points.");
            memberSinceLabel.setText("");
            txtUsername.setText(ses.getGuestName());
            txtEmail.setText(ses.getGuestEmail());
            return;
        }
        User u = ses.getCurrentUser();
        txtUsername.setText(u.getUsername());
        txtEmail.setText(u.getEmail());
        tierLabel.setText("⭐  " + u.getMembershipTier() + " Member");
        pointsLabel.setText(u.getLoyaltyPoints() + " loyalty points");
        memberSinceLabel.setText("Member since: " + u.getRegistrationDate());
    }

    private void handleProfileSave() {
        AppSession ses = AppSession.getInstance();
        if (ses.isGuest() || ses.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "Guests cannot update profiles. Please register.", "Guest Account", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String newEmail = txtEmail.getText().trim();
        if (!newEmail.contains("@")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email.", "Error", JOptionPane.ERROR_MESSAGE); return;
        }
        User u = ses.getCurrentUser();
        u.updateProfile(newEmail);
        new UserManagement().updateUser(u);
        JOptionPane.showMessageDialog(this, "Profile updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        frame.refreshUserLabel();
    }

    private void handlePasswordChange() {
        AppSession ses = AppSession.getInstance();
        if (ses.isGuest() || ses.getCurrentUser() == null) return;
        String cur  = new String(txtCurrentPwd.getPassword());
        String newP = new String(txtNewPwd.getPassword());
        String conf = new String(txtConfirmPwd.getPassword());
        if (!ses.getCurrentUser().validateLogin(cur)) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE); return;
        }
        if (newP.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE); return;
        }
        if (!newP.equals(conf)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE); return;
        }
        User u = ses.getCurrentUser();
        u.updatePassword(cur, newP);
        new UserManagement().updateUser(u);
        JOptionPane.showMessageDialog(this, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        txtCurrentPwd.setText(""); txtNewPwd.setText(""); txtConfirmPwd.setText("");
    }

    private JPasswordField styledPassword() {
        JPasswordField f = new JPasswordField();
        f.setFont(UITheme.F_INPUT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JPanel pageHeader(String title, String sub) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.NAVY);
        h.setBorder(new EmptyBorder(14, 24, 14, 24));
        JPanel txt = new JPanel(new GridLayout(2, 1, 0, 4));
        txt.setOpaque(false);
        txt.add(UITheme.label(title, UITheme.F_H2, Color.WHITE));
        txt.add(UITheme.label(sub, UITheme.F_BODY, new Color(180, 210, 255)));
        h.add(txt, BorderLayout.WEST);
        JButton back = new JButton("← Back");
        back.setFont(UITheme.F_BTN);
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(30, 60, 130));
        back.setBorderPainted(false);
        back.setFocusPainted(false);
        back.setOpaque(true);
        back.setPreferredSize(new Dimension(120, 36));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> frame.gotoDashboard());
        h.add(back, BorderLayout.EAST);
        return h;
    }
}
