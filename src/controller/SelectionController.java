package controller;

public class SelectionController extends AbstractSelectionController {
    
    public static final String SELECTION_X_PROPERTY = "X";
    public static final String SELECTION_Y_PROPERTY = "Y";
    public static final String SELECTION_WIDTH_PROPERTY = "Width";
    public static final String SELECTION_HEIGHT_PROPERTY = "Height";
    
    public void changeSelectionX(int newX) {
        setModelProperty(SELECTION_X_PROPERTY, newX);
    }
    
    public void changeSelectionY(int newY) {
        setModelProperty(SELECTION_Y_PROPERTY, newY);
    }
    
    public void changeSelectionWidth(int newWidth) {
        setModelProperty(SELECTION_WIDTH_PROPERTY, newWidth);
    }
    
    public void changeSelectionHeight(int newHeight) {
        setModelProperty(SELECTION_HEIGHT_PROPERTY, newHeight);
    }
    
}
