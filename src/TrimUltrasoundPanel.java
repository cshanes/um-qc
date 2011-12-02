import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import ij.ImagePlus;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import util.TrimUltrasoundHelper;
import view.ResizableSelection;
import view.RightClickPopup;
import view.ScanSelectionFieldPanel;

@SuppressWarnings("serial")
/**
 * Base TrimUltrasoundPanel class.  This panel is used to select the ROI on an ultrasound image.
 */
public abstract class TrimUltrasoundPanel extends JPanel {

    private Rectangle originalSelection;
    private BufferedImage selectionImage;
    private ResizableSelection resizer;
    private RightClickPopup menu;
    
    private JLayeredPane imagePanel = new JLayeredPane();
    private JLabel imageLabel;
    
    private ScanSelectionFieldPanel valuesPanel;
    
    protected JButton cropButton = new JButton("Crop");
    
    public TrimUltrasoundPanel(ImagePlus img, ScanSelectionFieldPanel valuePanel, ResizableSelection resizer, RightClickPopup menu) {
        this(img, TrimUltrasoundHelper.findScanOutline(img.getStack(), false), valuePanel, resizer, menu);
    }
    
    /**
     * @param args
     */
    public TrimUltrasoundPanel(ImagePlus img, Rectangle selection, ScanSelectionFieldPanel valuePanel, ResizableSelection resizer, RightClickPopup menu) {
        
        this.originalSelection = new Rectangle(selection);
        this.valuesPanel = valuePanel;
        this.resizer = resizer;
        this.menu = menu;
        
        selectionImage = img.getBufferedImage();
        
        img.setRoi(selection);

        initComponents();
        
        drawSelection();
        
        this.setVisible(true);
    }
    
    private void initComponents() {
        setupActionListeners();
        setupCropButtonActionListener();
        constructImagePanel();
        
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
        this.add(cropButton, c);
    }
    
    private void constructImagePanel() {
        imageLabel = new JLabel(new ImageIcon(selectionImage));
        imageLabel.setSize(selectionImage.getWidth(), selectionImage.getHeight());
        imagePanel.add(imageLabel, JLayeredPane.DEFAULT_LAYER);
        
        imagePanel.add(resizer, JLayeredPane.PALETTE_LAYER);
        
        imagePanel.setSize(selectionImage.getWidth(), selectionImage.getHeight());
        imagePanel.setMinimumSize(new Dimension(selectionImage.getWidth(), selectionImage.getHeight()));
    }
    
    private void drawSelection() {
        Graphics2D graphics = selectionImage.createGraphics();
        graphics.draw(originalSelection);
        
        this.validate();
        this.repaint();
    }
    
    private void setupActionListeners() {
        
        imagePanel.addMouseListener(new MouseInputAdapter() {
            public void mousePressed(MouseEvent me) {
                // right click
                if (me.getButton() == MouseEvent.BUTTON3) {
                    doPop(me);
                }
            }
        });
    }
    
    private void doPop(MouseEvent e) {
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
    
    protected abstract void setupCropButtonActionListener();
}
