///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
///**
// *
// * @author chris
// */
//import ij.IJ;
//import ij.ImagePlus;
//import ij.ImageStack;
//import ij.WindowManager;
//import ij.process.StackConverter;
//
//import java.awt.Cursor;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JTabbedPane;
//import javax.swing.SwingConstants;
//
//import view.ResizableSelection;
//import view.RightClickPopup;
//import view.ScanSelectionFieldPanel;
//import controller.SelectionController;
//
//@SuppressWarnings("serial")
//public class Quality_Control extends JFrame {
//    
//    private int scanSelectionLocations[] = { SwingConstants.NORTH, SwingConstants.SOUTH,
//            SwingConstants.WEST, SwingConstants.EAST,
//            SwingConstants.NORTH_WEST, SwingConstants.NORTH_EAST,
//            SwingConstants.SOUTH_WEST, SwingConstants.SOUTH_EAST };
//
//    private int scanSelectionCursors[] = { Cursor.N_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
//            Cursor.W_RESIZE_CURSOR, Cursor.E_RESIZE_CURSOR,
//            Cursor.NW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
//            Cursor.SW_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR };
//    
//    private int depthSelectionLocations[] = { SwingConstants.NORTH, SwingConstants.SOUTH };
//
//    private int depthSelectionCursors[] = { Cursor.N_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR };
//    
//    private JButton newProfileButton = new JButton("Set New Profile");
//    
//    private ImagePlus originalImg;
//    
//    private Rectangle depthSelection;
//    
//    TrimUltrasoundPanel trimUltrasoundPanel;
//    DepthSelectionPanel depthSelectionPanel;
//    ResultsPanel resultsPanel;
//    
//    private JTabbedPane mainTabbedPane = new JTabbedPane();
//
//    private ImagePlus medianImg;
//    
//    public Quality_Control() {
//        super("Quality Control");
//        this.setLayout(new GridBagLayout());
//        
//        originalImg = WindowManager.getCurrentImage();
//        if(originalImg == null) {
//            IJ.noImage();
//            return;
//        }
//        //Selected image must be a stack
//        if(originalImg.getNSlices() < 2) {
//            IJ.error("Error: No Stack", "Must run Quality_Control on a stack");
//            return;
//        }
//
//        if(originalImg.getType() != ImagePlus.GRAY8) {
//            StackConverter sc = new StackConverter(originalImg);
//            sc.convertToGray8();
//        }
//        
//        
//        initDepthSelectionPanel();
//        initScanSelectionPanel();
//        initResultsPanel();
//        
//        mainTabbedPane.addTab("Scan Selection", trimUltrasoundPanel);
//        mainTabbedPane.addTab("Depth Selection", depthSelectionPanel);
//        mainTabbedPane.addTab("Results", resultsPanel);
//        
////        //Algorithmically find region of interest from Ultrasound stack
////        Rectangle selection = TrimUltrasoundHelper.findScanOutline(originalImg.getStack(), true);
////        
////        //Get median image from trimmed stack
////        trimmedImg = QualityControlHelper.createMedianImageFromStackSelection(originalImg.getStack(), selection);
////        
////        //Autoselect starting depth region
////        depthSelection = QualityControlHelper.findOptimalDepthRegion(trimmedImg);
////        trimmedImg.setRoi(depthSelection);
////        
////        profile = new Profile(trimmedImg);
////        resultsPanel = new ResultsPanel(profile, trimmedImg.getBufferedImage(), depthSelection, this);
////        depthSelectionPanel = new DepthSelectionPanel(trimmedImg, depthSelection, profile, resultsPanel, mainTabbedPane);
////        trimUltrasoundPanel = new TrimUltrasoundQCPanel(originalImg, selection, depthSelectionPanel, mainTabbedPane);
////        
////        mainTabbedPane.addTab("Scan Selection", trimUltrasoundPanel);
////        mainTabbedPane.add("Depth Selection", depthSelectionPanel);
////        mainTabbedPane.add("Results", resultsPanel);
//        GridBagConstraints c = new GridBagConstraints();
//        c.weightx = 1.0; c.weighty = 0.8;
//        c.fill = GridBagConstraints.BOTH;
//        this.add(mainTabbedPane, c);
//
//        this.pack();
//        this.setVisible(true);
////        mainTabbedPane.setSelectedIndex(resultsPanel.TAB_INDEX);
//    }
//    
//    private void initResultsPanel() {
////        medianImg.setRoi(depthSelection);
//        resultsPanel = new ResultsPanel(new Profile(medianImg, depthSelection), medianImg.getBufferedImage(), depthSelection);
//    }
//
//    private void initDepthSelectionPanel() {
//        Rectangle scanSelection = TrimUltrasoundHelper.findScanOutline(originalImg.getStack(), true);
//        ImageStack croppedStack = TrimUltrasoundHelper.createNewCroppedImageStack(originalImg.getImageStack(), scanSelection);
//        medianImg = QualityControlHelper.getMedianImageFromStack(croppedStack);
//        depthSelection = QualityControlHelper.findOptimalDepthRegion(medianImg);
//        
//        /* model */
//        final SelectionModel selection = new SelectionModel();
//        selection.init(depthSelection);
//        
//        /* controller */
//        SelectionController controller = new SelectionController();
//        
//        /* views */
//        DepthSelectionFieldPanel fieldPanel = new DepthSelectionFieldPanel(controller, 
//                                                                           depthSelection, 
//                                                                           originalImg.getHeight());
//        RightClickPopup menu = new RightClickPopup(depthSelection, controller);
//        ResizableSelection resizer = new ResizableSelection(new JLabel(), 
//                                                            controller, 
//                                                            depthSelection, 
//                                                            menu, 
//                                                            depthSelectionLocations, 
//                                                            depthSelectionCursors, 
//                                                            originalImg.getWidth(), 
//                                                            originalImg.getHeight());
//        
//        controller.addView(menu);
//        controller.addView(resizer);
//        controller.addView(fieldPanel);
//        controller.addModel(selection);
//        
//        newProfileButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
////                mediaHnImg.setRoi(selection.toRectangle());
//                resultsPanel.setProfile(new Profile(medianImg, selection.toRectangle()), medianImg.getBufferedImage());
//                
//                mainTabbedPane.setSelectedIndex(resultsPanel.TAB_INDEX);
//            }
//        });
//        
//        depthSelectionPanel = new DepthSelectionPanel(medianImg, fieldPanel, selection, menu, resizer, newProfileButton);
//        
//    }
//
//    private void initScanSelectionPanel() {
//        Rectangle originalSelection = TrimUltrasoundHelper.findScanOutline(originalImg.getStack(), true);
//        
//        /* model */
//        SelectionModel selection = new SelectionModel();
//        selection.init(originalSelection);
//        
//        /* controller */
//        SelectionController controller = new SelectionController(); 
//
//        /* views */
//        ScanSelectionFieldPanel valuePanel = new ScanSelectionFieldPanel(controller, originalSelection, 
//                originalImg.getWidth(), originalImg.getHeight());
//        RightClickPopup menu = new RightClickPopup(originalSelection, controller);
//        ResizableSelection resizer = new ResizableSelection(new JLabel(), controller, originalSelection, 
//                                            menu, scanSelectionLocations, scanSelectionCursors, originalImg.getWidth(), originalImg.getHeight());
//        
//        
//        
//        controller.addView(menu);
//        controller.addView(resizer);
//        controller.addView(valuePanel);
//        controller.addModel(selection);
//        
//        trimUltrasoundPanel = new TrimUltrasoundQCPanel(originalImg, selection, depthSelectionPanel, 
//                mainTabbedPane, valuePanel, menu, resizer);
//        
//    }
//}
