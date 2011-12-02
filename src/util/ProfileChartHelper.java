package util;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import profile.Peak;
import profile.Profile;
import profile.SradProfile;
import profile.Valley;


public class ProfileChartHelper {
    
    private static float[] dashes = {5.0f, 150.0f};
    private static BasicStroke medianStroke = new BasicStroke(4.0f, 
            BasicStroke.CAP_SQUARE, 
            BasicStroke.JOIN_ROUND,
            10.0f,
            dashes,
            0f);

    public static JFreeChart getProfileChart(Profile profile, String title, int madNumber) {

        XYSeriesCollection dataset = createProfileDataset(profile, madNumber);
        
        XYPlot plot = createProfilePlot(profile, dataset);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        
        //customize chart
        ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
        xAxis.setRange(0,profile.getWidth());
        xAxis.setLabel("Position");
        ValueAxis yAxis = chart.getXYPlot().getRangeAxis();
        yAxis.setRange(profile.getProfileMin(), profile.getProfileMax());
        yAxis.setLabel("Value");
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
        chart.getXYPlot().getRenderer().setSeriesStroke(1, medianStroke);
        chart.getXYPlot().getRenderer().setSeriesPaint(2, Color.PINK);
        chart.getXYPlot().getRenderer().setSeriesPaint(3, Color.PINK);
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        
        return chart;
    }
    
    public static JFreeChart getIcovChart(SradProfile profile, String title) {

        XYSeriesCollection dataset = createICOVDataset(profile);
        
        XYPlot plot = createICOVPlot(profile, dataset);
        
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        
        //customize chart
        ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
        xAxis.setRange(0, profile.getWidth());
        
        ValueAxis yAxis = chart.getXYPlot().getRangeAxis();
        yAxis.setRange(profile.getICOVMin(), profile.getICOVMax());
        
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.PINK);
        chart.getXYPlot().getRenderer().setSeriesStroke(1, medianStroke);
        chart.getXYPlot().getRenderer().setSeriesPaint(2, Color.PINK);
        chart.getXYPlot().getRenderer().setSeriesPaint(3, Color.PINK);
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        
        return chart;
    }
    
//    public static JFreeChart getCombinedChart(SradProfile profile, String title) {
//        XYSeriesCollection data1 = createProfileDataset(profile);
//        XYPlot plot = createProfilePlot(profile, data1);
//        
//        
//        //add secondary axis
//        plot.setDataset(1, createICOVDataset(profile));
//        ValueAxis rangeAxis2 = new NumberAxis();
//        plot.setRangeAxis(1, rangeAxis2);
//        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
//        plot.setRenderer(1, new StandardXYItemRenderer());
//        plot.mapDatasetToRangeAxis(1, 1);
//        
//        addPeakMarkers(profile, plot);
//        
//        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
//        
//        //customize chart
//        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
//        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.PINK);
//        chart.getXYPlot().getRenderer().setSeriesStroke(1, medianStroke);
//        chart.getXYPlot().getRenderer().setSeriesPaint(2, Color.PINK);
//        chart.getXYPlot().getRenderer().setSeriesPaint(3, Color.PINK);
//        chart.getXYPlot().getRenderer(1).setSeriesPaint(0, Color.GREEN);
//        chart.getXYPlot().getRenderer(1).setSeriesPaint(1, Color.PINK);
//        chart.getXYPlot().getRenderer(1).setSeriesStroke(1, medianStroke);
//        chart.getXYPlot().getRenderer(1).setSeriesPaint(2, Color.PINK);
//        chart.getXYPlot().getRenderer(1).setSeriesPaint(3, Color.PINK);
//        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
//        
//        return chart;
//    }
//    
    private static XYPlot createProfilePlot(Profile profile, XYSeriesCollection dataset) {
        //create plot
        XYItemRenderer xyRenderer = new StandardXYItemRenderer();
        ValueAxis domainAxis = new NumberAxis();
        ValueAxis rangeAxis = new NumberAxis();
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, xyRenderer);
        
        //add domain markers for valley highlighting
