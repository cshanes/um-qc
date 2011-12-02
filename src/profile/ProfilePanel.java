package profile;

import ij.IJ;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import jxl.write.WriteException;
import model.ProfileModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import util.ExtensionFileFilter;
import util.ProfileChartHelper;
import util.QualityControlHelper;
import view.IView;
import view.ProfileChartViewPanel;
import view.SpringUtilities;
import controller.ProfileController;

public class ProfilePanel extends JPanel implements IView {

    private static final long serialVersionUID = 965634871039425317L;
    
    Profile profile;
    ProfileController controller;
    
    JPanel profilePanel;
    
    private int currentMad = 1;
    private double currentPercentile;
    
    private JButton exportProfileButton = new JButton("Export Profile");
    private JFileChooser fileChooser = new JFileChooser();
    private JPanel leftPanel = new JPanel(new SpringLayout());

    public ProfilePanel(Profile profile, ProfileController controller, ProfileModel model) {
        this.profile = profile;
        this.controller = controller;
        
        controller.addView(this);
        controller.addModel(model);
        
        fileChooser.setFileFilter(new ExtensionFileFilter("Excel spreadsheet (.xls)","xls"));
        initComponents();
    }

    private void initComponents() {
        setupActionListeners();
        initLeftPanel();
        
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        this.add(leftPanel);
        
        
        setProfilePanel();
        this.add(profilePanel);
    }

    private void initLeftPanel() {
        leftPanel.add(exportProfileButton);
        leftPanel.add(new ProfileChartViewPanel(controller, profile));
       
        
        SpringUtilities.makeCompactGrid(leftPanel,
                2, 1,             //row, cols
                6, 6,             //initX, initY
                6, 6);            //xPad, yPad
    }

    private void setupActionListeners() {
        
        exportProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showSaveDialog(ProfilePanel.this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    String filename = fileChooser.getSelectedFile().toString();
                    if(!filename.endsWith(".xls")) {
                        filename += ".xls";
                    }
                    File file = new File(filename);
                    try {
                        QualityControlHelper.exportProfileToExcel(profile, file);
                    } catch (IOException e1) {
                        IJ.error("Save file failed");
                        return;
                    } catch (WriteException we) {
                        // TODO Auto-generated catch block
                        we.printStackTrace();
                        return;
                    }
                }
            }
        });
        
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(ProfileController.PROFILE_MAD_PROPERTY)) {
            currentMad = (Integer) evt.getNewValue();
            setProfilePanel();
        }
        if(evt.getPropertyName().equals(ProfileController.PROFILE_PERCENTILE_PROPERTY)) {
            currentPercentile = (Double) evt.getNewValue();
        }
        
    }

    private void setProfilePanel() {
        if(profilePanel != null) {
            this.remove(profilePanel);
        }
        
        JFreeChart profileChart = ProfileChartHelper.getProfileChart(profile, "Profile", currentMad);
        profilePanel = new ChartPanel(profileChart, 800, 600,
                800, 600, 800, 600, false,
                false, false, false, true, true);

        this.add(profilePanel);
        this.repaint();
        this.revalidate();
        
    }
}
