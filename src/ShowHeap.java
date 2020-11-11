import com.frontangle.ichart.chart.XYChart;
import com.frontangle.ichart.chart.XYDataSeries;
import com.frontangle.ichart.chart.axis.YAxis;
import com.frontangle.ichart.chart.datapoint.DataPoint;
import com.frontangle.ichart.chart.draw.Area;
import com.frontangle.ichart.scaling.LinearNumericalAxisScaling;

import java.awt.*;
import java.util.ArrayList;

public class ShowHeap {

    public static XYChart createHeapXYChart() {

        ArrayList<XYDataSeries> al = new ArrayList<XYDataSeries>();

        ArrayList<DataPoint> dps = new ArrayList<DataPoint>();

        XYDataSeries x1 = new XYDataSeries<DataPoint>("Eden Used");
        x1.setArea(new Area(new Color(173, 13, 213, 80), Area.AreaType.STACKED));
        XYDataSeries x2 = new XYDataSeries<DataPoint>("Eden Left");
        x2.setArea(new Area(new Color(12, 42, 113, 80), Area.AreaType.STACKED));
        XYDataSeries x3 = new XYDataSeries<DataPoint>("From Survivor");
        x3.setArea(new Area(new Color(32, 42, 1, 80), Area.AreaType.STACKED));
        XYDataSeries x4 = new XYDataSeries<DataPoint>("To Survivor");
        x4.setArea(new Area(new Color(53, 23, 1, 80), Area.AreaType.STACKED));
        XYDataSeries x5 = new XYDataSeries<DataPoint>("Old Used");
        x5.setArea(new Area(new Color(76, 231, 1, 80), Area.AreaType.STACKED));
        XYDataSeries x6 = new XYDataSeries<DataPoint>("Old Left");
        x6.setArea(new Area(new Color(41, 211, 1, 80), Area.AreaType.STACKED));
        XYDataSeries x7 = new XYDataSeries<DataPoint>("Metaspace Used");
        x7.setArea(new Area(new Color(1, 145, 1, 80), Area.AreaType.STACKED));
        XYDataSeries x8 = new XYDataSeries<DataPoint>("Metaspace Left");
        x8.setArea(new Area(new Color(2, 1, 1, 80), Area.AreaType.STACKED));


        dps.add(new DataPoint("0.000", 4772));
        dps.add(new DataPoint("1.218 (Minor GC)", 6712));
        dps.add(new DataPoint("2.030", 10000));

        x1.dataPoints = dps;


        dps = new ArrayList<DataPoint>();
        dps.add(new DataPoint("0.000", 4772));
        dps.add(new DataPoint("1.218 (Minor GC)", 6712));
        dps.add(new DataPoint("2.030", 10000));

        x2.dataPoints = dps;


        dps = new ArrayList<DataPoint>();
        dps.add(new DataPoint("0.000", 4772));
        dps.add(new DataPoint("1.218 (Minor GC)", 6712));
        dps.add(new DataPoint("2.030", 10000));

        x3.dataPoints = dps;


        dps = new ArrayList<DataPoint>();
        dps.add(new DataPoint("0.000", 4772));
        dps.add(new DataPoint("1.218 (Minor GC)", 6712));
        dps.add(new DataPoint("2.030", 10000));

        x4.dataPoints = dps;


        dps = new ArrayList<DataPoint>();
        dps.add(new DataPoint("0.000", 4772));
        dps.add(new DataPoint("1.218 (Minor GC)", 6712));
        dps.add(new DataPoint("2.030", 10000));

        x5.dataPoints = dps;


        dps = new ArrayList<DataPoint>();
        dps.add(new DataPoint("0.000", 4772));
        dps.add(new DataPoint("1.218 (Minor GC)", 6712));
        dps.add(new DataPoint("2.030", 10000));

        x6.dataPoints = dps;


        dps = new ArrayList<DataPoint>();
        dps.add(new DataPoint("0.000", 4772));
        dps.add(new DataPoint("1.218 (Minor GC)", 6712));
        dps.add(new DataPoint("2.030", 10000));

        x7.dataPoints = dps;


        dps = new ArrayList<DataPoint>();
        dps.add(new DataPoint("0.000", 4772));
        dps.add(new DataPoint("1.218 (Minor GC)", 6712));
        dps.add(new DataPoint("2.030", 10000));

        x8.dataPoints = dps;

        al.add(x1);
        al.add(x2);
        al.add(x3);
        al.add(x4);
        al.add(x5);
        al.add(x6);
        al.add(x7);
        al.add(x8);


        XYChart chart = new XYChart("Heap Memory Usage ", "Time",
                "Size of Heap", al);
        chart.title.titleColor = Color.BLACK;
        chart.title.titleFont =new Font("Purisa", 1, 33);


        chart.yAxis = new YAxis(new LinearNumericalAxisScaling(-5, 170000), "Size of Heap");
        return chart;
    }
}
