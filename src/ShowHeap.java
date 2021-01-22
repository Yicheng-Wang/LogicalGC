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

    public static XYChart createHeapXYChart() {

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
        TimeStamps[0] = "0.000";

        for(int i=1;i<LogReader.HeapRecord.size();i++){
            TimeStamps[i] = Showing.df.format(LogReader.timeLine.get(i-1));
            if (LogReader.HeapRecord.get(i).phase.type == TimePeriod.usageType.OldGC)
                TimeStamps[i] += " (Full)";
            if (LogReader.HeapRecord.get(i).phase.type == TimePeriod.usageType.YoungGC)
                TimeStamps[i] += " (Minor)";
        }

        double min = 0;
        double max = 0;

        for(int i=0;i<LogReader.HeapRecord.size();i++){
            double used;
            al.get(0).dataPoints.add(new DataPoint(TimeStamps[i], used = LogReader.HeapRecord.get(i).HeapPartition[0].usedSize.ValueFormM));
            al.get(1).dataPoints.add(new DataPoint(TimeStamps[i], LogReader.HeapRecord.get(i).HeapPartition[0].totalSize.ValueFormM - used));
            al.get(2).dataPoints.add(new DataPoint(TimeStamps[i], used = LogReader.HeapRecord.get(i).HeapPartition[1].usedSize.ValueFormM));
            al.get(3).dataPoints.add(new DataPoint(TimeStamps[i], LogReader.HeapRecord.get(i).HeapPartition[1].totalSize.ValueFormM - used));
            al.get(4).dataPoints.add(new DataPoint(TimeStamps[i], LogReader.HeapRecord.get(i).HeapPartition[2].totalSize.ValueFormM));
            al.get(5).dataPoints.add(new DataPoint(TimeStamps[i], used = LogReader.HeapRecord.get(i).HeapPartition[3].usedSize.ValueFormM));
            al.get(6).dataPoints.add(new DataPoint(TimeStamps[i], LogReader.HeapRecord.get(i).HeapPartition[3].totalSize.ValueFormM - used));
            al.get(7).dataPoints.add(new DataPoint(TimeStamps[i], used = LogReader.HeapRecord.get(i).HeapPartition[4].usedSize.ValueFormM));
            al.get(8).dataPoints.add(new DataPoint(TimeStamps[i], LogReader.HeapRecord.get(i).HeapPartition[4].totalSize.ValueFormM - used));
            if(LogReader.HeapRecord.get(i).totalSize.ValueFormM < min)
                min = LogReader.HeapRecord.get(i).totalSize.ValueFormM;
            if(LogReader.HeapRecord.get(i).totalSize.ValueFormM > max)
                max = LogReader.HeapRecord.get(i).totalSize.ValueFormM;
        }

        XYChart chart = new XYChart("Heap Memory Usage ", "Time And Event (Sec)",
                "Size of Heap (MB)", al);
        chart.title.titleColor = Color.BLACK;
        chart.title.titleFont =new Font("Purisa", 1, 33);

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

    public static void YoungGenStats(JPanel mainPanel, int x, int y, int width, int depth) {
        ArrayList<XYDataSeries> temperatureSeriesList = new ArrayList<XYDataSeries>();

        ArrayList<DataPoint> tempMax = new ArrayList<DataPoint>();
        tempMax.add(new DataPoint("J", 17.2));
        tempMax.add(new DataPoint("F", 21.1));
        tempMax.add(new DataPoint("M", 23.3));
        tempMax.add(new DataPoint("A", 32.2));
        tempMax.add(new DataPoint("M", 30));
        tempMax.add(new DataPoint("J", 35.2));
        tempMax.add(new DataPoint("J", 36.2));
        tempMax.add(new DataPoint("A", 37.1));
        tempMax.add(new DataPoint("S", 30.0));
        tempMax.add(new DataPoint("O", 26.1));
        tempMax.add(new DataPoint("N", 18.8));
        tempMax.add(new DataPoint("D", 20.5));

        ArrayList<DataPoint> tempMin = new ArrayList<DataPoint>();
        tempMin.add(new DataPoint("J", -30.5));
        tempMin.add(new DataPoint("F", -22.7));
        tempMin.add(new DataPoint("M", -15.5));
        tempMin.add(new DataPoint("A", -6.1));
        tempMin.add(new DataPoint("M", -2.7));
        tempMin.add(new DataPoint("J", -2.7));
        tempMin.add(new DataPoint("J", 3.8));
        tempMin.add(new DataPoint("A", 3.8));
        tempMin.add(new DataPoint("S", 0));
        tempMin.add(new DataPoint("O", -6.1));
        tempMin.add(new DataPoint("N", -14.4));
        tempMin.add(new DataPoint("D", -21.1));

        ArrayList<DataPoint> tempAvg = new ArrayList<DataPoint>();
        tempAvg.add(new DataPoint("J", -2.2));
        tempAvg.add(new DataPoint("F", -0.4));
        tempAvg.add(new DataPoint("M", 3.4));
        tempAvg.add(new DataPoint("A", 7.6));
        tempAvg.add(new DataPoint("M", 12.2));
        tempAvg.add(new DataPoint("J", 15.4));
        tempAvg.add(new DataPoint("J", 17.3));
        tempAvg.add(new DataPoint("A", 16.6));
        tempAvg.add(new DataPoint("S", 13.4));
        tempAvg.add(new DataPoint("O", 8.2));
        tempAvg.add(new DataPoint("N", 2.8));
        tempAvg.add(new DataPoint("D", -0.9));

        XYDataSeries series = new XYDataSeries(new UIPointCircle(Color.ORANGE),
                new Line(Color.RED), "max");
        series.dataPoints = tempMax;

        XYDataSeries series2 = new XYDataSeries(new UIPointSquare(Color.BLUE),
                new Line(Color.BLUE), "min");
        series2.dataPoints = tempMin;

        XYDataSeries series3 = new XYDataSeries(new UIPointTriangle(
                Color.ORANGE), new Line(Color.ORANGE), "average");
        series3.dataPoints = tempAvg;

        temperatureSeriesList.add(series);
        temperatureSeriesList.add(series2);
        temperatureSeriesList.add(series3);

        ArrayList<DataPointBar> barSeries = new ArrayList<DataPointBar>();
        barSeries.add(new DataPointBar("J", 54.0));
        barSeries.add(new DataPointBar("F", 45.2));
        barSeries.add(new DataPointBar("M", 60.1));
        barSeries.add(new DataPointBar("A", 69.9));
        barSeries.add(new DataPointBar("M", 93.4));
        barSeries.add(new DataPointBar("J", 123.6));
        barSeries.add(new DataPointBar("J", 117.6));
        barSeries.add(new DataPointBar("A", 114.5));
        barSeries.add(new DataPointBar("S", 90.3));
        barSeries.add(new DataPointBar("O", 69.4));
        barSeries.add(new DataPointBar("N", 71.0));
        barSeries.add(new DataPointBar("D", 58.4));

        BarDisplayOptions bdo = new BarDisplayOptions();


        XYBarDataSeries rainfallSeries = new XYBarDataSeries(barSeries,
                bdo, null, "Rainfall");

        bdo.setGradiantRule(new GradiantRule(40, 100, new Color(230, 242, 255), Color.BLUE));

        rainfallSeries.setUpBarDisplayOptions(bdo);

        ArrayList<XYDataSeries> rainfallSeriesList = new ArrayList<XYDataSeries>();
        rainfallSeriesList.add(rainfallSeries);

        XYChart chart = new XYChart("Munich Weather", "Month", "Temperature",
                "Rainfall", rainfallSeriesList, temperatureSeriesList);

        chart.yAxis.axisScaling.interval1.styling.graphLine = null;
        chart.yAxis.axisScaling.interval2.styling.graphLine = null;

        chart.setSize(1000, 600);

        chart.setTitleFont(new Font("Ariel", Font.PLAIN, 24));
        chart.setTitle("Munich Weather");
        chart.setBounds(x,y,width,depth);

        mainPanel.add(chart);
    }
}
