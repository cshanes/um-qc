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


public class ProfileChartHelper {
    
    private static float[] dashes = {5.0f, 150.0f};
    private static BasicStroke medianStroke = new BasicStroke(1.5f, 
            BasicStroke.CAP_SQUARE, 
            BasicStroke.JOIN_ROUND,
            10.0f,
            dashes,
            0f);

    static JFreeChart getProfileChart(Profile profile, String title) {

        XYSeriesCollection dataset = createProfileDataset(profile);
        
        XYPlot plot = createProfilePlot(profile, dataset);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        
        //customize chart
        ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
        xAxis.setRange(0,profile.getWidth());
        ValueAxis yAxis = chart.getXYPlot().getRangeAxis();
        yAxis.setRange(profile.getProfileMin(), profile.getProfileMax());
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.PINK);
        chart.getXYPlot().getRenderer().setSeriesStroke(1, medianStroke);
        chart.getXYPlot().getRenderer().setSeriesPaint(2, Color.PINK);
        chart.getXYPlot().getRenderer().setSeriesPaint(3, Color.PINK);
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        
        return chart;
    }
    
    static JFreeChart getIcovChart(Profile profile, String title) {

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
    
    static JFreeChart getCombinedChart(Profile profile, String title) {
        XYSeriesCollection data1 = createProfileDataset(profile);
        XYPlot plot = createProfilePlot(profile, data1);
        
        
        //add secondary axis
        plot.setDataset(1, createICOVDataset(profile));
        ValueAxis rangeAxis2 = new NumberAxis();
        plot.setRangeAxis(1, rangeAxis2);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
        plot.setRenderer(1, new StandardXYItemRenderer());
        plot.mapDatasetToRangeAxis(1, 1);
        
        addPeakMarkers(profile, plot);
        
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        
        //customize chart
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.PINK);
        chart.getXYPlot().getRenderer().setSeriesStroke(1, medianStroke);
        chart.getXYPlot().getRenderer().setSeriesPaint(2, Color.PINK);
        chart.getXYPlot().getRenderer().setSeriesPaint(3, Color.PINK);
        chart.getXYPlot().getRenderer(1).setSeriesPaint(0, Color.GREEN);
        chart.getXYPlot().getRenderer(1).setSeriesPaint(1, Color.PINK);
        chart.getXYPlot().getRenderer(1).setSeriesStroke(1, medianStroke);
        chart.getXYPlot().getRenderer(1).setSeriesPaint(2, Color.PINK);
        chart.getXYPlot().getRenderer(1).setSeriesPaint(3, Color.PINK);
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        
        return chart;
    }
    
    private static XYPlot createProfilePlot(Profile profile, XYSeriesCollection dataset) {
        //create plot
        XYItemRenderer xyRenderer = new StandardXYItemRenderer();
        ValueAxis domainAxis = new NumberAxis();
        ValueAxis rangeAxis = new NumberAxis();
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, xyRenderer);
        
        //add domain markers for valley highlighting
        List<Valley> valleys = profile.getValleys();
        List<IntervalMarker> domainMarkers = new ArrayList<IntervalMarker>();
        for(Valley v : valleys) {
            domainMarkers.add(new IntervalMarker((double)v.start, (double)v.end, 
                                                  Color.RED));
        }
        //add interval markers to plot
        for(IntervalMarker m : domainMarkers) {
            m.setAlpha((float) 0.3);
            m.setOutlinePaint(null);
            plot.addDomainMarker(m);
        }
        
        return plot;
    }
    
    private static XYPlot createICOVPlot(Profile profile, XYSeriesCollection dataset) {
        //create plot
        XYItemRenderer xyRenderer = new StandardXYItemRenderer();
        ValueAxis domainAxis = new NumberAxis();
        ValueAxis rangeAxis = new NumberAxis();
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, xyRenderer);
        
        addPeakMarkers(profile, plot);
        
        return plot;
    }
    
    private static void addPeakMarkers(Profile profile, XYPlot plot) {
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
    
    private static XYSeriesCollection createProfileDataset(Profile profile) {
        XYSeries series = new XYSeries("Profile");
        XYSeries medianSeries = new XYSeries("Median");
        XYSeries madSeriesHigh = new XYSeries("MAD");
        XYSeries madSeriesLow = new XYSeries("MAD");
        double mad = profile.getProfileMAD();
        double median = profile.getProfileMedian();
        double[] arr = profile.getArr();
        for (int i = 0; i < arr.length; i++) {
            series.add(i, arr[i]);
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
    
    private static XYSeriesCollection createICOVDataset(Profile profile) {
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
