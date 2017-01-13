import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.Dimension;


public class MainPanel extends JPanel implements StatusUpdater {

    private String statusText = "";
    public LightTable table = null;
    private Dimension min;
    public ControlPanel control = null;

    public MainPanel(ControlPanel control) {
        super();
        setBackground(new Color(22, 22, 22));
        setLayout(new BorderLayout());

        this.control = control;
    }

    public String updateStatusText() {
        return this.statusText;
    }

    public void addPhoto(PhotoComponent photo) {


        if (photo != null) {
            this.control.getMainMenu().deleteItem.setEnabled(true);
            this.control.getMainMenu().photoViewItem.setEnabled(true);
            this.control.getMainMenu().gridViewItem.setEnabled(true);
            this.control.getMainMenu().splitViewItem.setEnabled(true);

            if (table == null) {
                table = new LightTable(photo, this);
                //table.addPhoto(photo);
                this.add(table, BorderLayout.CENTER);
            } else {
                table.addPhoto(photo);
            }

            photo.table = this.table;
            this.revalidate();
            this.repaint();

            
        }
        
    }

    public PhotoComponent getShown() {
        if (table != null) {
            return table.getShownPhoto();
        }
        return null;
    }

    public void deletePhoto() {

        if (table.getLength() > 0) {
            table.deletePhoto(table.getShownPhoto());
        } else {
            this.statusText = "There are no more photos listed.";
            this.control.getMainMenu().deleteItem.setEnabled(true);
            this.control.getMainMenu().deleteItem.revalidate();
            this.control.getMainMenu().deleteItem.repaint();
        }
        revalidate();
        repaint();
    }


    public void setMinSize(Dimension min) {
        this.min = min;
        setMinimumSize(this.min);
    }
}