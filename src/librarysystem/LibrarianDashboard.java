package librarysystem;

import javax.swing.*;
import java.awt.*;

public class LibrarianDashboard extends JFrame {

    public LibrarianDashboard() {
        setTitle("Librarian Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window
        setLocationRelativeTo(null); // Center on screen

        JLabel welcomeLabel = new JLabel("Welcome to the Librarian Dashboard", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        getContentPane().add(welcomeLabel, BorderLayout.CENTER);
    }

    public static void open() {
        SwingUtilities.invokeLater(() -> {
            new LibrarianDashboard().setVisible(true);
        });
    }
}
