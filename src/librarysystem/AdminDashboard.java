package librarysystem;

import librarysystem.tables.BooksTablePanel;
import librarysystem.tables.MembersTablePanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private JTable bookTable, memberTable;
    private DefaultTableModel bookTableModel, memberTableModel;
    private JTextField searchBookTitle, searchBookISBN, searchMemberField;
    private JButton addNewBookButton, addNewMemberButton;
    private JLabel tableTitle; // ✅ Dynamic Title for Books/Members
    private CardLayout cardLayout;
    private JButton booksButton, membersButton; // ✅ Track active button
    private JPanel sideNavBar; // ✅ Sidebar Panel
    private BooksTablePanel booksTablePanel;

    public AdminDashboard() {
        booksTablePanel = new BooksTablePanel();
        bookTable = booksTablePanel.getBookTable();
        setTitle("Admin Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🏷️ **Top Navigation Bar**
        JPanel topNavBar = new JPanel(new BorderLayout());
        topNavBar.setBackground(new Color(0,31,63));
        topNavBar.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel logoLabel = new JLabel("📚 Library Admin", SwingConstants.LEFT);
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topNavBar.add(logoLabel, BorderLayout.WEST);

        JLabel welcomeLabel = new JLabel("Welcome, Admin", SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topNavBar.add(welcomeLabel, BorderLayout.CENTER);

        // Avatar + Logout Dropdown
        JMenuBar menuBar = new JMenuBar();
        JMenu accountMenu = new JMenu("👤 Admin");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logged Out!");
            dispose();
            EventQueue.invokeLater(() -> LibrarySystem.INSTANCE.setVisible(true)); // ✅ Redirect to login
        });
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);
        topNavBar.add(menuBar, BorderLayout.EAST);

        add(topNavBar, BorderLayout.NORTH);

        // 📌 **Left Sidebar Navigation (Full-Width Buttons)**
        sideNavBar = new JPanel();
        sideNavBar.setLayout(new BoxLayout(sideNavBar, BoxLayout.Y_AXIS));
        sideNavBar.setPreferredSize(new Dimension(140, getHeight()));
        sideNavBar.setBackground(Color.LIGHT_GRAY);
        sideNavBar.setBorder(new EmptyBorder(10, 5, 10, 5));

        // ✅ Create Buttons with Sidebar Width Reference
        booksButton = createMenuButton("📖 Books");
        membersButton = createMenuButton("👥 Members");

        sideNavBar.add(booksButton);
        sideNavBar.add(Box.createRigidArea(new Dimension(0, 5))); // ✅ Small spacing
        sideNavBar.add(membersButton);

        add(sideNavBar, BorderLayout.WEST);

        // 📌 **Main Content Panel (Switchable via CardLayout)**
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 📚 Books Table Panel
        JPanel booksPanel = createBooksPanel();
        contentPanel.add(booksPanel, "Books");

        // 👥 Members Table Panel
        JPanel membersPanel = createMembersPanel();
        contentPanel.add(membersPanel, "Members");

        add(contentPanel, BorderLayout.CENTER);

        // 📌 **Navigation Actions**
        booksButton.addActionListener(e -> switchPanel("Books", booksButton));
        membersButton.addActionListener(e -> switchPanel("Members", membersButton));

        // ✅ Set default active button
        switchPanel("Books", booksButton);

        setVisible(true);
    }

    /** 📖 Creates a Styled Menu Button with Full-Width Stretch & Hover Effects */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // ✅ Pointer Cursor on Hover

        // ✅ Stretch Button Across Sidebar Width
        button.setPreferredSize(new Dimension(sideNavBar.getPreferredSize().width, 40));
        button.setMaximumSize(new Dimension(sideNavBar.getPreferredSize().width, 40));
        button.setMinimumSize(new Dimension(sideNavBar.getPreferredSize().width, 40));

        // ✅ Default Sidebar Button Color (Light Gray)
        button.setBackground(new Color(230, 230, 230));
        button.setForeground(Color.BLACK);

        // ✅ Hover Effect (Lighter Green)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getBackground() != new Color(0, 64, 128)) { // Keep active button unchanged
                    button.setBackground(new Color(173, 216, 230)); // ✅ Very Light Blue on Hover
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getBackground() != new Color(0, 64, 128)) {
                    button.setBackground(new Color(224, 224, 224)); // Reset to Default
                }
            }
        });

        return button;
    }

    /** 📌 **Switch Panel and Highlight Active Menu Button** */
    private void switchPanel(String panelName, JButton activeButton) {
        cardLayout.show(contentPanel, panelName);
        tableTitle.setText(panelName.equals("Books") ? "📖 Book List" : "👥 Member List");

        // ✅ Reset all buttons to default color
        booksButton.setBackground(new Color(224, 224, 224));
        membersButton.setBackground(new Color(224, 224, 224));
        booksButton.setForeground(Color.BLACK);
        membersButton.setForeground(Color.BLACK);

        // ✅ Set Active Button Color (Navy Blue)
        activeButton.setBackground(new Color(0, 31, 63)); // ✅ Navy Blue
        activeButton.setForeground(Color.WHITE);

        // ✅ Stretch Active Button Across Sidebar Width
        activeButton.setOpaque(true);
        activeButton.setBorderPainted(false);
        activeButton.setFocusPainted(false);
        activeButton.setContentAreaFilled(true);

        // ✅ Force UI Update to Reflect Changes
        sideNavBar.revalidate();
        sideNavBar.repaint();
    }

    /** 📚 **Creates the Books Table Panel** */
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 📌 **Title for Table**
        tableTitle = new JLabel("📖 Book List", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(tableTitle, BorderLayout.NORTH);

        // 🔍 **Search & Add Book Panel**
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel titleLabel = new JLabel("Search by Title:");
        searchBookTitle = new JTextField(20);
        searchBookTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        searchBookTitle.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        searchBookTitle.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(searchBookTitle.getText(), 1); }
            public void removeUpdate(DocumentEvent e) { filterTable(searchBookTitle.getText(), 1); }
            public void changedUpdate(DocumentEvent e) { filterTable(searchBookTitle.getText(), 1); }
        });

        JLabel isbnLabel = new JLabel("Search by ISBN:");
        searchBookISBN = new JTextField(20);
        searchBookISBN.setFont(new Font("Arial", Font.PLAIN, 14));
        searchBookISBN.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        searchBookISBN.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(searchBookISBN.getText(), 0); }
            public void removeUpdate(DocumentEvent e) { filterTable(searchBookISBN.getText(), 0); }
            public void changedUpdate(DocumentEvent e) { filterTable(searchBookISBN.getText(), 0); }
        });

        // ✅ Add Components to Search Panel
        searchPanel.add(titleLabel);
        searchPanel.add(searchBookTitle);
        searchPanel.add(isbnLabel);
        searchPanel.add(searchBookISBN);

        // ✅ Add New Book Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addNewBookButton = createActionButton("➕ Add New Book");
        addNewBookButton.addActionListener(e -> new BookWindow(booksTablePanel)); // ✅ Pass Reference
        buttonPanel.add(addNewBookButton);

        // ✅ Add Panels to Top Section
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // 📖 **Books Table**
        booksTablePanel = new BooksTablePanel(); // ✅ Initialize Here
        panel.add(booksTablePanel, BorderLayout.CENTER); // ✅ Add Table Panel

        return panel;
    }


    /** 👥 **Creates the Members Table Panel** */
    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 📌 **Title for Table**
        tableTitle = new JLabel("👥 Member List", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(tableTitle, BorderLayout.NORTH);

        // 🔍 Search & Add Member Panel (Above Table)
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchMemberField = new JTextField(20);
        searchMemberField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        searchPanel.add(new JLabel("Search by Name, ID, Address:"));
        searchPanel.add(searchMemberField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addNewMemberButton = createActionButton("➕ Add New Member");
        addNewMemberButton.addActionListener(e -> new LibraryMemberWindow());
        buttonPanel.add(addNewMemberButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.CENTER);

        // 👥 Members Table
        JPanel membersTablePanel = new MembersTablePanel();
        panel.add(membersTablePanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(160, 35)); // ✅ Fixed Compact Size
        button.setBackground(new Color(0, 31, 63)); // ✅ Navy Blue (Matches Top Nav)
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ✅ Force Background Color to Apply
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false); // Removes default border effect

        // ✅ Hover Effect (Light Blue)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 64, 128)); // ✅ Lighter Blue on Hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 31, 63)); // ✅ Reset to Navy Blue
            }
        });

        return button;
    }

    public static void open() {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }

    private void filterTable(String query, int columnIndex) {
        if (booksTablePanel == null || booksTablePanel.getBookTable() == null || booksTablePanel.getSorter() == null) {
            System.err.println("Error: BooksTablePanel or bookTable is not initialized.");
            return;
        }

        TableRowSorter<TableModel> sorter = booksTablePanel.getSorter();

        if (query.trim().isEmpty()) {
            sorter.setRowFilter(null); // ✅ Reset filter when query is empty
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, columnIndex)); // ✅ Case-insensitive search
            } catch (PatternSyntaxException e) {
                sorter.setRowFilter(null); // ✅ Prevents crashing due to invalid regex input
            }
        }
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}

