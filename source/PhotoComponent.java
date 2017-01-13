import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.Line2D;
import java.util.regex.*;

class PhotoComponent extends JComponent implements MouseListener, MouseMotionListener, KeyListener {

    //Directions
    private enum Dir {
        N("N"), S("S"), E("E"), W("W"), NE("B"), 
        NW("A"), SE("C"), SW("D"), GN("G. N"), 
        GS("G. S"), GE("G. E"), GW("G. W");

        private final String text;

        private Dir(String textReps) {
            this.text = textReps;
        }

        public String toString() {
            return text;
        }
    }

    //Templates
    private String nextTemp = ".{0,2}+[CES]+[DWS]+.{0,2}+";
    private String prevTemp = ".{0,2}+[DWS]+[CES]+.{0,2}+";
    private String family = ".{0,2}+[BEN]+[CES]+.{0,2}+";
    private String friends = ".{0,2}+[CES]+[BEN]+.{0,2}+";
    //String school = ".{0,2}+[NABW]+[EBCS]+[SCD]+.{0,2}+";
    private String school = ".{0,2}+[WABNSC]+[DWS]+[SCD]+[EBCS]+[CES]+[SCD]+[DWS]+[WABN]+.{0,2}+";
    private String work = ".{0,2}+[CES]+[BEN]+[CES]+[BEN]+.{0,2}+";
    private String delete = ".{0,2}+[SCD]+[DWS]+[WAB]+[AWN]+[NAB]+[BEN]+[EBC]+[CES]+[SCD]+.{0,2}+";
    private String selection = ".{0,2}+[NAB]+[BEN]+[EBC]+[CES]+[SCD]+[DWS]+[WAB]+[AWN]+.{0,2}+";

    private String[] temps = {nextTemp, prevTemp, family, friends, school, work, delete, selection};

    public String[] tags = new String[4];

    
    private BufferedImage image = null;
    private boolean flipped = false;
    private boolean textEnabled = false;

    private JPanel mainPanel;
    private int width;
    private int height;
    private int realWidth;
    private int realHeight;
    private int x;
    private int y;
    private Graphics g;

    private ArrayList<TextRegion> regions = new ArrayList<TextRegion>();
    private TextRegion newReg = null;
    private ArrayList displayListX = new ArrayList();
    private ArrayList displayListY = new ArrayList();
    private ArrayList origX = new ArrayList();
    private ArrayList origY = new ArrayList();

    //For gestures
    private ArrayList gestureX = new ArrayList();
    private ArrayList gestureY = new ArrayList();
    private ArrayList origXgest = new ArrayList();
    private ArrayList origYgest = new ArrayList();

    //Bounding box for selecting with circle
    private boolean selectionMode = false;
    private Point bbUL = null;
    private Point bbUR = null;
    private Point bbBL = null;
    private Point bbBR = null;
    private int bbHeight = 0;
    private int bbWidth = 0;

    //Record of selected items
    private ArrayList<Point> selectedPoints = new ArrayList<Point>();
    private ArrayList<Point> selectedOrigXY = new ArrayList<Point>();
    private ArrayList indexes = new ArrayList();
    private ArrayList indexes2 = new ArrayList();
    private ArrayList<TextRegion> selectedRegions = new ArrayList<TextRegion>();
    private boolean selectForDelete = false;
    private boolean move = false;
    private Point prevMouseLocation = null;
    private Point mouseLocation = null;

    private int xfirst;
    private int yfirst;
    private int first = 0;

    private boolean pressed = false;
    private Point endpt = null;
    private boolean rec = false;
    private boolean rightClick = false;

    private String last = "Image";
    private String current = "Flipped";
    private JLabel t = new JLabel("High");
    private Thumbnail pic = null;

    public LightTable table = null;


    //Creates photo component
    public PhotoComponent(BufferedImage img) {  
        super();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();
        add(t);

        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        //setSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
        image = img;

        this.width = image.getWidth(null);
        this.height = image.getHeight(null);

        this.x = (this.getWidth() - this.width) / 2;
        this.y = (this.getHeight() - this.height) / 2;

        this.realHeight = image.getHeight();
        this.realWidth = image.getWidth();

        pic = new Thumbnail(this);
    }

