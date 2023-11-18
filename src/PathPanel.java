import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

public class PathPanel extends JPanel {

    // You can add variables and methods related to path visualization here

    @Override
    protected void paintComponent(Graphics g) {

        final int ROWS = 10;
        final int COLS = 10;
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        // Calculate the width and height of each cell in the grid
        int cellWidth = width / COLS;
        int cellHeight = height / ROWS;

        // Draw light grey grid lines
        g.setColor(Color.LIGHT_GRAY);

        // Draw horizontal lines
        for (int row = 0; row < ROWS; row++) {
            int y = row * cellHeight;
            g.drawLine(0, y, width, y);
        }

        // Draw vertical lines
        for (int col = 0; col < COLS; col++) {
            int x = col * cellWidth;
            g.drawLine(x, 0, x, height);
        }

        // Example: Drawing a red rectangle in the grid cell (2, 2)
        g.setColor(Color.RED);
        int cellX = 2 * cellWidth;
        int cellY = 2 * cellHeight;
        g.fillRect(cellX, cellY, cellWidth, cellHeight);
    }
}
