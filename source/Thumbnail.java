import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.*;
import java.awt.image.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;

class Thumbnail extends JComponent {

    private PhotoComponent photo = null;
    public int width = 0;
    public int height = 0;
    private double scaledHeight = 0;
    public int x;
    public int y;
    private int h = 200;
    public Graphics g;
    private Color blueish = new Color(77, 127, 209);
    private Border border = BorderFactory.createLineBorder(blueish, 10);
    private boolean paintB = false;


    public Thumbnail(PhotoComponent photo) {
        this.photo = photo;

        scaledHeight = (double)h/photo.getImageHeight();
        width = photo.getImageWidth();
        double value = (double) width * (double) scaledHeight;
        this.width = (int) value + 1;
        this.height = h;

        Dimension d = new Dimension(this.width, h);
        //Dimension q = new Dimension(this.width, this.height);
        setPreferredSize(d);
        setMinimumSize(d);
    }

    public void setPaintB(boolean f) {
        paintB = f;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        this.height = h;

        scaledHeight = (double)h/photo.getImageHeight();

        int width = photo.getImageWidth();
        double value = (double) width * (double) scaledHeight;
        this.width = (int) value + 1;

        Dimension d = new Dimension(this.width, h);
        //Dimension q = new Dimension(this.width, this.height);
        setPreferredSize(d);
        setMinimumSize(d);

        g2.scale(scaledHeight, scaledHeight);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(this.photo.getImage(), 0, 0, null);

        this.g = g2;

        if (this.paintB) {
            border.paintBorder(this, g2, 0, 0, photo.getImageWidth(), photo.getImageHeight());
        }
        
        revalidate();
        repaint();
    }

    public void changeHeight(int h) {
        this.h = h;
        this.height = h;

        scaledHeight = (double)h/photo.getImageHeight();
        int width = photo.getImageWidth();
        double value = (double) width * (double) scaledHeight;
        this.width = (int) value + 1;

        Dimension d = new Dimension(this.width, h);
       // Dimension q = new Dimension(this.width, this.height);
        setPreferredSize(d);
        setMinimumSize(d);

        repaint();
        revalidate();
    }

    public int getHscale() {
        return this.h;
    }

    public boolean containsF(int x, int y) {
        this.x = this.getX();
        this.y = this.getY();

        if ((x >= this.x) && (x <= this.x + this.width)) {
            if ((y >= this.y) && (y <= this.y + this.height)) {
                return true;
                
            }
        }

        return false;
    }

    public static BufferedImage realCopy(BufferedImage photo) {
        ColorModel cm = photo.getColorModel();
        boolean alpha = cm.isAlphaPremultiplied();
        WritableRaster raster = photo.copyData(photo.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, alpha, null);
    }

}