    //If flipped, paints displaylist of strokes 
        //if also text enabled, paints text boxes
    //if not flipped, paints photo

    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        if (!flipped) {
            this.g = g;
            if (image != null) {
                int valx = (this.getWidth() - this.width) / 2;
                int valy = (this.getHeight() - this.height) / 2;

                //keep track of change in x as photocomponent changes position
                //to be centered in panel
                this.x = valx;
                this.y = valy;

                //does the same as above
                if (first == 0) {
                    xfirst = this.x;
                    yfirst = this.y;
                    first = 1;
                }

                this.g.drawImage(image, this.x, this.y, null);
                this.revalidate();
                this.repaint();
            }
        } else if (flipped) {
            this.g = g;
            drawingMode();
            
            //goes through and paints/repaints strokes drawn
            if ((displayListX.size() > 1) && (displayListY.size() > 1)) {
                for (int i = 0; i + 1 < displayListX.size(); i = i + 1) {

                    int x1 = (int)displayListX.get(i);
                    int y1 = (int)displayListY.get(i);
                    int oX1 = (int)origX.get(i);
                    int oY1 = (int)origY.get(i);


                    int x2 = (int)displayListX.get(i + 1);
                    int y2 = (int)displayListY.get(i + 1);
                    int oX2 = (int)origX.get(i + 1);
                    int oY2 = (int)origY.get(i + 1);

                    //System.out.println(selectionMode);
                    if (selectionMode && (containsBB(bbUL, bbWidth, bbHeight, x1, y1) && containsBB(bbUL, bbWidth, bbHeight, x2, y2))) {
                        
                        this.g.setColor(new Color(220, 20, 60));
                        //System.out.println("changed to red");
                        Point newP1 = new Point(x1, y1);
                        Point newP2 = new Point(x2, y2);

                        if (!selectedPoints.contains(newP1) || ((x1 == -1) && (y1 == -1))) {
                            selectedPoints.add(newP1);
                            if (x1 == -1 && y1 == -1) {
                                selectedOrigXY.add(new Point(-1, -1));
                            } else {
                                selectedOrigXY.add(new Point(this.x, this.y));
                            }
                        }

                        if (!indexes.contains(i)) {
                            indexes.add(i);
                        }

                        if (!selectedPoints.contains(newP2) || ((x2 == -1) && (y2 == -1))) {
                            selectedPoints.add(newP2);
                            if (x2 == -1 && y2 == -1) {
                                selectedOrigXY.add(new Point(-1, -1));
                            } else {
                                selectedOrigXY.add(new Point(this.x, this.y));
                            }
                            
                        }

                        if (!indexes.contains(i + 1)) {
                            indexes.add(i + 1);
                        }
                    } else {
                        this.g.setColor(new Color(53, 99, 173));
                        //System.out.println("Normal color");
                    }


                    if ((x1 != -1) && (x2 != -1) && (y1 != -1) && (y2 != -1) 
                            && (oX1 != -1) && (oY1 != -1) && (oX2 != -1) && (oY2 != -1)) {

                        //Points that do not have to be recentered
                        if (((this.x - oX1) == 0) && ((this.y - oY1) == 0)) {
                            this.g.drawLine(x1, y1, x2, y2);
                            first++;
                        } else {


                            //As component re-centers; points follow suit
                            this.g.drawLine(x1 + (this.x - oX1), y1 + (this.y - oY1), 
                            x2 + (this.x - oX2), y2 + (this.y - oY2));

                            this.revalidate();
                            this.repaint();

                        }
                    } 
                }
            }

            if (selectForDelete) {

                for (int n = 0; n < indexes.size(); n++) {
                    
                    displayListX.set((int)indexes.get(n), -1);
                    displayListY.set((int)indexes.get(n), -1);
                    origX.set((int)indexes.get(n), -1);
                    origY.set((int)indexes.get(n), -1);
                    
                }

                if (!move) {
                    selectedPoints.removeAll(selectedPoints);
                    selectedOrigXY.removeAll(selectedOrigXY);
                }
                
                indexes.removeAll(indexes);
            }

            //For points
            if (move) {
                if ((mouseLocation != null) && (prevMouseLocation != null)) {
                    for (int n = 0; n + 1 < selectedPoints.size(); n++) {

                        int x1 = (int)selectedPoints.get(n).x;
                        int y1 = (int)selectedPoints.get(n).y;

                        int x2 = (int)selectedPoints.get(n + 1).x;
                        int y2 = (int)selectedPoints.get(n + 1).y;

                        int dx = mouseLocation.x - prevMouseLocation.x;
                        int dy = mouseLocation.y - prevMouseLocation.y;


                        if ((x1 != -1) && (x2 != -1) && (y1 != -1) && (y2 != -1)) {
                            Point newP1 = new Point(x1 + dx, y1 + dy);
                            Point newP2 = new Point(x2 + dx, y2 + dy);

                            Point newOrig1 = new Point(this.x, this.y);
                            Point newOrig2 = new Point(this.x, this.y);

                            selectedPoints.set(n, newP1);
                            selectedOrigXY.set(n, newOrig1);
                            

                            if (n + 2 >= selectedPoints.size()) {
                                selectedPoints.set(n + 1, newP2);
                                selectedOrigXY.set(n + 1, newOrig2);
                            } else {
                                if ((int)selectedPoints.get(n + 2).x == -1) {
                                    selectedPoints.set(n + 1, newP2);
                                    selectedOrigXY.set(n + 1, newOrig2);
                                    //System.out.println(true);
                                }
                            }

                            if ((Math.abs(x2 - x1) > 10) && (Math.abs(y2 - y1) > 10)) {
                                //System.out.print("inside");
                                continue;
                            }
                            if ((x1 != -1) && (x2 != -1) && (y1 != -1) && (y2 != -1)) {
                                this.g.setColor(Color.WHITE);
                                this.g.drawLine(x1, y1, x2, y2);

                                this.g.setColor(new Color(220, 20, 60));
                                this.g.drawLine(newP1.x, newP1.y, newP2.x, newP2.y);
                            }

                            
                        }
                        
                    }

                    prevMouseLocation = mouseLocation;
                }

                
            }

                

            //Redrawing the textRegions
            if (regions.size() > 0) {

                for (int n = 0; n < regions.size(); n++) {
                    TextRegion reg = regions.get(n);
                    if (((this.x - reg.widX) == 0) && ((this.y - reg.heiY) == 0)) {
                        if (selectionMode && containsBB(bbUL, bbWidth, bbHeight, reg.x + reg.width/2, reg.y + reg.height/2)) {
                            reg.selected = true;
                            if (!selectedRegions.contains(reg)) {
                                selectedRegions.add(reg);

                                if (!indexes2.contains(n)) {
                                    indexes2.add(n);
                                }
                            }
                        }
                        reg.drawE(this.g, reg.x, reg.y, reg.x2, reg.y2, reg.widX, reg.heiY);
                    } else {
                         reg.drawE(this.g, reg.x + (this.x - reg.widX), reg.y + (this.y - reg.heiY), 
                        reg.x2 + (this.x - reg.widX), reg.y2 + (this.y - reg.heiY), reg.widX, reg.heiY);
                    }
                   
                }
            }

            if (move) {
                if ((mouseLocation != null) && (prevMouseLocation != null)) {
                    int dx = mouseLocation.x - prevMouseLocation.x;
                    int dy = mouseLocation.y - prevMouseLocation.y;

                    for (int n = 0; n < selectedRegions.size(); n++) {
                        TextRegion t = selectedRegions.get(n);

                        //this.g.clearRect(t.x, t.y, t.width, t.height);
                        t.x = t.x + dx;
                        t.y = t.y + dy;
                        t.x2 = t.x2 + dx;
                        t.y2 = t.y2 + dy;

                        if (((this.x - t.widX) == 0) && ((this.y - t.heiY) == 0)) {
                            
                            t.drawE(this.g, t.x, t.y, t.x2, t.y2, t.widX, t.heiY);
                        } else {
                            t.drawE(this.g, t.x + (this.x - t.widX), t.y + (this.y - t.heiY), 
                            t.x2 + (this.x - t.widX), t.y2 + (this.y - t.heiY), t.widX, t.heiY);
                        }


                    }
                }
            }

            if (selectForDelete) {

                for (int n = 0; n < selectedRegions.size(); n++) {
                    
                    regions.remove(selectedRegions.get(n));
                    
                }

                if (!move) {
                    selectForDelete = false;
                    selectionMode = false;
                }
                
                indexes2.removeAll(indexes);
                repaint();
            }

            //drawing a TextRegion
            if (textEnabled) {
                if (this.rec) {
                    if ((newReg != null) && (endpt != null)) {
                        if (contains(this.endpt.x, this.endpt.y)) {
                            if ((this.endpt.x >= newReg.x) && (this.endpt.y >= newReg.y)) {

                                newReg.drawF(this.g, newReg.x, newReg.y, endpt.x, endpt.y, this.x, this.y);

                                this.revalidate();
                                this.repaint();
                            } 
                        }
                    }             
                }
                
            }

            //selectionMode = false;
            
            
            
            this.revalidate();
            this.repaint();
        }

