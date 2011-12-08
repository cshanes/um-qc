//import java.awt.Graphics2D;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferByte;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JSpinner;
//import javax.swing.JTabbedPane;
//import javax.swing.JTable;
//import javax.swing.SpinnerNumberModel;
//import javax.swing.SpringLayout;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//
//import profile.SradProfile;
//import profile.Shadow;
//
//import util.ProfileChartHelper;
//import view.SpringUtilities;
//
//import view.SpringUtilities;
//
//@SuppressWarnings("serial")
//public class ResultsPanel extends JPanel {
//    
//    final int TAB_INDEX = 2;
//    
//    private JTabbedPane chartsTabbedPane = new JTabbedPane();
//    
//    private JSpinner epsilonSpinner;
//    private JSpinner timestepSpinner;
//    private JSpinner sensitivitySpinner;
//    
//    private SpinnerNumberModel epsilonSpinnerModel;
//    private SpinnerNumberModel timestepSpinnerModel;
//    private SpinnerNumberModel sensitivitySpinnerModel;
//    
//    private String[] labels = {"Epsilon: ", "Timestep: ", "Sensitivity: "};
//    private JSpinner[] spinners = new JSpinner[labels.length];
//    private JPanel spinnerPanel;
//    
//    private JButton performAnalysisButton = new JButton("Perform Analysis");
//    
//    
//    private JPanel leftPanel = new JPanel(new SpringLayout());
//    
//    private JPanel combinedPanel = new JPanel(new GridBagLayout());
//    private JPanel postSradPanel = new JPanel(new GridBagLayout());
//    private JPanel originalPanel = new JPanel(new GridBagLayout());
//    private JPanel shadowSelectionPanel = new JPanel(new GridBagLayout());
//    
//    private JFreeChart profileChart;
//    private JFreeChart icovChart;
//    private JFreeChart sradProfileChart;
//    private JFreeChart sradIcovChart;
//    private JFreeChart combinedChart;
//    
//    private Profile profile;
//    
//    private BufferedImage bImg;
//    
//    private JTable table;
//    private String[] columnNames = {"Shadow",
//                                    "Strength",
//                                    "Width"};
//    List<Shadow> shadows;
//    
//    public ResultsPanel(Profile profile, BufferedImage bImg, Rectangle depthSelection) {
//        this.profile = profile;
//        this.bImg = bImg;
//        
//        Graphics2D graphics = bImg.createGraphics();
//        graphics.draw(depthSelection);
//        
//        //set pre-SRAD charts
//        profileChart = ProfileChartHelper.getProfileChart(profile, "Profile");
//        icovChart = ProfileChartHelper.getIcovChart(profile, "Profile Icov");
//        
//        profile.applySrad();
//        
//        //set post-SRAD charts
//        sradProfileChart = ProfileChartHelper.getProfileChart(profile, "Post-SRAD Profile");
//        sradIcovChart = ProfileChartHelper.getIcovChart(profile, "Post-SRAD Profile ICOV");
//        combinedChart = ProfileChartHelper.getCombinedChart(profile, "Shadow Analysis");
//        
//        //set shadow selections panel
//        setShadowSelectionPanel();
//        
//        setTable();
//        
//        constructChartsTabbedPanel();
//        constructThisPanel();
//        setupActionListeners();
//        
//    }
//    
//    private void setupActionListeners() {
//        performAnalysisButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                performAnalysisButton.setText("Perform Analysis");
//                profile.restoreOriginalProfile();
//                setProfile(profile, bImg);
//            }
//        });
//    }
//    
//    private void constructThisPanel() {
//        initSpinnerPanel();
//        initLeftPanel();
//        
//        this.setLayout(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
//        c.fill = GridBagConstraints.BOTH;
//        c.weightx = 0.15;
//        this.add(leftPanel, c);
//        
//        c.gridx = 1;
//        c.weightx = 0.85;
//        c.gridheight = 2;
//        this.add(chartsTabbedPane, c);
//    }
//    
//    private void initLeftPanel() {
//        leftPanel.add(shadowSelectionPanel);
//        leftPanel.add(spinnerPanel);
//        leftPanel.add(new JLabel("Results:" ));
//        leftPanel.add(table.getTableHeader());
//        leftPanel.add(table);
//        leftPanel.add(performAnalysisButton);
//        
//        SpringUtilities.makeCompactGrid(leftPanel,
//                6, 1,             //row, cols
//                6, 6,             //initX, initY
//                6, 6);            //xPad, yPad
//    }
//    
//    private void initSpinnerPanel() {
//        spinnerPanel = new JPanel();
//        
//        epsilonSpinnerModel = new SpinnerNumberModel(profile.getSradEpsilon(), 0, 1, .00001);
//        timestepSpinnerModel = new SpinnerNumberModel(profile.getSradTimestep(), 0, 1, .01);
//        sensitivitySpinnerModel = new SpinnerNumberModel(profile.getSradSensitivity(), 0, 100, .1);
//        
//        spinners[0] = epsilonSpinner = new JSpinner(epsilonSpinnerModel);
//        spinners[1] = timestepSpinner = new JSpinner(timestepSpinnerModel);
//        spinners[2] = sensitivitySpinner = new JSpinner(sensitivitySpinnerModel);
//        
//        epsilonSpinner.setEditor(new JSpinner.NumberEditor(epsilonSpinner, "0.#####"));
//        
//        epsilonSpinnerModel.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                epsilonSpinnerStateChanged(e);
//            }
//        });
//        timestepSpinnerModel.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                timestepSpinnerStateChanged(e);
//            }
//        });
//        sensitivitySpinnerModel.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                sensitivitySpinnerStateChanged(e);
//            }
//        });
//        
//        spinnerPanel.setLayout(new SpringLayout());
//        
//        //add components
//        for(int i = 0; i < labels.length; i++) {
//            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
//            l.setLabelFor(spinners[i]);
//            spinnerPanel.add(l);
//            spinnerPanel.add(spinners[i]);
//        }
//        
//        SpringUtilities.makeCompactGrid(spinnerPanel,
//                                        labels.length, 2, //row, cols
//                                        6, 6,             //initX, initY
//                                        6, 6);            //xPad, yPad
//    }
//    
//    protected void epsilonSpinnerStateChanged(ChangeEvent e) {
//        profile.setSradEpsilon((Double)epsilonSpinner.getValue());
//        performAnalysisButton.setText("Perform Analysis*");
//    }
//    
//    protected void timestepSpinnerStateChanged(ChangeEvent e) {
//        profile.setSradTimestep((Double)timestepSpinner.getValue());
//        performAnalysisButton.setText("Perform Analysis*");
//    }
//
//    protected void sensitivitySpinnerStateChanged(ChangeEvent e) {
//        profile.setSradSensitivity((Double)sensitivitySpinner.getValue());
//        performAnalysisButton.setText("Perform Analysis*");
//    }
//    
//    private void setTable() {
//        
//        //convert shadow data into 2D array
//        String[][] rowData = new String[shadows.size()][columnNames.length];
//        int row = 0;
//        for(Shadow s : shadows) {
//            rowData[row][0] = "Shadow " + (row + 1);
//            rowData[row][1] = String.valueOf(s.strength);
//            rowData[row][2] = String.valueOf(s.width);
//            row++;
//        }
//        
//        table = new JTable(rowData, columnNames);
//        table.setCellSelectionEnabled(false);
//    }
//    
//    private void setShadowSelectionPanel() {
//        shadowSelectionPanel = new JPanel(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
//        shadowSelectionPanel.add(new JLabel("Shadow Selections"), c);
//        
//        c.gridy = 1;
//        c.weightx = 1.0;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        shadowSelectionPanel.add(new JLabel(new ImageIcon(createShadowSelectionsImage(profile))),c);
//        
//        c.gridy = 2;
//        c.weighty = 0.8;
//        c.fill = GridBagConstraints.BOTH;
//        shadowSelectionPanel.add(new JLabel(new ImageIcon(bImg)), c);
//    }
//    
//    private BufferedImage createShadowSelectionsImage(Profile profile) {
//        int height = 20;
//        int width = profile.getWidth();
//        
//        BufferedImage img = new BufferedImage(profile.getWidth(), height, BufferedImage.TYPE_BYTE_GRAY);
//        byte[] arr = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
//        Arrays.fill(arr, (byte)255);
//        
//        shadows = profile.getShadows();
//        int stride = width;
//        for(Shadow s : shadows) {
//            for(int y = 0; y < height; y++) {
//                for(int x = 0; x < width; x++) {
//                    if(x >= s.start && x <= s.end) {
//                        arr[y*stride + x] = (byte) 50;
//                    } 
//                }
//            }
//        }
//       
//        //draw shadow numbers
//        int shadowNumber = 1;
//        Graphics2D g = (Graphics2D) img.getGraphics();
//        for(Shadow s : shadows) {
//            g.drawString(String.valueOf(shadowNumber), s.start, 10);
//            shadowNumber++;
//        }
//        
//        return img;
//    }
//    
//    private void constructChartsTabbedPanel() {
//        //TODO: make all these widths and heights weighted values based on 
//        //      the size of shadow images + shadow table DUH
//        
//        chartsTabbedPane.setSize(600, 400);
//
//        combinedPanel = new ChartPanel(combinedChart, 600, 400,
//                                     600, 400, 600, 400, false,
//                                     false, false, false, true, true);
//        combinedPanel.setSize(600, 400);
//        chartsTabbedPane.addTab("Combined", combinedPanel);
//
//        JPanel SRADProfilePanel = new ChartPanel(sradProfileChart, 600, 200,
//                                                  600, 200, 600, 200, false,
//                                                  false, false, false, true, true);
//        JPanel SRADIcovPanel = new ChartPanel(sradIcovChart, 600, 200,
//                                                  600, 200, 600, 200, false,
//                                                  false, false, false, true, true);
//        postSradPanel = new JPanel(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
//        c.weighty = 1.0;
//        c.weightx = 0.5;
//        c.fill = GridBagConstraints.BOTH;
//        postSradPanel.add(SRADProfilePanel, c);
//        c.gridy = 1;
//        postSradPanel.add(SRADIcovPanel, c);
//        postSradPanel.setSize(600, 400);
//        chartsTabbedPane.addTab("Post SRAD", postSradPanel);
//
//        JPanel origProfilePanel = new ChartPanel(profileChart, 600, 200,
//                                                  600, 200, 600, 200, false,
//                                                  false, false, false, true, true);
//        JPanel origIcovPanel = new ChartPanel(icovChart, 600, 200,
//                                              600, 200, 600, 200, false,
//                                              false, false, false, true, true);
//        originalPanel = new JPanel(new GridBagLayout());
//        c = new GridBagConstraints();
//        c.weighty = 1.0;
//        c.weightx = 0.5;
//        c.fill = GridBagConstraints.BOTH;
//        originalPanel.add(origProfilePanel, c);
//        c.gridy = 1;
//        originalPanel.add(origIcovPanel, c);
//        originalPanel.setSize(600, 400);
//        chartsTabbedPane.addTab("Original", originalPanel);
//
//        return;
//    }
//
//    public void setProfile(Profile profile, BufferedImage bImg) {
//>>>>>>> refs/remotes/choose_remote_name/master
//        this.profile = profile;
//        this.bImg = bImg;
//        this.removeAll();
//        leftPanel.removeAll();
//        chartsTabbedPane.removeAll();
//        combinedPanel.removeAll();
//        originalPanel.removeAll();
//        postSradPanel.removeAll();
//        
//        //set pre-SRAD charts
//        profileChart = ProfileChartHelper.getProfileChart(profile, "Profile");
//        icovChart = ProfileChartHelper.getIcovChart(profile, "Profile Icov");
//        
//        profile.applySrad();
//        
//        //set post-SRAD charts
//        sradProfileChart = ProfileChartHelper.getProfileChart(profile, "Post-SRAD Profile");
//        sradIcovChart = ProfileChartHelper.getIcovChart(profile, "Post-SRAD Profile ICOV");
//        combinedChart = ProfileChartHelper.getCombinedChart(profile, "Shadow Analysis");
//        
//        
//        //set shadow selections panel
//        setShadowSelectionPanel();
//        setTable();
//        constructChartsTabbedPanel();
//        constructThisPanel();
//        
//        this.revalidate();
//        this.repaint();
//    }
//    
//}
