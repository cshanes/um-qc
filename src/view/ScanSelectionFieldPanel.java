package view;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.SelectionController;

public class ScanSelectionFieldPanel extends JPanel implements IView {
    
    SelectionController controller;
    
    String[] labels = {"Distance from left: ", "Width: ", 
                       "Distance from top: ", "Height: "};
    
    private JSpinner xSpinner;
    private JSpinner widthSpinner;
    private JSpinner ySpinner;
    private JSpinner heightSpinner;
    JSpinner[] spinners = new JSpinner[labels.length];
    
    
    private SpinnerNumberModel xSpinnerModel;
    private SpinnerNumberModel widthSpinnerModel;
    private SpinnerNumberModel ySpinnerModel;
    private SpinnerNumberModel heightSpinnerModel;
    
    int maxWidth;
    int maxHeight;
    
    public ScanSelectionFieldPanel(SelectionController controller, Rectangle r, int maxWidth, int maxHeight) {
        this.controller = controller;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        
        initComponents(r.x, r.y, r.width, r.height, maxWidth, maxHeight);
    }

    private void initComponents(int x, int y, int curWidth, int curHeight, int maxWidth, int maxHeight) {
        xSpinnerModel = new SpinnerNumberModel(x, 0, maxWidth - curWidth, 1);
        widthSpinnerModel = new SpinnerNumberModel(curWidth, 1, maxWidth - x, 1);
        ySpinnerModel = new SpinnerNumberModel(y, 0, maxHeight - curHeight, 1);
        heightSpinnerModel = new SpinnerNumberModel(curHeight, 1, maxHeight - y, 1);
        
        spinners[0] = xSpinner = new JSpinner(xSpinnerModel);
        spinners[1] = widthSpinner = new JSpinner(widthSpinnerModel);
        spinners[2] = ySpinner = new JSpinner(ySpinnerModel);
        spinners[3] = heightSpinner = new JSpinner(heightSpinnerModel);
        
        xSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                xSpinnerStateChanged(e);
            }
        });
        widthSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                widthSpinnerStateChanged(e);
            }
        });
        ySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ySpinnerStateChanged(e);
            }
        });
        heightSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                heightSpinnerStateChanged(e);
            }
        });
        
        this.setLayout(new SpringLayout());

        //add components
        for(int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
            l.setLabelFor(spinners[i]);
            this.add(l);
            this.add(spinners[i]);
        }
        
        SpringUtilities.makeCompactGrid(this,
                                        labels.length, 2, //row, cols
                                        6, 6,             //initX, initY
                                        6, 6);            //xPad, yPad
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(SelectionController.SELECTION_X_PROPERTY)) {
            Integer newValue = (Integer) evt.getNewValue();
            if(!xSpinner.getValue().equals(newValue)) {
                xSpinner.setValue(newValue);
            }
            widthSpinnerModel.setMaximum(maxWidth - (Integer)xSpinner.getValue());
        }
        if(evt.getPropertyName().equals(SelectionController.SELECTION_WIDTH_PROPERTY)) {
            Integer newValue = (Integer) evt.getNewValue();
            if(!widthSpinner.getValue().equals(newValue)) {
                widthSpinner.setValue(newValue);
            }
            xSpinnerModel.setMaximum(maxWidth - (Integer)widthSpinner.getValue());
        }
        if(evt.getPropertyName().equals(SelectionController.SELECTION_Y_PROPERTY)) {
            Integer newValue = (Integer) evt.getNewValue();
            if(!ySpinner.getValue().equals(newValue)) {
                ySpinner.setValue(newValue);
            }
            heightSpinnerModel.setMaximum(maxHeight - (Integer)ySpinner.getValue());
        }
        if(evt.getPropertyName().equals(SelectionController.SELECTION_HEIGHT_PROPERTY)) {
            Integer newValue = (Integer) evt.getNewValue();
            if(!heightSpinner.getValue().equals(newValue)) {
                heightSpinner.setValue(newValue);
            }
            ySpinnerModel.setMaximum(maxHeight - (Integer)heightSpinner.getValue());
        }

    }

    private void xSpinnerStateChanged(ChangeEvent e) {
        controller.changeSelectionX((Integer)xSpinner.getValue());
    }
    
    private void widthSpinnerStateChanged(ChangeEvent e) {
        controller.changeSelectionWidth((Integer)widthSpinner.getValue());
    }
    
    private void ySpinnerStateChanged(ChangeEvent e) {
        controller.changeSelectionY((Integer)ySpinner.getValue());
    }

    private void heightSpinnerStateChanged(ChangeEvent e) {
        controller.changeSelectionHeight((Integer)heightSpinner.getValue());
    }
}
