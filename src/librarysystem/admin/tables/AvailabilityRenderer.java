package librarysystem.admin.tables;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AvailabilityRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        if (value instanceof String) {
            String availabilityStatus = (String) value;
            String[] parts = availabilityStatus.split(" ");

            int available = Integer.parseInt(parts[0]);

            setToolTipText(availabilityStatus);

            if (available > 0) {
                setForeground(new Color(0, 128, 0)); // Green
                setText("✔ Available");
            } else {
                setForeground(Color.RED);
                setText("✖ Checked out");
            }

            setFont(getFont().deriveFont(Font.BOLD));
        }
    }
}

