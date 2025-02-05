package librarysystem;

import business.Author;
import business.Book;
import dataaccess.DataAccessFacade;
import librarysystem.tables.BooksTablePanel;
import rulesets.BookRuleSet;
import rulesets.RuleException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class BookWindow extends JFrame {
    private JTextField isbnField, titleField;
    private JComboBox<String> borrowPeriodComboBox;
    private JButton addAuthorButton, saveBookButton;
    private DefaultListModel<String> authorListModel;
    private JList<String> authorList;
    private List<Author> authors = new ArrayList<>();
    public JPanel overlay;
    private static BooksTablePanel booksTablePanel;
    private Book book;
    private boolean isEditMode;

    // Persistance Layer
    private DataAccessFacade dataAccess = new DataAccessFacade();

    public BookWindow(BooksTablePanel booksTablePanel, Book book, boolean isEditMode) {
        this.booksTablePanel = booksTablePanel;
        this.book = book;
        this.isEditMode = isEditMode;
        setTitle(isEditMode ? "Editing" + book.getTitle() + "(" + book.getIsbn() + ")" : "Add New Book");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40)); // ✅ Padding around the form
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // ✅ ISBN Field
        formPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        isbnField = new JTextField(20);
        formPanel.add(isbnField, gbc);
        isbnField.addKeyListener(new InputListener());

        // ✅ Title Field
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);
        titleField.addKeyListener(new InputListener());

        // ✅ Borrow Period (Fix the Unwanted "A")
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Borrow Period:"), gbc);
        gbc.gridx = 1;
        borrowPeriodComboBox = new JComboBox<>(new String[]{"7 days", "21 days"});
        borrowPeriodComboBox.setPrototypeDisplayValue("21 days"); // ✅ Ensures consistent dropdown width
        formPanel.add(borrowPeriodComboBox, gbc);
        borrowPeriodComboBox.addActionListener(e -> checkSaveButtonState());

        // ✅ Authors List
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Authors:"), gbc);
        gbc.gridx = 1;
        authorListModel = new DefaultListModel<>();
        authorList = new JList<>(authorListModel);
        JScrollPane authorScrollPane = new JScrollPane(authorList);
        authorScrollPane.setPreferredSize(new Dimension(200, 80));
        formPanel.add(authorScrollPane, gbc);

        // ✅ "+Author" Button (Only One!)
        gbc.gridy++;
        addAuthorButton = new JButton("+Author");
        formPanel.add(addAuthorButton, gbc);

        addAuthorButton.addActionListener(e -> {
            // ✅ Apply Blur Effect
            overlay = new JPanel();
            overlay.setBackground(new Color(0, 0, 0, 50)); // ✅ Semi-transparent gray
            overlay.setBounds(0, 0, getWidth(), getHeight());
            getLayeredPane().add(overlay, JLayeredPane.MODAL_LAYER);

            // ✅ Open Author Modal
            new AuthorWindow(BookWindow.this);
        });

        add(formPanel, BorderLayout.CENTER); // ✅ Add Form Panel to Center

        // ✅ **Save Button (Centered)**
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveBookButton = new JButton(isEditMode ? "Update" : "Save Book");
        saveBookButton.setPreferredSize(new Dimension(150, 40)); // ✅ Bigger Button
        saveBookButton.setEnabled(false); // Initially disabled
        buttonPanel.add(saveBookButton);
        add(buttonPanel, BorderLayout.SOUTH); // ✅ Add Button Panel to Bottom

        //Prepopulate
        if(isEditMode && book != null) {
            isbnField.setText(book.getIsbn());
            titleField.setText(book.getTitle());
            borrowPeriodComboBox.setSelectedItem(book.getMaxCheckoutLength() == 7 ? "7 days" : "21 days");
            authorListModel.clear();
            for (Author author : book.getAuthors()) {
                authorListModel.addElement(author.getFirstName() + " " + author.getLastName());
            }

        }
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

                    // ✅ Refresh Table Data
                    booksTablePanel.loadBooksData();

                    if (isEditMode) {
                        // Update existing book
                        dataAccess.updateBook(book);
                        JOptionPane.showMessageDialog(BookWindow.this, "Book updated successfully!");
                    } else {
                        // Save new book
                        dataAccess.saveNewBook(book);
                        JOptionPane.showMessageDialog(BookWindow.this, "Book added successfully!");
                    }

                    dispose();
                } catch (RuleException ex) {
                    JOptionPane.showMessageDialog(BookWindow.this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private class InputListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            checkSaveButtonState();
        }
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
        saveBookButton.setBackground(canEnable ? new Color(0, 31, 63) : Color.LIGHT_GRAY);
    }

}
