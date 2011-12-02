import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.process.StackConverter;

import java.awt.Cursor;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import model.SelectionModel;

import util.TrimUltrasoundHelper;
import view.ResizableSelection;
import view.RightClickPopup;
import view.ScanSelectionFieldPanel;

import controller.SelectionController;


@SuppressWarnings("serial")
public class Trim_Ultrasound extends JFrame{
    
    private int locations[] = { SwingConstants.NORTH, SwingConstants.SOUTH,
            SwingConstants.WEST, SwingConstants.EAST,
            SwingConstants.NORTH_WEST, SwingConstants.NORTH_EAST,
            SwingConstants.SOUTH_WEST, SwingConstants.SOUTH_EAST };

    private int cursors[] = { Cursor.N_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
            Cursor.W_RESIZE_CURSOR, Cursor.E_RESIZE_CURSOR,
            Cursor.NW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
            Cursor.SW_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR };
    
    public Trim_Ultrasound() {
        this(WindowManager.getCurrentImage());
    }
    
    /**
     * @param args
     */
    public Trim_Ultrasound(ImagePlus img) {
        super("Trim Ultrasound");
        
        if(img == null) {
            IJ.noImage();
            return;
        }
        if(img.getType() != ImagePlus.GRAY8){
            StackConverter sc = new StackConverter(img);
            sc.convertToGray8();
        }
        
        Rectangle originalSelection = TrimUltrasoundHelper.findScanOutline(img.getStack(), false);
        
        /* model */
        SelectionModel selection = new SelectionModel();
        selection.init(originalSelection);
        
        /* controller */
        SelectionController controller = new SelectionController(); 

        /* views */
        ScanSelectionFieldPanel valuePanel = new ScanSelectionFieldPanel(controller, originalSelection, 
                                            img.getWidth(), img.getHeight());
        RightClickPopup menu = new RightClickPopup(originalSelection, controller);
        ResizableSelection resizer = new ResizableSelection(new JLabel(), controller, originalSelection, 
                                            menu, locations, cursors, img.getWidth(), img.getHeight());
        
        
        
        controller.addView(menu);
        controller.addView(resizer);
        controller.addView(valuePanel);
        controller.addModel(selection);
        
        this.add(new TrimUltrasoundStandalonePanel(img, valuePanel, resizer, menu, 
                selection, img.getWidth(), img.getHeight()));
        this.pack();
        this.setVisible(true);
        
    }
}