//        List<Valley> valleys = profile.getValleys();
//        List<IntervalMarker> domainMarkers = new ArrayList<IntervalMarker>();
//        for(Valley v : valleys) {
//            domainMarkers.add(new IntervalMarker((double)v.start, (double)v.end, 
//                                                  Color.RED));
//        }
//        
//        //add interval markers to plot
//        for(IntervalMarker m : domainMarkers) {
//            m.setAlpha((float) 0.3);
//            m.setOutlinePaint(null);
//            plot.addDomainMarker(m);
//        }
        
        return plot;
    }
    
    private static XYPlot createICOVPlot(SradProfile profile, XYSeriesCollection dataset) {
        //create plot
        XYItemRenderer xyRenderer = new StandardXYItemRenderer();
        ValueAxis domainAxis = new NumberAxis();
        ValueAxis rangeAxis = new NumberAxis();
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, xyRenderer);
        
        addPeakMarkers(profile, plot);
        
        return plot;
    }
    
    private static void addPeakMarkers(SradProfile profile, XYPlot plot) {
        //add domain markers for peak highlighting
        List<Peak> peaks = profile.getPeaks();
        List<IntervalMarker> domainMarkers = new ArrayList<IntervalMarker>();
        for(Peak p : peaks) {
            domainMarkers.add(new IntervalMarker((double)p.start, (double)p.end, 
                                                  Color.YELLOW));
        }
        //add interval markers to plot
        for(IntervalMarker m : domainMarkers) {
            m.setAlpha((float) 0.3);
            m.setOutlinePaint(null);
            plot.addDomainMarker(m);
        }
    }
    
    private static XYSeriesCollection createProfileDataset(Profile profile, int madNumber) {
        XYSeries series = new XYSeries("Profile");
        XYSeries medianSeries = new XYSeries("Median");
        XYSeries[] madSeriesHigh = createMadSeries(profile, madNumber, true);
        XYSeries[] madSeriesLow = createMadSeries(profile, madNumber, false);
        double median = profile.getProfileMedian();
        double[] arr = profile.getArr();
        for (int i = 0; i < arr.length; i++) {
            series.add(i, arr[i]);
            medianSeries.add(i, median);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(medianSeries);
        for(XYSeries madSeries : madSeriesHigh) {
            dataset.addSeries(madSeries);
        }
        for(XYSeries madSeries : madSeriesLow) {
            dataset.addSeries(madSeries);
        }
        
        return dataset;
    }
    
    //TODO:  Make this so it creates high and low at the same time.  No need to do it separately
    private static XYSeries[] createMadSeries(Profile profile, int madNumber, boolean high) {
        XYSeries[] madSeries = new XYSeries[madNumber];
        
        double mad = profile.getProfileMAD();
        double median = profile.getProfileMedian();
        
        for(int i = 0; i < madSeries.length; i++) {
            madSeries[i] = new XYSeries("MAD" + (i+1));
            for(int j = 0; j < profile.getArr().length; j++) {
                if(high) {
                    madSeries[i].add(j, median + ( mad * (i+1)));
                } else {
                    madSeries[i].add(j, median - ( mad * (i+1)));
                }
            }
        }
        
        return madSeries;
    }
    
    private static XYSeriesCollection createICOVDataset(SradProfile profile) {
        XYSeries series = new XYSeries("Profile");
        XYSeries medianSeries = new XYSeries("Median");
        XYSeries madSeriesHigh = new XYSeries("MAD");
        XYSeries madSeriesLow = new XYSeries("MAD");
        double mad = profile.getICOVMAD();
        double median = profile.getICOVMedian();
        double[] icov = profile.getICOV();
        for (int i = 0; i < icov.length; i++) {
            series.add(i, icov[i]);
            medianSeries.add(i, median);
            madSeriesHigh.add(i, median + mad);
            madSeriesLow.add(i, median - mad);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(medianSeries);
        dataset.addSeries(madSeriesHigh);
        dataset.addSeries(madSeriesLow);
        
        return dataset;
    }

    
}
