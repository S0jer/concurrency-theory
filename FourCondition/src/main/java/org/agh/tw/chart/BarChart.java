package org.agh.tw.chart;

import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChart extends JFrame {

    private static final long serialVersionUID = 1L;

    public BarChart(String applicationTitle, List<String> labels, List<Double> values, String chartLabel, String fileName, String categoryAxLabel, String scoreAxLabel) {
        super(applicationTitle);

        if (labels.size() != values.size()) {
            throw new IllegalArgumentException(
                "Labels and values lists must have the same size!"
            );
        }

        DefaultCategoryDataset barChartDataset = new DefaultCategoryDataset();
        for (int i = 0; i < labels.size(); i++) {
            barChartDataset.addValue(values.get(i), chartLabel, labels.get(i));
        }

        JFreeChart barChartObject = ChartFactory.createBarChart(
                chartLabel,
                categoryAxLabel,
                scoreAxLabel,
                barChartDataset,
                PlotOrientation.HORIZONTAL,
                false,
                false,
                false
        );

        ChartPanel chartPanel = new ChartPanel(barChartObject);
        setContentPane(chartPanel);

        File barChart = new File(fileName);
        try {
            ChartUtilities.saveChartAsJPEG(barChart, barChartObject, 600, 400);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }
}