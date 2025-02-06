package librarysystem.admin.tables;

import business.LibraryMember;
import dataaccess.DataAccessFacade;
import librarysystem.admin.LibraryMemberWindow;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

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

            popupMenu.add(editMenuItem);
            popupMenu.add(deleteMenuItem);
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

