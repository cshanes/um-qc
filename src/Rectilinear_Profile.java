

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;

import java.awt.Cursor;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import model.ProfileModel;
import model.SelectionModel;
import rectilinear.RectilinearProfilePanel;
import view.ResizableSelection;
import view.RightClickPopup;
import view.ScanSelectionFieldPanel;
import controller.ProfileController;
import controller.SelectionController;

public class Rectilinear_Profile extends JFrame {
    
    private static final long serialVersionUID = -4332911281255893686L;

    private int scanSelectionLocations[] = { SwingConstants.NORTH, SwingConstants.SOUTH,
            SwingConstants.WEST, SwingConstants.EAST,
            SwingConstants.NORTH_WEST, SwingConstants.NORTH_EAST,
            SwingConstants.SOUTH_WEST, SwingConstants.SOUTH_EAST };

    private int scanSelectionCursors[] = { Cursor.N_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
            Cursor.W_RESIZE_CURSOR, Cursor.E_RESIZE_CURSOR,
            Cursor.NW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
            Cursor.SW_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR };
    
    private ImagePlus originalImg;
    private RectilinearProfilePanel panel;

    public Rectilinear_Profile() {
        super("Rectilinear Profile");
        
        this.originalImg = WindowManager.getCurrentImage();
        
        if(originalImg == null) {
            IJ.noImage();
            return;
        }
        
        if(originalImg.getNSlices() > 1) {
            IJ.error("Curvilinear Profile plugin must be run on a median image.");
            return;
        }
        
        initScanSelectionPanel();
        
        this.add(panel);
        this.pack();
        this.setVisible(true);
    }
    
    private void initScanSelectionPanel() {
        int selectionHeight = 50;
        if(originalImg.getHeight() < 50) {
            selectionHeight = originalImg.getHeight();
        }
        
        //TODO: make this safer (like height)
        Rectangle originalSelection = new Rectangle(5, 5, originalImg.getWidth() - 10, selectionHeight);
        
        /* model */
        SelectionModel selection = new SelectionModel();
        selection.init(originalSelection);
        
        /* controller */
        SelectionController controller = new SelectionController(); 
        
        /* views */
        ScanSelectionFieldPanel valuePanel = new ScanSelectionFieldPanel(controller, originalSelection, 
                originalImg.getWidth(), originalImg.getHeight());
        RightClickPopup menu = new RightClickPopup(originalSelection, controller);
        ResizableSelection resizer = new ResizableSelection(new JLabel(), controller, originalSelection, 
                                            menu, scanSelectionLocations, scanSelectionCursors, originalImg.getWidth(), originalImg.getHeight());
        
        
        controller.addView(menu);
        controller.addView(resizer);
        controller.addView(valuePanel);
        controller.addModel(selection);
        
        panel = new RectilinearProfilePanel(originalImg, selection, valuePanel, resizer);
    }

}
