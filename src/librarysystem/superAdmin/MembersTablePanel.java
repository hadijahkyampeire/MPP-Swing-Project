package librarysystem.SuperAdmin;

import business.LibraryMember;
import dataaccess.DataAccessFacade;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

public class MembersTablePanel extends JPanel {
    private JTable memberTable;
    private DefaultTableModel memberTableModel;
    private DataAccessFacade dataAccess;
    private MembersTablePanel membersTablePanel;

    public MembersTablePanel() {
        membersTablePanel = this;
        setLayout(new BorderLayout());
        dataAccess = new DataAccessFacade();

        // Table Columns
        String[] memberColumns = {"Member ID", "Name", "Address", "Phone", "Actions"};
        memberTableModel = new DefaultTableModel(memberColumns, 0);

        memberTable = new JTable(memberTableModel);
        JTableHeader header = memberTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        memberTable.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        memberTable.getColumn("Actions").setCellEditor(new ActionsEditor());

        memberTable.setRowHeight(35);
        memberTable.setAutoCreateRowSorter(true);
        loadMembersData();

        JScrollPane scrollPane = new JScrollPane(memberTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    /** ✅ Load Members Data into the Table */
    public void loadMembersData() {
        HashMap<String, LibraryMember> membersMap = dataAccess.readMemberMap();
        memberTableModel.setRowCount(0);

        for (LibraryMember member : membersMap.values()) {
            String memberId = member.getMemberId();
            String name = member.getFirstName() + " " + member.getLastName();
            String address = member.getAddress().getStreet() + ", " + member.getAddress().getCity();
            String phone = member.getTelephone();

            // Add Row to Table
            memberTableModel.addRow(new Object[]{memberId, name, address, phone, "⋮"});
        }
    }

    // Custom renderer for the "Actions" column
    private class ActionsRenderer extends DefaultTableCellRenderer {
        private JButton button;

        public ActionsRenderer() {
            button = new JButton("⋮");
            button.setFont(new Font("Arial", Font.PLAIN, 18));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return button;
        }
    }

    // Custom editor for the "Actions" column
    private class ActionsEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
        private JPopupMenu popupMenu;
        private JMenuItem editMenuItem;
        private JMenuItem deleteMenuItem;
        private JMenuItem printCheckoutDetailsMenuItem; // New Menu Item
        private int selectedRow;

        public ActionsEditor() {
            button = new JButton("⋮");
            button.setFont(new Font("Arial", Font.PLAIN, 18));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.addActionListener(this);

            // Create the popup menu
            popupMenu = new JPopupMenu();
            editMenuItem = new JMenuItem("Edit");
            deleteMenuItem = new JMenuItem("Delete");
            printCheckoutDetailsMenuItem = new JMenuItem("Print Checkout Details"); // New Option

            // Add action listeners for the menu items
            editMenuItem.addActionListener(e -> {
                // Get the selected member's details
                String memberId = (String) memberTable.getValueAt(selectedRow, 0);

                // Fetch the member object from the data access layer
                LibraryMember member = dataAccess.readMemberMap().get(memberId);

                if (member != null) {
                    // Open the LibraryMemberWindow in edit mode
                    new LibraryMemberWindow(membersTablePanel, member, true);
                } else {
                    JOptionPane.showMessageDialog(membersTablePanel, "Member not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            deleteMenuItem.addActionListener(e -> {
                String memberId = (String) memberTable.getValueAt(selectedRow, 0);
                deleteMember(memberId);
            });

            printCheckoutDetailsMenuItem.addActionListener(e -> {
                String memberId = (String) memberTable.getValueAt(selectedRow, 0);
                printCheckoutDetails(memberId);
            });

            popupMenu.add(editMenuItem);
            popupMenu.add(deleteMenuItem);
            popupMenu.add(printCheckoutDetailsMenuItem); // Add new action to menu
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "⋮";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Show the popup menu when the button is clicked
            popupMenu.show(button, 0, button.getHeight());
        }
    }

    private void printCheckoutDetails(String memberId) {
        Optional<LibraryMember> memberOpt = Optional.ofNullable(dataAccess.readMemberMap().get(memberId));

        if (memberOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Member not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LibraryMember member = memberOpt.get();

        // Retrieve member details using Optional to handle nulls
        String memberDetails = String.format(
                "Member ID: %s\nName: %s %s\nAddress: %s, %s\nPhone: %s\n\n",
                member.getMemberId(),
                Optional.ofNullable(member.getFirstName()).orElse("N/A"),
                Optional.ofNullable(member.getLastName()).orElse("N/A"),
                Optional.ofNullable(member.getAddress()).map(a -> a.getStreet()).orElse("N/A"),
                Optional.ofNullable(member.getAddress()).map(a -> a.getCity()).orElse("N/A"),
                Optional.ofNullable(member.getTelephone()).orElse("N/A")
        );

        // Retrieve checkout entries
        StringBuilder checkoutDetails = new StringBuilder("Checkout Records:\n");
        Optional.ofNullable(member.getCheckoutEntries())
                .filter(entries -> !entries.isEmpty())
                .ifPresentOrElse(entries -> entries.forEach(entry -> {
                    checkoutDetails.append("- Book: ")
                            .append(Optional.ofNullable(entry.getBookCopy())
                                    .map(copy -> copy.getBook().getTitle())
                                    .orElse("Unknown Book"))
                            .append("\n  Checkout Date: ")
                            .append(Optional.ofNullable(entry.getCheckoutDate()).orElse(LocalDate.parse("N/A")))
                            .append("\n  Due Date: ")
                            .append(Optional.ofNullable(entry.getDueDate()).orElse(LocalDate.parse("N/A")))
                            .append("\n\n");
                }), () -> checkoutDetails.append("No checkout records found.\n"));

        // Combine member details and checkout details
        String details = memberDetails + checkoutDetails;

        // Print the details in a dialog
        JTextArea textArea = new JTextArea(details);
        textArea.setEditable(false);
        System.out.println(memberDetails);
        System.out.println(checkoutDetails);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Checkout Details", JOptionPane.INFORMATION_MESSAGE);
    }


    private void deleteMember(String memberId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this member: " + memberId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            HashMap<String, LibraryMember> members = dataAccess.readMemberMap();
            members.remove(memberId);
            dataAccess.updateMembersStorage(members);
            loadMembersData(); // Refresh Table
        }
    }

    public JTable getMemberTable() {

        return memberTable; // Expose table reference for filtering
    }

    public void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(memberTableModel);
        memberTable.setRowSorter(sorter);

        if (searchText.length() == 0) {
            sorter.setRowFilter(null);
        } else {

            RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + searchText, 0, 1, 2, 3);
            sorter.setRowFilter(rowFilter);
        }
    }
}

