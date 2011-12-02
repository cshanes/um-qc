package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import controller.SelectionController;

/**
 * Resizable selection is a resizable JComponent. For an explanation of this
 * implementation, see :
 * 
 * http://zetcode.com/tutorials/javaswingtutorial/resizablecomponent/
 * 
 */
@SuppressWarnings("serial")
public class ResizableSelection extends JComponent implements IView {

    boolean isDepthResizer = false;
    SelectionController controller;
    RightClickPopup menu;

    int maxWidth;
    int maxHeight;

    public ResizableSelection(Component comp, SelectionController controller, Rectangle originalSelection,
            RightClickPopup menu, int[] locations, int[] cursors, int maxWidth, int maxHeight) {
        this(comp, controller, menu, new ResizableBorder(8, locations, cursors), originalSelection, maxWidth, maxHeight);

        if (locations.length == 2) {
            isDepthResizer = true;
        }
    }

    public ResizableSelection(Component comp, SelectionController controller, RightClickPopup menu,
            ResizableBorder border, Rectangle originalSelection, int maxWidth, int maxHeight) {
        setLayout(new BorderLayout());
        add(comp);
        addMouseListener(resizeListener);
        addMouseMotionListener(resizeListener);
        setBorder(border);
        this.setBounds(originalSelection);

        this.controller = controller;
        this.menu = menu;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    private void resize() {
        if (getParent() != null) {
            ((JComponent) getParent()).revalidate();
        }
    }

    MouseInputListener resizeListener = new MouseInputAdapter() {
        public void mouseMoved(MouseEvent me) {
            if (hasFocus()) {
                ResizableBorder border = (ResizableBorder) getBorder();
                setCursor(Cursor.getPredefinedCursor(border.getCursor(me)));
            }
        }

        public void mouseExited(MouseEvent mouseEvent) {
            setCursor(Cursor.getDefaultCursor());
        }

        private int cursor;
        private Point startPos = null;

        public void mousePressed(MouseEvent me) {
            // right click
            if (me.getButton() == MouseEvent.BUTTON3) {
                doPop(me);
            }

            ResizableBorder border = (ResizableBorder) getBorder();
            cursor = border.getCursor(me);
            startPos = me.getPoint();
            requestFocus();
            repaint();
        }

        public void mouseDragged(MouseEvent me) {

            if (startPos != null) {

                int x = getX();
                int y = getY();
                int w = getWidth();
                int h = getHeight();

                int dx = me.getX() - startPos.x;
                int dy = me.getY() - startPos.y;

                switch (cursor) {
                case Cursor.N_RESIZE_CURSOR:
                    if (!(h - dy < 50)) {
                        if (hitNorthBoundary(y, dy)) {
                            dy = (0 - y);
                            setBounds(x, 0, w, h - dy);
                        } else {
                            setBounds(x, y + dy, w, h - dy);
                        }
                        resize();

                        yValueChanged(y + dy);
                        heightValueChanged(h - dy);
                    }
                    break;

                case Cursor.S_RESIZE_CURSOR:
                    if (!(h + dy < 50)) {
                        if (hitSouthBoundary(y, dy, h)) {
                            dy = maxHeight - h - y;
                            setBounds(x, y, w, maxHeight - y);
                        } else {
                            setBounds(x, y, w, h + dy);
                        }
                        startPos = me.getPoint();
                        resize();

                        heightValueChanged(h + dy);
                    }
                    break;

                case Cursor.W_RESIZE_CURSOR:
                    if (!(w - dx < 50)) {
                        if (hitWestBoundary(x, dx)) {
                            dx = (0 - x);
                            setBounds(0, y, w - dx, h);
                        } else {
                            setBounds(x + dx, y, w - dx, h);
                        }
                        resize();

                        xValueChanged(x + dx);
                        widthValueChanged(w - dx);
                    }
                    break;

                case Cursor.E_RESIZE_CURSOR:
                    if (!(w + dx < 50)) {
                        if (hitEastBoundary(x, dx, w)) {
                            dx = maxWidth - w - x;
                            setBounds(x, y, maxWidth - x, h);
                        } else {
                            setBounds(x, y, w + dx, h);
                        }
                        startPos = me.getPoint();
                        resize();

                        widthValueChanged(w + dx);
                    }
                    break;

                case Cursor.NW_RESIZE_CURSOR:
                    if (!(w - dx < 50) && !(h - dy < 50)) {
                        if (hitWestBoundary(x, dx)) { 
                            dx = (0 - x);
                            setBounds(0, y, w - dx, h);
                        }
                        if(hitNorthBoundary(y, dy)) {
                            dy = (0 - y);
                            setBounds(x, 0, w, h - dy);
                        }
                        
                        setBounds(x + dx, y + dy, w - dx, h - dy);
                        resize();

                        xValueChanged(x + dx);
                        yValueChanged(y + dy);
                        widthValueChanged(w - dx);
                        heightValueChanged(h - dy);
                    }
                    break;

                case Cursor.NE_RESIZE_CURSOR:
                    if (!(w + dx < 50) && !(h - dy < 50)) {
                        if(hitEastBoundary(x, dx, w)) {
                            dx = maxWidth - w - x;
                            setBounds(x, y, maxWidth - x, h);
                        }
                        if (hitNorthBoundary(y, dy)) {
                            dy = (0 - y);
                            setBounds(x, 0, w, h - dy);
                        }
                        setBounds(x, y + dy, w + dx, h - dy);
                        startPos = new Point(me.getX(), startPos.y);
                        resize();

                        yValueChanged(y + dy);
                        widthValueChanged(w + dx);
                        heightValueChanged(h - dy);
                    }
                    break;

                case Cursor.SW_RESIZE_CURSOR:
                    if (!(w - dx < 50) && !(h + dy < 50)) {
                        if(hitWestBoundary(x, dx)) {
                            dx = (0 - x);
                            setBounds(0, y, w - dx, h);
                        }
                        if(hitSouthBoundary(y, dy, h)) {
                            dy = maxHeight - h - y;
                            setBounds(x, y, w, maxHeight - y);
                        }
                        setBounds(x + dx, y, w - dx, h + dy);
                        startPos = new Point(startPos.x, me.getY());
                        resize();

                        xValueChanged(x + dx);
                        widthValueChanged(w - dx);
                        heightValueChanged(h + dy); 
                    }
                    break;

                case Cursor.SE_RESIZE_CURSOR:
                    if (!(w + dx < 50) && !(h + dy < 50)) {
                        if(hitEastBoundary(x, dx, w)) {
                            dx = maxWidth - w - x;
                            setBounds(x, y, maxWidth - x, h);
                        }
                        if(hitSouthBoundary(y, dy, h)) {
                            dy = maxHeight - h - y;
                            setBounds(x, y, w, maxHeight - y);
                        }
                        setBounds(x, y, w + dx, h + dy);
                        startPos = me.getPoint();
                        resize();

                        widthValueChanged(w + dx);
                        heightValueChanged(h + dy);
                    }
                    break;

                case Cursor.MOVE_CURSOR:
                    // TODO: don't let user move the selection out of image area
                    Rectangle bounds = getBounds();
                    
                    if(x + dx < 0) 
                        dx = (0 - x);
                    else if(x + dx + w > maxWidth) 
                        dx = (maxWidth - (x + w));
                    
                    if(y + dy < 0)
                        dy = (0 - y);
                    else if(y + dy + h > maxHeight)
                        dy = (maxHeight - (y + h));
                    
                    // if this is a depthResizer, only allow vertical
                    // translation
                    if (isDepthResizer) {
                        bounds.translate(0, dy);
                    } else {
                        bounds.translate(dx, dy);
                    }
                    setBounds(bounds);
                    resize();

                    xValueChanged(x + dx);
                    yValueChanged(y + dy);
                }

                setCursor(Cursor.getPredefinedCursor(cursor));
            }
        }

        public void mouseReleased(MouseEvent mouseEvent) {
            startPos = null;
        }
    };
    
    private boolean hitWestBoundary(int x, int dx) {
        return (x + dx < 0);
    }
    
    private boolean hitNorthBoundary(int y, int dy) {
        return (y + dy < 0);
    }
    
    private boolean hitEastBoundary(int x, int dx, int w) {
        return (w + x + dx > maxWidth);
    }
    
    private boolean hitSouthBoundary(int y, int dy, int h) {
        return (h + y + dy > maxHeight);
    }

    private void doPop(MouseEvent e) {
        // RightClickPopup menu = new RightClickPopup(this, originalSelection);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        if (evt.getPropertyName().equals(SelectionController.SELECTION_X_PROPERTY)) {
            Integer newValue = (Integer) evt.getNewValue();
            if (!(((Integer) x).equals(newValue))) {
                setBounds(newValue, y, width, height);
            }
        }
        if (evt.getPropertyName().equals(SelectionController.SELECTION_Y_PROPERTY)) {
            Integer newValue = (Integer) evt.getNewValue();
            if (!(((Integer) y).equals(newValue))) {
                setBounds(x, newValue, width, height);
            }
        }
        if (evt.getPropertyName().equals(SelectionController.SELECTION_WIDTH_PROPERTY)) {
            Integer newValue = (Integer) evt.getNewValue();
            if (!(((Integer) width).equals(newValue))) {
                setBounds(x, y, newValue, height);
            }
        }
        if (evt.getPropertyName().equals(SelectionController.SELECTION_HEIGHT_PROPERTY)) {
            Integer newValue = (Integer) evt.getNewValue();
            if (!(((Integer) height).equals(newValue))) {
                setBounds(x, y, width, newValue);
            }
        }
    }

    private void xValueChanged(Integer newX) {
        controller.changeSelectionX(newX);
    }

    private void yValueChanged(Integer newY) {
        controller.changeSelectionY(newY);
    }

    private void widthValueChanged(Integer newWidth) {
        controller.changeSelectionWidth(newWidth);
    }

    private void heightValueChanged(Integer newHeight) {
        controller.changeSelectionHeight(newHeight);
    }
    
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}
