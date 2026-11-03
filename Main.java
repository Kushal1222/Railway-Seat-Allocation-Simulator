import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Cross-platform look so custom colours show the same on Mac and Windows
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            Class.forName("org.sqlite.JDBC");
            DBManager db = new DBManager();
            BookingService service = new BookingService(db);
            SwingUtilities.invokeLater(() -> new MainFrame(service).setVisible(true));
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "SQLite JDBC jar not found.\nPut sqlite-jdbc.jar in this folder and run:\n"
                + "java -cp \".:sqlite-jdbc.jar\" Main   (Mac/Linux)\n"
                + "java -cp \".;sqlite-jdbc.jar\" Main   (Windows)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not start: " + e.getMessage());
        }
    }
}
