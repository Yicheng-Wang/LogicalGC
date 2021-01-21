import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

public class Table {
    static DecimalFormat df = new DecimalFormat("#0.000");
    static DecimalFormat df2 = new DecimalFormat("#0");
    static Font TitleStyle = new Font("Dialog", 1, 20);
    static Font TableStyle = new Font("Dialog", 0, 18);

    public static void OverallStats(JPanel mainPanel, String name, int x, int y, int width, int height) {
        JPanel TotalGC = initialTable(10,2);
        JLabel[] TextLable = initialBorde(20);

        TextLable[0].setText("应用执行总时长");
        TextLable[1].setText(LogReader.timeLine.get(LogReader.timeLine.size()-1) + " sec");

        TextLable[2].setText("应用线程用时");
        TextLable[3].setText(df.format(Showing.Apptime) + " sec");

        TextLable[4].setText("吞吐率");
        TextLable[5].setText(df.format(Showing.Apptime/ Showing.totalExecutionTime * 100) + "%");

        TextLable[6].setText("GC暂停用时");
        TextLable[7].setText(df.format(Showing.GCtimesum) + " sec");

        TextLable[8].setText("Young GC用时");
        TextLable[9].setText(df.format(Showing.MinorGCtimeSum) + " sec");

        TextLable[10].setText("Full GC用时");
        TextLable[11].setText(df.format(Showing.FullGCtimeSum) + " sec");

        TextLable[12].setText("安全点用时");
        TextLable[13].setText(df.format(Showing.SafePointTotal) + " sec");

        TextLable[14].setText("虚拟机启动用时");
        TextLable[15].setText(df.format(Showing.WarmupTime) + " sec");

        TextLable[16].setText("信息输出用时");
        TextLable[17].setText(df.format(Showing.CollectInfoTime) + " sec");

        TextLable[18].setText("其他用时");
        TextLable[19].setText(df.format(Showing.PrintInforTime + Showing.AdaptiveTime) + " sec");

        /*TextLable[4].setText("GC平均用时");
        TextLable[5].setText(df.format(GCtimesum / GCcount)  + " sec" );

        TextLable[8].setText("GC平均触发间隔");
        TextLable[9].setText(df.format(Apptime / LogReader.ApplicationRecord.size()) + " sec");

        TextLable[10].setText(" GC清理对象总大小 ");
        TextLable[11].setText(String.valueOf(totalReclaimed) + " bytes");

        TextLable[12].setText(" 应用创建对象总大小 ");
        TextLable[13].setText(String.valueOf(totalReclaimed + LogReader.lastcreate) + " bytes");*/

        for(int i=0;i<20;i++)
            TotalGC.add(TextLable[i]);

        JLabel Title = SetTitle(name,x,y-60,width,50);
        mainPanel.add(Title);

        TotalGC.setBounds(x,y,width,height);
        mainPanel.add(TotalGC);
    }

    private static JLabel[] initialBorde(int num) {
        JLabel[] TextLable = new JLabel[num];
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        for(int i=0;i<num;i++){
            TextLable[i] = new JLabel("",JLabel.CENTER);
            TextLable[i].setPreferredSize(new Dimension(50,30));
            TextLable[i].setBorder(border);
            TextLable[i].setFont(TableStyle);
        }
        return TextLable;
    }

    private static JPanel initialTable(int row, int col) {
        GridLayout layout = new GridLayout(row, col);
        JPanel TotalGC = new JPanel(layout);
        TotalGC.setBackground(Color.WHITE);
        TotalGC.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        return TotalGC;
    }

