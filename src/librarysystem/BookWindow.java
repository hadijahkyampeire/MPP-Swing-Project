package librarysystem;

import business.Author;
import business.Book;
import dataaccess.DataAccessFacade;
import rulesets.BookRuleSet;
import rulesets.RuleException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BookWindow extends JFrame {
    private JTextField isbnField, titleField;
    private JComboBox<String> borrowPeriodComboBox;
    private JButton addAuthorButton, saveBookButton;
    private DefaultListModel<String> authorListModel;
    private JList<String> authorList;
    private List<Author> authors = new ArrayList<>();

    // Persistance Layer
    private DataAccessFacade dataAccess = new DataAccessFacade();

    public BookWindow() {
        setTitle("Add New Book");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        addFormField("ISBN:", isbnField = new JTextField(20), gbc);
        addFormField("Title:", titleField = new JTextField(20), gbc);
        addFormField("Borrow Period:", borrowPeriodComboBox = new JComboBox<>(new String[]{"7 days", "21 days"}), gbc);

        add(new JLabel("Authors:"), gbc);
        gbc.gridy++;
        authorListModel = new DefaultListModel<>();
        authorList = new JList<>(authorListModel);
        JScrollPane authorScrollPane = new JScrollPane(authorList);
        add(authorScrollPane, gbc);

        // ✅ Add Author Button
        addAuthorButton = new JButton("➕ Add Author");
        gbc.gridy++;
        add(addAuthorButton, gbc);
        addAuthorButton.addActionListener(e -> new AuthorWindow(BookWindow.this));

        saveBookButton = new JButton("Save Book");
        saveBookButton.setEnabled(false);
        gbc.gridy++;
        add(saveBookButton);

        addAuthorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthorWindow authorWindow = new AuthorWindow(BookWindow.this);
                authorWindow.setVisible(true);
            }
        });

        saveBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate the form using the Ruleset
                    BookRuleSet bookRuleSet = new BookRuleSet();
                    bookRuleSet.applyRules(BookWindow.this);

                    // If validation passes, create the Book object
                    String isbn = getIsbnValue();
                    String title = getTitleValue();
                    int borrowPeriod = getBorrowPeriodValue().equals("7 days") ? 7 : 21;
                    Book book = new Book(isbn, title, borrowPeriod, getAuthors());
                    // save  book to storage
                    dataAccess.saveNewBook(book);

                    JOptionPane.showMessageDialog(BookWindow.this, "Book added successfully!");
                } catch (RuleException ex) {
                    JOptionPane.showMessageDialog(BookWindow.this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    public String getIsbnValue() {
        return isbnField.getText().trim();
    }

    public String getTitleValue() {
        return titleField.getText().trim();
    }

    public String getBorrowPeriodValue() {
        return (String) borrowPeriodComboBox.getSelectedItem();
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        authorListModel.addElement(author.getFirstName() + " " + author.getLastName());
        checkSaveButtonState();
    }

    private void checkSaveButtonState() {
        boolean canEnable = !isbnField.getText().trim().isEmpty() &&
                !titleField.getText().trim().isEmpty() &&
                borrowPeriodComboBox.getSelectedIndex() != -1 &&
                !authors.isEmpty();
        saveBookButton.setEnabled(canEnable);
    }

    private void addFormField(String label, JComponent field, GridBagConstraints gbc) {
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(field, gbc);
    }
    public static void main(String[] args) {
        new BookWindow();
    }
}
