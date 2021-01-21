import com.frontangle.ichart.chart.XYChart;
import com.frontangle.ichart.chart.XYDataSeries;
import com.frontangle.ichart.chart.axis.YAxis;
import com.frontangle.ichart.chart.datapoint.DataPoint;
import com.frontangle.ichart.chart.draw.Area;
import com.frontangle.ichart.pie.PieChart;
import com.frontangle.ichart.pie.Segment;
import com.frontangle.ichart.scaling.LinearNumericalAxisScaling;
import sun.rmi.runtime.Log;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;

public class Showing {

    static DecimalFormat df = new DecimalFormat("#0.000");
    static Font TitleStyle = new Font("Dialog", 1, 20);
    static Font TableStyle = new Font("Dialog", 0, 18);

    static long GCcount = 0;
    static long MinorGCcount = 0;
    static long FullGCcount = 0;

    static double SafePointTotal = 0;
    static double GCtimesum = 0;
    static double MinorGCtimeSum = 0;
    static double FullGCtimeSum = 0;

    static double MinorAdaptiveTime = 0;

    static long MaxThreadAtTime = 0;
    static long MeanThreadNum = 0;
    static double MaxWastePer = 0;
    static double MeanWastePer = 0;
    static long InitialMeanTLAB = 0;
    static long InitialRefillWaste = 0;
    static long FinalMeanTLAB = 0;
    static long FinalRefillWaste = 0;
    static long MeanRefillTimes = 0;
    static long ThreadTotalNum = 0;
    static long MeanSlowAlloc = 0;

    static long GCWasteTotal = 0;
    static long SlowWasteTotal = 0;
    static long FastWasteTotal = 0;

    static double MinorRunTime = 0;
    static double FullRunTime = 0;

    static long totalReclaimed = 0;
    static long MinorTotalProcess = 0;
    static long FullTotalProcess = 0;

    static long MinorTotalClean = 0;
    static long FullTotalClean = 0;

    static double MinorCPUPercentage = 0;
    static double FullCPUPercentage = 0;

    static long survivedTotal = 0;
    static long promotionTotal = 0;

    static double MarkingTimeTotal = 0;
    static double CompactTimeTotal = 0;
    static double PreCompactTotal = 0;
    static double AdjustRootsTotal= 0 ;
    static double PostCompactTotal = 0;
    static double SummaryTimeTotal = 0;
    static double FullAdaptive = 0;

    static double totalExecutionTime = 0;
    static double WarmupTime = 0;
    static double AdaptiveTime = 0;
    static double CollectInfoTime = 0;
    static double PrintInforTime = 0;

    static HashMap<String,Double[]> GCCauseTotal = new HashMap<>();
    static HashMap<String,Double[]> GCCauseYoung = new HashMap<>();
    static HashMap<String,Double[]> GCCauseOld = new HashMap<>();

    //static double MinorReclaimPercentage = 0;
    //static double FullReclaimPercentage = 0;

    static int overFlowTime = 0;
    static double Apptime = 0;

    public static void shows() {

        JFrame Mainframe = new JFrame();
        Mainframe.setLayout(null);
        Mainframe.setSize(1920,1080);

        JPanel MainPanel = new JPanel();
        MainPanel.setLayout(null);
        MainPanel.setBounds(0,0,2100, 2600);
        MainPanel.setPreferredSize(new Dimension(2100, 2600));
        MainPanel.setBackground(Color.WHITE);

        SettleInformation();

        Table.OverallStats(MainPanel,"用时情况",150,110,600,340);

        Drawing.createTimePieChart(MainPanel,950,10,740,520);

        Table.ApplicationStats(MainPanel,"应用线程",150,640,600,374);

        Drawing.createThreadChart(MainPanel,950,540,740,520);

        Table.TotalGCStats(MainPanel,"GC情况",150,1170,600,374);




        JLabel TitleSecond = new JLabel("Minor GC",JLabel.CENTER);
        TitleSecond.setFont(TitleStyle);
        TitleSecond.setBounds(620,1210,500,50);
        MainPanel.add(TitleSecond);
        JPanel MinorGCStats = Showing.MinorGCStats();
        MinorGCStats.setBounds(620,1260,500,240);
        MainPanel.add(MinorGCStats);

        JLabel TitleThird = new JLabel("Full GC",JLabel.CENTER);
        TitleThird.setFont(TitleStyle);
        TitleThird.setBounds(1220,1210,500,50);
        MainPanel.add(TitleThird);
        JPanel FullGCStats = Showing.FullGCStats();
        FullGCStats.setBounds(1220,1260,500,210);
        MainPanel.add(FullGCStats);

        JScrollPane jsp = new JScrollPane(MainPanel);
        jsp.setBounds(20,10,1880, 980);
        jsp.setBackground(Color.WHITE);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        if(FullGCcount > 0 ){
            PieChart FullPieChart = Showing.createFullPieChart();
            MainPanel.add(FullPieChart);

            PieChart ObjectPieChart = Showing.createObjectPieChart();
            MainPanel.add(ObjectPieChart);
        }

        XYChart chart = ShowHeap.createHeapXYChart();
        chart.setBounds(10,2200,1800,800);
        MainPanel.add(chart);

        Mainframe.add(jsp);
        Mainframe.setVisible(true);

    }

