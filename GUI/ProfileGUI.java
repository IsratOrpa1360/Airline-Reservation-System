package GUI;

import Entities_All.User;
import FileManagement.UserManagement;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ProfileGUI extends JFrame {

    private final User currentUser;
    private final MainDashboard parent;

    private JTextField    txtEmail;
    private JTextField    txtUsername;
    private JPasswordField txtOldPass, txtNewPass, txtConfPass;

    public ProfileGUI(User user, MainDashboard parent) {
        this.currentUser = user;
        this.parent      = parent;
        setTitle("Update Profile");
        setSize(560, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.add(SimpleTheme.createHeader("Update Profile", e -> dispose()), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(14, 40, 14, 40));

        content.add(buildInfoCard(), BorderLayout.NORTH);
        content.add(buildPasswordCard(), BorderLayout.CENTER);

        main.add(content, BorderLayout.CENTER);
        add(main);
        setVisible(true);
    }

    private JPanel buildInfoCard() {
        JPanel card = SimpleTheme.sectionPanel("Account Information");

        JPanel grid = new JPanel(new GridLayout(4, 2, 8, 8));
        grid.setBackground(Color.WHITE);

        txtUsername = SimpleTheme.createField();
        txtUsername.setText(currentUser.getUsername());
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(245, 245, 245));

        txtEmail = SimpleTheme.createField();
        txtEmail.setText(currentUser.getEmail());

        String[][] info = {
            {"Username :", null},
            {"Email Address :", null},
            {"Membership Tier :", currentUser.getMembershipTier()},
            {"Loyalty Points :", String.format("%,d", currentUser.getLoyaltyPoints())},
        };

        for (int i = 0; i < info.length; i++) {
            grid.add(SimpleTheme.createLabel(info[i][0]));
            if (i == 0) grid.add(txtUsername);
            else if (i == 1) grid.add(txtEmail);
            else {
                JLabel v = new JLabel(info[i][1]);
                v.setFont(SimpleTheme.BOLD_FONT);
                v.setForeground(SimpleTheme.HEADER_BG);
                grid.add(v);
            }
        }
        card.add(grid, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnRow.setBackground(Color.WHITE);
        JButton btnSave = SimpleTheme.createButton("Save Email");
        btnSave.setPreferredSize(new Dimension(110, 28));
        btnSave.addActionListener(e -> saveEmail());
        btnRow.add(btnSave);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildPasswordCard() {
        JPanel card = SimpleTheme.sectionPanel("Change Password");

        JPanel grid = new JPanel(new GridLayout(3, 2, 8, 10));
        grid.setBackground(Color.WHITE);

        txtOldPass  = SimpleTheme.createPasswordField();
        txtNewPass  = SimpleTheme.createPasswordField();
        txtConfPass = SimpleTheme.createPasswordField();

        grid.add(SimpleTheme.createLabel("Current Password :"));
        grid.add(txtOldPass);
        grid.add(SimpleTheme.createLabel("New Password :"));
        grid.add(txtNewPass);
        grid.add(SimpleTheme.createLabel("Confirm New Password :"));
        grid.add(txtConfPass);
        card.add(grid, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnRow.setBackground(Color.WHITE);
        JButton btnChange = SimpleTheme.createButton("Change Password");
        btnChange.setPreferredSize(new Dimension(150, 28));
        btnChange.addActionListener(e -> changePassword());
        btnRow.add(btnChange);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private void saveEmail() {
        String email = txtEmail.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            SimpleTheme.showError(this, "Enter a valid email address."); return;
        }
        currentUser.setEmail(email);
        new UserManagement().updateUser(currentUser);
        SimpleTheme.showInfo(this, "Email updated successfully.");
    }

    private void changePassword() {
        String old  = new String(txtOldPass.getPassword()).trim();
        String nw   = new String(txtNewPass.getPassword()).trim();
        String conf = new String(txtConfPass.getPassword()).trim();
        if (old.isEmpty() || nw.isEmpty()) {
            SimpleTheme.showError(this, "All password fields are required."); return;
        }
        if (!currentUser.validateLogin(old)) {
            SimpleTheme.showError(this, "Current password is incorrect."); return;
        }
        if (!nw.equals(conf)) {
            SimpleTheme.showError(this, "New passwords do not match."); return;
        }
        if (nw.length() < 6) {
            SimpleTheme.showError(this, "New password must be at least 6 characters."); return;
        }
        currentUser.setPassword(nw);
        new UserManagement().updateUser(currentUser);
        SimpleTheme.showInfo(this, "Password changed successfully.");
        txtOldPass.setText(""); txtNewPass.setText(""); txtConfPass.setText("");
    }
}
