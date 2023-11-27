import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MainFrame extends JFrame  {



    public MainFrame() {
        // Set the title of the frame
        setTitle("Path Visualization");

        // Set the size of the frame
        setSize(800, 600);

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create an instance of the PathPanel class and add it to the frame
        PathPanel pathPanel = new PathPanel();
        add(pathPanel);

        // You can add other components or controls here if needed

        // Set the frame to be visible
        setVisible(true);
    }


    public static void main(String[] args) {
        // Create an instance of the MainFrame class
        new MainFrame();
    }

}
