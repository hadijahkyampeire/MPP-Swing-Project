package librarysystem.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class AuthorsRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        if (value instanceof String) {
            String authors = (String) value; // ✅ String data from table

            setToolTipText(authors); // ✅ Tooltip shows full author list
            setText(authors); // ✅ Display first author in table
        } else {
            setText("No Authors"); // ✅ Fallback if empty
        }
    }
}

