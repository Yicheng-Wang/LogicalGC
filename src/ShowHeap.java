import com.frontangle.ichart.chart.ChartUtils;
import com.frontangle.ichart.chart.DataRange;
import com.frontangle.ichart.chart.XYChart;
import com.frontangle.ichart.chart.XYDataSeries;
import com.frontangle.ichart.chart.axis.NumericalInterval;
import com.frontangle.ichart.chart.axis.YAxis;
import com.frontangle.ichart.chart.datapoint.DataPoint;
import com.frontangle.ichart.chart.draw.Area;
import com.frontangle.ichart.chart.draw.Line;
import com.frontangle.ichart.scaling.LinearNumericalAxisScaling;

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
}
