import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.gui.Roi;
import ij.process.FloatPolygon;
import ij.process.StackConverter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.ProfileModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import controller.ProfileController;

import profile.Profile;
import profile.ProfilePanel;
import util.ProfileChartHelper;

public class Curvilinear_Profile extends ImageWindow implements MouseListener {

    private static final long serialVersionUID = -8838364843084240460L;
    
    private static final String SELECT_CIRCLE_DIALOG_STRING = "Select three points along the top of the arc.";
    private static final String SELECT_WIDTH_DIALOG_STRING = "Select the left and right bounds of the intensity profile region of interest.";
    private static final String SELECT_RADII_DIALOG_STRING = "Select two radii (width of profile) of interest.";
    
    private boolean selectingCirclePoints = false;
    private boolean selectingWidth = false;
    private boolean selectingRadii = false;

    private ImagePlus img;
    private ImagePlus originalImg;

    private float circleCenterX;
    private float circleCenterY;

    private Point2D.Float minWidthPoint;
    private Point2D.Float maxWidthPoint;
    private Point2D.Float minRadiusPoint;
    private Point2D.Float maxRadiusPoint;
    
    private ProfileModel profileModel;
    private ProfileController profileController;
    
    public Curvilinear_Profile() {
        this(WindowManager.getCurrentImage());
    }

    public Curvilinear_Profile(ImagePlus img) {
        super(img);
        
        if(img == null) {
            IJ.noImage();
            return;
        }
        
        if(img.getNSlices() > 1) {
            IJ.error("Curvilinear Profile plugin must be run on a median image.");
            return;
        }
        
        
        originalImg = img.duplicate();
        this.img = img;
        img.getWindow().getCanvas().addMouseListener(this);
        this.profileController = new ProfileController();
        this.profileModel = new ProfileModel();

        selectingCirclePoints = true;
        IJ.setTool("multi");
        if (img.getRoi() != null && img.getRoi().getFloatPolygon().npoints == 3) {
            FloatPolygon fp = img.getRoi().getFloatPolygon();
            setCircleCenterAndRadius(fp);
        } else {
            JOptionPane.showMessageDialog(this, SELECT_CIRCLE_DIALOG_STRING);
        }

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (selectingCirclePoints) {
            FloatPolygon fp = img.getRoi().getFloatPolygon();
            if (fp.npoints == 3) {
                endSelectingCircle(fp);
                startSelectingWidth();
            }
        } else if (selectingWidth) {
            FloatPolygon fp = img.getRoi().getFloatPolygon();
            if (fp.npoints == 2) {
                endSelectingWidth(fp);
                startSelectingRadii();
            }
        } else if (selectingRadii) {
            FloatPolygon fp = img.getRoi().getFloatPolygon();
            if (fp.npoints == 2) {
                endSelectingRadii(fp);
                calculateProfile();
            }
        }

    }

    private void calculateProfile() {
        float radiusROIInner = (float) Math.sqrt(Math.pow(minRadiusPoint.x - circleCenterX,2) + Math.pow(minRadiusPoint.y - circleCenterY, 2));
        float radiusROIOuter = (float) Math.sqrt(Math.pow(maxRadiusPoint.x - circleCenterX,2) + Math.pow(maxRadiusPoint.y - circleCenterY, 2));
        int width = Math.round(radiusROIOuter - radiusROIInner);
        
        int numRays = Math.round(maxWidthPoint.x - minWidthPoint.x);
        float[] xBinsMin = new float[numRays];
        //populate x values
        for(int i = 0; i < xBinsMin.length; i++) {
            xBinsMin[i] = minWidthPoint.x + i;
        }
        
        float[] yInnerArc = new float[numRays];
        //solve for y values of arc based on xBinsMin
        for(int i = 0; i < xBinsMin.length; i++) {
            yInnerArc[i] = (circleCenterY + (float) (Math.sqrt(Math.pow(radiusROIInner, 2) - Math.pow(xBinsMin[i] - circleCenterX, 2))));
        }
        
        float[] avgIntensity = new float[numRays];
        float[] xBinsMax = new float[numRays];
        float[] yOuterArc = new float[numRays];
        BufferedImage bi = img.getProcessor().getBufferedImage();
        Graphics2D g = (Graphics2D)bi.getGraphics();
        for(int i = 0; i < numRays; i++) {
            float sum = 0;
            float m = (yInnerArc[i] - circleCenterY) / (xBinsMin[i] - circleCenterX);
            float mag = (float) Math.sqrt(1 + Math.pow(m, 2));
            
            float xN = 1.0f / mag; float yN = m / mag;
            if(m < 0) {
                xN *= -1.0f;
                yN *= -1.0f;
            }
            
            float xA = xBinsMin[i]; float yA = yInnerArc[i];
            
            int counter = 0;
            for(int r = 0; r < width; r++) {
                float tempX = xA + r * xN;
                float tempY = yA + r * yN;
                float tempVal = originalImg.getPixel(Math.round(tempX), Math.round(tempY))[0];
                sum += tempVal;
                counter++;
                if(counter == (width - 1)) {
                    xBinsMax[i] = tempX;
                    yOuterArc[i] = tempY;
                    if(i == 0) {
                        g.drawLine((int)xBinsMin[i], (int)yInnerArc[i], (int)xBinsMax[i], (int)yOuterArc[i]);
                    } else if(i > 0 && i < (numRays-1)) {
                        g.drawLine((int)xBinsMin[i], (int)yInnerArc[i], (int)xBinsMin[i-1], (int)yInnerArc[i-1]);
                        g.drawLine((int)xBinsMax[i], (int)yOuterArc[i], (int)xBinsMax[i-1], (int)yOuterArc[i-1]);
                    } else if(i == (numRays-1)) {
                        g.drawLine((int)xBinsMin[i], (int)yInnerArc[i], (int)xBinsMax[i], (int)yOuterArc[i]);
                    }
                }
            }
            
            avgIntensity[i] = sum/counter;
            img.setImage(bi);
        }
        
        
        JFrame frame = new JFrame();
        frame.add(new ProfilePanel(new Profile(avgIntensity, img), profileController, profileModel));
        frame.pack(); frame.setVisible(true);
        
    }

