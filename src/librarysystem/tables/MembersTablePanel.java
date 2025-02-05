package librarysystem.tables;

import business.LibraryMember;
import dataaccess.DataAccessFacade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;

public class MembersTablePanel extends JPanel {
    private JTable memberTable;
    private DefaultTableModel memberTableModel;
    private DataAccessFacade dataAccess;

    public MembersTablePanel() {
        setLayout(new BorderLayout());
        dataAccess = new DataAccessFacade();

        // ✅ Table Columns
        String[] memberColumns = {"Member ID", "Name", "Address", "Phone"};
        memberTableModel = new DefaultTableModel(memberColumns, 0);

        memberTable = new JTable(memberTableModel);
        memberTable.setRowHeight(35);
        memberTable.setAutoCreateRowSorter(true);
        loadMembersData();

        // ✅ Add Table to ScrollPane
        JScrollPane scrollPane = new JScrollPane(memberTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    /** ✅ Load Members Data into the Table */
    private void loadMembersData() {
        HashMap<String, LibraryMember> membersMap = dataAccess.readMemberMap();
        memberTableModel.setRowCount(0); // ✅ Clear existing rows

        for (LibraryMember member : membersMap.values()) {
            String memberId = member.getMemberId();
            String name = member.getFirstName() + " " + member.getLastName();
            String address = member.getAddress().getStreet() + ", " + member.getAddress().getCity();
            String phone = member.getTelephone();

            // ✅ Add Row to Table
            memberTableModel.addRow(new Object[]{memberId, name, address, phone});
        }
    }
}

