import java.awt.Rectangle;

import model.AbstractModel;

import controller.SelectionController;

public class SelectionModel extends AbstractModel {
    
    private int x;
    private int y;
    private int width;
    private int height;
    
    public void init(Rectangle r) {
        setX(r.x);
        setY(r.y);
        setWidth(r.width);
        setHeight(r.height);
    }
    
    public void setX(Integer x) {
        int oldX = this.x;
        this.x = x;
        
        firePropertyChange(SelectionController.SELECTION_X_PROPERTY, oldX, x);
    }
    
    public void setY(Integer y) {
        int oldY = this.y;
        this.y = y;
        
        firePropertyChange(SelectionController.SELECTION_Y_PROPERTY, oldY, y);
    }
    
    public void setWidth(Integer width) {
        int oldWidth = this.width;
        this.width = width;
        firePropertyChange(SelectionController.SELECTION_WIDTH_PROPERTY, oldWidth, width);
    }
    
    public void setHeight(Integer height) {
        int oldHeight = this.height;
        this.height = height;
        
        firePropertyChange(SelectionController.SELECTION_HEIGHT_PROPERTY, oldHeight, height);
    }
    
    public Rectangle toRectangle() {
        return new Rectangle(x, y, width, height);
    }

}
