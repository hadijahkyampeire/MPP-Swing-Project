package librarysystem.tables;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

public class CustomHeaderRenderer extends DefaultTableCellRenderer {
    public CustomHeaderRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(new Font("Arial", Font.BOLD, 14));
        setPreferredSize(new Dimension(100, 40)); // ✅ Increase Header Height
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(new Color(0, 31, 63)); // ✅ Navy Blue Background
        setForeground(Color.WHITE); // ✅ White Text
        setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); // ✅ Make Borders Visible

        // ✅ Force Sort Icons to Always Show
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            RowSorter<? extends TableModel> sorter = table.getRowSorter();
            if (sorter != null) {
                SortOrder sortOrder = getSortOrder(table, column);
                Icon sortIcon = getSortIcon(sortOrder);
                setIcon(sortIcon); // ✅ Set Sort Icon
            }
        }
        return this;
    }

    private SortOrder getSortOrder(JTable table, int column) {
        RowSorter<? extends TableModel> sorter = table.getRowSorter();
        if (sorter == null) return SortOrder.UNSORTED;

        for (RowSorter.SortKey sortKey : sorter.getSortKeys()) {
            if (sortKey.getColumn() == column) {
                return sortKey.getSortOrder();
            }
        }
        return SortOrder.UNSORTED;
    }

    private Icon getSortIcon(SortOrder sortOrder) {
        if (sortOrder == SortOrder.ASCENDING) {
            return UIManager.getIcon("Table.ascendingSortIcon"); // ✅ Default Ascending Icon
        } else if (sortOrder == SortOrder.DESCENDING) {
            return UIManager.getIcon("Table.descendingSortIcon"); // ✅ Default Descending Icon
        }
        return UIManager.getIcon("Table.naturalSortIcon"); // ✅ Default Unsorted Icon
    }

}

