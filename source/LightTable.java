import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import javax.swing.Timer;


class LightTable extends JComponent {

    private ArrayList<PhotoComponent> photoList = new ArrayList<PhotoComponent>();
    private PhotoComponent shown = null;
    private Thumbnail select = null;
    private int index = 0;
    private int size = 0;
    private JPanel split = null;
    private JPanel topSplit = null;
    public JPanel gridView = null;
    private JScrollPane scrollpane;
    private JScrollPane scrollpane2;
    private JScrollPane scrollpane3;
    private FlowLayout flow = new FlowLayout(FlowLayout.LEADING, 20, 20);
    private GridLayout browser = new GridLayout(2, 0, 10, 10);
    public MainPanel main;


    //keeps track of the horizontal size of content pane (based on width of photos)
    //so that the scrollbar appears correctly for FlowLayout and GridLayout
    private int keepSize = 0;

    public ViewMode mode = ViewMode.PHOTOVIEWER;

    //For magnet mode
    private ArrayList<Point> tNailPoints = null;
    private ArrayList<JLabel> magnets = null;
    private ArrayList<Point> magDest = null;
    private JComboBox chooseMag = null;
    private JLabel mag = null;
    private int magCount = 0;
    private int famMagC = 0;
    private int frMagC = 0;
    private int schMagC = 0;
    private int workMagC = 0;
    public JLabel pressed = null;
    public final static int time = 5000;
    public int iters = 0;
    public Timer timer;
    


    public LightTable(PhotoComponent photo1, MainPanel main) {

        photoList.add(photo1);
        size++;
        shown = photo1;

        if (mode == ViewMode.PHOTOVIEWER) {
            createPhotoViewMode();
        } else if (mode == ViewMode.SPLITVIEW) {
            createSplitViewMode();
        } else if (mode == ViewMode.BROWSER) {
            createBrowserMode();
        }
        this.main = main;
        revalidate();
        repaint();
    }

    private void createPhotoViewMode() {

        keepSize = 0;
        this.removeAll();

        if (chooseMag != null) {
            main.control.getLeftMenu().center.remove(chooseMag);
            main.control.getLeftMenu().center.remove(mag);
            main.control.getLeftMenu().revalidate();
            mag = null;
            chooseMag = null;
            tNailPoints = null;
            LightTable.this.magCount = 0;
            magnets = null;
            famMagC = 0;
            frMagC = 0;
            schMagC = 0;
            workMagC = 0;
        }
        
        setLayout(new BorderLayout());

         if (shown != null) {
             setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
             add(shown, BorderLayout.CENTER);
         } else if ((shown == null) && (photoList.size() > 0)) {
             if (select != null) {
                 shown = photoList.get(index);
             } else {
                 shown = photoList.get(0);
             }
             
             setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
             add(shown, BorderLayout.CENTER);
         }

         repaint();
         revalidate();

    }

    private void createSplitViewMode() {
        this.removeAll();

        if (chooseMag != null) {
            main.control.getLeftMenu().center.remove(chooseMag);
            main.control.getLeftMenu().center.remove(mag);
            main.control.getLeftMenu().revalidate();
            mag = null;
            chooseMag = null;
            tNailPoints = null;
            LightTable.this.magCount = 0;
            magnets = null;
            famMagC = 0;
            frMagC = 0;
            schMagC = 0;
            workMagC = 0;
        }

        keepSize = 0;
        setLayout(new BorderLayout());

        split = new JPanel();
        split.setLayout(flow);
        
        Dimension p = new Dimension(keepSize, 240);
        split.setPreferredSize(p);


        topSplit = new JPanel();
        topSplit.setLayout(new BorderLayout());
        scrollpane2 = new JScrollPane(this.topSplit);
        add(scrollpane2, BorderLayout.CENTER);

        for (int i = 0; i < photoList.size(); i++) {

            if (photoList.get(i).getThumbnail().getHscale() == 300) {
                photoList.get(i).getThumbnail().changeHeight(200);
            }
            
            split.add(photoList.get(i).getThumbnail());
            if (i != 0) {
                keepSize = keepSize + photoList.get(i).getThumbnail().width + 20;
            } else {
                if (shown == null) {
                    shown = photoList.get(i);
                }

                keepSize = keepSize + shown.getThumbnail().width + 40;
                topSplit.add(shown, BorderLayout.CENTER);
                topSplit.setPreferredSize(new Dimension(shown.getImageWidth(), shown.getImageHeight()));
                setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));
                super.setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
            }

