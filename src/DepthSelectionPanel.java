import ij.IJ;
import ij.ImagePlus;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.event.MouseInputAdapter;

import profile.SradProfile;

import model.SelectionModel;

import util.QualityControlHelper;
import view.ResizableSelection;
import view.RightClickPopup;
import view.SpringUtilities;

@SuppressWarnings("serial")
public class DepthSelectionPanel extends JPanel {
    private ImagePlus img;
    private JLayeredPane imagePanel = new JLayeredPane();
    private JLabel imageLabel;
    private BufferedImage selectionImage;
    private ResizableSelection resizer;
    private RightClickPopup menu;
    protected final int TAB_INDEX = 1;

    private SelectionModel depthSelection;
    
    private JButton depthButton;
    private JButton exportProfileButton = new JButton("Export Profile");
    private JFileChooser fileChooser = new JFileChooser();
    
    private DepthSelectionFieldPanel fieldPanel;
    
    private JPanel leftPanel = new JPanel(new SpringLayout());
    
    public DepthSelectionPanel(ImagePlus img, DepthSelectionFieldPanel panel, SelectionModel depthSelection, 
            RightClickPopup menu, ResizableSelection resizer, JButton depthButton) {
        this.menu = menu;
        this.img = img;
        this.selectionImage = img.getBufferedImage();
        this.fieldPanel = panel;
        this.resizer = resizer;
        this.depthSelection = depthSelection;
        this.depthButton = depthButton;
        
        initComponents();
    }
    
    private void initComponents() {
        setupActionListeners();
        constructImagePanel();
        initLeftPanel();
        
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        this.add(leftPanel, c);
        
        c.gridx = 1; c.gridy = 0;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = selectionImage.getWidth();
        c.ipady = selectionImage.getHeight();
        this.add(imagePanel, c);
    }
    
    private void initLeftPanel() {
        leftPanel.add(fieldPanel);
        leftPanel.add(depthButton);
        leftPanel.add(exportProfileButton);
        
        SpringUtilities.makeCompactGrid(leftPanel,
                3, 1,             //row, cols
                6, 6,             //initX, initY
                6, 6);            //xPad, yPad
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
        
        exportProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showSaveDialog(DepthSelectionPanel.this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
//                    img.setRoi();
//                    try {
////                        QualityControlHelper.exportProfileToText(new SradProfile(img, depthSelection.toRectangle()), file);
//                    } catch (IOException e1) {
//                        IJ.error("Save file failed");
//                        return;
//                    }
                }
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
    
    private void doPop(MouseEvent e) {
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
    
    protected void setNewImage(ImagePlus img) {
        selectionImage = img.getBufferedImage();
        imagePanel.removeAll();
        constructImagePanel();
        this.revalidate();
        this.repaint();
        resizer.setMaxHeight(img.getHeight());
        
        Rectangle r = QualityControlHelper.findOptimalDepthRegion(img);
        
        //this is a temporary bug fix where findOptimalRegion can return a rectangle with 0 height
        if(r.height == 0) {
            r.height = 20;
        }
        
        depthSelection.setX(r.x);
        depthSelection.setY(r.y);
        depthSelection.setWidth(r.width);
        depthSelection.setHeight(r.height);
    }
}
