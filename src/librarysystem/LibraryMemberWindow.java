package librarysystem;

import business.Address;
import business.LibraryMember;
import dataaccess.DataAccessFacade;
import rulesets.MemberRuleSet;
import rulesets.RuleException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibraryMemberWindow extends JFrame {
    private JTextField memberIdField, firstNameField, lastNameField, phoneField;
    private JTextField streetField, cityField, stateField, zipField;
    private JButton saveMemberButton;

    private DataAccessFacade dataAccess = new DataAccessFacade();

    public LibraryMemberWindow() {
        setTitle("Add New Library Member");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40)); // ✅ Padding around the form


        formPanel.add(new JLabel("Member ID:"));
        memberIdField = new JTextField();
        formPanel.add(memberIdField);

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Street:"));
        streetField = new JTextField();
        formPanel.add(streetField);

        formPanel.add(new JLabel("City:"));
        cityField = new JTextField();
        formPanel.add(cityField);

        formPanel.add(new JLabel("State:"));
        stateField = new JTextField();
        formPanel.add(stateField);

        formPanel.add(new JLabel("Zip:"));
        zipField = new JTextField();
        formPanel.add(zipField);

        formPanel.add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveMemberButton = new JButton("Save Member");
        saveMemberButton.setEnabled(false); // Initially disabled
        buttonPanel.add(saveMemberButton);
        add(buttonPanel, BorderLayout.SOUTH);

        saveMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate form using MemberRuleSet
                    MemberRuleSet memberRuleSet = new MemberRuleSet();
                    memberRuleSet.applyRules(LibraryMemberWindow.this);

                    // If validation passes, create the Member object
                    String memberId = getMemberIdValue();
                    String firstName = getFirstNameValue();
                    String lastName = getLastNameValue();
                    Address address = new Address(getStreetValue(), getCityValue(), getStateValue(), getZipValue());
                    String phone = getPhoneValue();

                    LibraryMember member = new LibraryMember(memberId, firstName, lastName, phone, address);
                    // save member to storage
                    dataAccess.saveNewMember(member);

                    JOptionPane.showMessageDialog(LibraryMemberWindow.this, "Library Member added successfully!");
                } catch (RuleException ex) {
                    JOptionPane.showMessageDialog(LibraryMemberWindow.this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add input listeners to enable Save button when all fields are filled
        addInputListeners();

        setVisible(true);
    }

    private void addInputListeners() {
        memberIdField.getDocument().addDocumentListener(new InputChangeListener(this));
        firstNameField.getDocument().addDocumentListener(new InputChangeListener(this));
        lastNameField.getDocument().addDocumentListener(new InputChangeListener(this));
        streetField.getDocument().addDocumentListener(new InputChangeListener(this));
        cityField.getDocument().addDocumentListener(new InputChangeListener(this));
        stateField.getDocument().addDocumentListener(new InputChangeListener(this));
        zipField.getDocument().addDocumentListener(new InputChangeListener(this));
        phoneField.getDocument().addDocumentListener(new InputChangeListener(this));
    }

    public void checkSaveButtonState() {
        boolean canEnable = !memberIdField.getText().trim().isEmpty() &&
                !firstNameField.getText().trim().isEmpty() &&
                !lastNameField.getText().trim().isEmpty() &&
                !streetField.getText().trim().isEmpty() &&
                !cityField.getText().trim().isEmpty() &&
                !stateField.getText().trim().isEmpty() &&
                !zipField.getText().trim().isEmpty() &&
                !phoneField.getText().trim().isEmpty();
        saveMemberButton.setEnabled(canEnable);
    }

    public String getMemberIdValue() {
        return memberIdField.getText().trim();
    }

    public String getFirstNameValue() {
        return firstNameField.getText().trim();
    }

    public String getLastNameValue() {
        return lastNameField.getText().trim();
    }

    public String getStreetValue() {
        return streetField.getText().trim();
    }

    public String getCityValue() {
        return cityField.getText().trim();
    }

    public String getStateValue() {
        return stateField.getText().trim();
    }

    public String getZipValue() {
        return zipField.getText().trim();
    }

    public String getPhoneValue() {
        return phoneField.getText().trim();
    }

    public static void main(String[] args) {
        new LibraryMemberWindow();
    }
}

