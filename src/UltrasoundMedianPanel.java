import ij.ImagePlus;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import model.SelectionModel;

import util.QualityControlHelper;
import view.ResizableSelection;
import view.RightClickPopup;

@SuppressWarnings("serial")
public class UltrasoundMedianPanel extends JPanel {
    
    private JButton createProfileButton = new JButton("Update Profile Graph");
    private SelectionModel selection;
    private ProfilePanel profilePanel;
    
    
    public UltrasoundMedianPanel(ImagePlus img, DepthSelectionFieldPanel panel, SelectionModel selection, 
            RightClickPopup menu, ResizableSelection resizer) {
        
        ImagePlus medianImg = QualityControlHelper.getMedianImageFromStack(img.getImageStack());
        Rectangle originalSelection = QualityControlHelper.findOptimalDepthRegion(medianImg);
        
        this.selection = selection;
        this.profilePanel = new ProfilePanel(medianImg, originalSelection);
        
        initComponents(medianImg, panel, selection, menu, resizer);
        setupActionListeners();
    }
    
    private void initComponents(ImagePlus medianImg, DepthSelectionFieldPanel fieldPanel, SelectionModel selection,
            RightClickPopup menu, ResizableSelection resizer) {
        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.15;
        this.add(new DepthSelectionPanel(medianImg, fieldPanel, selection, menu, resizer, createProfileButton), c);
        c.gridx = 1; c.weightx = 0.85;
        this.add(profilePanel);
        this.setVisible(true);
    }
    
    private void setupActionListeners() {
        createProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                profilePanel.setProfile(selection.toRectangle());
            }
        });
    }

}
