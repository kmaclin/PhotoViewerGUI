import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class TextRegion extends JComponent implements MouseListener {
    
    private String text = "";
    private ArrayList prevRect = new ArrayList();
    private JLabel t = new JLabel("");
    public int x = -1;
    public int y = -1;
    public int x2;
    public int y2;
    public int width = -1;
    public int height = -1;
    public int widX;
    public int heiY;
    private Graphics g;
    public boolean rubberbanding = true;
    public Color normal = Color.YELLOW;
    public Color select = new Color(220, 20, 60);
    public Color clear = Color.WHITE;
    public boolean selected = false;


    public TextRegion(int x, int y) {

        addMouseListener(this);
        this.x = x;
        this.y = y;
        
    }

    public boolean contains(int x, int y) {
        return true;
    }

    public void drawE(Graphics g, int x1, int y1, int x2, int y2, int widX, int heiY) {
        //this.widX = widX;
        //this.heiY = heiY;

        if (!selected) {
            g.setColor(normal);
        } else {
            g.setColor(select);
        }

        //if (clear)
        
        g.fillRect(x1, y1, width, height);
        repaint();
    }

   public void drawF(Graphics g, int x1, int y1, int x2, int y2, int widX, int heiY) {
       if ((this.x != -1) && (this.y != -1) && (this.width != -1) && (this.height != -1)) {
           g.clearRect(this.x, this.y, width, height);
           repaint();
       }


       this.widX = widX;
       this.heiY = heiY;
       this.x = x1;
       this.y = y1;
       int w = Math.abs(x1 - x2);
       int h = Math.abs(y1 - y2);
       
       this.width = w;
       this.height = h;
       this.x2 = x2;
       this.y2 = y2;
       g.setColor(Color.YELLOW);
       g.fillRect(this.x, this.y, w, h);
       repaint();
   }

   //pbulic void drawN(Graphics g, int x1)

   public void rubberbanding(boolean b) {
       this.rubberbanding = b;

       if (b == false) {
           
           //this.setEditable(true);
           //this.setCaretPosition(0);
       }
   }


   public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }
}