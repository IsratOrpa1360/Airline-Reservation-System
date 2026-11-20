package FileManagement;

import Entities_All.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserManagement {
    private static final String FILE = "DataFiles/users.txt";

    public UserManagement() { ensureFile(); }

    private void ensureFile() {
        try {
            new File("DataFiles").mkdirs();
            File f = new File(FILE);
            if (!f.exists()) f.createNewFile();
        } catch (Exception ignored) {}
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists() || f.length() == 0) return list;
        try (Scanner sc = new java.util.Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(";", -1);
                if (d.length < 6) continue;
                try {
                    User u = new User(d[0], d[1], d[2], Integer.parseInt(d[3]), d[4], d[5]);
                    list.add(u);
                } catch (Exception ignored) {}
            }
        } catch (Exception e) { System.err.println("UserManagement read error: " + e.getMessage()); }
        return list;
    }

    public User findByUsername(String username) {
        for (User u : getAllUsers())
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        return null;
    }

    public User findByEmail(String email) {
        for (User u : getAllUsers())
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        return null;
    }

    public boolean usernameExists(String username) { return findByUsername(username) != null; }

    public boolean registerUser(User u) {
        if (usernameExists(u.getUsername())) return false;
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.write(u.toFileLine() + "\n");
            return true;
        } catch (Exception e) { return false; }
    }

    public User loginUser(String username, String password) {
        User u = findByUsername(username);
        if (u != null && u.validateLogin(password)) return u;
        return null;
    }

    public void updateUser(User updated) {
        List<User> all = getAllUsers();
        try (FileWriter fw = new FileWriter(FILE, false)) {
            for (User u : all) {
                if (u.getUsername().equalsIgnoreCase(updated.getUsername()))
                    fw.write(updated.toFileLine() + "\n");
                else
                    fw.write(u.toFileLine() + "\n");
            }
        } catch (Exception e) { System.err.println("UserManagement update error: " + e.getMessage()); }
    }
}
