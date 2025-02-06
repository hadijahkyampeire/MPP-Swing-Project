package librarysystem.admin;

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
        super(parent, "Add Author", true);
        this.parentWindow = parent;

        setSize(700, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // ✅ Consistent Padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1;

        addFormField("First Name:", firstNameField = createTextField(), gbc, formPanel);
        addFormField("Last Name:", lastNameField = createTextField(), gbc, formPanel);
        addFormField("Phone:", phoneField = createTextField(), gbc, formPanel);
        addFormField("Credentials:", credentialsField = createTextField(), gbc, formPanel);
        addFormField("Bio:", bioArea = createTextArea(), gbc, formPanel);
        addFormField("Street:", streetField = createTextField(), gbc, formPanel);
        addFormField("City:", cityField = createTextField(), gbc, formPanel);
        addFormField("State:", stateField = createTextField(), gbc, formPanel);
        addFormField("Zip:", zipField = createTextField(), gbc, formPanel);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveAuthorButton = new JButton("Save Author");
        saveAuthorButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveAuthorButton.setBackground(new Color(50, 50, 50));
        saveAuthorButton.setForeground(Color.BLACK);
        saveAuthorButton.setPreferredSize(new Dimension(180, 40)); // ✅ Larger Button
        buttonPanel.add(saveAuthorButton);
        add(buttonPanel, BorderLayout.SOUTH); // ✅ Place at Bottom

        saveAuthorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
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

                    Author author = new Author(firstName, lastName, phone, address, bio, credentials);

                    parentWindow.addAuthor(author);

                    if (parentWindow.overlay != null) {
                        parentWindow.getLayeredPane().remove(parentWindow.overlay);
                        parentWindow.overlay = null;
                        parentWindow.repaint();
                    }

                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AuthorWindow.this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(250, 30));
        textField.setMinimumSize(new Dimension(250, 30));
        textField.setMaximumSize(new Dimension(250, 30));
        return textField;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(3, 20);
        textArea.setPreferredSize(new Dimension(250, 60));
        textArea.setMinimumSize(new Dimension(250, 60));
        textArea.setMaximumSize(new Dimension(250, 60));
        return textArea;
    }


    private void addFormField(String label, JComponent field, GridBagConstraints gbc, JPanel panel) {
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}
