import java.awt.Rectangle;

import ij.ImagePlus;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import profile.SradProfile;
import util.ProfileChartHelper;


@SuppressWarnings("serial")
public class ProfilePanel extends JPanel {
    
    ChartPanel chartPanel;
    ImagePlus img;
    
    ProfilePanel(ImagePlus img, Rectangle depthSelection) {
        this.img = img;
//        img.setRoi(depthSelection);
        chartPanel = new ChartPanel(ProfileChartHelper.getProfileChart(new SradProfile(img, depthSelection), "Profile"), 
                600, 400,
                600, 400, 600, 400, false,
                false, false, false, true, true);
        
        initComponents();
    }
       
    private void initComponents() {
        this.add(chartPanel);
    }
    
    protected void setProfile(Rectangle newSelection) {
        this.removeAll();
        
//        img.setRoi(newSelection);
        
        JFreeChart profileChart = ProfileChartHelper.getProfileChart(new SradProfile(img, newSelection), "Profile");
        this.add(new ChartPanel(profileChart, 600, 400,
                600, 400, 600, 400, false,
                false, false, false, true, true));
        this.revalidate();
        this.repaint();
    }

}
