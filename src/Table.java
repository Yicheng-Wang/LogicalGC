import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.text.DecimalFormat;

public class Table {
    static DecimalFormat df = new DecimalFormat("#0.000");
    static Font TitleStyle = new Font("Dialog", 1, 20);
    static Font TableStyle = new Font("Dialog", 0, 18);

    public static void TotalGCStats(JPanel mainPanel, String name, int x, int y, int width, int height) {
        JPanel TotalGC = initialTable(9,2);
        JLabel[] TextLable = initialBorde(18);

        TextLable[0].setText("应用执行总时长");
        TextLable[1].setText(LogReader.timeLine.get(LogReader.timeLine.size()-1) + " sec");

        TextLable[2].setText("应用线程用时");
        TextLable[3].setText(df.format(Showing.Apptime) + " sec");

        TextLable[4].setText("GC暂停用时");
        TextLable[5].setText(df.format(Showing.GCtimesum) + " sec");

        TextLable[6].setText("Young GC用时");
        TextLable[7].setText(df.format(Showing.MinorGCtimeSum) + " sec");

        TextLable[8].setText("Full GC用时");
        TextLable[9].setText(df.format(Showing.FullGCtimeSum) + " sec");

        TextLable[10].setText("安全点用时");
        TextLable[11].setText(df.format(Showing.SafePointTotal) + " sec");

        TextLable[12].setText("虚拟机启动用时");
        TextLable[13].setText(df.format(Showing.WarmupTime) + " sec");

        TextLable[14].setText("信息输出用时");
        TextLable[15].setText(df.format(Showing.CollectInfoTime) + " sec");

        TextLable[16].setText("其他用时");
        TextLable[17].setText(df.format(Showing.PrintInforTime + Showing.AdaptiveTime) + " sec");

        /*TextLable[4].setText("GC平均用时");
        TextLable[5].setText(df.format(GCtimesum / GCcount)  + " sec" );

        TextLable[8].setText("GC平均触发间隔");
        TextLable[9].setText(df.format(Apptime / LogReader.ApplicationRecord.size()) + " sec");

        TextLable[10].setText(" GC清理对象总大小 ");
        TextLable[11].setText(String.valueOf(totalReclaimed) + " bytes");

        TextLable[12].setText(" 应用创建对象总大小 ");
        TextLable[13].setText(String.valueOf(totalReclaimed + LogReader.lastcreate) + " bytes");*/

        for(int i=0;i<18;i++)
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

        //static long MaxThreadAtTime = 0;
        //static long MeanThreadNum = 0;
        //static double MaxWastePer = 0;
        //static double MeanWastePer = 0;
        //static long InitialMeanTLAB = 0;
        //static long InitialRefillWaste = 0;
        //static long FinalMeanTLAB = 0;
        //static long FinalRefillWaste = 0;
        //static long MeanRefillTimes = 0;
        //static long ThreadTotalNum = 0;

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
}
