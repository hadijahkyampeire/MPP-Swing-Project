package librarysystem.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AvailabilityRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        if (value instanceof String) {
            String availabilityStatus = (String) value;
            String[] parts = availabilityStatus.split(" "); // "2 available, 3 unavailable"

            int available = Integer.parseInt(parts[0]); // ✅ Extract available count
            int unavailable = Integer.parseInt(parts[2]); // ✅ Extract unavailable count

            setToolTipText(availabilityStatus); // ✅ Tooltip shows "2 available, 3 unavailable"

            if (available > 0) {
                setForeground(new Color(0, 128, 0)); // ✅ Green for Available
                setText("✔ Available"); // ✅ Badge text
            } else {
                setForeground(Color.RED); // ✅ Red for Unavailable
                setText("✖ Unavailable"); // ✅ Badge text
            }

            setFont(getFont().deriveFont(Font.BOLD)); // ✅ Make it bold
        }
    }
}

