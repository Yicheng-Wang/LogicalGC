import com.frontangle.ichart.pie.PieChart;
import com.frontangle.ichart.pie.Segment;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Drawing {
    static DecimalFormat df = new DecimalFormat("#0.000");
    public static void createTimePieChart(JPanel mainPanel, int x, int y, int width, int height) {
        ArrayList<Segment> values = new ArrayList<>();
        double time;
        values.add(new Segment(time = Showing.FullRunTime / Showing.totalExecutionTime * 100, "Full GC -" + df.format(time) + "%", new Color(255, 0, 0,160)));
        values.add(new Segment(time = Showing.MinorRunTime / Showing.totalExecutionTime * 100, "Minor GC -" + df.format(time) + "%", new Color(255, 128, 0,160)));
        values.add(new Segment(time = Showing.SafePointTotal / Showing.totalExecutionTime * 100, "Safe Point -" + df.format(time) + "%", new Color(255, 0, 255,120)));
        values.add(new Segment(time = Showing.AdaptiveTime / Showing.totalExecutionTime * 100, "Adaptive Policy -" + df.format(time) + "%", new Color(255, 128, 128,160)));
        values.add(new Segment(time = Showing.CollectInfoTime / Showing.totalExecutionTime * 100, "Collect Infor -" + df.format(time) + "%", new Color(255, 128, 128,80)));
        values.add(new Segment(time = Showing.PrintInforTime / Showing.totalExecutionTime * 100, "Print Infor -" + df.format(time) + "%", new Color(255, 255, 0,160)));
        values.add(new Segment(time = Showing.WarmupTime/ Showing.totalExecutionTime * 100, "Warm Up -" + df.format(time) + "%", new Color(150, 255, 0,100)));
        values.add(new Segment(time = Showing.Apptime/ Showing.totalExecutionTime * 100, "Application -" + df.format(time) + "%", new Color(0, 255, 0,160)));

        PieChart pieChart = new PieChart(values, "Time Distribution");
        pieChart.setSize(width, height);
        pieChart.setBounds(x,y,width,height);
        pieChart.setVisible(true);
        mainPanel.add(pieChart);
    }

    public static void createThreadChart(JPanel mainPanel, int x, int y, int width, int height) {
        ArrayList<Segment> values = new ArrayList<>();
        double size;
        double Total = Showing.GCWasteTotal + Showing.FastWasteTotal + Showing.SlowWasteTotal;
        values.add(new Segment(size = Showing.GCWasteTotal / Total * 100, "GC Waste -" + df.format(size) + "%", new Color(255, 0, 0,160)));
        values.add(new Segment(size = Showing.FastWasteTotal / Total * 100, "Fast Waste -" + df.format(size) + "%", new Color(0, 255, 0,160)));
        values.add(new Segment(size = Showing.SlowWasteTotal / Total * 100, "Slow Waste -" + df.format(size) + "%", new Color(0, 0, 255,160)));
        PieChart pieChart = new PieChart(values, "TLAB Waste Distribution");
        pieChart.setSize(width, height);
        pieChart.setBounds(x,y,width,height);
        pieChart.setVisible(true);
        mainPanel.add(pieChart);
    }
}
