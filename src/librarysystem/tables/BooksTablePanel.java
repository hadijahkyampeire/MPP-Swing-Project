package librarysystem.tables;

import business.Book;
import dataaccess.DataAccessFacade;
import librarysystem.BookWindow;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.HashMap;
import java.util.stream.Collectors;

public class BooksTablePanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private DataAccessFacade dataAccess;

    public BooksTablePanel() {
        setLayout(new BorderLayout());
        dataAccess = new DataAccessFacade();

        // ✅ Table Columns
        String[] bookColumns = {"ISBN", "Title", "Authors", "Copies", "Availability Status", "Max Checkout(days)", "Actions"};
        bookTableModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // ✅ Only the Actions column is editable (for button)
            }
        };

        bookTable = new JTable(bookTableModel);
        bookTable.setRowHeight(35); // ✅ Increase Row Height
        bookTable.setAutoCreateRowSorter(true); // ✅ Enable Sorting

        // ✅ Disable Sorting on Actions and Availability Column
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(bookTableModel);
        sorter.setSortable(4, false);
        sorter.setSortable(6, false);
        bookTable.setRowSorter(sorter);

        // ✅ Set Renderers
        TableColumn availabilityColumn = bookTable.getColumnModel().getColumn(4);
        availabilityColumn.setCellRenderer(new AvailabilityRenderer());

        TableColumn authorsColumn = bookTable.getColumnModel().getColumn(2);
        authorsColumn.setCellRenderer(new AuthorsRenderer());

        // ✅ Customize "Actions" Column with Three Dots Button
        TableColumn actionsColumn = bookTable.getColumnModel().getColumn(6);
        actionsColumn.setCellRenderer(new ActionButtonRenderer());
        actionsColumn.setCellEditor(new ActionButtonEditor(new JCheckBox()));

        loadBooksData();

        // ✅ Add Table to ScrollPane
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    /** ✅ Load Books Data into the Table */
    private void loadBooksData() {
        HashMap<String, Book> booksMap = dataAccess.readBooksMap();
        bookTableModel.setRowCount(0); // ✅ Clear existing rows

        for (Book book : booksMap.values()) {
            String isbn = book.getIsbn();
            String title = book.getTitle();
            // ✅ Convert List<Author> to a formatted String
            String authorsList = book.getAuthors().stream()
                    .map(author -> author.getFirstName() + " " + author.getLastName())
                    .collect(Collectors.joining(", "));

            // ✅ Extract First Author (Before First Comma)
            String displayAuthor = authorsList.contains(",") ? authorsList.substring(0, authorsList.indexOf(",")) + "..." : authorsList;

            int maxCheckout = book.getMaxCheckoutLength();
            int totalCopies = book.getCopyNums().size();  // ✅ Use `getCopyNums()`
            int availableCopies = book.getAvailableCopies();  // ✅ Available copies count
            String availabilityStatus = availableCopies + " available, " + (totalCopies - availableCopies) + " unavailable";

            // ✅ Add Row to Table
            bookTableModel.addRow(new Object[]{isbn, title, displayAuthor, totalCopies, availabilityStatus, maxCheckout, "⋮"});
        }
    }

    /** 📌 **Custom Renderer for Actions Column** (Displays Three Dots Button) */
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

    /** 📌 **Custom Editor for Actions Column** (Opens Dropdown on Click) */
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
                JMenuItem addCopy = new JMenuItem("Add Copy");

                edit.addActionListener(evt -> editBook(isbn));
                delete.addActionListener(evt -> deleteBook(isbn));
                addCopy.addActionListener(evt -> addBookCopy(isbn));

                menu.add(edit);
                menu.add(delete);
                menu.add(addCopy);
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

    /** ✅ Handle Book Actions */
    private void editBook(String isbn) {
        JOptionPane.showMessageDialog(this, "Editing book with ISBN: " + isbn);
        new BookWindow(); // Open Edit Window
    }

    private void deleteBook(String isbn) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this book?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            HashMap<String, Book> books = dataAccess.readBooksMap();
            books.remove(isbn);
            dataAccess.updateBooksStorage(books);
            loadBooksData(); // ✅ Refresh Table
        }
    }

    private void addBookCopy(String isbn) {
        DataAccessFacade dataAccess = new DataAccessFacade();
        HashMap<String, Book> books = dataAccess.readBooksMap();

        if (books.containsKey(isbn)) {
            Book book = books.get(isbn);
            book.addCopy();  // ✅ Add a new copy
            dataAccess.updateBooksStorage(books); // ✅ Save changes
            loadBooksData();  // ✅ Refresh table
            JOptionPane.showMessageDialog(this, "A new copy of \"" + book.getTitle() + "\" has been added!");
        } else {
            JOptionPane.showMessageDialog(this, "Error: Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

