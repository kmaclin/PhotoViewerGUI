import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;

public class MainMenu extends JMenuBar implements StatusUpdater{

    private String statusText = "Menu Selected";
    private JFileChooser openFile = null;
    public ControlPanel controlPanel;
    private ButtonGroup viewGroup;
    public JMenuItem deleteItem;

    public JRadioButtonMenuItem photoViewItem;
    public JRadioButtonMenuItem gridViewItem;
    public JRadioButtonMenuItem splitViewItem;

    public MainMenu(ControlPanel panel) {
        super();
        setOpaque(true);
        setBackground(new Color(234, 234, 234));
        setPreferredSize(new Dimension(200, 20));
        this.controlPanel = panel;

        //create file menu and buttons
        fileMenu(this);
        viewMenu(this);
        deleteItem.setEnabled(false);
        photoViewItem.setEnabled(false);
        gridViewItem.setEnabled(false);
        splitViewItem.setEnabled(false);
    }

    /*
    * Creates the File Menu
    */
    private void fileMenu(JMenuBar main) {

        //create file menu
        JMenu fileMenu = new JMenu("File");
        main.add(fileMenu);

        JMenuItem importItem = new JMenuItem("Import");
        fileMenu.add(importItem);

        //read file in separate method
        chooseFile(importItem);

        deleteItem = new JMenuItem("Delete");
        fileMenu.add(deleteItem);

        deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {

                if (MainMenu.this.controlPanel.getMainPanel().table.getLength() != 0) {
                    MainMenu.this.controlPanel.getMainPanel().deletePhoto();
                }

                if (MainMenu.this.controlPanel.getMainPanel().table.getLength() == 0) {
                    deleteItem.setEnabled(false);
                }
                
                

                MainMenu.this.statusText = "Delete Selected";
                MainMenu.this.controlPanel.updateStatusBar(MainMenu.this.statusText);
            }
        });

        //create exit choice and apply action
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);
        
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                MainMenu.this.statusText = "Exit Selected";
                MainMenu.this.controlPanel.updateStatusBar(MainMenu.this.statusText);
                System.exit(0);
            }
        });
    }

    private void chooseFile(JMenuItem importOption) {
        
        this.openFile = new JFileChooser(".");

        importOption.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {
                //handle status update
                MainMenu.this.statusText = "Import Selected";
                MainMenu.this.controlPanel.updateStatusBar(MainMenu.this.statusText);

                openFile.setMultiSelectionEnabled(true);
                //open file chooser dialog
                int option = openFile.showOpenDialog(MainMenu.this);
                

                if (option == JFileChooser.APPROVE_OPTION) {
                    File[] file = openFile.getSelectedFiles();
                    //System.out.println(file);

                    for (int i = 0; i < file.length; i++) {
                        try {
                            BufferedImage img = ImageIO.read(file[i]);
                            
                            PhotoComponent photo = new PhotoComponent(img);
                            MainMenu.this.controlPanel.getMainPanel().addPhoto(photo);

                            MainMenu.this.statusText = "Photo was uploaded.";
                            MainMenu.this.controlPanel.updateStatusBar(MainMenu.this.statusText);
                            //System.out.println("here");

                        } catch (IOException e1) {
                            System.out.println("failed to file.");
                            continue;
                        }
                    }

                } else if (option == JFileChooser.CANCEL_OPTION) {
                    System.out.println("canceled");
                }

            }
        });


    }

    private void viewMenu(JMenuBar main) {

        //creates view menu
        JMenu viewMenu = new JMenu("Menu");
        main.add(viewMenu);

        photoViewItem = new JRadioButtonMenuItem("Photo View");
        viewMenu.add(photoViewItem);

        gridViewItem = new JRadioButtonMenuItem("Grid View");
        viewMenu.add(gridViewItem);

        splitViewItem = new JRadioButtonMenuItem("Split View");
        viewMenu.add(splitViewItem);

        viewGroup = new ButtonGroup();
        viewGroup.add(photoViewItem);
        viewGroup.add(gridViewItem);
        viewGroup.add(splitViewItem);

        photoViewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (MainMenu.this.controlPanel.getMainPanel().table != null) {
                    if (MainMenu.this.controlPanel.getMainPanel().table.getMode() !=
                    ViewMode.PHOTOVIEWER) {
                        MainMenu.this.controlPanel.getMainPanel().table.changeMode(ViewMode.PHOTOVIEWER);
                        revalidate();
                    }
                }

                MainMenu.this.statusText = "Photo View Selected";
                MainMenu.this.controlPanel.updateStatusBar(MainMenu.this.statusText);
            }
        });
        
        gridViewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (MainMenu.this.controlPanel.getMainPanel().table != null) {
                    if (MainMenu.this.controlPanel.getMainPanel().table.getMode() !=
                    ViewMode.BROWSER) {
                        MainMenu.this.controlPanel.getMainPanel().table.changeMode(ViewMode.BROWSER);
                        revalidate();
                    }
                }

                MainMenu.this.statusText = "Grid View Selected";
                MainMenu.this.controlPanel.updateStatusBar(MainMenu.this.statusText);
            }
        });

        splitViewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (MainMenu.this.controlPanel.getMainPanel().table != null) {
                    if (MainMenu.this.controlPanel.getMainPanel().table.getMode() !=
                    ViewMode.SPLITVIEW) {
                        MainMenu.this.controlPanel.getMainPanel().table.changeMode(ViewMode.SPLITVIEW);
                        revalidate();
                    }
                }

                MainMenu.this.statusText = "Split View Selected";
                MainMenu.this.controlPanel.updateStatusBar(MainMenu.this.statusText);
            }
        });

    }

    /*
    * Updates the status text for all functions within this class
    */
    public String updateStatusText() {
        //return text
        return this.statusText;
    }
}