        //Paint Gestures

        if ((gestureX.size() > 1) && (gestureY.size() > 1)) {
            for (int i = 0; i + 1 < gestureX.size(); i = i + 1) {
                this.g.setColor(new Color(153, 153, 153));
                Graphics2D g2 = (Graphics2D) this.g;
                g2.setStroke(new BasicStroke(3));

                int x1 = (int)gestureX.get(i);
                int y1 = (int)gestureY.get(i);
                int oX1 = (int)origXgest.get(i);
                int oY1 = (int)origYgest.get(i);


                int x2 = (int)gestureX.get(i + 1);
                int y2 = (int)gestureY.get(i + 1);
                int oX2 = (int)origXgest.get(i + 1);
                int oY2 = (int)origYgest.get(i + 1);



                if ((x1 != -1) && (x2 != -1) && (y1 != -1) && (y2 != -1) 
                        && (oX1 != -1) && (oY1 != -1) && (oX2 != -1) && (oY2 != -1)) {

                    //Points taht do not have to be recentered
                    if (((this.x - oX1) == 0) && ((this.y - oY1) == 0)) {
                        g2.drawLine(x1, y1, x2, y2);
                        first++;
                    } else {


                        //As component re-centers; points follow suit
                        g2.drawLine(x1 + (this.x - oX1), y1 + (this.y - oY1), 
                        x2 + (this.x - oX2), y2 + (this.y - oY2));

                        this.revalidate();
                        this.repaint();

                    }
                } 
            }
        }
        //}

