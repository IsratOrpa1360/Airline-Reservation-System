package GUI;

import Entities_All.User;
import FileManagement.UserManagement;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LoginGUI extends JFrame {

    private JTextField    txtUsername;
    private JPasswordField txtPassword;

    public LoginGUI() {
        setTitle("Airline Reservation System - Login");
        setSize(480, 440);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(SimpleTheme.HEADER_BG);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel title = new JLabel("Airline Reservation System");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title);
        main.add(header, BorderLayout.NORTH);

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 60, 15, 60));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);

        gc.gridwidth = 2; gc.gridy = 0;
        JLabel heading = new JLabel("Login to Your Account", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 14));
        heading.setForeground(SimpleTheme.HEADER_BG);
        form.add(heading, gc);

        gc.gridwidth = 1;
        gc.gridy = 1; gc.gridx = 0; gc.weightx = 0.35;
        form.add(SimpleTheme.createLabel("Username :"), gc);
        gc.gridx = 1; gc.weightx = 0.65;
        txtUsername = SimpleTheme.createField();
        txtUsername.setPreferredSize(new Dimension(0, 32));
        form.add(txtUsername, gc);

        gc.gridy = 2; gc.gridx = 0; gc.weightx = 0.35;
        form.add(SimpleTheme.createLabel("Password :"), gc);
        gc.gridx = 1; gc.weightx = 0.65;
        txtPassword = SimpleTheme.createPasswordField();
        txtPassword.setPreferredSize(new Dimension(0, 32));
        form.add(txtPassword, gc);

        main.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(0, 60, 20, 60));

        JButton btnLogin    = SimpleTheme.createButton("Login");
        JButton btnCreate   = SimpleTheme.createButton("Create Account");
        JButton btnForgot   = SimpleTheme.createButton("Forgot Password");
        JButton btnGuest    = SimpleTheme.createButton("Guest Booking");
        JButton btnExit     = SimpleTheme.createDangerButton("Exit");
        JLabel  spacer      = new JLabel();

        btnLogin .addActionListener(e -> handleLogin());
        btnCreate.addActionListener(e -> handleCreateAccount());
        btnForgot.addActionListener(e -> handleForgotPassword());
        btnGuest .addActionListener(e -> handleGuestBooking());
        btnExit  .addActionListener(e -> System.exit(0));

        txtPassword.addActionListener(e -> handleLogin());

        btnPanel.add(btnLogin);
        btnPanel.add(btnCreate);
        btnPanel.add(btnForgot);
        btnPanel.add(btnGuest);
        btnPanel.add(btnExit);
        btnPanel.add(spacer);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);
        setVisible(true);
    }

    private void handleLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();
        if (u.isEmpty() || p.isEmpty()) {
            SimpleTheme.showError(this, "Please enter username and password.");
            return;
        }
        User user = new UserManagement().loginUser(u, p);
        if (user == null) {
            SimpleTheme.showError(this, "Invalid username or password.");
            txtPassword.setText("");
            return;
        }
        dispose();
        new MainDashboard(user);
    }

    private void handleCreateAccount() {
        JDialog dlg = new JDialog(this, "Create Account", true);
        dlg.setSize(420, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.add(SimpleTheme.createHeader("Create New Account"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(18, 30, 10, 30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 0, 5, 0);

        JTextField   fUser    = SimpleTheme.createField();
        JTextField   fEmail   = SimpleTheme.createField();
        JPasswordField fPass  = SimpleTheme.createPasswordField();
        JPasswordField fConf  = SimpleTheme.createPasswordField();

        String[] labels = {"Username :", "Email :", "Password :", "Confirm Password :"};
        JComponent[] comps = {fUser, fEmail, fPass, fConf};
        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i; gc.gridx = 0; gc.weightx = 0.4;
            form.add(SimpleTheme.createLabel(labels[i]), gc);
            gc.gridx = 1; gc.weightx = 0.6;
            form.add(comps[i], gc);
        }
        dlg.add(form, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bp.setBackground(Color.WHITE);
        JButton reg = SimpleTheme.createButton("Register");
        JButton cancel = SimpleTheme.createButton("Cancel");
        reg.setPreferredSize(new Dimension(110, 30));
        cancel.setPreferredSize(new Dimension(110, 30));
        reg.addActionListener(e -> {
            String u2 = fUser.getText().trim();
            String em = fEmail.getText().trim();
            String p2 = new String(fPass.getPassword()).trim();
            String c2 = new String(fConf.getPassword()).trim();
            if (u2.isEmpty() || em.isEmpty() || p2.isEmpty()) {
                SimpleTheme.showError(dlg, "All fields are required."); return;
            }
            if (!p2.equals(c2)) { SimpleTheme.showError(dlg, "Passwords do not match."); return; }
            if (p2.length() < 6) { SimpleTheme.showError(dlg, "Password must be at least 6 characters."); return; }
            if (!em.contains("@")) { SimpleTheme.showError(dlg, "Invalid email address."); return; }
            if (!u2.matches("[a-zA-Z0-9_]+")) {
                SimpleTheme.showError(dlg, "Username: letters, numbers and underscore only."); return;
            }
            UserManagement um = new UserManagement();
            if (um.usernameExists(u2)) { SimpleTheme.showError(dlg, "Username already taken."); return; }
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            um.registerUser(new User(u2, em, p2, 0, "Bronze", today));
            SimpleTheme.showInfo(dlg, "Account created! You can now login.");
            dlg.dispose();
        });
        cancel.addActionListener(e -> dlg.dispose());
        bp.add(reg); bp.add(cancel);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void handleForgotPassword() {
        String u = JOptionPane.showInputDialog(this, "Enter your username:", "Forgot Password",
            JOptionPane.QUESTION_MESSAGE);
        if (u == null || u.trim().isEmpty()) return;
        User found = new UserManagement().findByUsername(u.trim());
        if (found == null) { SimpleTheme.showError(this, "Username not found."); return; }
        JOptionPane.showMessageDialog(this,
            "Account found for: " + found.getEmail() + "\nPlease contact admin to reset password.",
            "Account Found", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleGuestBooking() {
        String name = JOptionPane.showInputDialog(this, "Enter your name:", "Guest Booking",
            JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;
        String email = JOptionPane.showInputDialog(this, "Enter your email:", "Guest Booking",
            JOptionPane.QUESTION_MESSAGE);
        if (email == null || !email.contains("@")) { SimpleTheme.showError(this, "Invalid email."); return; }
        User guest = new User(name.trim() + "_guest", email.trim(), "", 0, "Guest",
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dispose();
        new MainDashboard(guest);
    }
}
