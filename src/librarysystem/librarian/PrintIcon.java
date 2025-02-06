package librarysystem.librarian;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import java.awt.*;
import javax.swing.*;

public class PrintIcon implements Icon {
    private final int size = 16;
    private final Color navyBlue = new Color(0, 0, 128);

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(navyBlue);
        g2d.fillRect(x + 3, y + 4, 10, 7);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + 5, y, 6, 5);

        g2d.setColor(navyBlue);
        g2d.drawLine(x + 6, y + 2, x + 10, y + 2);
        g2d.drawLine(x + 6, y + 4, x + 10, y + 4);

        g2d.setColor(navyBlue);
        g2d.fillRect(x + 4, y + 11, 8, 3);

        g2d.fillRect(x + 2, y + 6, 2, 4);
        g2d.fillRect(x + 12, y + 6, 2, 4);

        g2d.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}


