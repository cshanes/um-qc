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

@SuppressWarnings("serial")
public class DepthSelectionFieldViewPanel extends JPanel implements IView {

    SelectionController controller;
    
    String[] labels = {"Distance from top: ", "Height: "};
    
    private JSpinner ySpinner;
    private JSpinner heightSpinner;
    JSpinner[] spinners = new JSpinner[labels.length];
    
    private SpinnerNumberModel ySpinnerModel;
    private SpinnerNumberModel heightSpinnerModel;
    
    int maxHeight;
    
    public DepthSelectionFieldViewPanel(SelectionController controller, Rectangle r, int maxHeight) {
        this.controller = controller;
        this.maxHeight = maxHeight;
        
        initComponents(r.y, r.height, maxHeight);
    }

    private void initComponents(int y, int curHeight, int maxHeight) {
        ySpinnerModel = new SpinnerNumberModel(y, 0, maxHeight - curHeight, 1);
        heightSpinnerModel = new SpinnerNumberModel(curHeight, 1, maxHeight - y, 1);
        
        spinners[0] = ySpinner = new JSpinner(ySpinnerModel);
        spinners[1] = heightSpinner = new JSpinner(heightSpinnerModel);
        
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

    private void ySpinnerStateChanged(ChangeEvent e) {
        controller.changeSelectionY((Integer)ySpinner.getValue());
    }

    private void heightSpinnerStateChanged(ChangeEvent e) {
        controller.changeSelectionHeight((Integer)heightSpinner.getValue());
    }


}