    private static void SettleInformation() {
        GCcount = LogReader.GCRecord.size();

        for(int i=0;i<LogReader.SafePoints.size();i++)
            SafePointTotal += LogReader.SafePoints.get(i).length;

        for(int i=0;i<LogReader.distributions.size();i++)
            CollectInfoTime += LogReader.distributions.get(i).timecost;

        for(int i=0;i<LogReader.ApplicationRecord.size();i++)
            Apptime += LogReader.ApplicationRecord.get(i).length;

        for(int i=0;i<GCcount;i++){
            GC Judge =  LogReader.GCRecord.get(i);
            if(Judge instanceof FullGC){
                FullGCcount++;
                FullGCtimeSum += Judge.timeCost;
                FullTotalProcess += Judge.processSize.valueForm;
                FullTotalClean += Judge.cleanSize.valueForm;
                FullCPUPercentage += Judge.CPUpercentage;
                MarkingTimeTotal += ((FullGC)Judge).Markingphase;
                SummaryTimeTotal += ((FullGC)Judge).Summaryphase;
                CompactTimeTotal += ((FullGC)Judge).Compactionphase;
                PreCompactTotal += ((FullGC)Judge).PreCompact;
                AdjustRootsTotal += ((FullGC)Judge).AdjustRoots;
                PostCompactTotal += ((FullGC)Judge).PostCompact;
                FullAdaptive += Judge.AdaptiveTime;
                Double old[];
                if(GCCauseOld.containsKey(Judge.Cause)){
                    old = GCCauseOld.get(Judge.Cause);
                    old[0] += 1;
                    old[1] += Judge.timeCost;
                    GCCauseOld.put(Judge.Cause,old);
                }
                else{
                    old = new Double[2];
                    old[0] = 1.0;
                    old[1] = Judge.timeCost;
                    GCCauseOld.put(Judge.Cause,old);
                }
            }

            else{
                MinorGCcount++;
                MinorGCtimeSum += Judge.timeCost;
                MinorTotalProcess += Judge.processSize.valueForm;
                MinorTotalClean += Judge.cleanSize.valueForm;
                survivedTotal += ((YoungGC)Judge).survivedSize.valueForm;
                promotionTotal += ((YoungGC)Judge).promotionSize.valueForm;
                MinorCPUPercentage += Judge.CPUpercentage;
                MinorAdaptiveTime += Judge.AdaptiveTime;
                if(((YoungGC)Judge).overflow)
                    overFlowTime ++;
                Double[] young;
                if(GCCauseYoung.containsKey(Judge.Cause)){
                    young = GCCauseYoung.get(Judge.Cause);
                    young[0] += 1;
                    young[1] += Judge.timeCost;
                    GCCauseYoung.put(Judge.Cause,young);
                }
                else{
                    young = new Double[2];
                    young[0] = 1.0;
                    young[1] = Judge.timeCost;
                    GCCauseYoung.put(Judge.Cause,young);
                }

                long threadnum = ((YoungGC)Judge).allocation.threadNum;
                if(threadnum>MaxThreadAtTime)
                    MaxThreadAtTime = threadnum;
                MeanThreadNum += threadnum;

                double waste = ((YoungGC)Judge).allocation.wastePercent;
                if(waste>MaxWastePer)
                    MaxWastePer = waste;
                MeanWastePer += waste;

                MeanSlowAlloc += ((YoungGC)Judge).allocation.slowAlloc;
                MeanRefillTimes += ((YoungGC)Judge).allocation.refillTotal;

                GCWasteTotal += ((YoungGC)Judge).allocation.gc_waste;
                SlowWasteTotal += ((YoungGC)Judge).allocation.slow_waste;
                FastWasteTotal += ((YoungGC)Judge).allocation.fast_waste;

            }

            Double[] all;
            if(GCCauseTotal.containsKey(Judge.Cause)){
                all = GCCauseTotal.get(Judge.Cause);
                all[0] += 1;
                all[1] += Judge.timeCost;
                GCCauseTotal.put(Judge.Cause,all);
            }
            else{
                all = new Double[2];
                all[0] = 1.0;
                all[1] = Judge.timeCost;
                GCCauseTotal.put(Judge.Cause,all);
            }

            AdaptiveTime += Judge.AdaptiveTime;
            GCtimesum += Judge.timeCost;
            totalReclaimed += Judge.cleanSize.valueForm;
        }

        Iterator map1it=LogReader.AllThread.entrySet().iterator();
        while(map1it.hasNext())
        {
            Map.Entry<String, Thread> entry=(Map.Entry<String, Thread>) map1it.next();
            InitialMeanTLAB += entry.getValue().TLABSizeList.get(0);
            InitialRefillWaste += entry.getValue().WasteSizeList.get(0);
            FinalMeanTLAB += entry.getValue().TLABSizeList.get(entry.getValue().TLABSizeList.size() - 1);
            FinalRefillWaste += entry.getValue().WasteSizeList.get(entry.getValue().WasteSizeList.size() - 1);
        }

        ThreadTotalNum = LogReader.AllThread.size();
        InitialMeanTLAB /= ThreadTotalNum;
        InitialRefillWaste /= ThreadTotalNum;
        FinalMeanTLAB /= ThreadTotalNum;
        FinalRefillWaste /= ThreadTotalNum;
        MeanRefillTimes /= MeanThreadNum;
        MeanSlowAlloc /= MeanThreadNum;
        MeanThreadNum /= MinorGCcount;
        MeanWastePer /= MinorGCcount;

        totalExecutionTime = LogReader.timeLine.peek();
        WarmupTime = LogReader.HeapRecord.get(0).phase.length;
        MinorRunTime = MinorGCtimeSum - MinorAdaptiveTime;
        FullRunTime = FullGCtimeSum - AdaptiveTime + MinorAdaptiveTime;

        PrintInforTime = totalExecutionTime - CollectInfoTime - FullRunTime - MinorRunTime - WarmupTime - AdaptiveTime - Apptime - SafePointTotal;

    }

