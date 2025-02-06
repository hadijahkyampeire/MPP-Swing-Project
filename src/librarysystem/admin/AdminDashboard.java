package librarysystem.admin;

import librarysystem.LoginWindow;
import librarysystem.admin.tables.BooksTablePanel;
import librarysystem.admin.tables.MembersTablePanel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private JTable bookTable, memberTable;
    private JTextField searchBook, searchMemberField;
    private JButton addNewBookButton, addNewMemberButton;
    private JLabel tableTitle;
    private CardLayout cardLayout;
    private JButton booksButton, membersButton;
    private JPanel sideNavBar;
    private BooksTablePanel booksTablePanel;
    private MembersTablePanel membersTablePanel;
    private JButton activeNavButton;

    public AdminDashboard() {
        booksTablePanel = new BooksTablePanel();
        bookTable = booksTablePanel.getBookTable();
        membersTablePanel = new MembersTablePanel();
        memberTable = membersTablePanel.getMemberTable();
        setTitle("Admin Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topNavBar = new JPanel(new BorderLayout());
        topNavBar.setBackground(new Color(0,31,63));
        topNavBar.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel logoLabel = new JLabel("📚 O's Library", SwingConstants.LEFT);
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
            EventQueue.invokeLater(() -> LoginWindow.INSTANCE.setVisible(true));
        });
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);
        topNavBar.add(menuBar, BorderLayout.EAST);

        add(topNavBar, BorderLayout.NORTH);


        sideNavBar = new JPanel();
        sideNavBar.setLayout(new BoxLayout(sideNavBar, BoxLayout.Y_AXIS));
        sideNavBar.setPreferredSize(new Dimension(140, getHeight()));
        sideNavBar.setBackground(Color.LIGHT_GRAY);
        sideNavBar.setBorder(new EmptyBorder(10, 5, 10, 5));

        membersButton = createMenuButton("👥 Members");
        booksButton = createMenuButton("📖 Books");


        sideNavBar.add(membersButton);
        sideNavBar.add(Box.createRigidArea(new Dimension(0, 5))); // ✅ Small spacing
        sideNavBar.add(booksButton);

        add(sideNavBar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);


        JPanel booksPanel = createBooksPanel();
        contentPanel.add(booksPanel, "Books");


        JPanel membersPanel = createMembersPanel();
        contentPanel.add(membersPanel, "Members");

        add(contentPanel, BorderLayout.CENTER);


        membersButton.addActionListener(e -> switchPanel("Members", membersButton));
        booksButton.addActionListener(e -> switchPanel("Books", booksButton));


        switchPanel("Members", membersButton);

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
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Stretch Button Across Sidebar Width
        button.setPreferredSize(new Dimension(sideNavBar.getPreferredSize().width, 40));
        button.setMaximumSize(new Dimension(sideNavBar.getPreferredSize().width, 40));
        button.setMinimumSize(new Dimension(sideNavBar.getPreferredSize().width, 40));

        // Default Sidebar Button Color (Light Gray)
        button.setBackground(new Color(230, 230, 230));
        button.setForeground(Color.BLACK);

        // Hover Effect (Lighter Green)
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

    /** **Switch Panel and Highlight Active Menu Button** */
    private void switchPanel(String panelName, JButton clickedButton) {
        cardLayout.show(contentPanel, panelName);
        tableTitle.setText(panelName.equals("Books") ? "📖 Book List" : "👥 Member List");

        booksButton.setBackground(new Color(224, 224, 224));
        membersButton.setBackground(new Color(224, 224, 224));
        booksButton.setForeground(Color.BLACK);
        membersButton.setForeground(Color.BLACK);

        clickedButton.setBackground(new Color(0, 31, 63)); // Navy Blue
        clickedButton.setForeground(Color.WHITE);
        activeNavButton = clickedButton;

        sideNavBar.revalidate();
        sideNavBar.repaint();
    }

    /** **Creates the Books Table Panel** */
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableTitle = new JLabel("📖 Book List", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(tableTitle, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("Search by ISBN or Title:");
        searchBook = new JTextField(20);
        searchBook.setFont(new Font("Arial", Font.PLAIN, 14));
        searchBook.setPreferredSize(new Dimension(searchBook.getPreferredSize().width, 30)); // Increase height of the search bar
        searchBook.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Add padding inside the search field
        ));


        searchPanel.add(titleLabel);
        searchPanel.add(searchBook);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addNewBookButton = createActionButton("➕ Add New Book");
        addNewBookButton.addActionListener(e -> new BookWindow(booksTablePanel, null, false)); // ✅ Pass Reference
        buttonPanel.add(addNewBookButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.add(tableTitle);
        topContainer.add(topPanel);

        panel.add(topContainer, BorderLayout.NORTH);

        booksTablePanel = new BooksTablePanel(); // ✅ Initialize Here
        JTable bookTable = booksTablePanel.getBookTable();
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(booksTablePanel.getBookTable());
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 128), 2)); // Navy blue border
        scrollPane.setViewportBorder(null);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the table
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        searchBook.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchBook.getText().trim();
                booksTablePanel.filterTable(searchText);
            }
        });

        return panel;
    }

    /** **Creates the Members Table Panel** */
    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout());


        tableTitle = new JLabel("👥 Member List", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel searchLabel = new JLabel("Search by Name, ID, Address:");
        searchMemberField = new JTextField(20);
        searchMemberField.setPreferredSize(new Dimension(searchMemberField.getPreferredSize().width, 30));
        searchMemberField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Add padding inside the search field
        ));

        searchPanel.add(new JLabel("Search by Name, ID, Address:"));
        searchPanel.add(searchMemberField);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addNewMemberButton = createActionButton("➕ Add New Member");
        addNewMemberButton.addActionListener(e -> new LibraryMemberWindow(membersTablePanel, null, false));
        buttonPanel.add(addNewMemberButton);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        topContainer.add(tableTitle);

        JPanel searchAddRow = new JPanel(new BorderLayout());
        // Left side: search label + text field
        JPanel leftSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftSearchPanel.add(searchLabel);
        leftSearchPanel.add(searchMemberField);

        // Right side: add button
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightButtonPanel.add(addNewMemberButton);

        searchAddRow.add(leftSearchPanel, BorderLayout.WEST);
        searchAddRow.add(rightButtonPanel, BorderLayout.EAST);

        topContainer.add(searchAddRow);
        panel.add(topContainer, BorderLayout.NORTH);

        // Members Table
        membersTablePanel = new MembersTablePanel();
        JTable memberTable = membersTablePanel.getMemberTable();
        memberTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        JScrollPane scrollPane = new JScrollPane(membersTablePanel.getMemberTable());
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 128), 2)); // Navy blue border
        scrollPane.setViewportBorder(null);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        searchMemberField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchMemberField.getText().trim();
                membersTablePanel.filterTable(searchText);
            }
        });

        return panel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(160, 35));
        button.setBackground(new Color(0, 31, 63));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);

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
                    button.setBackground(new Color(180, 220, 255));
                }
            }
        });

        return button;
    }

    public static void open() {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}