        this.revalidate();
        this.repaint();
    }

    public void drawingMode() {
        int valx = (this.getWidth() - this.width) / 2;
        int valy = (this.getHeight() - this.height) / 2;


        this.x = valx;
        this.y = valy;

        //adds antialiasing
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        this.g = g2;
        this.g.clearRect(this.x, this.y, this.width, this.height);
    }

    /*
    * Takes in two arrayLists of the x and y points and creates a direction vector
    * of 8 ordinal direction constants. This represents the true shape of the gesture.
    * Returns: String
    */
    private String dirVec(ArrayList gestureX, ArrayList gestureY) {

        ArrayList<Dir> direction = new ArrayList<Dir>();
        String dir = "";

        for (int n = 0; n + 1 < gestureX.size(); n++) {

            int x1 = (int) gestureX.get(n);
            int y1 = (int) gestureY.get(n);

            int x2 = (int) gestureX.get(n + 1);
            int y2 = (int) gestureY.get(n + 1);

            int dx = x2 - x1;
            int dy = y2 - y2;

            double angle = (double) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
            
            if (angle < 0) {
                angle = angle + 360;
            }

            if ((angle == 0) || (angle >= 337.5) || (angle < 22.5)) {
                direction.add(Dir.E);
                dir = dir + Dir.E.toString();
            } else if ((angle == 315) || ((angle < 337.5) && (angle >= 292.5))) {
                direction.add(Dir.NE);
                dir = dir + Dir.NE.toString();
            } else if ((angle == 270) || ((angle < 292.5) && (angle >= 247.5))) {
                direction.add(Dir.N);
                dir = dir + Dir.N.toString();
            } else if ((angle == 225) || ((angle < 247.5) && (angle >= 202.5))) {
                direction.add(Dir.NW);
                dir = dir + Dir.NW.toString();
            } else if ((angle == 180) || ((angle < 202.5) && (angle >= 157.5))) {
                direction.add(Dir.W);
                dir = dir + Dir.W.toString();
            } else if ((angle == 135) || ((angle < 157.5) && (angle >= 112.5))) {
                direction.add(Dir.SW);
                dir = dir + Dir.SW.toString();
            } else if ((angle == 90) || ((angle < 112.5) && (angle >= 67.5))) {
                direction.add(Dir.S);
                dir = dir + Dir.S.toString();
            } else if ((angle == 45) || ((angle < 67.5) && (angle >= 22.5))) {
                direction.add(Dir.SE);
                dir = dir + Dir.SE.toString();
            }


        }

        if (dir.length() >= 20) {
            dir = dir.substring(3, dir.length() - 3);
        }
        

        //System.out.println(direction);
        //System.out.println(dir);
        return dir;
    }

    /*
    * Matches the direction String to the regular expression template of the gesture.
    */
    private boolean matchCharTemp(String dirV) {
        boolean matched = false;

        for (int n = 0; n < temps.length; n++) {
            String t = temps[n];
            Pattern p = Pattern.compile(t);

            if (p.matcher(dirV).matches()) {
                //System.out.println(p);

                if (p.toString() == nextTemp) {
                    table.nextPhoto();
                    table.main.control.updateStatusBar("Next Page");
                } else if (p.toString() == prevTemp) {
                    table.prevPhoto();
                    table.main.control.updateStatusBar("Previous Page");
                } else if (p.toString() == delete) {
                    if (!flipped) {
                        table.main.deletePhoto();
                    } else {
                        selectForDelete = true;
                        repaint();
                    }
                    
                    table.main.control.updateStatusBar("Delete selected.");
                } else if (p.toString() == family) {
                    if (tags[0] == null) {
                        tags[0] = family;
                        table.main.control.getLeftMenu().family.setSelected(true);
                        table.main.control.updateStatusBar("Tagged as family");
                    } else {
                        tags[0] = null;
                        table.main.control.getLeftMenu().family.setSelected(false);
                        table.main.control.updateStatusBar("Untagged as family");
                    }
                } else if (p.toString() == friends) {
                    if (tags[1] == null) {
                        tags[1] = friends;
                        table.main.control.getLeftMenu().friends.setSelected(true);
                        table.main.control.updateStatusBar("Tagged as friends");
                    } else {
                        tags[1] = null;
                        table.main.control.getLeftMenu().friends.setSelected(false);
                        table.main.control.updateStatusBar("Untagged as friends");
                    }
                } else if (p.toString() == school) {
                    if (tags[2] == null) {
                        tags[2] = school;
                        table.main.control.getLeftMenu().school.setSelected(true);
                        table.main.control.updateStatusBar("Tagged as school");
                    } else {
                        tags[2] = null;
                        table.main.control.getLeftMenu().school.setSelected(false);
                        table.main.control.updateStatusBar("Untagged as school");
                    }
                } else if (p.toString() == work) {
                    if (tags[3] == null) {
                        tags[3] = work;
                        table.main.control.getLeftMenu().work.setSelected(true);
                        table.main.control.updateStatusBar("Tagged as work");
                    } else {
                        tags[3] = null;
                        table.main.control.getLeftMenu().work.setSelected(false);
                        table.main.control.updateStatusBar("Untagged as work");
                    }
                } else if (p.toString() == selection) {
                    //System.out.println("select");
                    selectionMode = true;
                    table.main.control.updateStatusBar("Selected Region.");
                }

                matched = true;
                break;
            }

        }

        if (!matched) {
            table.main.control.updateStatusBar("Unrecognized gesture");
        }

        return matched;
    }

    /*
    * Function to update left tags when a new photo is switched to
    */
    public void updateTagInfo() {
        //Removes all tags from the previous photo
        JRadioButton fam = table.main.control.getLeftMenu().family;
        JRadioButton fr = table.main.control.getLeftMenu().friends;
        JRadioButton sch = table.main.control.getLeftMenu().school;
        JRadioButton w = table.main.control.getLeftMenu().work;

        for (int i = 0; i < tags.length; i++) {
            if ((tags[0] != null) && (!fam.isSelected())) {
                //table.main.control.getLeftMenu().family.doClick();
                table.main.control.getLeftMenu().family.setSelected(true);
            } else if ((tags[0] == null) && (fam.isSelected())) {
                //table.main.control.getLeftMenu().family.doClick();
                table.main.control.getLeftMenu().family.setSelected(false);
            }
            if ((tags[1] != null) && (!fr.isSelected())) {
                //table.main.control.getLeftMenu().friends.doClick();
                table.main.control.getLeftMenu().friends.setSelected(true);
            } else if ((tags[1] == null) && (fr.isSelected())) {
                //table.main.control.getLeftMenu().friends.doClick();
                table.main.control.getLeftMenu().friends.setSelected(false);
            }
            if ((tags[2] != null) && (!sch.isSelected())) {
                //table.main.control.getLeftMenu().school.doClick();
                table.main.control.getLeftMenu().school.setSelected(true);
            } else if ((tags[2] == null) && (sch.isSelected())) {
                //table.main.control.getLeftMenu().school.doClick();
                table.main.control.getLeftMenu().school.setSelected(false);
            }
            if ((tags[3] != null) && (!w.isSelected())) {
                //table.main.control.getLeftMenu().work.doClick();
                table.main.control.getLeftMenu().work.setSelected(true);
            } else if ((tags[3] == null) && (w.isSelected())) {
                //table.main.control.getLeftMenu().work.doClick();
                table.main.control.getLeftMenu().work.setSelected(false);
            }
        }

        table.main.control.getLeftMenu().repaint();
    }


    /*
    * Checks if points are inside component
    */
    public boolean contains(int x, int y) {

        if ((x >= this.x) && (x <= this.x + this.width)) {
            if ((y >= this.y) && (y <= this.y + this.height)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsBB(Point upperLeft, int bbWidth, int bbHeight, int x, int y) {
        int xUL = upperLeft.x;
        int yUL = upperLeft.y;

        if ((x == -1) && (y == -1)) {
            return true;
        }          

        if ((x >= xUL) && (x <= xUL + bbWidth)) {
            if ((y >= yUL) && (y <= yUL + bbHeight)) {
                //System.out.println(true);
                return true;
            }
        }
        return false;
    }

    public void setFlipped(boolean b) {
        flipped = b;
    }

    public boolean getFlipped() {
        return flipped;
    }


    public void setTextEnabled(boolean b) {
        textEnabled = b;
    }

    public boolean getTextSetting() {
        return textEnabled;
    }

    public Image getImage() {
        return this.image;
    }

    public int getImageHeight() {
        return this.image.getHeight();
    }

    public int getImageWidth() {
        return this.image.getWidth();
    }

    public Thumbnail getThumbnail() {
        return pic;
    }

    // public void getRealHeight() {
    //     return realHeight;
    // }

    // public void getRealWidth() {
    //     return realWidth;
    // }

    public void mouseClicked(MouseEvent e) {

        if (SwingUtilities.isLeftMouseButton(e)) {

            if (e.getClickCount() == 2) {
                if (this.flipped == false) {
                    this.flipped = true;
                    //repaint();
                } else {
                    this.flipped = false;
                    //repaint();
                }
            }
        }
        
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        pressed = true;
        // initial = e.getPoint();
        // last = initial;
        if ((!rightClick && SwingUtilities.isLeftMouseButton(e)) || 
        (!rightClick && SwingUtilities.isLeftMouseButton(e) && selectionMode)) {
            if (selectionMode) {
                move = true;
                selectForDelete = true;

                prevMouseLocation = e.getPoint();
            } else if (textEnabled) {
                this.rec = true;
                if (contains(e.getX(), e.getY())) {
                    newReg = new TextRegion(e.getX(), e.getY());
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightClick = true;
        }
        
    }

    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        

        int xPt = p.x;
        int yPt = p.y;

        if (!pressed) {
            pressed = true;
        }

        if (rightClick || SwingUtilities.isRightMouseButton(e)) {

            int[] list = new int[2];

            if (contains(xPt, yPt)) {
                list[0] = xPt;
                list[1] = yPt;

                this.gestureX.add(xPt);
                this.gestureY.add(yPt);
                this.origXgest.add(this.x);
                this.origYgest.add(this.y);
                
                repaint();
            }
        } else if (!rightClick && SwingUtilities.isLeftMouseButton(e)) {

            if (flipped) {
                if (!move) {
                    if (!textEnabled) {
                        int[] list = new int[2];

                        if (contains(xPt, yPt)) {
                            list[0] = xPt;
                            list[1] = yPt;

                            this.displayListX.add(xPt);
                            this.displayListY.add(yPt);
                            this.origX.add(this.x);
                            this.origY.add(this.y);
                            
                            repaint();
                        }
                    } else {
                        this.endpt = p;
                        repaint();
                    } 
                } else {
                    //Moving code
                    if (contains(e.getPoint().x + bbWidth, e.getPoint().y + bbHeight)) {
                        mouseLocation = p;
                    }
                    
                    repaint();
                }
                   
                
            }
        }
        
    }

    public void mouseReleased(MouseEvent e) {
        Point end = e.getPoint();
        if (pressed == true) {

            if (!rightClick && SwingUtilities.isLeftMouseButton(e)) {
                if (!move) {
                    this.displayListX.add(-1);
                    this.displayListY.add(-1);
                    this.origX.add(-1);
                    this.origY.add(-1);

                    if (textEnabled) {
                        if (newReg != null) {
                            if (this.rec == true) {
                                this.rec = false;
                                newReg.rubberbanding(false);
                                regions.add(newReg);
                                this.g.clearRect(newReg.x, newReg.y, newReg.width, newReg.height);
                                newReg = null;
                                endpt = null;
                                repaint();
                            }
                        }
                        
                    }
                } else {
                    move = false;
                    selectForDelete = false;
                    selectionMode = false;

                    this.displayListX.add(-1);
                    this.displayListY.add(-1);
                    this.origX.add(-1);
                    this.origY.add(-1);

                    for (int i = 0; i < selectedPoints.size(); i++) {
                        displayListX.add((int)selectedPoints.get(i).x);
                        displayListY.add((int)selectedPoints.get(i).y);
                        origX.add((int)selectedOrigXY.get(i).x);
                        origY.add((int)selectedOrigXY.get(i).y);

                        if ((i + 1) >= selectedPoints.size()) {
                            this.displayListX.add(-1);
                            this.displayListY.add(-1);
                            this.origX.add(-1);
                            this.origY.add(-1);
                        }

                    }

                    
                    selectedPoints.removeAll(selectedPoints);
                    selectedOrigXY.removeAll(selectedOrigXY);
                    repaint();

                }
                

            } else {
                rightClick = false;

                //Do recognizing stuff
                String dir = dirVec(gestureX, gestureY);
                boolean match = matchCharTemp(dir);

                if (match) {
                    if (selectionMode) {


                        if ((gestureX.size() > 1) && (gestureY.size() > 1)) {
                            int topX = -1;
                            int topY = -1;
                            int rightX = -1;
                            int rightY = -1;
                            int bottomX = -1;
                            int bottomY = -1;
                            int leftX = -1;
                            int leftY = -1;

                            for (int i = 0; i < gestureX.size(); i = i + 1) {
                                int x1 = (int)gestureX.get(i);
                                int y1 = (int)gestureY.get(i);

                                if (((topX == -1) && (topY == -1)) && ((rightX == -1) && (rightY == -1)) &&
                                ((bottomX == -1) && (bottomY == -1)) && ((leftX == -1) && (leftY == -1))) {
                                    topX = x1;
                                    topY = y1;
                                    rightX = x1;
                                    rightY = y1;
                                    bottomX = x1;
                                    bottomY = y1;
                                    leftX = x1;
                                    leftY = y1;
                                } else {
                                    if (y1 < topY) {
                                        topX = x1;
                                        topY = y1;
                                    }
                                    if (x1 > rightX) {
                                        rightX = x1;
                                        rightY = y1;
                                    }
                                    if (y1 > bottomY) {
                                        bottomX = x1;
                                        bottomY = y1;
                                    }
                                    if (x1 < leftX) {
                                        leftX = x1;
                                        leftY = y1;
                                    }
                                }
                            
                            }

                            bbUL = new Point(leftX, topY);
                            // System.out.print("upperLeft: ");
                            // System.out.println(bbUL.toString());
                            bbUR = new Point(rightX, topY);
                            // System.out.print("upperRight: ");
                            // System.out.println(bbUR.toString());
                            bbBL = new Point(leftX, bottomY);
                            // System.out.print("bottomLeft: ");
                            // System.out.println(bbBL.toString());
                            bbBR = new Point(rightX, bottomY);
                            // System.out.print("bottomRight: ");
                            // System.out.println(bbBR.toString());
                            bbHeight = bottomY - topY;
                            bbWidth = rightX - leftX;



                        }

                        // System.out.print("selection Mode: ");
                        // System.out.println(selectionMode);
                        repaint();


                        //set selection mode to off
                        //selectionMode = false;
                        // bbUL = null;
                        // bbUR = null;
                        // bbBL = null;
                        // bbBR = null;
                        // bbHeight = 0;
                        // bbWidth = 0;
                    }

                    
                }

                if ((gestureX.size() > 1) && (gestureY.size() > 1)) {
                    for (int i = 0; i + 1 < gestureX.size(); i = i + 1) {
                        this.g.setColor(Color.WHITE);
                        Graphics2D g2 = (Graphics2D) this.g;
                        g2.setStroke(new BasicStroke(3));

                        int x1 = (int)gestureX.get(i);
                        int y1 = (int)gestureY.get(i);
                        int oX1 = (int)origXgest.get(i);
                        int oY1 = (int)origYgest.get(i);


                        int x2 = (int)gestureX.get(i + 1);
                        int y2 = (int)gestureY.get(i + 1);
                        int oX2 = (int)origXgest.get(i + 1);
                        int oY2 = (int)origYgest.get(i + 1);

                        g2.drawLine(x1 + (this.x - oX1), y1 + (this.y - oY1), 
                        x2 + (this.x - oX2), y2 + (this.y - oY2));

                        this.repaint();
                        
                    }
                }

                gestureX.removeAll(gestureX);
                gestureY.removeAll(gestureY);
                origXgest.removeAll(origXgest);
                origYgest.removeAll(origYgest);

            }

            pressed = false;
        }

        repaint();
    }

    public void mouseExited(MouseEvent e) {
        //this.inside = false;
    }

    public void mouseEntered(MouseEvent e) {
        //this.inside = true;
    }

    public void keyTyped(KeyEvent ke) {
       int id = ke.getID();
       
       if (id == KeyEvent.KEY_TYPED) {
           char ch = ke.getKeyChar();
           //text = text + Character.toString(ch);
           
       }
       
       //t.setText(text);
       repaint();
       System.out.println("everything");
   }

   public void keyReleased(KeyEvent ke) {
       
   }

   public void keyPressed(KeyEvent ke) {

   }
}   