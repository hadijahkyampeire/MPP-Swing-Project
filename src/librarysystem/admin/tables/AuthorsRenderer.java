package librarysystem.admin.tables;

import javax.swing.table.DefaultTableCellRenderer;

public class AuthorsRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        if (value instanceof String) {
            String authors = (String) value;

            setToolTipText(authors);
            setText(authors);
        } else {
            setText("No Authors");
        }
    }
}

