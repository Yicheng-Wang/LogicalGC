import com.frontangle.ichart.chart.ChartUtils;
import com.frontangle.ichart.chart.DataRange;
import com.frontangle.ichart.chart.XYChart;
import com.frontangle.ichart.chart.XYDataSeries;
import com.frontangle.ichart.chart.axis.NumericalInterval;
import com.frontangle.ichart.chart.axis.YAxis;
import com.frontangle.ichart.chart.bar.BarDisplayOptions;
import com.frontangle.ichart.chart.bar.GradiantRule;
import com.frontangle.ichart.chart.bar.XYBarDataSeries;
import com.frontangle.ichart.chart.datapoint.DataPoint;
import com.frontangle.ichart.chart.datapoint.DataPointBar;
import com.frontangle.ichart.chart.draw.Area;
import com.frontangle.ichart.chart.draw.Line;
import com.frontangle.ichart.chart.draw.point.UIPointCircle;
import com.frontangle.ichart.chart.draw.point.UIPointSquare;
import com.frontangle.ichart.chart.draw.point.UIPointTriangle;
import com.frontangle.ichart.scaling.LinearNumericalAxisScaling;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ShowHeap {

    public static XYChart createHeapXYChart(int count) {

        ArrayList<XYDataSeries> al = new ArrayList<XYDataSeries>();

        XYDataSeries x1 = new XYDataSeries<DataPoint>("Eden Used");
        x1.setArea(new Area(new Color(0, 255, 0, 40), Area.AreaType.STACKED));
        XYDataSeries x2 = new XYDataSeries<DataPoint>("Eden Left");
        x2.setArea(new Area(new Color(0, 255, 0, 160), Area.AreaType.STACKED));
        XYDataSeries x3 = new XYDataSeries<DataPoint>("From Space Used");
        x3.setArea(new Area(new Color(255, 0, 0, 100), Area.AreaType.STACKED));
        XYDataSeries x4 = new XYDataSeries<DataPoint>("From Space Left");
        x4.setArea(new Area(new Color(255, 255, 0, 255), Area.AreaType.STACKED));
        XYDataSeries x5 = new XYDataSeries<DataPoint>("To Space");
        x5.setArea(new Area(new Color(255, 128, 0, 160), Area.AreaType.STACKED));
        XYDataSeries x6 = new XYDataSeries<DataPoint>("Old Used");
        x6.setArea(new Area(new Color(0, 0, 255, 80), Area.AreaType.STACKED));
        XYDataSeries x7 = new XYDataSeries<DataPoint>("Old Left");
        x7.setArea(new Area(new Color(0, 0, 255, 160), Area.AreaType.STACKED));
        XYDataSeries x8 = new XYDataSeries<DataPoint>("Metaspace Used");
        x8.setArea(new Area(new Color(0, 255, 255, 80), Area.AreaType.STACKED));
        XYDataSeries x9 = new XYDataSeries<DataPoint>("Metaspace Left");
        x9.setArea(new Area(new Color(0, 255, 255, 160), Area.AreaType.STACKED));

        al.add(x1);
        al.add(x2);
        al.add(x3);
        al.add(x4);
        al.add(x5);
        al.add(x6);
        al.add(x7);
        al.add(x8);
        al.add(x9);
        for(int i=0;i<al.size();i++){
            al.get(i).dataPoints = new ArrayList<DataPoint>();
        }

        String[] TimeStamps = new String[LogReader.HeapRecord.size()];

        TimeStamps[0] = (count==0)?"0":Showing.df2.format(LogReader.timeLine.get(count * 200-1));
        int number = Math.min(LogReader.HeapRecord.size(), (count+1) * 200);
        for(int i = count*200 + 1;i<number;i++){
            TimeStamps[i - count*200] = Showing.df2.format(LogReader.timeLine.get(i-1));
            /*if (LogReader.HeapRecord.get(i).phase.type == TimePeriod.usageType.OldGC)
                TimeStamps[i] += "F";
            if (LogReader.HeapRecord.get(i).phase.type == TimePeriod.usageType.YoungGC)
                TimeStamps[i] += "M";*/
        }

        double min = 0;
        double max = 0;

        for(int i= count*200;i<number;i++){
            double used;
            al.get(0).dataPoints.add(new DataPoint(TimeStamps[i - count*200], used = LogReader.HeapRecord.get(i).HeapPartition[0].usedSize.ValueFormM));
            al.get(1).dataPoints.add(new DataPoint(TimeStamps[i - count*200], LogReader.HeapRecord.get(i).HeapPartition[0].totalSize.ValueFormM - used));
            al.get(2).dataPoints.add(new DataPoint(TimeStamps[i - count*200], used = LogReader.HeapRecord.get(i).HeapPartition[1].usedSize.ValueFormM));
            al.get(3).dataPoints.add(new DataPoint(TimeStamps[i - count*200], LogReader.HeapRecord.get(i).HeapPartition[1].totalSize.ValueFormM - used));
            al.get(4).dataPoints.add(new DataPoint(TimeStamps[i - count*200], LogReader.HeapRecord.get(i).HeapPartition[2].totalSize.ValueFormM));
            al.get(5).dataPoints.add(new DataPoint(TimeStamps[i - count*200], used = LogReader.HeapRecord.get(i).HeapPartition[3].usedSize.ValueFormM));
            al.get(6).dataPoints.add(new DataPoint(TimeStamps[i - count*200], LogReader.HeapRecord.get(i).HeapPartition[3].totalSize.ValueFormM - used));
            al.get(7).dataPoints.add(new DataPoint(TimeStamps[i - count*200], used = LogReader.HeapRecord.get(i).HeapPartition[4].usedSize.ValueFormM));
            al.get(8).dataPoints.add(new DataPoint(TimeStamps[i - count*200], LogReader.HeapRecord.get(i).HeapPartition[4].totalSize.ValueFormM - used));
            if(LogReader.HeapRecord.get(i).totalSize.ValueFormM < min)
                min = LogReader.HeapRecord.get(i).totalSize.ValueFormM;
            if(LogReader.HeapRecord.get(i).totalSize.ValueFormM > max)
                max = LogReader.HeapRecord.get(i).totalSize.ValueFormM;
        }

        XYChart chart = new XYChart("Heap Memory Usage", "Time And Event (Sec)",
                "Size of Heap (MB)", al);
        chart.title.titleColor = Color.BLACK;
        chart.title.titleFont =new Font("Purisa", 1, 33);

        max = (max<100)?100:max;
        DataRange drY = ChartUtils.getDataRange(max, min, 10);
        double initialIntervalY = ChartUtils.getInterval(drY);

        NumericalInterval t1 = new NumericalInterval(initialIntervalY);
        NumericalInterval t2 = new NumericalInterval(initialIntervalY / 10);

        t1.styling.graphLine = new Line(Color.GRAY, false, 1);
        t1.styling.lineLength = 6;

        t2.styling.graphLine = new Line(Color.LIGHT_GRAY, true, 1);
        t2.styling.lineLength = 3;

        chart.yAxis = new YAxis(new LinearNumericalAxisScaling(drY.min,
                drY.max, t1, t2, null), "Size of Heap (MB)");

        return chart;
    }

    public static void YoungGenStats(JPanel mainPanel, int x, int y, int width, int depth,int count) {
        ArrayList<XYDataSeries> temperatureSeriesList = new ArrayList<XYDataSeries>();
        ArrayList<DataPointBar> DisiredSize = new ArrayList<DataPointBar>();
        ArrayList<DataPoint> Threshold = new ArrayList<DataPoint>();
        ArrayList<DataPoint> LiveRate = new ArrayList<DataPoint>();

        int number = Math.min(Showing.YoungGCRecord.size(), (count+1) * 100);

        for(int i= 100*count;i<number;i++) {
            GC Judge = Showing.YoungGCRecord.get(i);
            if (Judge instanceof YoungGC){
                Threshold.add(new DataPoint("I", ((YoungGC)Judge).newThreshold));
                LiveRate.add(new DataPoint("I", (1-Judge.cleanSize.valueFormK/Judge.processSize.valueFormK)*100));
                DisiredSize.add(new DataPointBar(String.valueOf(i+1),((YoungGC)Judge).desiredSize.ValueFormM));
            }
        }

        XYDataSeries series = new XYDataSeries(new UIPointCircle(Color.ORANGE),
                new Line(Color.ORANGE), "Threshold");
        series.dataPoints = Threshold;

        XYDataSeries series2 = new XYDataSeries(new UIPointSquare(Color.RED),
                new Line(Color.RED), "LiveRate");
        series2.dataPoints = LiveRate;


        temperatureSeriesList.add(series);
        temperatureSeriesList.add(series2);

        BarDisplayOptions bdo = new BarDisplayOptions();


        XYBarDataSeries rainfallSeries = new XYBarDataSeries(DisiredSize,
                bdo, null, "Survivor Size");

        bdo.setGradiantRule(new GradiantRule(100, 100, Color.BLUE, Color.BLUE));

        rainfallSeries.setUpBarDisplayOptions(bdo);

        ArrayList<XYDataSeries> rainfallSeriesList = new ArrayList<XYDataSeries>();
        rainfallSeriesList.add(rainfallSeries);

        XYChart chart = new XYChart("Young Generation Stats", "Minor GC", "Desired Survivor (MB)",
                "Promotion Stats", rainfallSeriesList, temperatureSeriesList);

        chart.yAxis.axisScaling.interval1.styling.graphLine = null;
        chart.yAxis.axisScaling.interval2.styling.graphLine = null;

        chart.setSize(1000, 600);

        chart.setTitleFont(new Font("Ariel", Font.PLAIN, 24));
        chart.setTitle("Young Generation Stats");
        chart.setBounds(x,y,width,depth);

        mainPanel.add(chart);
    }
}
