import com.frontangle.ichart.pie.Segment;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public class WriteInfo {
    static DecimalFormat df = new DecimalFormat("#0.000");
    static DecimalFormat df2 = new DecimalFormat("#0");
    static Font TitleStyle = new Font("Dialog", 1, 20);
    static Font TableStyle = new Font("Dialog", 0, 18);

    public static void writeTime(BufferedWriter out) throws IOException {
        out.write("应用执行总时长: " + LogReader.timeLine.get(LogReader.timeLine.size()-1) + " sec");
        out.newLine();

        out.write("应用线程用时: " + df.format(Showing.Apptime) + " sec");
        out.newLine();

        out.write("吞吐率: " + df.format(Showing.Apptime/ Showing.totalExecutionTime * 100) + "%");
        out.newLine();

        out.write("GC暂停用时: " + df.format(Showing.GCtimesum) + " sec");
        out.newLine();

        out.write("Young GC用时: " + df.format(Showing.MinorGCtimeSum) + " sec");
        out.newLine();

        out.write("Full GC用时: " + df.format(Showing.FullGCtimeSum) + " sec");
        out.newLine();

        out.write("安全点用时: " + df.format(Showing.SafePointTotal) + " sec");
        out.newLine();

        out.write("虚拟机启动用时: " + df.format(Showing.WarmupTime) + " sec");
        out.newLine();

        out.write("信息输出用时: " + df.format(Showing.CollectInfoTime) + " sec");
        out.newLine();

        out.write("其他用时: " + df.format(Showing.PrintInforTime + Showing.AdaptiveTime) + " sec");
        out.newLine();
    }

    public static void ApplicationStats(BufferedWriter out) throws IOException {
        out.write("应用线程总数: " + String.valueOf(Showing.ThreadTotalNum));
        out.newLine();

        out.write("最大同时应用线程数: " + String.valueOf(Showing.MaxThreadAtTime));
        out.newLine();

        out.write("平均同时应用线程数: " + String.valueOf(Showing.MeanThreadNum));
        out.newLine();

        out.write("TLAB最大浪费比例: " + df.format(Showing.MaxWastePer) + "%");
        out.newLine();

        out.write("TLAB平均浪费比例: " + df.format(Showing.MeanWastePer) + "%");
        out.newLine();

        out.write("初始TLAB大小: " + String.valueOf(Showing.InitialMeanTLAB) + " KB");
        out.newLine();

        out.write("初始TLAB重填阈值: " + String.valueOf(Showing.InitialRefillWaste) + " KB");
        out.newLine();

        out.write("最终TLAB大小: " + String.valueOf(Showing.FinalMeanTLAB) + " KB");
        out.newLine();

        out.write("最终TLAB重填阈值: " + String.valueOf(Showing.FinalRefillWaste) + " KB");
        out.newLine();

        out.write("平均重填次数: " + String.valueOf(Showing.MeanRefillTimes));
        out.newLine();

        out.write("平均慢速分配: " + String.valueOf(Showing.MeanSlowAlloc));
        out.newLine();
    }

    public static void TotalGCStats(BufferedWriter out) throws IOException {
        out.write("GC总次数: " + String.valueOf(Showing.GCcount));
        out.newLine();

        out.write("GC平均用时: " + df.format(Showing.GCtimesum / Showing.GCcount) + " sec");
        out.newLine();

        out.write("单次GC最大用时: " + df.format(Showing.MaxGCTime) + " sec");
        out.newLine();

        out.write("GC平均触发间隔: " + df.format(Showing.Apptime / LogReader.ApplicationRecord.size()) + " sec");
        out.newLine();

        Iterator map1it=Showing.GCCauseTotal.entrySet().iterator();
        int index = 0;

        while(map1it.hasNext())
        {
            Map.Entry<String, Double[]> entry=(Map.Entry<String, Double[]>) map1it.next();

            out.write("GC触发原因 " + (index+1) + ": " + entry.getKey());
            out.newLine();
            out.write("触发次数(平均时间): " + df2.format(entry.getValue()[0]) + "次  (平均" + df.format((entry.getValue()[1] / entry.getValue()[0])) + " sec)");
            out.newLine();
            index ++;
        }
    }

    public static void MinorGCStats(BufferedWriter out) throws IOException {

        out.write("Minor GC次数: " + String.valueOf(Showing.MinorGCcount));
        out.newLine();


        out.write("平均Minor GC时间: " + df.format(Showing.MinorGCtimeSum / Showing.MinorGCcount) + " sec");
        out.newLine();


        out.write("最大Minor GC时间: " + df.format(Showing.MaxMinorGCTime) + " sec");
        out.newLine();

        out.write("溢出次数: " + String.valueOf(Showing.overFlowTime));
        out.newLine();

        out.write("平均幸存比例: " + (df.format((double) Showing.survivedTotal / (double)Showing.MinorTotalProcess * 100)) + "%");
        out.newLine();

        out.write("平均晋升比例: " + (df.format((double)Showing.promotionTotal / (double)Showing.MinorTotalProcess * 100)) + "%");
        out.newLine();

        out.write("平均清理比例: " + (df.format((double)Showing.MinorTotalClean / (double)Showing.MinorTotalProcess * 100)) + "%");
        out.newLine();

        out.write("平均CPU利用率: " + (df.format(Showing.MinorCPUPercentage / Showing.MinorGCcount * 100)) + "%");
        out.newLine();

        out.write("最终晋升阈值: " + df2.format(Showing.FinalThreshold));
        out.newLine();

        double EdenSize = LogReader.HeapRecord.get(LogReader.HeapRecord.size()-1).HeapPartition[0].totalSize.ValueFormM;
        double FromSize = LogReader.HeapRecord.get(LogReader.HeapRecord.size()-1).HeapPartition[1].totalSize.ValueFormM;
        double ToSize = LogReader.HeapRecord.get(LogReader.HeapRecord.size()-1).HeapPartition[2].totalSize.ValueFormM;

        out.write("最终幸存者区占比: " + (df.format((FromSize+ToSize) / (FromSize+ToSize+EdenSize) * 100)) + "%");
        out.newLine();
    }

    public static void MajorGCStats(BufferedWriter out) throws IOException {
        double backup = 0;
        long numberbackup = 0;

        out.write("Full GC总数: " + (String.valueOf(Showing.FullGCcount)));
        out.newLine();

        out.write("平均Full GC时间: " + df.format (backup = (Showing.FullGCcount != 0) ? (Showing.FullGCtimeSum / Showing.FullGCcount) : 0) + " sec");
        out.newLine();

        out.write("最大Full GC时间: " + df.format( backup = (Showing.FullGCcount != 0) ? (Showing.MaxMajorGCTime) : 0) + " sec");
        out.newLine();

        out.write("平均CPU利用率: " + df.format( backup = (Showing.FullGCcount != 0) ? (Showing.FullCPUPercentage / Showing.FullGCcount * 100)  : 0)+ "%");
        out.newLine();

        out.write("平均幸存比例: " + df.format (numberbackup = (Showing.FullGCcount != 0) ? (1 - Showing.FullTotalClean / Showing.FullTotalProcess)  * 100 : 0) + "%");
        out.newLine();

        double[] topFive = new double[5];
        String[] topFiveName = new String[5];

        ArrayList<Segment> values = new ArrayList<>();
        ArrayList<Map.Entry<String, Double[]>> list = new ArrayList<>(Showing.SurvivedObjects.entrySet());
        list.sort(new Comparator<Map.Entry<String, Double[]>>() {
            @Override
            public int compare(Map.Entry<String, Double[]> o1, Map.Entry<String, Double[]> o2) {
                return o2.getValue()[0].compareTo(o1.getValue()[0]);
            }
        });

        for(int i = 0; i < 5; i++){
            topFive[i] = list.get(i).getValue()[0];
            topFiveName[i] = list.get(i).getKey().trim();
        }

        for(int i=0;i<5;i++){
            out.write("主要幸存对象类型" + i + "(大小): " + InstanceDistribution.DealingName(topFiveName[i]) + " (" + df2.format(topFive[i] / 1024) + " MB)");
            out.newLine();
        }
    }
}
