package org.agh.tw.chart;

import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterPlot extends JFrame {

    private static final long serialVersionUID = 1L;

    public ScatterPlot(String applicationTitle, List<Integer> xData, List<Double> yData1, List<Double> yData2, String chartLabel, String fileName, String xAxisLabel, String yAxisLabel) {
        super(applicationTitle);

        if (xData.size() != yData1.size() || xData.size() != yData2.size()) {
            throw new IllegalArgumentException("X data and Y data lists must have the same size!");
        }

        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries series1 = new XYSeries("4 Conditions");
        for (int i = 0; i < xData.size(); i++) {
            series1.add(xData.get(i), yData1.get(i));
        }
        dataset.addSeries(series1);

        XYSeries series2 = new XYSeries("Nested Locks");
        for (int i = 0; i < xData.size(); i++) {
            series2.add(xData.get(i), yData2.get(i));
        }
        dataset.addSeries(series2);

        JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                chartLabel,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );

        ChartPanel chartPanel = new ChartPanel(scatterPlot);
        setContentPane(chartPanel);

        File chartFile = new File(fileName);
        try {
            ChartUtilities.saveChartAsJPEG(chartFile, scatterPlot, 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ScatterPlot(String applicationTitle,
                       List<Integer> xData,
                       Map<Integer, List<Double>> seriesData,
                       String chartLabel,
                       String fileName,
                       String xAxisLabel,
                       String yAxisLabel) {
        super(applicationTitle);

        for (List<Double> yData : seriesData.values()) {
            if (xData.size() != yData.size()) {
                throw new IllegalArgumentException("All series must have the same number of elements as xData!");
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Map.Entry<Integer, List<Double>> entry : seriesData.entrySet()) {
            XYSeries series = new XYSeries("Buffer Size " + entry.getKey());
            List<Double> yData = entry.getValue();
            for (int i = 0; i < xData.size(); i++) {
                series.add(xData.get(i), yData.get(i));
            }
            dataset.addSeries(series);
        }

        JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                chartLabel,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );

        ChartPanel chartPanel = new ChartPanel(scatterPlot);
        setContentPane(chartPanel);

        File chartFile = new File(fileName);
        try {
            ChartUtilities.saveChartAsJPEG(chartFile, scatterPlot, 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
