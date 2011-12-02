import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.SelectionModel;

import util.TrimUltrasoundHelper;
import view.ResizableSelection;
import view.RightClickPopup;
import view.ScanSelectionFieldPanel;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.ImageWindow;

/**
 * This class extends TrimUltrasoundPanel and overrides the abstract method 
 * <code> setupCropButtonActionListener</code> so that the crop button will
 * have different behavior when it is used for the stand alone Trim_Ultrasound
 * plug-in
 * 
 * @author chris
 *
 */
@SuppressWarnings("serial")
public class TrimUltrasoundStandalonePanel extends TrimUltrasoundPanel {
    
    private ImagePlus img;
    private ImageStack stack;
    private SelectionModel selection;
    private int maxWidth;
    private int maxHeight;

    public TrimUltrasoundStandalonePanel(ImagePlus img, ScanSelectionFieldPanel valuesPanel, 
            ResizableSelection resizer, RightClickPopup menu, SelectionModel selection, 
            int maxWidth, int maxHeight) {
        super(img, valuesPanel, resizer, menu);
        this.img = img;
        this.stack = img.getStack();
        this.selection = selection;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    protected void setupCropButtonActionListener() {
        cropButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            Rectangle curSelection = selection.toRectangle();
            if(curSelection.x < 0 || 
               curSelection.y < 0 || 
               ((curSelection.x + curSelection.width) > maxWidth) || 
               ((curSelection.y + curSelection.height) > maxHeight)) {
                IJ.error("Selection must be within image dimensions");
                return;
            }
            ImageStack croppedStack = TrimUltrasoundHelper.createNewCroppedImageStack(stack, selection.toRectangle());
            
            ImagePlus newImg = new ImagePlus(img.getTitle(), croppedStack);
            ImageWindow imgWin = new ImageWindow(newImg);
            WindowManager.setCurrentWindow(imgWin);
          }
       });
    }

}