            p = new Dimension(keepSize, 240);
            split.setPreferredSize(p);

            split.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {

                    int x = e.getX();
                    int y = e.getY();
                    Thumbnail t = null;

                    if (e.getClickCount() == 2) {

                        for (int i = 0; i < size; i++) {
                            t = LightTable.this.photoList.get(i).getThumbnail();
                            if (t.containsF(x, y)) {
                                LightTable.this.shown = photoList.get(i);
                                LightTable.this.index = i;
                                LightTable.this.changeMode(ViewMode.PHOTOVIEWER);
                                break;
                            }
                        }

                        repaint();
                        revalidate();
                        
                    } else if (e.getClickCount() == 1) {

                        for (int i = 0; i < size; i++) {
                            t = LightTable.this.photoList.get(i).getThumbnail();
                            if (t.containsF(x, y)) {
                                LightTable.this.shown = photoList.get(i);
                                LightTable.this.topSplit.removeAll();
                                LightTable.this.topSplit.add(LightTable.this.shown, BorderLayout.CENTER);
                                LightTable.this.index = i;

                                if (select != null) {
                                    select.setPaintB(false);
                                    select.repaint();
                                }
                                
                                select = t;
                                select.setPaintB(true);
                                select.repaint();
                                break;
                            }
                        }

                        repaint();
                        revalidate();
                    }
                }

                public void mousePressed(MouseEvent e) {

                }

                public void mouseExited(MouseEvent e) {

                }

                public void mouseEntered(MouseEvent e) {

                }

