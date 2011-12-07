import ij.ImagePlus;
import ij.ImageStack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;

import model.SelectionModel;

import util.QualityControlHelper;
import util.TrimUltrasoundHelper;
import view.ResizableSelection;
import view.RightClickPopup;
import view.ScanSelectionFieldPanel;


@SuppressWarnings("serial")
public class TrimUltrasoundQCPanel extends TrimUltrasoundPanel {
    
    private ImagePlus img;
    private SelectionModel selection;
    private DepthSelectionPanel dsPanel = null;
    private JTabbedPane tabbedPane = null;
    
    public TrimUltrasoundQCPanel(ImagePlus img, SelectionModel selection, DepthSelectionPanel dsPanel, JTabbedPane tabbedPane, 
                        ScanSelectionFieldPanel valuesPanel, RightClickPopup menu, ResizableSelection resizer) {
        super(img, valuesPanel, resizer, menu);
        this.dsPanel = dsPanel;
        this.tabbedPane = tabbedPane;
        this.img = img;
        this.selection = selection;
    }

    @Override
    protected void setupCropButtonActionListener() {
        cropButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                ImageStack croppedStack = TrimUltrasoundHelper.createNewCroppedImageStack(img.getImageStack(), selection.toRectangle());
                ImagePlus newImg = QualityControlHelper.createMedianImageFromStackSelection(croppedStack, selection.toRectangle());
                dsPanel.setNewImage(newImg);
                
                tabbedPane.setSelectedIndex(dsPanel.TAB_INDEX);
            }
         });
    }

}
