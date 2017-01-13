import javax.swing.*;
import java.awt.Dimension;
import java.awt.BorderLayout;

public class GuiInterface {


    private static void createGUI() {
        //create a new window
        JFrame frame = new JFrame("Photo Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setMinimumSize(new Dimension(800, 600));

        //create main panel for all other panels
        ControlPanel controlPanel = new ControlPanel();
        frame.add(controlPanel);

        //Show window
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            //run GUI
            public void run() {
                createGUI();
            }
        });
    }
}



    