                public void mouseReleased(MouseEvent e) {

                }

            });

            revalidate();
            repaint();
        }

        scrollpane = new JScrollPane(this.split);
        Dimension d1 = scrollpane.getMinimumSize();
        Dimension d2 = scrollpane.getMaximumSize();
        d1.height = 260;
        d2.height = 260;
        d1.width = super.getWidth();
        d2.width = super.getWidth();

        add(scrollpane, BorderLayout.SOUTH);

    }

    private void createBrowserMode() {
        // LightTable.this.magCount = 0;
        // magnets = null;
        keepSize = 0;
        this.removeAll();

        if (chooseMag != null) {
            main.control.getLeftMenu().center.remove(chooseMag);
            main.control.getLeftMenu().center.remove(mag);
            main.control.getLeftMenu().revalidate();
            mag = null;
            chooseMag = null;
            tNailPoints = null;
            LightTable.this.magCount = 0;
            magnets = null;
            famMagC = 0;
            frMagC = 0;
            schMagC = 0;
            workMagC = 0;
        }
        setLayout(new BorderLayout());
        // famMagC = 0;
        // frMagC = 0;
        // schMagC = 0;
        // workMagC = 0;

        gridView = new JPanel();

        gridView.setLayout(browser);

        scrollpane3 = new JScrollPane(gridView);

        add(scrollpane3, BorderLayout.CENTER);

        for (int i = 0; i < photoList.size(); i++) {
            photoList.get(i).getThumbnail().changeHeight(300);
            if (i == 0) {
                shown = photoList.get(i);
                shown.getThumbnail().setAlignmentX(CENTER_ALIGNMENT);
                shown.getThumbnail().setAlignmentY(CENTER_ALIGNMENT);
                gridView.add(shown.getThumbnail());
            } else {
                photoList.get(i).getThumbnail().setAlignmentX(CENTER_ALIGNMENT);
                photoList.get(i).getThumbnail().setAlignmentY(CENTER_ALIGNMENT);
                gridView.add(photoList.get(i).getThumbnail());
            }
            
            keepSize = keepSize + photoList.get(i).getThumbnail().width + 10;
            scrollpane3.setPreferredSize(new Dimension(keepSize, super.getHeight()));
            setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));

            gridView.revalidate();
            gridView.repaint();
        }

        shown = null;

        gridView.addMouseListener(new MouseListener() {
            private JLabel pressed = null;

            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                int x = p.x;
                int y = p.y;
                Thumbnail t = null;

                //JPanel panel = (JPanel) getComponentAt(e.getPoint());

                if (e.getClickCount() == 2) {

                    JLabel delete = null;
                    if (LightTable.this.magnets != null) {
                        for (int i = 0; i < LightTable.this.magnets.size(); i++) {
                            JLabel label= LightTable.this.magnets.get(i);
                            if (label.getBounds().contains(p)) {
                                delete = label;
                                break;
                            }
                        }
                    }
                    

                    if (delete != null) {

                        magnets.remove(delete);
                        gridView.remove(delete);

                        gridView.repaint();
                        iters = 0;

                        for (int n = 0; n < size; n++) {
                            PhotoComponent photo = photoList.get(n);
                            double finalX = 0;
                            double finalY = 0;
                            double count = 0;
                            boolean enter = false;

                            if (magnets != null) {
                                for (int i = 0; i < magnets.size(); i++) {
                                    JLabel mag = magnets.get(i);
                                    int magX = mag.getBounds().x;
                                    int magY = mag.getBounds().y;

                                    if (mag.getText().contains("Family Magnet")) {
                                        if (photo.tags[0] != null) {
                                            enter = true;
                                            finalX = finalX + magX;                        
                                            finalY = finalY + magY;
                                            count++;
                                        }
                                    } else if (mag.getText().contains("Friends Magnet")) {
                                        
                                        if (photo.tags[1] != null) {
                                            enter = true;
                                            finalX = finalX + magX;
                                            finalY = finalY +  magY;
                                            count++;
                                        }
                                    } else if (mag.getText().contains("School Magnet")) {
                                        if (photo.tags[2] != null) {
                                            enter = true;
                                            finalX = finalX + magX;
                                            finalY = finalY + magY;
                                            count++;
                                        }
                                    } else if (mag.getText().contains("Work Magnet")) {
                                        if (photo.tags[3] != null) {
                                            enter = true;
                                            finalX = finalX + magX;
                                            finalY = finalY + magY;
                                            count++;
                                        }
                                    }
                                }

                                if (!enter) {
                                    if (magDest.size() == size) {
                                        magDest.set(n, new Point(-1, -1));
                                    } else {
                                        magDest.add(new Point(-1, -1));
                                    }
                                } else {
                                    if (magDest.size() == size) {
                                        magDest.set(n, new Point((int)(finalX/count), (int)(finalY/count)));
                                    } else {
                                        magDest.add(new Point((int)(finalX/count), (int)(finalY/count)));
                                    }
                                }

                                if (magnets.size() == 0) {
                                    //magDest.set(n, tNailPoints.get(n));
                                    createBrowserMode();
                                }
                            }
                            
                            count = 0;
                        }

                        
                        ActionListener t1 = new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                if (iters == 40) {
                                    timer.stop();
                                } else {
                                    for (int i = 0; i < size; i++) {
                                        
                                        Thumbnail t2 = photoList.get(i).getThumbnail();
                                        Point p = magDest.get(i);
                                        if ((p.x == -1) && (p.y == -1)) {
                                            continue;
                                        }

                                        int oX = t2.getLocation().x;
                                        int oY = t2.getLocation().y;

                                        int dx = oX + (iters * ((magDest.get(i).x - oX)/40));
                                        int dy = oY + (iters * ((magDest.get(i).y - oY)/40));
                                        t2.setLocation(dx, dy);

                                        t2.repaint();
                                        gridView.repaint();
                                        gridView.revalidate();


                                    }
                                    iters++;
                                }
                            }
                        };

                        timer = new Timer(50, t1);
                        timer.start();

                    }

                    if (delete == null) {
                        for (int i = 0; i < size; i++) {
                            t = LightTable.this.photoList.get(i).getThumbnail();
                            if (t.containsF(x, y)) {
                                LightTable.this.shown = photoList.get(i);
                                LightTable.this.index = i;
                                LightTable.this.changeMode(ViewMode.PHOTOVIEWER);
                                break;
                            }
                        }
                    }
                    

                    repaint();
                    revalidate();
                    
                } else if (e.getClickCount() == 1) {
                    boolean possibleDel = false;

                    if (LightTable.this.magnets != null) {
                        for (int i = 0; i < LightTable.this.magnets.size(); i++) {
                            JLabel label= LightTable.this.magnets.get(i);
                            if (label.getBounds().contains(p)) {
                                possibleDel = true;
                                break;
                            }
                        }
                    }

                    

                    if (!possibleDel) {
                        for (int i = 0; i < size; i++) {
                            t = LightTable.this.photoList.get(i).getThumbnail();
                            //t.changeHeight(300);
                            if (t.containsF(x, y)) {
                                if (select != null) {
                                    select.setPaintB(false);
                                    select.repaint();
                                }
                                
                                select = t;
                                select.setPaintB(true);
                                select.repaint();

                                LightTable.this.index = i;
                                revalidate();
                                break;
                            }
                        }
                    }
                    
                    

                    repaint();
                    revalidate();
                }
            }

            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();

                if (LightTable.this.magnets != null) {
                    for (int i = 0; i < LightTable.this.magnets.size(); i++) {
                        JLabel label= LightTable.this.magnets.get(i);
                        if (label.getBounds().contains(p)) {
                            LightTable.this.pressed = label;
                            break;
                        }
                    }
                }
                
            }

            public void mouseExited(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
                LightTable.this.pressed = null;
            }

        });

        gridView.addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseMoved(MouseEvent e) {

            }


            public void mouseDragged(MouseEvent e) {
                Point g = e.getPoint();

                if (LightTable.this.pressed != null) {
                    LightTable.this.pressed.setBounds(g.x, g.y, LightTable.this.pressed.getWidth(), LightTable.this.pressed.getHeight());
                    
                    iters = 0;
                    for (int n = 0; n < size; n++) {
                        PhotoComponent photo = photoList.get(n);
                        Thumbnail t = photo.getThumbnail();
                        double finalX = 0;
                        double finalY = 0;
                        double count = 0;
                        boolean enter = false;

                        for (int i = 0; i < magnets.size(); i++) {
                            JLabel mag = magnets.get(i);
                            int magX = mag.getBounds().x;
                            int magY = mag.getBounds().y;

                            if (mag.getText().contains("Family Magnet")) {
                                if (photo.tags[0] != null) {
                                    enter = true;
                                    finalX = finalX + magX;                        
                                    finalY = finalY + magY;
                                    count++;
                                }
                            } else if (mag.getText().contains("Friends Magnet")) {
                                
                                if (photo.tags[1] != null) {
                                    enter = true;
                                    finalX = finalX + magX;
                                    finalY = finalY +  magY;
                                    count++;
                                }
                            } else if (mag.getText().contains("School Magnet")) {
                                if (photo.tags[2] != null) {
                                    enter = true;
                                    finalX = finalX + magX;
                                    finalY = finalY + magY;
                                    count++;
                                }
                            } else if (mag.getText().contains("Work Magnet")) {
                                if (photo.tags[3] != null) {
                                    enter = true;
                                    finalX = finalX + magX;
                                    finalY = finalY + magY;
                                    count++;
                                }
                            }
                        }

                        if (!enter) {
                            if (magDest.size() == size) {
                                magDest.set(n, new Point(-1, -1));
                            } else {
                                magDest.add(new Point(-1, -1));
                            }
                        } else {
                            if (magDest.size() == size) {
                                magDest.set(n, new Point((int)(finalX/count), (int)(finalY/count)));
                            } else {
                                magDest.add(new Point((int)(finalX/count), (int)(finalY/count)));
                            }
                        }
                        
                        count = 0;
                    }

                    
                    ActionListener t1 = new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            if (iters == 40) {
                                timer.stop();
                            } else {
                                for (int i = 0; i < size; i++) {
                                    
                                    Thumbnail t = photoList.get(i).getThumbnail();
                                    Point p = magDest.get(i);
                                    if ((p.x == -1) && (p.y == -1)) {
                                        continue;
                                    }

                                    int oX = t.getLocation().x;
                                    int oY = t.getLocation().y;

                                    int dx = oX + (iters * ((magDest.get(i).x - oX)/40));
                                    int dy = oY + (iters * ((magDest.get(i).y - oY)/40));
                                    t.setLocation(dx, dy);

                                    t.repaint();
                                    gridView.repaint();
                                    gridView.revalidate();


                                }
                                iters++;
                            }
                        }
                    };

                    timer = new Timer(50, t1);
                    timer.start();
                    
                    
                    LightTable.this.pressed.repaint();
                    gridView.revalidate();
                    gridView.repaint();
                }
            }


        });

        //Magnet set up and action listener
        if (chooseMag == null) {
            String[] magNames = { "Select Magnet Type", "Add Family Magnet", "Add Friends Magnet", "Add School Magnet", "Add Work Magnet"};
            chooseMag = new JComboBox(magNames);
            chooseMag.setSelectedIndex(0);
            chooseMag.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    String magName = (String)chooseMag.getSelectedItem();

                    if (magName != "Select Magnet Type") {
                        if (LightTable.this.magCount == 0) {
                            createMagnetMode();
                            magnets = new ArrayList<JLabel>();
                            magDest = new ArrayList<Point>();
                            LightTable.this.magCount += 1;
                        }
                    }

                    iters = 0;

                    LightTable.this.gridView.setForeground(Color.BLACK);

                    if (magName == "Add Family Magnet") {
                        LightTable.this.famMagC += 1;
                        JLabel famMag = new JLabel("Family Magnet " + Integer.toString(LightTable.this.famMagC));
                        famMag.setBounds(0, 0, 100, 20);
                        magnets.add(famMag);
                        
                        LightTable.this.gridView.add(famMag);
                        gridView.setComponentZOrder(famMag, 0);

                    
                    } else if (magName == "Add Friends Magnet") {
                        LightTable.this.frMagC += 1;
                        JLabel frMag = new JLabel("Friends Magnet " + Integer.toString(LightTable.this.frMagC));
                        
                        frMag.setBounds(0, 20, 100, 20);
                        magnets.add(frMag);

                        LightTable.this.gridView.add(frMag);
                        gridView.setComponentZOrder(frMag, 0);

                    } else if (magName == "Add School Magnet") {
                        LightTable.this.schMagC += 1;
                        JLabel schMag = new JLabel("School Magnet " + Integer.toString(LightTable.this.schMagC));
                        
                        schMag.setBounds(0, 40, 100, 20);
                        magnets.add(schMag);

                        LightTable.this.gridView.add(schMag);
                        gridView.setComponentZOrder(schMag, 0);


                    } else if (magName == "Add Work Magnet") {
                        LightTable.this.workMagC += 1;
                        JLabel workMag = new JLabel("Work Magnet " + Integer.toString(LightTable.this.workMagC));
                       
                        workMag.setBounds(0, 60, 100, 20);
                         magnets.add(workMag);

                        LightTable.this.gridView.add(workMag);
                        gridView.setComponentZOrder(workMag, 0);
                    }




                    for (int n = 0; n < size; n++) {
                        PhotoComponent photo = photoList.get(n);
                        Thumbnail t = photo.getThumbnail();
                        double finalX = 0;
                        double finalY = 0;
                        double count = 0;
                        boolean enter = false;

                        for (int i = 0; i < magnets.size(); i++) {
                            JLabel mag = magnets.get(i);
                            int magX = mag.getBounds().x;
                            int magY = mag.getBounds().y;

                            if (mag.getText().contains("Family Magnet")) {
                                if (photo.tags[0] != null) {
                                    enter = true;
                                    finalX = finalX + magX;                        
                                    finalY = finalY + magY;
                                    count++;
                                }
                            } else if (mag.getText().contains("Friends Magnet")) {
                                
                                if (photo.tags[1] != null) {
                                    enter = true;
                                    finalX = finalX + magX;
                                    finalY = finalY +  magY;
                                    count++;
                                }
                            } else if (mag.getText().contains("School Magnet")) {
                                if (photo.tags[2] != null) {
                                    enter = true;
                                    finalX = finalX + magX;
                                    finalY = finalY + magY;
                                    count++;
                                }
                            } else if (mag.getText().contains("Work Magnet")) {
                                if (photo.tags[3] != null) {
                                    enter = true;
                                    finalX = finalX + magX;
                                    finalY = finalY + magY;
                                    count++;
                                }
                            }
                        }

                        if (!enter) {
                            if (magDest.size() == size) {
                                magDest.set(n, new Point(-1, -1));
                            } else {
                                magDest.add(new Point(-1, -1));
                            }
                        } else {
                            if (magDest.size() == size) {
                                magDest.set(n, new Point((int)(finalX/count), (int)(finalY/count)));
                            } else {
                                magDest.add(new Point((int)(finalX/count), (int)(finalY/count)));
                            }
                        }
                        
                        count = 0;
                    }

                    
                    ActionListener t1 = new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            if (iters == 40) {
                                timer.stop();
                            } else {
                                for (int i = 0; i < size; i++) {
                                    
                                    Thumbnail t = photoList.get(i).getThumbnail();
                                    Point p = magDest.get(i);
                                    if ((p.x == -1) && (p.y == -1)) {
                                        continue;
                                    }

                                    int oX = t.getLocation().x;
                                    int oY = t.getLocation().y;

                                    int dx = oX + (iters * ((magDest.get(i).x - oX)/40));
                                    int dy = oY + (iters * ((magDest.get(i).y - oY)/40));
                                    t.setLocation(dx, dy);

                                    t.repaint();
                                    gridView.repaint();
                                    gridView.revalidate();


                                }
                                iters++;
                            }
                        }
                    };

                    timer = new Timer(50, t1);
                    timer.start();


                    LightTable.this.gridView.revalidate();
                    LightTable.this.gridView.repaint();
                    
                }
            });
        }

        mag = new JLabel("Magnets");
        mag.setForeground(Color.WHITE);
        main.control.getLeftMenu().center.add(mag, BorderLayout.WEST);
        mag.setAlignmentX(LEFT_ALIGNMENT);

        main.control.getLeftMenu().center.add(chooseMag, BorderLayout.WEST);
        chooseMag.setAlignmentX(LEFT_ALIGNMENT);
        chooseMag.setMaximumSize(new Dimension(200, 20));
        main.control.getLeftMenu().revalidate();
        //-main.control.getLeftMenu().revalidate();

        revalidate();
        repaint();
    }

    // private void paintComponent() {
    //     super.paintComponent();

        
    // }

    private void createMagnetMode() {

        Thumbnail tn = null;
        keepSize = 0;
        int keepH = 0;

        if (tNailPoints == null) {
            tNailPoints = new ArrayList<Point>();
        }
        
        for (int n = 0; n < size; n++) {
            tn = photoList.get(n).getThumbnail();
            tNailPoints.add(tn.getLocation());
        }

        //System.out.println("it's happening");

        gridView.removeAll();
        gridView.setLayout(null);

        for (int n = 0; n < size; n++) {
            tn = photoList.get(n).getThumbnail();
            Point p = tNailPoints.get(n);

            keepSize = keepSize + tn.width + 10;
            keepH = keepH + tn.height + 10;

            keepSize = keepSize + tn.width + 10;
            gridView.setPreferredSize(new Dimension((keepSize/4 + 1), keepH/4));
            setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));
            scrollpane3.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));


            tn.x = p.x;
            tn.y = p.y;
            //tn.setBounds(p.x, p.y, tn.width, tn.height);
            gridView.add(tn);
            //tn.setLocation(p.x, p.y);

            gridView.revalidate();
            gridView.repaint();

            //System.out.println("it's happening 2");
        }

        //System.out.println("it's happening 3");
        scrollpane3.revalidate();
        scrollpane3.repaint();
        gridView.revalidate();
        gridView.repaint();

    }

    public PhotoComponent getShownPhoto() {
        return shown;
    }

    public int getLength() {
        return size;
    }

    public int getIndex() {
        return index;
    }

    public ViewMode getMode() {
        return mode;
    }

    public void changeMode(ViewMode newMode) {
        mode = newMode;

        if (newMode == ViewMode.PHOTOVIEWER) {
            this.main.control.getMainMenu().photoViewItem.setSelected(true);
            createPhotoViewMode();
        } else if (newMode == ViewMode.SPLITVIEW) {
            this.main.control.getMainMenu().splitViewItem.setSelected(true);
            createSplitViewMode();
        } else if (newMode == ViewMode.BROWSER) {
            this.main.control.getMainMenu().gridViewItem.setSelected(true);
            createBrowserMode();
        }

        revalidate();
        repaint();
    }

    public void addPhoto(PhotoComponent photo) {

        if (size == 0) {
            shown = photo;

            if (mode == ViewMode.PHOTOVIEWER) {
                add(shown, BorderLayout.CENTER);
                setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
            } else if (mode == ViewMode.SPLITVIEW) {
                topSplit.add(shown, BorderLayout.CENTER);
            } else if (mode == ViewMode.BROWSER) {

            }
        }

        photoList.add(photo);
        size++;
        
        if (mode == ViewMode.SPLITVIEW) {

            split.add(photo.getThumbnail());
            keepSize = keepSize + photo.getThumbnail().width + 20;
            //System.out.println(photo.getThumbnail().width);
            Dimension p = new Dimension(keepSize, 240);
            // System.out.print("keepSize: ");
            // System.out.println(keepSize);
            // System.out.print("pref layout size: ");
            // System.out.println(flow.preferredLayoutSize(split));
            topSplit.setPreferredSize(new Dimension(shown.getImageWidth(), shown.getImageHeight()));
            //setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));
            super.setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
            
            split.setPreferredSize(p);
            split.revalidate();
            topSplit.revalidate();

        } else if (mode == ViewMode.BROWSER) {
            shown = null;
            photo.getThumbnail().changeHeight(300);
            gridView.add(photo.getThumbnail());
            keepSize = keepSize + photo.getThumbnail().width + 10;
            scrollpane3.setPreferredSize(new Dimension(keepSize, super.getHeight()));
            setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));
        }

        repaint();
        revalidate();
        
    }

    public void deletePhoto(PhotoComponent photo) {
        if (mode == ViewMode.PHOTOVIEWER) {
            if (size > 1) {
                this.remove(shown);
                if (index == 0) {
                    shown = photoList.get(index + 1);
                    setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
                    photoList.remove(index);
                } else {
                    shown = photoList.get(index - 1);
                    setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
                    photoList.remove(index);
                    index--;
                }

                add(shown, BorderLayout.CENTER);
                shown.updateTagInfo();
                size--;
                repaint();

            } else if (size == 1) {
                this.remove(shown);
                shown = null;
                photoList.remove(index);
                
                setPreferredSize(new Dimension(0, 0));
                size--;
                repaint();
            }
        } else if (mode == ViewMode.SPLITVIEW) {
            if (size > 1) {
                topSplit.remove(shown);
                if (index == 0) {
                    keepSize = keepSize - photo.getThumbnail().width - 20;
                    split.setPreferredSize(new Dimension(keepSize, 240));
                    shown = photoList.get(index + 1);
                    shown.updateTagInfo();
                    topSplit.setPreferredSize(new Dimension(shown.getImageWidth(), shown.getImageHeight()));
                    split.remove(photoList.get(index).getThumbnail());
                    photoList.remove(index);
                    
                } else {
                    keepSize = keepSize - photo.getThumbnail().width - 20;
                    split.setPreferredSize(new Dimension(keepSize, 240));
                    shown = photoList.get(index - 1);
                    shown.updateTagInfo();
                    topSplit.setPreferredSize(new Dimension(shown.getImageWidth(), shown.getImageHeight()));
                    split.remove(photoList.get(index).getThumbnail());
                    photoList.remove(index);
                    
                    index--;
                }
                setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));
                super.setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
                topSplit.add(shown, BorderLayout.CENTER);
                size--;
                repaint();
                revalidate();

            } else if (size == 1) {
                topSplit.remove(shown);
                keepSize = keepSize - photo.getThumbnail().width - 40;
                split.setPreferredSize(new Dimension(keepSize, 240));
                shown = null;
                split.remove(photoList.get(index).getThumbnail());
                photoList.remove(index);
                
                topSplit.setPreferredSize(new Dimension(keepSize, keepSize));
                setPreferredSize(new Dimension(keepSize, keepSize));
                size--;
                repaint();
                revalidate();
            }
        } else if (mode == ViewMode.BROWSER) {
            if (select != null) {

                gridView.remove(photoList.get(index).getThumbnail());
                photoList.remove(index);
                size--;
                keepSize = keepSize - select.width - 10;
                select = null;
                scrollpane3.setPreferredSize(new Dimension(keepSize, super.getHeight()));
                setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));

                if (size > 1) {
                    if (index > 0) {
                        index--;
                    }
                }
                repaint();
                revalidate();
            }
            
        }
    }

    public void nextPhoto() {
        if (mode == ViewMode.PHOTOVIEWER) {
            if ((index + 1) < size) {
                index++;
                this.remove(shown);
                shown = photoList.get(index);
                add(shown, BorderLayout.CENTER);
                shown.updateTagInfo();
                setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));
                super.setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
            }
        } else if (mode == ViewMode.SPLITVIEW) {
             if ((index + 1) < size) {
                index++;
                topSplit.remove(shown);
                shown = photoList.get(index);
                shown.updateTagInfo();
                topSplit.add(shown, BorderLayout.CENTER);
                topSplit.setPreferredSize(new Dimension(shown.getImageWidth(), shown.getImageHeight()));
                setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));
                super.setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));

                //Add selection of Thumbnail
                if (select != null) {
                    select.setPaintB(false);
                    select = shown.getThumbnail();
                    select.setPaintB(true);
                    select.repaint();
                }
               
            }
        } else if (mode == ViewMode.BROWSER) {

            if ((index + 1) < size) {
                index++;
                if (select != null) {
                    select.setPaintB(false);
                    select = photoList.get(index).getThumbnail();
                    select.setPaintB(true);
                    select.repaint();
                } else {
                    index = 0;
                    if (photoList.get(index) != null) {
                        select = photoList.get(index).getThumbnail();
                        select.setPaintB(true);
                        select.repaint();
                    }
                }
            }
        }
        
        
        repaint();
        revalidate();
    }

    public void prevPhoto() {
        
        if (mode == ViewMode.PHOTOVIEWER) {
            if (index >= 1) {
                index--;
                this.remove(shown);
                shown = photoList.get(index);
                shown.updateTagInfo();
                add(shown, BorderLayout.CENTER);
                setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));
                super.setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));

            }
        } else if (mode == ViewMode.SPLITVIEW) {
            if (index >= 1) {
                index--;
                topSplit.remove(shown);
                shown = photoList.get(index);
                shown.updateTagInfo();
                topSplit.add(shown, BorderLayout.CENTER);
                topSplit.setPreferredSize(new Dimension(shown.getImageWidth(), shown.getImageHeight()));
                setPreferredSize(new Dimension(super.getWidth(), super.getHeight()));
                super.setPreferredSize(new Dimension(shown.getWidth(), shown.getHeight()));
                
                //Add selection of Thumbnail
                if (select != null) {
                    select.setPaintB(false);
                    select = shown.getThumbnail();
                    select.setPaintB(true);
                    select.repaint();
                }
            }
        } else if (mode == ViewMode.BROWSER) {

            if (index >= 1) {
                index--;

                if (select != null) {
                    select.setPaintB(false);
                    select = photoList.get(index).getThumbnail();
                    select.setPaintB(true);
                    select.repaint();
                } else {
                    index = 0;
                    if (photoList.get(index) != null) {
                        select = photoList.get(index).getThumbnail();
                        select.setPaintB(true);
                        select.repaint();
                    }
                }
            }
        }


        repaint();
        revalidate();
    }

}