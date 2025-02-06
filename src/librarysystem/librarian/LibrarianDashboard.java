package librarysystem.librarian;

import business.CheckoutEntry;
import business.LibraryMember;
import dataaccess.DataAccessFacade;
import librarysystem.LoginWindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;

public class LibrarianDashboard extends JFrame {
    private JPanel contentPanel, sideNavBar;
    private CardLayout cardLayout;
    private JButton booksButton, membersButton, overDueBooksButton, checkedOutBooksButton;
    private JTable borrowedBooksTable, memberTable;
    private DefaultTableModel memberTableModel;
    private JButton activeNavButton;

    public LibrarianDashboard() {
        setTitle("Librarian Dashboard");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopNavBar(), BorderLayout.NORTH);
        add(createSideNavBar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(createOverdueBooksPanel(), "Overdue Books");
        contentPanel.add(createBorrowedBooksPanel(), "Checked out Books");
        contentPanel.add(createBooksPanel(), "Books");
        contentPanel.add(createMembersPanel(), "Members");

        add(contentPanel, BorderLayout.CENTER);

        switchPanel("Overdue Books", overDueBooksButton);
        setVisible(true);
    }

    /** 🔝 **Creates the top navigation bar** */
    private JPanel createTopNavBar() {
        JPanel topNavBar = new JPanel(new BorderLayout());
        topNavBar.setBackground(new Color(0,31,63));
        topNavBar.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel logoLabel = new JLabel("📚 O's Library", SwingConstants.LEFT);
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel welcomeLabel = new JLabel("Welcome, Librarian", SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JMenuBar menuBar = new JMenuBar();
        JMenu accountMenu = new JMenu("👤 Librarian");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logged Out!");
            dispose();
            EventQueue.invokeLater(() -> LoginWindow.INSTANCE.setVisible(true));
        });

        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        topNavBar.add(logoLabel, BorderLayout.WEST);
        topNavBar.add(welcomeLabel, BorderLayout.CENTER);
        topNavBar.add(menuBar, BorderLayout.EAST);

