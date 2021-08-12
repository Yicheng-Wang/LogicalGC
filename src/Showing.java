import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.frontangle.ichart.chart.XYChart;
import com.frontangle.ichart.pie.PieChart;
import com.frontangle.ichart.pie.Segment;

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

    static double MaxGCTime = 0;
    static double MaxMinorGCTime = 0;
    static double MaxMajorGCTime = 0;
    static double FinalThreshold = 0;

    static double totalExecutionTime = 0;
    static double WarmupTime = 0;
    static double AdaptiveTime = 0;
    static double CollectInfoTime = 0;
    static double PrintInforTime = 0;

    static HashMap<String,Double[]> GCCauseTotal = new HashMap<>();
    static HashMap<String,Double[]> GCCauseYoung = new HashMap<>();
    static HashMap<String,Double[]> GCCauseOld = new HashMap<>();

    static HashMap<String,Double[]> SurvivedObjects = new HashMap<>();
    static double Totalbytes;
    //static double MinorReclaimPercentage = 0;
    //static double FullReclaimPercentage = 0;

    static int overFlowTime = 0;
    static double Apptime = 0;

    public static void shows(String title) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(title + "_Info.txt"));

        SettleInformation();

        out.write("=================用时情况==================");
        out.newLine();
        WriteInfo.writeTime(out);

        out.write("=================应用线程==================");
        out.newLine();
        WriteInfo.ApplicationStats(out);

        out.write("=================GC情况==================");
        out.newLine();
        WriteInfo.TotalGCStats(out);

        out.write("=================Minor GC==================");
        out.newLine();
        WriteInfo.MinorGCStats(out);

        if(FullGCcount > 0){
            out.write("=================Major GC==================");
            out.newLine();
            WriteInfo.MajorGCStats(out);
        }

        out.close();

        final int MAX_HEIGHT = 850;
        final int MAX_WIDTH = 1430;
        final int PADDING = 50;
        final int FIRST_COLUMN_WIDTH = 500;
        final int SECOND_COLUMN_WIDTH = 800;
        final int SECOND_COLUMN_START = PADDING + FIRST_COLUMN_WIDTH;

        JPanel MainPanel = new JPanel();
        MainPanel.setLayout(null);
        MainPanel.setBounds(0, 0, MAX_WIDTH, 3800);
        MainPanel.setPreferredSize(new Dimension(MAX_WIDTH, 3800));
        MainPanel.setBackground(Color.WHITE);

        Table.OverallStats(MainPanel,"用时情况",PADDING,110,FIRST_COLUMN_WIDTH,340);

        Drawing.createTimePieChart(MainPanel,SECOND_COLUMN_START,20,SECOND_COLUMN_WIDTH,520);

        Table.ApplicationStats(MainPanel,"应用线程",PADDING,620,FIRST_COLUMN_WIDTH,374);

        Drawing.createThreadChart(MainPanel,SECOND_COLUMN_START,550,SECOND_COLUMN_WIDTH,520);

        Table.TotalGCStats(MainPanel,"GC情况",PADDING,1200,FIRST_COLUMN_WIDTH,34 * (4 + 2 * GCCauseTotal.size()));

        Drawing.createCauseChart(MainPanel,SECOND_COLUMN_START,1080,SECOND_COLUMN_WIDTH,520);

        Table.MinorGCStats(MainPanel,"Minor GC",PADDING,1740,FIRST_COLUMN_WIDTH,340);

        ShowHeap.YoungGenStats(MainPanel,SECOND_COLUMN_START,1620,SECOND_COLUMN_WIDTH,600);

        if(FullGCcount > 0){
            Drawing.ObjectDistributionChart(MainPanel,SECOND_COLUMN_START,2300,SECOND_COLUMN_WIDTH,520);
            Table.MajorGCStats(MainPanel, "Major GC", PADDING, 2380, FIRST_COLUMN_WIDTH, 340);
        }

        JScrollPane jsp = new JScrollPane(MainPanel);
        jsp.setBounds(10,10,MAX_WIDTH-30, MAX_HEIGHT-50);
        jsp.setBackground(Color.WHITE);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        XYChart chart = ShowHeap.createHeapXYChart();
        chart.setBounds(PADDING, 2800, 1300, 800);
        MainPanel.add(chart);
        MainPanel.setVisible(true);

        /*JFrame Mainframe = new JFrame();
        Mainframe.setLayout(null);
        Mainframe.setSize(MAX_WIDTH, MAX_HEIGHT);
        Mainframe.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Mainframe.setTitle(title);

        Mainframe.add(jsp);
        Mainframe.setVisible(true);*/

        //Component c = jsp.getViewport().getView();
        BufferedImage image = new BufferedImage(MainPanel.getWidth(),MainPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        //c.paint(image.getGraphics());
        MainPanel.print(g2);
        ImageIO.write(image, "png", new java.io.File(title + "InfoPic.jpg"));
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
            if (Judge instanceof FullGC) {
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
                MaxMajorGCTime = (Judge.timeCost > MaxMajorGCTime)?Judge.timeCost:MaxMajorGCTime;

                Double old[];
                if (GCCauseOld.containsKey(Judge.Cause)) {
                    old = GCCauseOld.get(Judge.Cause);
                    old[0] += 1;
                    old[1] += Judge.timeCost;
                    GCCauseOld.put(Judge.Cause,old);
                } else {
                    old = new Double[2];
                    old[0] = 1.0;
                    old[1] = Judge.timeCost;
                    GCCauseOld.put(Judge.Cause,old);
                }
            } else {
                MinorGCcount++;
                MinorGCtimeSum += Judge.timeCost;
                MinorTotalProcess += Judge.processSize.valueForm;
                MinorTotalClean += Judge.cleanSize.valueForm;
                survivedTotal += ((YoungGC)Judge).survivedSize.valueForm;
                promotionTotal += ((YoungGC)Judge).promotionSize.valueForm;
                MinorCPUPercentage += Judge.CPUpercentage;
                MinorAdaptiveTime += Judge.AdaptiveTime;
                FinalThreshold = ((YoungGC)Judge).newThreshold;
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

                MaxMinorGCTime = (Judge.timeCost > MaxMinorGCTime)?Judge.timeCost:MaxMinorGCTime;

            }

            MaxGCTime = (Judge.timeCost > MaxGCTime)?Judge.timeCost:MaxGCTime;
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

        for(int i=1;i<LogReader.distributions.size();i+=2){
            InstanceDistribution after = LogReader.distributions.get(i);
            Double[] handle;
            for(int j=0;j<after.bytes.size();j++){
                if(SurvivedObjects.containsKey(after.className.get(j))){
                    handle = SurvivedObjects.get(after.className.get(j));
                    handle[0] += after.bytes.get(j);
                    handle[1] += after.instances.get(j);
                    SurvivedObjects.put(after.className.get(j),handle);
                }
                else{
                    handle = new Double[2];
                    handle[0] = (double)after.bytes.get(j);
                    handle[1] = (double)after.instances.get(j);
                    SurvivedObjects.put(after.className.get(j),handle);
                }
                Totalbytes += after.bytes.get(j);
            }
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
        pieChart.setBounds(600,1820,600,700);
        pieChart.setVisible(true);
        return pieChart;

    }

}
