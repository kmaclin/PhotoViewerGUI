import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ItemListener;
import javax.swing.Box;

public class LeftMenu extends JPanel implements StatusUpdater {

    private String statusText = "Side Menu Selected";
    private ControlPanel controlPanel;
    private JRadioButton drawing = new JRadioButton("Drawing");
    private JRadioButton text = new JRadioButton("Text");
    public Box center = null;

    public JRadioButton family;
    public JRadioButton friends;
    public JRadioButton school;
    public JRadioButton work;

    public LeftMenu(ControlPanel panel) {
        super();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, this.getHeight()));
        setBackground(new Color(35, 35, 35));
        this.controlPanel = panel;

        //make title
        JLabel photoSelection = new JLabel("PHOTO SELECTION");
        photoSelection.setFont(new Font("Verdana", Font.PLAIN, 12));
        photoSelection.setForeground(Color.WHITE);
        photoSelection.setAlignmentX(CENTER_ALIGNMENT);

        this.add(photoSelection, BorderLayout.NORTH);


        addToggleButtons(this);
        addAnnotationButtons(this);
        addPageButtons(this);
    }

    private void addToggleButtons(JPanel left) {
        Box center = new Box(BoxLayout.Y_AXIS);
        ButtonGroup toggleButtons = new ButtonGroup();

        JLabel title = new JLabel("Photo Tags");
        center.add(title);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        
        family = new JRadioButton("Family");
        //toggleButtons.add(family);
        family.setAlignmentX(LEFT_ALIGNMENT);
        center.add(family);

        friends = new JRadioButton("Friends");
        //toggleButtons.add(friends);
        friends.setAlignmentX(LEFT_ALIGNMENT);
        center.add(friends);

        school = new JRadioButton("School");
        //toggleButtons.add(school);
        school.setAlignmentX(LEFT_ALIGNMENT);
        center.add(school);

        work = new JRadioButton("Work");
        //toggleButtons.add(work);
        work.setAlignmentX(LEFT_ALIGNMENT);
        center.add(work);

        //item listener for work toggle
        ItemListener items = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                AbstractButton button = (AbstractButton)e.getSource();
                PhotoComponent shown = LeftMenu.this.controlPanel.getMainPanel().getShown();

                if (LeftMenu.this.controlPanel.getMainPanel().table.getMode() != ViewMode.BROWSER) {
                    if (button.getText() == work.getText()) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            shown.tags[3] = "work";
                            //work.setSelected(true);
                            LeftMenu.this.statusText = "Tagged as work";
                            LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
                        }
                        else if (e.getStateChange() == ItemEvent.DESELECTED) {
                            shown.tags[3] = null;
                            //work.setSelected(false);
                            LeftMenu.this.statusText = "Untagged as work";
                            LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
                        }
                    } else if (button.getText() == school.getText()) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            shown.tags[2] = "school";
                            //school.setSelected(true);
                            LeftMenu.this.statusText = "Tagged as school";
                            LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
                        }
                        else if (e.getStateChange() == ItemEvent.DESELECTED) {
                            shown.tags[2] = null;
                            //school.setSelected(false);
                            LeftMenu.this.statusText = "Untagged as school";
                            LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
                        }
                    } else if (button.getText() == friends.getText()) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            shown.tags[1] = "friends";
                            //friends.setSelected(true);
                            LeftMenu.this.statusText = "Tagged as friends";
                            LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
                        }
                        else if (e.getStateChange() == ItemEvent.DESELECTED) {
                            shown.tags[1] = null;
                            //friends.setSelected(false);
                            LeftMenu.this.statusText = "Untagged as friends";
                            LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
                        }
                    } else if (button.getText() == family.getText()) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            shown.tags[0] = "family";
                            //family.setSelected(true);
                            LeftMenu.this.statusText = "Tagged as family";
                            LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
                        }
                        else if (e.getStateChange() == ItemEvent.DESELECTED) {
                            shown.tags[0] = null;
                            //family.setSelected(false);
                            LeftMenu.this.statusText = "Untagged as family";
                            LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
                        }
                    }
                }
                
                

                repaint();
            }
        };

        work.addItemListener(items);
        school.addItemListener(items);
        friends.addItemListener(items);
        family.addItemListener(items);


        center.setAlignmentX(CENTER_ALIGNMENT);
        left.add(center, BorderLayout.NORTH);
    }

    //add annotation buttons: drawing and text
    private void addAnnotationButtons(JPanel left) {
        center = new Box(BoxLayout.Y_AXIS);

        ButtonGroup annotButtons = new ButtonGroup();

        JLabel title = new JLabel("Annotation Mode");
        center.add(title);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        
        //JRadioButton text = new JRadioButton("Text");
        annotButtons.add(text);
        text.setAlignmentX(LEFT_ALIGNMENT);
        center.add(text);

        //action listener for family toggle
        text.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {

                if ( LeftMenu.this.controlPanel.getMainPanel().getShown() != null) {

                     if(LeftMenu.this.controlPanel.getMainPanel().getShown().getTextSetting() == false) {

                         LeftMenu.this.controlPanel.getMainPanel().getShown().setTextEnabled(true);
                         if(LeftMenu.this.controlPanel.getMainPanel().getShown().getFlipped() == false) {

                            LeftMenu.this.controlPanel.getMainPanel().getShown().setFlipped(true);

                            
                         }
                         LeftMenu.this.controlPanel.getMainPanel().getShown().revalidate();
                         LeftMenu.this.controlPanel.getMainPanel().getShown().repaint();
                     }
                }

                LeftMenu.this.statusText = "Text Mode";
                LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
            }
        });

        //JRadioButton drawing = new JRadioButton("Drawing");
        annotButtons.add(drawing);
        drawing.setAlignmentX(LEFT_ALIGNMENT);
        center.add(drawing);

        //action listener for friends toggle
        drawing.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {

                if ( LeftMenu.this.controlPanel.getMainPanel().getShown() != null) {

                     if(LeftMenu.this.controlPanel.getMainPanel().getShown().getFlipped() == false) {

                         LeftMenu.this.controlPanel.getMainPanel().getShown().setFlipped(true);
                     }

                     LeftMenu.this.controlPanel.getMainPanel().getShown().setTextEnabled(false);
                }
               
                LeftMenu.this.controlPanel.getMainPanel().getShown().revalidate();
                LeftMenu.this.controlPanel.getMainPanel().getShown().repaint();

                LeftMenu.this.statusText = "Drawing Mode";
                LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
            }
        });


        center.setAlignmentX(CENTER_ALIGNMENT);
        left.add(center, BorderLayout.WEST);
    }

    private void addPageButtons(JPanel left) {
        Box center = new Box(BoxLayout.X_AXIS);

        ButtonGroup pageButtons = new ButtonGroup();

        JLabel title = new JLabel("Change Page");
        //center.add(title);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JButton prevPage = new JButton("Prev Page");
        pageButtons.add(prevPage);
        prevPage.setAlignmentX(LEFT_ALIGNMENT);
        center.add(prevPage);

        //action listener for friends toggle
        prevPage.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                if (LeftMenu.this.controlPanel.getMainPanel().table != null) {

                    LeftMenu.this.controlPanel.getMainPanel().table.prevPhoto();
                }


                LeftMenu.this.statusText = "Prev Page";
                LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
            }
        });

        JButton nextPage = new JButton("Next Page");
        pageButtons.add(nextPage);
        nextPage.setAlignmentX(LEFT_ALIGNMENT);
        center.add(nextPage);

        //action listener for family toggle
        nextPage.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {

                if (LeftMenu.this.controlPanel.getMainPanel().table != null) {

                    LeftMenu.this.controlPanel.getMainPanel().table.nextPhoto();

                }

                LeftMenu.this.statusText = "Next Page";
                LeftMenu.this.controlPanel.updateStatusBar(LeftMenu.this.statusText);
            }
        });


        center.setAlignmentX(CENTER_ALIGNMENT);
        left.add(center, BorderLayout.SOUTH);
    }

    public String updateStatusText() {
        return this.statusText;
    }
}