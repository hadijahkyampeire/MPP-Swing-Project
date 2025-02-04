package librarysystem;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window
        setLocationRelativeTo(null); // Center on screen

        JLabel welcomeLabel = new JLabel("Welcome to the Admin Dashboard", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        getContentPane().add(welcomeLabel, BorderLayout.CENTER);
    }

    public static void open() {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }
}
