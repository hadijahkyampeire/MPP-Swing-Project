package librarysystem;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private JTable bookTable, memberTable;
    private DefaultTableModel bookTableModel, memberTableModel;
    private JTextField searchBookTitle, searchBookISBN, searchMemberField;
    private JButton addNewBookButton, addNewMemberButton;
    private CardLayout cardLayout;
    private JLabel tableTitle;
    private JButton booksButton, membersButton;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🏷️ **Top Navigation Bar**
        JPanel topNavBar = new JPanel(new BorderLayout());
        topNavBar.setBackground(Color.DARK_GRAY);
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
            dispose(); // Close the dashboard


            // ✅ Redirect to the Main Login Window
            EventQueue.invokeLater(() -> {
                LoginWindow.INSTANCE.setVisible(true);
            });
        });
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);
        topNavBar.add(menuBar, BorderLayout.EAST);

        add(topNavBar, BorderLayout.NORTH);

        // 📌 **Left Sidebar Navigation**
        JPanel sideNavBar = new JPanel();
        sideNavBar.setLayout(new BoxLayout(sideNavBar, BoxLayout.Y_AXIS));
        sideNavBar.setPreferredSize(new Dimension(120, getHeight()));
        sideNavBar.setBackground(Color.LIGHT_GRAY);
        sideNavBar.setBorder(new EmptyBorder(10, 5, 10, 5));

        booksButton = createMenuButton("📖 Books");
        membersButton = createMenuButton("👥 Members");

        sideNavBar.add(booksButton);
        sideNavBar.add(Box.createRigidArea(new Dimension(0, 5)));
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

    /** 📖 Creates a Styled Menu Button */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBackground(Color.LIGHT_GRAY);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ✅ Default Sidebar Color
        button.setBackground(new Color(230, 230, 230)); // Light Gray Background
        button.setForeground(Color.BLACK);

        // ✅ Hover Effect (Light Blue)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!Objects.equals(button.getBackground(), new Color(50, 130, 200))) { // Keep active button unchanged
                    button.setBackground(new Color(180, 220, 255)); // Light Blue on Hover
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!Objects.equals(button.getBackground(), new Color(50, 130, 200))) {
                    button.setBackground(new Color(230, 230, 230)); // Reset to Default
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
        booksButton.setBackground(new Color(230, 230, 230));
        membersButton.setBackground(new Color(230, 230, 230));

        booksButton.setForeground(Color.BLACK);
        membersButton.setForeground(Color.BLACK);

        // ✅ Set Active Button Color (Dark Blue)
        activeButton.setBackground(new Color(50, 130, 200));
        activeButton.setForeground(Color.WHITE);
    }

    /** 📚 **Creates the Books Table Panel** */
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 📌 **Title for Table**
        tableTitle = new JLabel("📖 Book List", SwingConstants.CENTER);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(tableTitle, BorderLayout.NORTH);

        // 🔍 Search & Add Book Panel (Above Table)
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchBookTitle = new JTextField(20);
        searchBookTitle.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true)); // ✅ Rounded Border
        searchBookISBN = new JTextField(20);
        searchBookISBN.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        searchPanel.add(new JLabel("Search by Title:"));
        searchPanel.add(searchBookTitle);
//        searchPanel.add(new JLabel("Search by ISBN:"));
//        searchPanel.add(searchBookISBN);

        addNewBookButton = new JButton("➕ Add New Book");
        addNewBookButton.setFont(new Font("Arial", Font.BOLD, 12));
        addNewBookButton.setPreferredSize(new Dimension(140, 35)); // ✅ Fixed Size
        addNewBookButton.addActionListener(e -> new BookWindow());

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(addNewBookButton, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.CENTER);

        // 📖 Books Table
        String[] bookColumns = {"ISBN", "Title", "Authors", "Max Checkout"};
        bookTableModel = new DefaultTableModel(bookColumns, 0);
        bookTable = new JTable(bookTableModel);
        panel.add(new JScrollPane(bookTable), BorderLayout.SOUTH);

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

        addNewMemberButton = new JButton("➕ Add New Member");
        addNewMemberButton.setFont(new Font("Arial", Font.BOLD, 12));
        addNewMemberButton.setPreferredSize(new Dimension(140, 35));
        addNewMemberButton.addActionListener(e -> new LibraryMemberWindow());

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(addNewMemberButton, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.CENTER);

        // 👥 Members Table
        String[] memberColumns = {"Member ID", "First Name", "Last Name", "Address", "Phone Number", "No. of Checkouts", "Actions"};
        memberTableModel = new DefaultTableModel(memberColumns, 0);
        memberTable = new JTable(memberTableModel);
        panel.add(new JScrollPane(memberTable), BorderLayout.SOUTH);

        return panel;
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

