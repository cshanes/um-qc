package view;

import java.beans.PropertyChangeEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import profile.Profile;
import controller.ProfileController;

/**
 * This class is a JPanel that is on the left hand side of the ProfilePanel.  
 * It is a view for controlling the profile MAD and percentile shown on the 
 * profile graph.  It also displays the mean and MAD/mean
 * 
 * @author cshanes
 *
 */
public class ProfileChartViewPanel extends JPanel implements IView {
    
    private static final long serialVersionUID = 717747672086520073L;

    Profile profile;
    
    ProfileController controller;

    private JSpinner madSpinner;
//    private JSpinner percentileSpinner;
    
    private SpinnerNumberModel madSpinnerModel;
//    private SpinnerNumberModel percentileSpinnerModel;
    
    private JLabel madLabel = new JLabel("MAD");
    private JLabel meanLabel = new JLabel("Mean");
    private JLabel madMeanLabel = new JLabel("MAD/mean");
    private JFormattedTextField madMeanValueField;
    private JFormattedTextField madValueField;
    private JFormattedTextField meanValueField;
    
    
    public ProfileChartViewPanel(ProfileController controller, Profile profile) {
        this.controller = controller;
        this.profile = profile;
        initComponents();
    }
    
    private void initComponents() {
        madLabel.setLabelFor(madValueField);
        madValueField = new JFormattedTextField(new Double(profile.getProfileMAD()));
        madValueField.setEditable(false);
        
        meanLabel.setLabelFor(meanValueField);
        meanValueField = new JFormattedTextField(new Double(profile.getProfileMean()));
        meanValueField.setEditable(false);
        
        madMeanLabel.setLabelFor(madMeanValueField);
        madMeanValueField = new JFormattedTextField(new Double(profile.getProfileMAD() / profile.getProfileMean()));
        madMeanValueField.setEditable(false);
        
        madSpinnerModel = new SpinnerNumberModel(1, 0, 50, 1);
//        percentileSpinnerModel = new SpinnerNumberModel(0.0f, 0.0f, 1.0f, 0.1f);
        
        madSpinner = new JSpinner(madSpinnerModel);
//        percentileSpinner = new JSpinner(percentileSpinnerModel);
        
        madSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                madSpinnerStateChanged(e);
            }
        });
        
//        percentileSpinner.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                percentileSpinnerStateChanged(e);
//            }
//        });
        
        this.setLayout(new SpringLayout());
        
        JLabel madNumLabel = new JLabel("# MAD", JLabel.TRAILING);
//        JLabel percentileLabel = new JLabel("Percentile", JLabel.TRAILING);
        madNumLabel.setLabelFor(madSpinner);
//        percentileLabel.setLabelFor(percentileSpinner);
        
        this.add(madNumLabel); this.add(madSpinner);
//        this.add(percentileLabel); this.add(percentileSpinner);
        this.add(madLabel); this.add(madValueField);
        this.add(meanLabel); this.add(meanValueField);
        this.add(madMeanLabel); this.add(madMeanValueField);
        
        SpringUtilities.makeCompactGrid(this,
                5, 2, //row, cols
                6, 6,             //initX, initY
                6, 6);            //xPad, yPad
        
    }

    protected void madSpinnerStateChanged(ChangeEvent e) {
        controller.changeProfileMad((Integer)madSpinner.getValue());
        madMeanValueField.setValue((Double)((Integer)madSpinner.getValue()*profile.getProfileMAD()) / profile.getProfileMean());
    }

//    protected void percentileSpinnerStateChanged(ChangeEvent e) {
//        controller.changeProfilePercentile((Double)percentileSpinner.getValue());
//    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(ProfileController.PROFILE_MAD_PROPERTY)) {
            Integer newValue = (Integer) evt.getNewValue();
            if(!madSpinner.getValue().equals(newValue)) {
                madSpinner.setValue(newValue);
            }
            
        }
//        if(evt.getPropertyName().equals(ProfileController.PROFILE_PERCENTILE_PROPERTY)) {
//            Double newValue = (Double) evt.getNewValue();
//            if(!percentileSpinner.getValue().equals(newValue)) {
//                percentileSpinner.setValue(newValue);
//            }
//        }

    }

}