    public static void ApplicationStats(JPanel mainPanel, String name, int x, int y, int width, int height) {
        JPanel Application = initialTable(11,2);
        JLabel[] TextLable = initialBorde(22);

        TextLable[0].setText("应用线程总数");
        TextLable[1].setText(String.valueOf(Showing.ThreadTotalNum));

        TextLable[2].setText("最大同时应用线程数");
        TextLable[3].setText(String.valueOf(Showing.MaxThreadAtTime));

        TextLable[4].setText("平均同时应用线程数");
        TextLable[5].setText(String.valueOf(Showing.MeanThreadNum));

        TextLable[6].setText("TLAB最大浪费比例");
        TextLable[7].setText(df.format(Showing.MaxWastePer) + "%");

        TextLable[8].setText("TLAB平均浪费比例");
        TextLable[9].setText(df.format(Showing.MeanWastePer) + "%");

        TextLable[10].setText("初始TLAB大小");
        TextLable[11].setText(String.valueOf(Showing.InitialMeanTLAB) + " KB");

        TextLable[12].setText("初始TLAB重填阈值");
        TextLable[13].setText(String.valueOf(Showing.InitialRefillWaste) + " KB");

        TextLable[14].setText("最终TLAB大小");
        TextLable[15].setText(String.valueOf(Showing.FinalMeanTLAB) + " KB");

        TextLable[16].setText("最终TLAB重填阈值");
        TextLable[17].setText(String.valueOf(Showing.FinalRefillWaste) + " KB");

        TextLable[18].setText("平均重填次数");
        TextLable[19].setText(String.valueOf(Showing.MeanRefillTimes));

        TextLable[20].setText("平均慢速分配");
        TextLable[21].setText(String.valueOf(Showing.MeanSlowAlloc));

        for(int i=0;i<22;i++)
            Application.add(TextLable[i]);

        JLabel Title = SetTitle(name,x,y-60,width,50);
        mainPanel.add(Title);

        Application.setBounds(x,y,width,height);
        mainPanel.add(Application);
    }


    public static JLabel SetTitle(String name, int x, int y, int width, int height) {
        JLabel TitleFirst = new JLabel(name,JLabel.CENTER);
        TitleFirst.setFont(TitleStyle);
        TitleFirst.setBounds(x,y,width,height);
        return TitleFirst;
    }

    public static void MinorGCStats(JPanel mainPanel, String name, int x, int y, int width, int height) {

    }

    public static void TotalGCStats(JPanel mainPanel, String name, int x, int y, int width, int height) {
        int CauseNum = Showing.GCCauseTotal.size();
        int rowCount = 4 + CauseNum * 2;
        JPanel Application = initialTable(rowCount,2);
        JLabel[] TextLable = initialBorde(rowCount * 2);

        TextLable[0].setText("GC总次数");
        TextLable[1].setText(String.valueOf(Showing.ThreadTotalNum));

        TextLable[2].setText("GC平均用时");
        TextLable[3].setText(String.valueOf(Showing.MaxThreadAtTime));

        TextLable[4].setText("单次GC最大用时");
        TextLable[5].setText(String.valueOf(Showing.MeanThreadNum));

        TextLable[6].setText("GC平均触发间隔");
        TextLable[7].setText(df.format(Showing.MaxWastePer) + "%");

        Iterator map1it=Showing.GCCauseTotal.entrySet().iterator();
        int index = 0;
        while(map1it.hasNext())
        {
            Map.Entry<String, Double[]> entry=(Map.Entry<String, Double[]>) map1it.next();
            TextLable[8 + 4 * index].setText("GC触发原因 " + index);
            TextLable[8 + 4 * index + 1].setText(entry.getKey());
            TextLable[8 + 4 * index + 2].setText("触发次数（时间）");
            TextLable[8 + 4 * index + 3].setText((entry.getValue()[0]) + "次" + (entry.getValue()[0]) + " sec");
            index ++;
        }
        for(int i=0;i<rowCount * 2;i++)
            Application.add(TextLable[i]);

        JLabel Title = SetTitle(name,x,y-60,width,50);
        mainPanel.add(Title);

        Application.setBounds(x,y,width,height);
        mainPanel.add(Application);
    }
}
