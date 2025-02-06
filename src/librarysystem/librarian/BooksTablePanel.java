package librarysystem.librarian;

import business.Book;
import business.LibraryMember;
import dataaccess.DataAccessFacade;
import librarysystem.admin.tables.AuthorsRenderer;
import librarysystem.admin.tables.AvailabilityRenderer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class BooksTablePanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private DataAccessFacade dataAccess;

    public BooksTablePanel() {
        setLayout(new BorderLayout());
        dataAccess = new DataAccessFacade();

        // Table Columns
        String[] bookColumns = {"ISBN", "Title", "Authors", "Copies", "Availability Status", "Max Checkout(days)", "Actions"};
        bookTableModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only the Actions column is editable (for button)
            }
        };

        bookTable = new JTable(bookTableModel);
        JTableHeader header = bookTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        bookTable.setRowHeight(35);
        bookTable.setAutoCreateRowSorter(true);

        // Disable Sorting on Actions and Availability Column
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(bookTableModel);
        sorter.setSortable(4, false);
        sorter.setSortable(6, false);
        bookTable.setRowSorter(sorter);

        // Set Renderers
        TableColumn availabilityColumn = bookTable.getColumnModel().getColumn(4);
        availabilityColumn.setCellRenderer(new AvailabilityRenderer());

        TableColumn authorsColumn = bookTable.getColumnModel().getColumn(2);
        authorsColumn.setCellRenderer(new AuthorsRenderer());

        // Customize "Actions" Column with Checkout Button
        TableColumn actionsColumn = bookTable.getColumnModel().getColumn(6);
        actionsColumn.setCellRenderer(new CheckoutButtonRenderer());
        actionsColumn.setCellEditor(new CheckoutButtonEditor(new JCheckBox()));

        loadBooksData();

        int rowHeight = bookTable.getRowHeight();
        int rowCount = bookTableModel.getRowCount();
        int tableHeight = (rowCount > 0) ? (rowCount * rowHeight) + header.getPreferredSize().height + 10 : 100;

        // Add Table to ScrollPane
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, tableHeight));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 128), 2));
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setViewportBorder(null);
        scrollPane.revalidate();
        scrollPane.repaint();

        // Add the table to a panel with padding
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);


        add(tablePanel, BorderLayout.CENTER);
    }

    /** Load Books Data into the Table */
    private void loadBooksData() {
        HashMap<String, Book> booksMap = dataAccess.readBooksMap();
        System.out.println(booksMap.values());
        bookTableModel.setRowCount(0); // Clear existing rows

        for (Book book : booksMap.values()) {
            String isbn = book.getIsbn();
            String title = book.getTitle();
            // Convert List<Author> to a formatted String
            String authorsList = book.getAuthors().stream()
                    .map(author -> author.getFirstName() + " " + author.getLastName())
                    .collect(Collectors.joining(", "));

            // Extract First Author (Before First Comma)
            String displayAuthor = authorsList.contains(",") ? authorsList.substring(0, authorsList.indexOf(",")) + "..." : authorsList;

            int maxCheckout = book.getMaxCheckoutLength();
            int totalCopies = book.getCopyNums().size();
            int availableCopies = book.getAvailableCopies();  // Available copies count
            String availabilityStatus = availableCopies + " available, " + (totalCopies - availableCopies) + " unavailable";

            // Add Row to Table with Checkout Button (Enabled/Disabled Based on Availability)
            Object[] rowData = new Object[]{
                    isbn,
                    title,
                    displayAuthor,
                    totalCopies,
                    availabilityStatus,
                    maxCheckout,
                    availableCopies > 0 ? "Checkout" : "Out of Stock" // Change button text based on availability
            };
            bookTableModel.addRow(rowData);
        }
    }

    /** **Custom Renderer for Actions Column** (Displays Checkout Button) */
    private static class CheckoutButtonRenderer extends JButton implements TableCellRenderer {
        public CheckoutButtonRenderer() {
            setText("Checkout");
            setFont(new Font("Arial", Font.BOLD, 14));
            setBackground(Color.LIGHT_GRAY);
            setBorderPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            // If the value in the Actions column is "Out of Stock", disable the button
            String actionText = (String) value;
            if ("Out of Stock".equals(actionText)) {
                setEnabled(false); // Disable the button
                setText("Out of Stock"); // Change text to reflect the status
            } else {
                setEnabled(true); // Enable the button
                setText("Checkout");
            }
            return this;
        }
    }


    /** **Custom Editor for Actions Column** (Handles Checkout Button Click) */
    private class CheckoutButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String isbn;
        private boolean isPushed;

        public CheckoutButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Checkout");
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(Color.LIGHT_GRAY);
            button.setBorderPainted(true);

            button.addActionListener(e -> {
                // Perform checkout action when button is clicked
                checkOutCopy(isbn);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            isbn = (String) table.getValueAt(row, 0);

            // Get the availability status from the availability column
            String availability = (String) table.getValueAt(row, 4);
            if (availability.contains("0 available")) {  // Check if no available copies
                button.setEnabled(false); // Disable the button if no copies are available
                button.setText("Out of Stock");
            } else {
                button.setEnabled(true); // Enable the button if copies are available
                button.setText("Checkout");
            }

            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return "Checkout";
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

    public DefaultTableModel getBookTableModel() {
        return bookTableModel;
    }

    public JTable getBookTable() {
        return bookTable;
    }
}