    private static PieChart createObjectPieChart() {
        ArrayList<Segment> values = new ArrayList<>();

        double time = 0;
        InstanceDistribution last = LogReader.distributions.get(LogReader.distributions.size()-1);
        long[] topFive = new long[5];
        long totalFive = 0;

        for(int i=0;i<5;i++){
            topFive[i] = last.bytes.get(i);
            totalFive += topFive[i];
        }

        long others = last.totalBytes - totalFive;

        values.add(new Segment(time = (double)topFive[0] / last.totalBytes * 100,
                "1- " + InstanceDistribution.DealingName(last.className.get(0)) + " -" + df.format(time) + "%", new Color(255, 0, 255,160)));
        values.add(new Segment(time = (double)topFive[1] / last.totalBytes * 100,
                "2- " + InstanceDistribution.DealingName(last.className.get(1)) + " -"  + df.format(time) + "%", new Color(128, 0, 255,160)));
        values.add(new Segment(time = (double)topFive[2] / last.totalBytes * 100,
                "3- " + InstanceDistribution.DealingName(last.className.get(2)) + " -"  + df.format(time) + "%", new Color(255, 0, 128,160)));
        values.add(new Segment(time = (double)topFive[3] / last.totalBytes * 100,
                "4- " + InstanceDistribution.DealingName(last.className.get(3)) + " -"  + df.format(time) + "%", new Color(128, 0, 128,160)));
        values.add(new Segment(time = (double)topFive[4] / last.totalBytes * 100,
                "5- " + InstanceDistribution.DealingName(last.className.get(4)) + " -"  + df.format(time) + "%", new Color(0, 0, 255,160)));
        values.add(new Segment(time = (double)others / last.totalBytes * 100, "Others -" + df.format(time) + "%", new Color(255, 0, 0,160)));

        PieChart pieChart = new PieChart(values, "Object Type Distribution");
        pieChart.setSize(600, 700);
        pieChart.setBounds(1200,1120,600,700);
        pieChart.setVisible(true);
        return pieChart;
    }

