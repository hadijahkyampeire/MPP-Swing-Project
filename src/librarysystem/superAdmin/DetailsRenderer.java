package librarysystem.SuperAdmin;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class DetailsRenderer {
    TableCellRenderer iconRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            if (value instanceof Icon) {
                setIcon((Icon) value);
                setText(null);
            } else {
                setIcon(null);
                if (value != null) {
                    setText(value.toString());
                }
            }
            return this;
        }
    };
}
