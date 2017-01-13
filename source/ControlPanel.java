import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private StatusBar statusUpdate = new StatusBar(this.getWidth());
    private MainMenu mainMenuBar;
    private LeftMenu leftMenu;
    private MainPanel mainPanel;
    private JScrollPane scrollpane;

    public ControlPanel() {
        //set up control panel
        super();
        setOpaque(true);
        setBackground(new Color(0, 0, 0));
        setLayout(new BorderLayout());

        //Create a menu bar with File and View
        this.mainMenuBar = new MainMenu(this);
        add(this.mainMenuBar, BorderLayout.NORTH);

        //create left menu panel
        this.leftMenu = new LeftMenu(this);
        add(this.leftMenu, BorderLayout.WEST);

        //create main panel (right)
        //Puts main panel in scroll panel
        this.mainPanel = new MainPanel(this);
        scrollpane = new JScrollPane(this.mainPanel);
        add(scrollpane, BorderLayout.CENTER);
        //add(this.mainPanel, BorderLayout.CENTER);

        //create panel for status bar
        add(this.statusUpdate, BorderLayout.SOUTH);
    }

    public void updateStatusBar(String message) {
        statusUpdate.setMessage(message);
        statusUpdate.repaint();
    }

    public MainPanel getMainPanel() {
        return this.mainPanel;
    }

    public MainMenu getMainMenu() {
        return mainMenuBar;
    }

    public LeftMenu getLeftMenu() {
        return leftMenu;
    }

}