    private static PieChart createFullPieChart() {

        ArrayList<Segment> values = new ArrayList<>();

        double time = 0;

        values.add(new Segment(time = PreCompactTotal / FullGCtimeSum * 100, "Pre Compact -" + df.format(time) + "%", new Color(0, 0, 255,160)));
        values.add(new Segment(time = MarkingTimeTotal / FullGCtimeSum * 100 , "Marking -" + df.format(time) + "%", new Color(0, 128, 255,160)));
        values.add(new Segment(time = SummaryTimeTotal / FullGCtimeSum * 100, "Summary -" + df.format(time) + "%", new Color(0, 255, 255,160)));
        values.add(new Segment(time = AdjustRootsTotal / FullGCtimeSum * 100, "Adjust Roots -" + df.format(time) + "%", new Color(0, 255, 0,160)));
        values.add(new Segment(time = CompactTimeTotal / FullGCtimeSum * 100, "Compact -" + df.format(time) + "%", new Color(0, 255, 128,80)));
        values.add(new Segment(time = PostCompactTotal / FullGCtimeSum * 100, "Post Compact -" + df.format(time) + "%", new Color(0, 128, 0,80)));
        values.add(new Segment(time = FullAdaptive / FullGCtimeSum * 100, "Adaptive Policy -" + df.format(time) + "%", new Color(0, 64, 0,80)));

        PieChart pieChart = new PieChart(values, "Full GC Time Distribution");
        pieChart.setSize(600, 700);
        pieChart.setBounds(600,1120,600,700);
        pieChart.setVisible(true);
        return pieChart;

    }

    private static JPanel FullGCStats() {
        GridLayout layout = new GridLayout(7, 2);
        JPanel FullGC = new JPanel(layout);
        FullGC.setBackground(Color.WHITE);

        FullGC.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        JLabel[] TextLable = new JLabel[14];
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        for(int i=0;i<14;i++){
            TextLable[i] = new JLabel("",JLabel.CENTER);
            TextLable[i].setPreferredSize(new Dimension(50,30));
            TextLable[i].setBorder(border);
            Font set = new Font("Dialog", 0, 18);
            TextLable[i].setFont(set);
        }

        double backup = 0;
        long numberbackup = 0;
        TextLable[0].setText("Full GC总数");
        TextLable[1].setText(String.valueOf(FullGCcount));

        TextLable[2].setText("平均Full GC时间");
        TextLable[3].setText(df.format (backup = (FullGCcount != 0) ? (FullGCtimeSum / FullGCcount) : 0) + " sec");

        TextLable[4].setText("平均标记阶段时间");
        TextLable[5].setText(df.format( backup = (FullGCcount != 0) ? (MarkingTimeTotal / FullGCcount) : 0) + " sec");

        TextLable[6].setText("平均整理阶段时间");
        TextLable[7].setText(df.format( backup = (FullGCcount != 0) ? (CompactTimeTotal / FullGCcount)  : 0)+ " sec");

        TextLable[8].setText("平均处理对象大小");
        TextLable[9].setText( (numberbackup = (FullGCcount != 0) ? (FullTotalProcess / FullGCcount) : 0) + " bytes");

        TextLable[10].setText("平均清理对象大小");
        TextLable[11].setText( (numberbackup = (FullGCcount != 0) ? (FullTotalClean / FullGCcount) : 0) + " bytes");

        TextLable[12].setText("平均CPU利用率");
        TextLable[13].setText(df.format( backup = (FullGCcount != 0) ? (FullCPUPercentage / FullGCcount * 100) : 0) + "%");

        for(int i=0;i<14;i++)
            FullGC.add(TextLable[i]);

        return FullGC;
    }

    private static JPanel MinorGCStats() {
        GridLayout layout = new GridLayout(8, 2);
        JPanel MinorGC = new JPanel(layout);
        MinorGC.setBackground(Color.WHITE);

        MinorGC.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        JLabel[] TextLable = new JLabel[16];
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        for(int i=0;i<16;i++){
            TextLable[i] = new JLabel("",JLabel.CENTER);
            TextLable[i].setPreferredSize(new Dimension(50,30));
            TextLable[i].setBorder(border);
            Font set = new Font("Dialog", 0, 18);
            TextLable[i].setFont(set);
        }

        TextLable[0].setText("Minor GC 总数");
        TextLable[1].setText(String.valueOf(MinorGCcount));

        TextLable[2].setText("平均Minor GC时间");
        TextLable[3].setText(df.format(MinorGCtimeSum / MinorGCcount) + " sec");

        TextLable[4].setText("溢出次数");
        TextLable[5].setText(String.valueOf(overFlowTime));

        TextLable[6].setText("平均处理对象大小");
        TextLable[7].setText(MinorTotalProcess / MinorGCcount + " bytes");

        TextLable[8].setText("平均清理对象大小");
        TextLable[9].setText(MinorTotalClean / MinorGCcount + " bytes");

        TextLable[10].setText("平均幸存对象大小");
        TextLable[11].setText(survivedTotal / MinorGCcount + " bytes");

        TextLable[12].setText("平均晋升对象大小");
        TextLable[13].setText(promotionTotal / MinorGCcount + " bytes");

        TextLable[14].setText("平均CPU利用率");
        TextLable[15].setText((df.format(MinorCPUPercentage / MinorGCcount * 100)) + "%");

        for(int i=0;i<16;i++)
            MinorGC.add(TextLable[i]);

        return MinorGC;
    }

}
