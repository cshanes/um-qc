package rectilinear;

import ij.ImagePlus;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import model.ProfileModel;
import model.SelectionModel;
import profile.Profile;
import profile.ProfilePanel;
import view.ResizableSelection;
import view.ScanSelectionFieldPanel;
import controller.ProfileController;

public class RectilinearProfilePanel extends JPanel {
    
    private static final long serialVersionUID = -4881521046669867771L;

    private ImagePlus img;

    private SelectionModel selection;
    private BufferedImage selectionImage;
    private ResizableSelection resizer;
    
    private ProfileModel profileModel;
    private ProfileController profileController;
    
    private JLayeredPane imagePanel = new JLayeredPane();
    private JLabel imageLabel;
    
    private ScanSelectionFieldPanel valuesPanel;
    
    protected JButton profileButton = new JButton("Get Profile");
    
    public RectilinearProfilePanel(ImagePlus img, SelectionModel selection, ScanSelectionFieldPanel valuePanel, ResizableSelection resizer) {
        
        this.selection = selection; 
        this.valuesPanel = valuePanel;
        this.resizer = resizer;
        this.selectionImage = img.getBufferedImage();
        this.img = img;
        this.profileController = new ProfileController();
        this.profileModel = new ProfileModel();
        
        initComponents();
        this.setVisible(true);
    }
    
    private void initComponents() {
        constructImagePanel();
        addProfileButtonActionListener();
        
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.ipady = 5;
        c.insets = new Insets(2, 2, 2, 2);
        c.gridx = 0; c.gridy = 0;
        this.add(valuesPanel, c);
        
         /* I needed to set the ipad parameters like this to get the image panel to
         * display correctly, I'm not sure why it doesn't work without them.
         */
        c.gridx = 1; c.gridy = 0;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = selectionImage.getWidth();
        c.ipady = selectionImage.getHeight();
        this.add(imagePanel, c);
        
        c.gridheight = 1;
        c.ipadx = 0; c.ipady = 5;
        c.gridx = 0; c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(profileButton, c);
    }
    
    private void addProfileButtonActionListener() {
        profileButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent arg0) {
               JFrame frame = new JFrame();
               frame.add(new ProfilePanel(new Profile(img, selection.toRectangle()), profileController, profileModel));
               frame.pack(); frame.setVisible(true);
           }
        });
        
    }

    private void constructImagePanel() {
        imageLabel = new JLabel(new ImageIcon(selectionImage));
        imageLabel.setSize(selectionImage.getWidth(), selectionImage.getHeight());
        imagePanel.add(imageLabel, JLayeredPane.DEFAULT_LAYER);
        
        imagePanel.add(resizer, JLayeredPane.PALETTE_LAYER);
        
        imagePanel.setSize(selectionImage.getWidth(), selectionImage.getHeight());
        imagePanel.setMinimumSize(new Dimension(selectionImage.getWidth(), selectionImage.getHeight()));
    }
    
}
