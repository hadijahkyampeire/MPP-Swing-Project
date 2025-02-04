package librarysystem;

import business.Address;
import business.Author;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthorWindow extends JDialog {
    private JTextField firstNameField, lastNameField, phoneField, credentialsField;
    private JTextArea bioArea;
    private JTextField streetField, cityField, stateField, zipField;
    private JButton saveAuthorButton;
    private BookWindow parentWindow;
    private JPanel overlay;

    public AuthorWindow(BookWindow parent) {
        this.parentWindow = parent;
        setTitle("Add Author");
        setSize(600, 500);
        setLayout(new GridBagLayout());
        setModal(true); // ✅ Make it a blocking modal

        // ✅ Create a Blur Effect Overlay
        overlay = new JPanel();
        overlay.setSize(parent.getSize());
        overlay.setBackground(new Color(0, 0, 0, 100)); // ✅ Semi-transparent black overlay
        overlay.setOpaque(true);
        parent.getLayeredPane().add(overlay, JLayeredPane.POPUP_LAYER); // Add blur effect

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // ✅ Add padding

        // ✅ Form Layout (Vertically Aligned)
        addFormField("First Name:", firstNameField = new JTextField(20), gbc);
        addFormField("Last Name:", lastNameField = new JTextField(20), gbc);
        addFormField("Phone:", phoneField = new JTextField(20), gbc);
        addFormField("Credentials:", credentialsField = new JTextField(20), gbc);
        addFormField("Bio:", bioArea = new JTextArea(3, 20), gbc);
        addFormField("Street:", streetField = new JTextField(20), gbc);
        addFormField("City:", cityField = new JTextField(20), gbc);
        addFormField("State:", stateField = new JTextField(20), gbc);
        addFormField("Zip:", zipField = new JTextField(20), gbc);

        saveAuthorButton = new JButton("Save Author");
        saveAuthorButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveAuthorButton.setBackground(new Color(50, 50, 50));
        saveAuthorButton.setForeground(Color.WHITE);
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveAuthorButton);

        saveAuthorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String firstName = firstNameField.getText().trim();
                    String lastName = lastNameField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String credentials = credentialsField.getText().trim();
                    String bio = bioArea.getText().trim();
                    Address address = new Address(streetField.getText().trim(), cityField.getText().trim(), stateField.getText().trim(), zipField.getText().trim());

                    // Create Author object
                    Author author = new Author(firstName, lastName, phone, address, bio, credentials);

                    // Add the author to the parent book window
                    parentWindow.addAuthor(author);
                    parent.getLayeredPane().remove(overlay); // ✅ Remove blur effect
                    parent.repaint();

                    // Close the modal
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AuthorWindow.this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setModal(true);
    }
    private void addFormField(String label, JComponent field, GridBagConstraints gbc) {
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(field, gbc);
    }
}