    private void endSelectingCircle(FloatPolygon fp) {
        selectingCirclePoints = false;
        setCircleCenterAndRadius(fp);
        img.setRoi((Roi) null); // clear points
        
    }

    private void startSelectingWidth() {
        selectingWidth = true;
        JOptionPane.showMessageDialog(this, SELECT_WIDTH_DIALOG_STRING);
    }

    private void endSelectingWidth(FloatPolygon fp) {
        selectingWidth = false;
        setWidth(fp);
        img.setRoi((Roi) null); // clear points
        
        BufferedImage bi = img.getProcessor().getBufferedImage();
        Graphics2D g = (Graphics2D)bi.getGraphics();
        g.setColor(Color.WHITE);
        g.drawLine((int)circleCenterX, (int) (circleCenterY), (int)minWidthPoint.x, (int)(minWidthPoint.y));
        g.drawLine((int)circleCenterX, (int) (circleCenterY), (int)maxWidthPoint.x, (int)(maxWidthPoint.y));
        
        img.setImage(bi);
    }

    private void startSelectingRadii() {
        selectingRadii = true;
        JOptionPane.showMessageDialog(this, SELECT_RADII_DIALOG_STRING);
    }

    private void endSelectingRadii(FloatPolygon fp) {
        selectingRadii = false;
        setRadii(fp);
        img.setRoi((Roi) null); // clear points
        

    }

    private void setRadii(FloatPolygon fp) {
        if (fp.npoints != 2) {
            JOptionPane.showMessageDialog(this, SELECT_RADII_DIALOG_STRING);
            return;
        }

        // order points so that point[0] is the topmost and point[1] is the
        // bottom
        List<Point2D.Float> points = orderPoints(fp.xpoints, fp.ypoints, new PointYCompare());
        minRadiusPoint = points.get(0);
        maxRadiusPoint = points.get(1);
        
    }

    private void setWidth(FloatPolygon fp) {
        if (fp.npoints != 2) {
            JOptionPane.showMessageDialog(this, SELECT_WIDTH_DIALOG_STRING);
            return;
        }

        // order the points so that point[0] is the leftmost and point[1] is the
        // rightmost
        List<Point2D.Float> points = orderPoints(fp.xpoints, fp.ypoints, new PointXCompare());
        minWidthPoint = points.get(0);
        maxWidthPoint = points.get(1);
    }

    /**
     * Sets the circle center and radius after the user has selected three
     * points along the arc of the circle
     * 
     * @param fp
     */
    private void setCircleCenterAndRadius(FloatPolygon fp) {
        if (fp.npoints != 3) {
            JOptionPane.showMessageDialog(this, SELECT_CIRCLE_DIALOG_STRING);
            return;
        }

        // order the points so that point[0] is the leftmost and point[2] is the
        // rightmost
        List<Point2D.Float> points = orderPoints(fp.xpoints, fp.ypoints, new PointXCompare());

        Point2D.Float p1 = points.get(0);
        Point2D.Float p2 = points.get(1);
        Point2D.Float p3 = points.get(2);
        float ma = (p2.y - p1.y) / (p2.x - p1.x);
        float mb = (p3.y - p2.y) / (p3.x - p2.x);
        circleCenterX = (ma * mb * (p1.y - p3.y) + mb * (p1.x + p2.x) - ma * (p2.x + p3.x)) / (2.0f * (mb - ma));
        circleCenterY = -1.0f / ma * (circleCenterX - (p1.x + p2.x) / 2.0f) + (p1.y + p2.y) / 2.0f;
    }

    private List<Point2D.Float> orderPoints(float[] xPoints, float[] yPoints, Comparator<Point2D.Float> c) {
        /*
         * convert points into Point2D.Float. 
         */
        List<Point2D.Float> points = new ArrayList<Point2D.Float>();
        for (int i = 0; i < xPoints.length; i++) {
            points.add(new Point2D.Float(xPoints[i], yPoints[i]));
        }

        Collections.sort(points, c);

        return points;
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    private class PointXCompare
            implements Comparator<Point2D.Float> {

        public int compare(final Point2D.Float a, final Point2D.Float b) {
            if (a.x < b.x) {
                return -1;
            } else if (a.x > b.x) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private class PointYCompare
            implements Comparator<Point2D.Float> {

        public int compare(final Point2D.Float a, final Point2D.Float b) {
            if (a.y < b.y) {
                return -1;
            } else if (a.y > b.y) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
