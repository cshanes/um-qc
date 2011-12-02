import java.awt.Cursor;
import java.awt.Rectangle;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.process.StackConverter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import model.SelectionModel;

import util.QualityControlHelper;
import view.ResizableSelection;
import view.RightClickPopup;

import controller.SelectionController;

@SuppressWarnings("serial")
public class Ultrasound_Median extends JFrame {
    
    private int locations[] = { SwingConstants.NORTH, SwingConstants.SOUTH };

    private int cursors[] = { Cursor.N_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR };
    
    ImagePlus medianImg;
    Rectangle originalSelection;
    
    public Ultrasound_Median() { 
        this(WindowManager.getCurrentImage());
    }

    public Ultrasound_Median(ImagePlus img) {
        super("Ultrasound Median");
        
        
        
        if(img == null) {
            IJ.noImage();
            return;
        }
        
        if(img.getType() != ImagePlus.GRAY8) {
            StackConverter sc = new StackConverter(img);
            sc.convertToGray8();
        }
        
        medianImg = QualityControlHelper.getMedianImageFromStack(img.getImageStack());
        originalSelection = QualityControlHelper.findOptimalDepthRegion(medianImg);
        
        /* model */
        final SelectionModel selection = new SelectionModel();
        selection.init(originalSelection);
        
        /* controller */
        SelectionController controller = new SelectionController();
        
        /* views */
        DepthSelectionFieldPanel fieldPanel = new DepthSelectionFieldPanel(controller, 
                                                                           originalSelection, 
                                                                           img.getHeight());
        RightClickPopup menu = new RightClickPopup(originalSelection, controller);
        ResizableSelection resizer = new ResizableSelection(new JLabel(), 
                                                            controller, 
                                                            originalSelection, 
                                                            menu, 
                                                            locations, 
                                                            cursors, 
                                                            img.getWidth(), 
                                                            img.getHeight());
        
        controller.addView(menu);
        controller.addView(resizer);
        controller.addView(fieldPanel);
        controller.addModel(selection);
        
        this.add(new UltrasoundMedianPanel(medianImg, fieldPanel, selection, menu, resizer));
        this.pack();
        this.setVisible(true);
        
    }
    
    

}
