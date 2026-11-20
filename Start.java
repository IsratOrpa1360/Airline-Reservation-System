import GUI.LoginGUI;

public class Start {
    public static void main(String[] args) {
        DataInitializer.initialize();
        javax.swing.SwingUtilities.invokeLater(LoginGUI::new);
    }
}
