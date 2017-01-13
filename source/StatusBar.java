
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class StatusBar extends JPanel {

    //initial message
    private JLabel message = new JLabel("Ready");

    //sets up status bar attributes
    //sets initial message "Ready"
    public StatusBar(int width) {
        super();
        setPreferredSize(new Dimension(width, 20));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        this.message.setHorizontalAlignment(SwingConstants.LEFT);
        add(this.message);
    }

    //updates ready text
    public void setMessage(String status) {
        this.message.setText(status);
        repaint();
    }

}