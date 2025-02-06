package librarysystem.SuperAdmin;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class AddCopyButtonRenderer extends DefaultTableCellRenderer {
    private JButton button;

    public AddCopyButtonRenderer() {
        button = new JButton("Add New Copy");
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setFocusPainted(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return button;
    }
}