        return topNavBar;
    }

    /** **Creates the sidebar navigation panel** */
    private JPanel createSideNavBar() {
        sideNavBar = new JPanel();
        sideNavBar.setLayout(new BoxLayout(sideNavBar, BoxLayout.Y_AXIS));
        sideNavBar.setPreferredSize(new Dimension(180, getHeight()));
        sideNavBar.setBackground(Color.LIGHT_GRAY);
        sideNavBar.setBorder(new EmptyBorder(10, 5, 10, 5));

        overDueBooksButton = createMenuButton("Overdue Books");
        checkedOutBooksButton = createMenuButton("Checked out Books");
        booksButton = createMenuButton("📖 Books");
        membersButton = createMenuButton("👥 Members");

        sideNavBar.add(overDueBooksButton);
        sideNavBar.add(checkedOutBooksButton);
        sideNavBar.add(booksButton);
        sideNavBar.add(membersButton);

        overDueBooksButton.addActionListener(e -> switchPanel("Overdue Books", overDueBooksButton));
        checkedOutBooksButton.addActionListener(e -> switchPanel("Checked out Books", checkedOutBooksButton));
        booksButton.addActionListener(e -> switchPanel("Books", booksButton));
        membersButton.addActionListener(e -> switchPanel("Members", membersButton));

        return sideNavBar;
    }

    /**  **Creates a sidebar menu button with hover effect** */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(224, 224, 224));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(sideNavBar.getPreferredSize().width, 40));
        button.setMaximumSize(new Dimension(sideNavBar.getPreferredSize().width, 40));
        button.setMinimumSize(new Dimension(sideNavBar.getPreferredSize().width, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeNavButton) {
                    button.setBackground(new Color(180, 220, 255));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeNavButton) {
                    button.setBackground(new Color(230, 230, 230));
                }
            }
        });

        return button;
    }

    /** **Switches the main panel and highlights the active menu button** */
    private void switchPanel(String panelName, JButton clickedButton) {
        cardLayout.show(contentPanel, panelName);

        // Reload the borrowed books panel when it's switched to
        if (panelName.equals("Checked out Books")) {
            reloadBorrowedBooks();
        }

        overDueBooksButton.setBackground(new Color(230, 230, 230));
        booksButton.setBackground(new Color(230, 230, 230));
        membersButton.setBackground(new Color(230, 230, 230));

        overDueBooksButton.setForeground(Color.BLACK);
        booksButton.setForeground(Color.BLACK);
        membersButton.setForeground(Color.BLACK);

        clickedButton.setBackground(new Color(0, 31, 63)); // Navy Blue
        clickedButton.setForeground(Color.WHITE);
        activeNavButton = clickedButton;
    }

    private JPanel createBorrowedBooksPanel() {
        // Create a panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // Add title to the top of the panel
        JLabel tableTitle = new JLabel("📚 Borrowed Books List", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Create a search bar for filtering the borrowed books (optional)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Add padding inside the search field
        ));
        searchField.setPreferredSize(new Dimension(searchField.getPreferredSize().width, 30));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        // Combine search and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topPanel.add(tableTitle, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // Define the columns for the table
        String[] borrowedColumns = {"Title", "Borrower", "Checkout Date", "Due Date"};
        DefaultTableModel borrowedTableModel = new DefaultTableModel(borrowedColumns, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevents any cell from being edited
            }
        };
        borrowedBooksTable = new JTable(borrowedTableModel);
        JTableHeader header = borrowedBooksTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        borrowedBooksTable.setRowHeight(35);

        // Fetch borrowed books and load into the table
        DataAccessFacade dataAccess = new DataAccessFacade();
        HashMap<String, LibraryMember> members = dataAccess.readMemberMap();
        loadBorrowedBooks(borrowedTableModel, members);

        // Add the JTable wrapped in a JScrollPane to the panel
        JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 128), 2)); // Navy blue border
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside the table
        scrollPane.setViewportBorder(null);

        // Add the table to a panel with padding
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the table
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim();
                filterTable(searchText, borrowedTableModel, borrowedBooksTable);
            }
        });

        return panel;
    }

    /** Loads all borrowed books into the table. */
    private void loadBorrowedBooks(DefaultTableModel tableModel, HashMap<String, LibraryMember> members) {
        tableModel.setRowCount(0); // Clear existing data

        for (LibraryMember member : members.values()) {
            List<CheckoutEntry> borrowedBooks = Optional.ofNullable(member.getCheckoutEntries()).orElse(Collections.emptyList());
            System.out.println(borrowedBooks+ "BB");
            for (CheckoutEntry entry : borrowedBooks) {
                if (entry.getDueDate().isAfter(LocalDate.now())) { // Ensure it's still borrowed
                    String title = entry.getBookCopy().getBook().getTitle();
                    String borrower = member.getFirstName() + " " + member.getLastName();
                    LocalDate checkoutDate = entry.getCheckoutDate();
                    LocalDate dueDate = entry.getDueDate();

                    // Add row to table
                    tableModel.addRow(new Object[]{title, borrower, checkoutDate, dueDate});
                }
            }
        }
    }

    private void reloadBorrowedBooks() {
        // Fetch the latest members from the DataAccessFacade
        DataAccessFacade dataAccess = new DataAccessFacade();
        HashMap<String, LibraryMember> members = dataAccess.readMemberMap(); // Re-fetch members with checkout entries

        // Access the existing borrowedBooksTable in the UI
        DefaultTableModel borrowedTableModel = (DefaultTableModel) borrowedBooksTable.getModel(); // Access existing model

        // Clear existing rows in the table
        borrowedTableModel.setRowCount(0);

        // Reload the borrowed books data into the table
        loadBorrowedBooks(borrowedTableModel, members);
    }

    /** **Creates the Overdue Books Panel** */
    private JPanel createOverdueBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel tableTitle = new JLabel("📖 Overdue Books List", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JTextField searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Add padding inside the search field
        ));
        searchField.setPreferredSize(new Dimension(searchField.getPreferredSize().width, 30));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        // Combine search and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topPanel.add(tableTitle, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] overdueColumns = {"Title", "Borrower", "Checkout Date", "Due Date"};
        DefaultTableModel overdueTableModel = new DefaultTableModel(overdueColumns, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevents any cell from being edited
            }
        };

        JTable overdueBooksTable = new JTable(overdueTableModel);
        JTableHeader header = overdueBooksTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        overdueBooksTable.setRowHeight(35);

        DataAccessFacade dataAccess = new DataAccessFacade();
        HashMap<String, LibraryMember> members = dataAccess.readMemberMap();
        loadOverdueBooks(overdueTableModel, members);

        JScrollPane scrollPane = new JScrollPane(overdueBooksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 128), 2));
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setViewportBorder(null);

        // Add the table to a panel with padding
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim();
                filterTable(searchText, overdueTableModel, overdueBooksTable);
            }
        });

        return panel;
    }

    /** **Creates the Books Panel with book data** */
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel tableTitle = new JLabel("📖 All Books List", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JTextField searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.setPreferredSize(new Dimension(searchField.getPreferredSize().width, 30));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        // Combine search and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topPanel.add(tableTitle, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        BooksTablePanel booksPanel = new BooksTablePanel();
        panel.add(booksPanel, BorderLayout.CENTER);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim();
                filterTable(searchText, booksPanel.getBookTableModel(), booksPanel.getBookTable());
            }
        });
        return panel;
    }

    private void loadMembers(DefaultTableModel tableModel, HashMap<String, LibraryMember> members) {
        tableModel.setRowCount(0); // Clear existing data

        for (LibraryMember member : members.values()) {
            int checkoutCount = (member.getCheckoutEntries() != null) ? member.getCheckoutEntries().size() : 0;

            tableModel.addRow(new Object[]{
                    member.getMemberId(),
                    member.getFirstName(),
                    member.getLastName(),
                    member.getAddress(),
                    member.getTelephone(),
                    checkoutCount,
                    checkoutCount > 0 ? "🖨️" : ""
            });
        }
    }

    private void loadOverdueBooks(DefaultTableModel tableModel, HashMap<String, LibraryMember> members) {
        tableModel.setRowCount(0); // Clear existing data

        LocalDate today = LocalDate.now();

        members.values().stream()
                .map(member -> Optional.ofNullable(member.getCheckoutEntries()) // Wrap checkout entries in Optional
                        .orElseGet(Collections::emptyList)) // Provide an empty list if null
                .flatMap(List::stream)
                .filter(entry -> Optional.ofNullable(entry.getBookCopy()) // Ensure bookCopy is not null
                        .map(copy -> !copy.isAvailable() && entry.getDueDate().isBefore(today)) // Check availability and due date
                        .orElse(false)) // Default to false if bookCopy is null
                .map(entry -> {
                    LibraryMember member = findMemberByEntry(members, entry);
                    return new Object[]{
                            entry.getBookCopy().getBook().getTitle(),
                            member != null ? member.getFirstName() + " " + member.getLastName() : "Unknown",
                            entry.getCheckoutDate(),
                            entry.getDueDate()
                    };
                })
                .forEach(tableModel::addRow);
    }

    /** Finds the member associated with a given checkout entry */
    private LibraryMember findMemberByEntry(HashMap<String, LibraryMember> members, CheckoutEntry entry) {
        return members.values().stream()
                .filter(member -> Optional.ofNullable(member.getCheckoutEntries())
                        .orElseGet(Collections::emptyList)
                        .contains(entry))
                .findFirst()
                .orElse(null);
    }

    /** **Creates the Members Panel** */
    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel tableTitle = new JLabel("👥 Member List", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.setPreferredSize(new Dimension(searchField.getPreferredSize().width, 30));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        // Combine search and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topPanel.add(tableTitle, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] memberColumns = {"Member ID", "First Name", "Last Name", "Address", "Phone Number", "No. of Checkouts", "Print Record"};
        memberTableModel = new DefaultTableModel(memberColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        memberTable = new JTable(memberTableModel);
        JTableHeader header = memberTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        memberTable.setRowHeight(35);

        TableColumn printColumn = memberTable.getColumnModel().getColumn(6); // Last column
        printColumn.setCellRenderer(new PrintIconRenderer());
        printColumn.setCellEditor(new PrintIconEditor(new JCheckBox()));

        // Fetch members from storage and load them into the table
        DataAccessFacade dataAccess = new DataAccessFacade();
        HashMap<String, LibraryMember> members = dataAccess.readMemberMap();
        loadMembers(memberTableModel, members);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim();
                filterTable(searchText, memberTableModel, memberTable);
            }
        });

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 128), 2));
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setViewportBorder(null);

        // Add the table to a panel with padding
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    private void printMemberDetails(String memberId) {
        // Retrieve LibraryMember object
        DataAccessFacade dataAccess = new DataAccessFacade();
        LibraryMember member = dataAccess.getMemberById(memberId);

        if (member == null) {
            JOptionPane.showMessageDialog(null, "❌ Member not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Retrieve checkout records
        List<CheckoutEntry> checkoutEntries = member.getCheckoutEntries();

        // Create panel for displaying details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding for the panel

        // **Member Info Section**
        detailsPanel.add(new JLabel("<html><b>Member ID:</b> " + member.getMemberId() + "</html>"));
        detailsPanel.add(Box.createVerticalStrut(10)); // Add spacing
        detailsPanel.add(new JLabel("<html><b>Name:</b> " + member.getFirstName() + " " + member.getLastName() + "</html>"));
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(new JLabel("<html><b>Address:</b> " + member.getAddress().toString() + "</html>"));
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(new JLabel("<html><b>Phone:</b> " + member.getTelephone() + "</html>"));
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(new JLabel("<html><b>No. of Checkouts:</b> " + checkoutEntries.size() + "</html>"));
        detailsPanel.add(Box.createVerticalStrut(15));

        if (checkoutEntries.isEmpty()) {
            detailsPanel.add(new JLabel("<html><b>Checkout Entries:</b> No checkouts found.</html>"));
        } else {
            detailsPanel.add(new JLabel("<html><b>📚 Checkout Entries:</b></html>"));
            detailsPanel.add(Box.createVerticalStrut(5));

            for (int i = 0; i < checkoutEntries.size(); i++) {
                CheckoutEntry entry = checkoutEntries.get(i);

                // **Panel for Individual Entry**
                JPanel entryPanel = new JPanel();
                entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
                entryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                // **Checkout Date as Title**
                entryPanel.add(new JLabel("<html><p style='white-space: nowrap;'><b>📅 Checkout Date:</b> " + entry.getCheckoutDate() + "</p></html>"));
                entryPanel.add(Box.createVerticalStrut(5));

                // **Other Details**
                entryPanel.add(new JLabel("<html><p style='white-space: nowrap;'><b>Title:</b> " + entry.getBookCopy().getBook().getTitle() + "</p></html>"));
                entryPanel.add(new JLabel("<html><p style='white-space: nowrap;'><b>ISBN:</b> " + entry.getBookCopy().getBook().getIsbn() + "</p></html>"));
                entryPanel.add(new JLabel("<html><p style='white-space: nowrap;'><b>Copy Number:</b> " + entry.getBookCopy().getCopyNum() + "</p></html>"));
                entryPanel.add(new JLabel("<html><p style='white-space: nowrap;'><b>Due Date:</b> " + entry.getDueDate() + "</p></html>"));

                // **Add entry panel to main details panel**
                detailsPanel.add(entryPanel);

                // **Add a separator after every entry except the last one**
                if (i < checkoutEntries.size() - 1) {
                    JSeparator separator = new JSeparator();
                    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); // Stretch across width
                    detailsPanel.add(separator);
                    detailsPanel.add(Box.createVerticalStrut(10)); // Space after separator
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        // Show details in dialog
        JOptionPane.showMessageDialog(null, detailsPanel, "📄 Member Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private class PrintIconEditor extends DefaultCellEditor {
        private final JButton button;
        private String memberId;

        public PrintIconEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton(new PrintIcon());
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.addActionListener(e -> printMemberDetails(memberId));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            memberId = (String) table.getValueAt(row, 0);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private class PrintIconRenderer extends DefaultTableCellRenderer {
        private final Icon printIcon = new PrintIcon();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);

            int checkouts = (int) table.getValueAt(row, 5);

            if (checkouts > 0) {
                label.setIcon(printIcon);
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            return label;
        }
    }


    public void filterTable(String searchText, DefaultTableModel model, JTable table) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        if (searchText.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // Create a RowFilter that matches any of the columns (ID, Name, Address, Phone)
            RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + searchText, 0, 1, 2, 3);
            sorter.setRowFilter(rowFilter);
        }
    }

    public static void open() {
        SwingUtilities.invokeLater(LibrarianDashboard::new);
    }

    public static void main(String[] args) {
        new LibrarianDashboard();
    }
}
