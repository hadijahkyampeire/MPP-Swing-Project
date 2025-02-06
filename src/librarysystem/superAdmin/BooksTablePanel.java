package librarysystem.SuperAdmin;

import business.Book;
import business.LibraryMember;
import dataaccess.DataAccessFacade;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class BooksTablePanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private DataAccessFacade dataAccess;
    private TableRowSorter<TableModel> sorter;
    private BooksTablePanel booksTablePanel;

    public BooksTablePanel() {
        booksTablePanel = this;
        setLayout(new BorderLayout());
        dataAccess = new DataAccessFacade();

        // Table Columns
        String[] bookColumns = {"ISBN", "Title", "Authors", "Total Copies", "Availability Status", "Max Checkout(days)", "Add New Copy", "Actions", "Details"};
        bookTableModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return (column == 6 || column == 7);// The Actions and add copy column is editable (for button)
            }
        };

        bookTable = new JTable(bookTableModel);
        bookTable.setRowHeight(35);
        bookTable.setAutoCreateRowSorter(true);
        for (int i = 0; i < bookTable.getColumnModel().getColumnCount(); i++) {
            bookTable.getColumnModel().getColumn(i).setHeaderRenderer(new CustomHeaderRenderer());
        }

        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                bookTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                bookTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                int row = bookTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    bookTable.setSelectionBackground(new Color(230, 230, 230)); // Light Gray Hover
                }
            }
        });

        // Disable Sorting on Actions and Availability Column
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(bookTableModel);
        sorter.setSortable(4, false);
        sorter.setSortable(6, false);
        sorter.setSortable(7, false);
        sorter.setSortable(8, false);
        bookTable.setRowSorter(sorter);

        // Set Renderers

        TableColumn availabilityColumn = bookTable.getColumnModel().getColumn(4);
        availabilityColumn.setCellRenderer(new AvailabilityRenderer());

        TableColumn authorsColumn = bookTable.getColumnModel().getColumn(2);
        authorsColumn.setCellRenderer(new AuthorsRenderer());

        // Customize "Actions" Column with Three Dots Button
        TableColumn actionsColumn = bookTable.getColumnModel().getColumn(7);
        actionsColumn.setCellRenderer(new ActionButtonRenderer());
        actionsColumn.setCellEditor(new ActionButtonEditor(new JCheckBox()));

        bookTable.getColumn("Add New Copy").setCellRenderer(new AddCopyButtonRenderer());
        bookTable.getColumn("Add New Copy").setCellEditor(new AddCopyButtonEditor(new JCheckBox()));

        // Add Mouse Listener for Click to Expand
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                bookTable.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Pointer Cursor
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                bookTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                int row = bookTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    bookTable.setSelectionBackground(new Color(230, 230, 230)); // Light Gray Hover
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bookTable.rowAtPoint(e.getPoint());
                int col = bookTable.columnAtPoint(e.getPoint());

                // Check if the user clicked the last column (column 7)
                if (col == 8 && row >= 0) {
                    // Trigger expand
                    toggleRowExpansion(row);
                    bookTable.setSelectionBackground(new Color(230, 230, 230));
                }
            }
        });

        loadBooksData();

        // Add Table to ScrollPane
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);
    }


    private void toggleRowExpansion(int row) {
        // Get Book Data
        String isbn = (String) bookTable.getValueAt(row, 0);
        Book book = dataAccess.readBooksMap().get(isbn);

        if (book != null) {
            // Create Panel for Book Details
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            // General info
            detailsPanel.add(new JLabel("<html><b>ISBN:</b> " + book.getIsbn() + "</html>"));
            detailsPanel.add(Box.createVerticalStrut(8));
            detailsPanel.add(new JLabel("<html><b>Title:</b> " + book.getTitle() + "</html>"));
            detailsPanel.add(Box.createVerticalStrut(8));
            detailsPanel.add(new JLabel("<html><b>⏳Max Checkout:</b> " + book.getMaxCheckoutLength() + " days</html>"));
            detailsPanel.add(Box.createVerticalStrut(4));
            // Authors
            String authors = book.getAuthors().stream()
                    .map(a -> a.getFirstName() + " " + a.getLastName() + " - " + a.getAddress())
                    .collect(Collectors.joining("<br>"));
            JLabel authorsLabel = new JLabel("<html><b>👤 Authors:</b><br>" + (authors.isEmpty() ? "No Authors" : authors) + "</html>");
            detailsPanel.add(authorsLabel);
            detailsPanel.add(Box.createVerticalStrut(5));

            // Book copies
            String copies = Arrays.stream(book.getCopies())
                    .map(copy -> "Copy #" + copy.getCopyNum() + " - " + (copy.isAvailable() ? "✅ Available" : "❌ Checked out"))
                    .collect(Collectors.joining("<br>"));
            JLabel copiesLabel = new JLabel("<html><b>📚 Copies:</b><br>" + (copies.isEmpty() ? "No Copies" : copies) + "</html>");
            detailsPanel.add(copiesLabel);
            detailsPanel.add(Box.createVerticalStrut(10));

            JScrollPane scrollPane = new JScrollPane(detailsPanel);
            scrollPane.setPreferredSize(new Dimension(550, 400));
            scrollPane.setBorder(null);

            JOptionPane.showMessageDialog(this, detailsPanel, "📘 Book Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public class AddCopyButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String isbn;
        private JTable table;

        public AddCopyButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Add New Copy");
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setFocusPainted(false);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Get the ISBN from the table
                    int row = table.convertRowIndexToModel(table.getEditingRow());
                    isbn = (String) table.getModel().getValueAt(row, 0); // Assuming ISBN is in the first column

                    // Open the AddBookCopyWindow
                    HashMap<String, Book> books = dataAccess.readBooksMap();
                    Book book = books.get(isbn);
                    if (book != null) {
                        book.addCopy();
                        dataAccess.updateBooksStorage(books); // Save changes
                        loadBooksData();
                    } else {
                        JOptionPane.showMessageDialog(table, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Stop editing
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return isbn;
        }
    }
    /** **Custom Renderer for Actions Column** (Displays Three Dots Button) */
    private class ActionButtonRenderer extends JButton implements TableCellRenderer {
        public ActionButtonRenderer() {
            setText("⋮"); // Three vertical dots
            setFont(new Font("Arial", Font.BOLD, 16));
            setBackground(Color.LIGHT_GRAY);
            setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    /** **Custom Editor for Actions Column** (Opens Dropdown on Click) */
    private class ActionButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String isbn;
        private boolean isPushed;

        public ActionButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("⋮");
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(Color.LIGHT_GRAY);
            button.setBorderPainted(false);

            button.addActionListener(e -> {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem edit = new JMenuItem("Edit");
                JMenuItem delete = new JMenuItem("Delete");
                JMenuItem checkout = new JMenuItem("Checkout"); // New Checkout Action

                edit.addActionListener(evt -> editBook(isbn));
                delete.addActionListener(evt -> deleteBook(isbn));
                checkout.addActionListener(evt -> checkOutCopy(isbn)); // Call checkout method

                menu.add(edit);
                menu.add(delete);
                menu.add(checkout); // Add the checkout action

                menu.show(button, button.getWidth() / 2, button.getHeight() / 2);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            isbn = (String) table.getValueAt(row, 0);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return "⋮";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void checkOutCopy(String isbn) {
        JTextField memberIdField = new JTextField();

        int result = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Enter Member ID:", memberIdField},
                "Checkout Book",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String memberId = memberIdField.getText().trim();
            if (memberId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Member ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate member ID
            DataAccessFacade dataAccess = new DataAccessFacade();
            if (!dataAccess.memberExists(memberId)) {
                JOptionPane.showMessageDialog(this, "Invalid Member ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fetch book and checkout a copy
            Book book = dataAccess.getBookByISBN(isbn);
            if (book == null) {
                JOptionPane.showMessageDialog(this, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Perform checkout
            LibraryMember member = dataAccess.getMemberById(memberId);
            boolean success = dataAccess.checkoutBook(member, book);
            if (success) {
                JOptionPane.showMessageDialog(this, "Checkout successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooksData(); // Refresh table to update availability
            } else {
                JOptionPane.showMessageDialog(this, "Checkout failed. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(bookTableModel);
        bookTable.setRowSorter(sorter);

        if (searchText.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // Create a RowFilter that matches any of the columns (ID, Name, Address, Phone)
            RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + searchText, 0, 1, 2, 3);
            sorter.setRowFilter(rowFilter);
        }
    }

    /** Handle Book Actions */
    private void editBook(String isbn) {
        Book book = dataAccess.readBooksMap().get(isbn);
        new BookWindow(booksTablePanel, book, true); // Open Edit Window
    }

    private void deleteBook(String isbn) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this book?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            HashMap<String, Book> books = dataAccess.readBooksMap();
            books.remove(isbn);
            dataAccess.updateBooksStorage(books);
            loadBooksData(); // Refresh Table
        }
    }


    public JTable getBookTable() {

        return bookTable; // Expose table reference for filtering
    }

    public void loadBooksData() {
        bookTableModel.setRowCount(0);

        HashMap<String, Book> booksMap = dataAccess.readBooksMap();
        bookTableModel.setRowCount(0);

        for (Book book : booksMap.values()) {
            String isbn = book.getIsbn();
            String title = book.getTitle();


            String authorsList = book.getAuthors().stream()
                    .map(author -> author.getFirstName() + " " + author.getLastName())
                    .collect(Collectors.joining(", "));

            String displayAuthor = authorsList.contains(",") ? authorsList.substring(0, authorsList.indexOf(",")) + "..." : authorsList;

            int maxCheckout = book.getMaxCheckoutLength();
            int totalCopies = book.getCopyNums().size();
            int availableCopies = book.getAvailableCopies();
            String availabilityStatus = availableCopies + " Available, " + (totalCopies - availableCopies) + " Checked out";

            bookTableModel.addRow(new Object[]{isbn, title, displayAuthor, totalCopies, availabilityStatus, maxCheckout, "", "⋮", "View details"});
        }
    }

    public TableRowSorter<TableModel> getSorter() {
        return sorter;
    }
}

