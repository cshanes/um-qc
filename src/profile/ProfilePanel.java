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
import javax.swing.JOptionPane;
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
    
    JOptionPane optionPane;
    
    private int currentMad = 1;
//    private double currentPercentile;
    
    private JButton exportToExcelButton = new JButton("Export Profile to Excel");
    private JButton exportToTextButton = new JButton("Export Profile to text");
    private JFileChooser fileChooser = new JFileChooser();
    private ExtensionFileFilter txtFilter = new ExtensionFileFilter("Text file (.txt)", "txt");
    private ExtensionFileFilter excelFilter = new ExtensionFileFilter("Excel spreadsheet (.xls)","xls");
    private JPanel leftPanel = new JPanel(new SpringLayout());

    private JFreeChart profileChart;

    public ProfilePanel(Profile profile, ProfileController controller, ProfileModel model) {
        this.profile = profile;
        this.controller = controller;
        
        controller.addView(this);
        controller.addModel(model);
        
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
        leftPanel.add(exportToExcelButton);
        leftPanel.add(new ProfileChartViewPanel(controller, profile));
       
        
        SpringUtilities.makeCompactGrid(leftPanel,
                2, 1,             //row, cols
                6, 6,             //initX, initY
                6, 6);            //xPad, yPad
    }

    private void setupActionListeners() {
        
        exportToExcelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileFilter(excelFilter);
                int returnVal = fileChooser.showSaveDialog(ProfilePanel.this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    
                    String filename = fileChooser.getSelectedFile().toString();
                    if(!filename.endsWith(".xls")) {
                        filename += ".xls";
                    }
                    File file = new File(filename);
                    
                    checkIfFileExists(file);
                    try {
                        
                        QualityControlHelper.exportProfileToExcel(profile, currentMad, file, profileChart);
                    } catch (IOException e1) {
                        IJ.error("Save file failed: " + e1.getLocalizedMessage());
                        return;
                    } catch (WriteException we) {
                        // TODO Auto-generated catch block
                        we.printStackTrace();
                        return;
                    }
                }
            }
        });
        
        exportToTextButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               fileChooser.setFileFilter(txtFilter);
               int returnVal = fileChooser.showSaveDialog(ProfilePanel.this);
               if(returnVal == JFileChooser.APPROVE_OPTION) {
                   String filename = fileChooser.getSelectedFile().toString();
                   if(!filename.endsWith(".txt")) {
                       filename += ".txt";
                   }
                   File file = new File(filename);
                   
                   checkIfFileExists(file);
                   try {
                       QualityControlHelper.exportProfileToText(profile, currentMad, file);
                   } catch (IOException e1) {
                       IJ.error("Save file failed");
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
//        if(evt.getPropertyName().equals(ProfileController.PROFILE_PERCENTILE_PROPERTY)) {
//            currentPercentile = (Double) evt.getNewValue();
//        }
        
    }

    private void setProfilePanel() {
        if(profilePanel != null) {
            this.remove(profilePanel);
        }
        
        profileChart = ProfileChartHelper.getProfileChart(profile, "Profile", currentMad);
        profilePanel = new ChartPanel(profileChart, 800, 600,
                800, 600, 800, 600, false,
                false, false, false, true, true);

        this.add(profilePanel);
        this.repaint();
        this.revalidate();
        
    }
    
    private void checkIfFileExists(File file) {
        if(file.exists()) {
            int res = JOptionPane.showConfirmDialog(null, "File " + file.getName() + " already exists.  Click\n" +
                                                          "OK to overwrite and save.",
                                                          "File exists",
                                                          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(res == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
    }
}
