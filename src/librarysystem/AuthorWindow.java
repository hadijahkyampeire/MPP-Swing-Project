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

    public AuthorWindow(BookWindow parent) {
        super(parent, "Add Author", true); // ✅ Modal Dialog
        this.parentWindow = parent;

        setSize(700, 600);
        setLayout(new BorderLayout()); // ✅ Proper layout
        setLocationRelativeTo(parent);

        // ✅ Form Panel (Holds Input Fields)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // ✅ Consistent Padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1;

        // ✅ Form Fields (Larger Input Fields)
        addFormField("First Name:", firstNameField = createTextField(), gbc, formPanel);
        addFormField("Last Name:", lastNameField = createTextField(), gbc, formPanel);
        addFormField("Phone:", phoneField = createTextField(), gbc, formPanel);
        addFormField("Credentials:", credentialsField = createTextField(), gbc, formPanel);
        addFormField("Bio:", bioArea = createTextArea(), gbc, formPanel);
        addFormField("Street:", streetField = createTextField(), gbc, formPanel);
        addFormField("City:", cityField = createTextField(), gbc, formPanel);
        addFormField("State:", stateField = createTextField(), gbc, formPanel);
        addFormField("Zip:", zipField = createTextField(), gbc, formPanel);

        // ✅ Add Form Panel to Center
        add(formPanel, BorderLayout.CENTER);

        // ✅ **Save Author Button (Bottom-Centered)**
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveAuthorButton = new JButton("Save Author");
        saveAuthorButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveAuthorButton.setBackground(new Color(50, 50, 50));
        saveAuthorButton.setForeground(Color.BLACK);
        saveAuthorButton.setPreferredSize(new Dimension(180, 40)); // ✅ Larger Button
        buttonPanel.add(saveAuthorButton);
        add(buttonPanel, BorderLayout.SOUTH); // ✅ Place at Bottom

        // ✅ **Save Author Button Action**
        saveAuthorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // ✅ Collect author details
                    String firstName = firstNameField.getText().trim();
                    String lastName = lastNameField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String credentials = credentialsField.getText().trim();
                    String bio = bioArea.getText().trim();
                    Address address = new Address(
                            streetField.getText().trim(),
                            cityField.getText().trim(),
                            stateField.getText().trim(),
                            zipField.getText().trim()
                    );

                    // ✅ Create Author object
                    Author author = new Author(firstName, lastName, phone, address, bio, credentials);

                    // ✅ Add author to the parent BookWindow
                    parentWindow.addAuthor(author);

                    // ✅ Remove blur effect from BookWindow
                    if (parentWindow.overlay != null) {
                        parentWindow.getLayeredPane().remove(parentWindow.overlay);
                        parentWindow.overlay = null;
                        parentWindow.repaint();
                    }

                    // ✅ Close this window and return to BookWindow
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AuthorWindow.this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true); // ✅ Ensure modal is visible
    }

    // ✅ Utility method to create text fields with a consistent size
    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(250, 30)); // ✅ Wider Fields
        textField.setMinimumSize(new Dimension(250, 30));
        textField.setMaximumSize(new Dimension(250, 30));
        return textField;
    }

    // ✅ Utility method to create a larger text area for Bio
    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(3, 20);
        textArea.setPreferredSize(new Dimension(250, 60)); // ✅ Taller Text Area
        textArea.setMinimumSize(new Dimension(250, 60));
        textArea.setMaximumSize(new Dimension(250, 60));
        return textArea;
    }

    // ✅ Utility method to add form fields
    private void addFormField(String label, JComponent field, GridBagConstraints gbc, JPanel panel) {
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}
