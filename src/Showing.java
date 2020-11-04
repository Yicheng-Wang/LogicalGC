import com.frontangle.ichart.pie.PieChart;
import com.frontangle.ichart.pie.Segment;
import sun.rmi.runtime.Log;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

public class Showing {

    static DecimalFormat df = new DecimalFormat("#0.000");
    static Font TitleStyle = new Font("Dialog", 1, 20);
    static Font TableStyle = new Font("Dialog", 0, 18);

    static int GCcount = 0;
    static int MinorGCcount = 0;
    static int FullGCcount = 0;

    static double GCtimesum = 0;
    static double MinorGCtimeSum = 0;
    static double FullGCtimeSum = 0;

    static long totalReclaimed = 0;
    static long MinorTotalProcess = 0;

    static double MinorReclaimPercentage = 0;
    static double FullReclaimPercentage = 0;

    static int overFlowTime = 0;
    static double Apptime = 0;

    public static void shows() {

        JFrame Mainframe = new JFrame();
        Mainframe.setLayout(null);
        Mainframe.setSize(1800,1000);

        JPanel MainPanel = new JPanel();
        MainPanel.setLayout(null);
        MainPanel.setBounds(0,0,1800, 1000);
        MainPanel.setPreferredSize(new Dimension(1000, 800));
        MainPanel.setBackground(Color.WHITE);
        JLabel TitleFirst = new JLabel("整体情况",JLabel.CENTER);

        TitleFirst.setFont(TitleStyle);
        TitleFirst.setBounds(20,10,550,50);
        MainPanel.add(TitleFirst);
        JPanel TotalGCStats = Showing.TotalGCStats();
        TotalGCStats.setBounds(20,60,550,200);
        MainPanel.add(TotalGCStats);

        JLabel TitleSecond = new JLabel("Minor GC",JLabel.CENTER);
        TitleSecond.setFont(TitleStyle);
        TitleSecond.setBounds(590,10,550,50);
        MainPanel.add(TitleSecond);
        JPanel MinorGCStats = Showing.MinorGCStats();
        TotalGCStats.setBounds(20,60,550,200);
        MainPanel.add(MinorGCStats);
        
        JScrollPane jsp = new JScrollPane(MainPanel);
        jsp.setBounds(20,10,1600, 800);
        jsp.setBackground(Color.WHITE);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        //jsp.getViewport().add(panel2);
        ArrayList<Segment> values = new ArrayList<Segment>();
        values.add(new Segment(46, "Full GC", Color.RED));
        values.add(new Segment(32, "Minor GC", Color.ORANGE));
        values.add(new Segment(22, "Application", Color.GREEN));

        PieChart pieChart = new PieChart(values, "Time Period");
        pieChart.setSize(700, 600);
        pieChart.setBounds(50,280,600,600);
        pieChart.setVisible(true);
        MainPanel.add(pieChart);


        Mainframe.add(jsp);
        Mainframe.setVisible(true);

    }

    private static JPanel MinorGCStats() {
        GridLayout layout = new GridLayout(7, 2);
        JPanel MinorGC = new JPanel(layout);
        MinorGC.setBackground(Color.WHITE);

        MinorGC.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        JLabel[] TextLable = new JLabel[14];
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        for(int i=0;i<14;i++){
            TextLable[i] = new JLabel("",JLabel.CENTER);
            TextLable[i].setPreferredSize(new Dimension(50,30));
            TextLable[i].setBorder(border);
            Font set = new Font("Dialog", 0, 18);
            TextLable[i].setFont(set);
        }
        return MinorGC;
    }

    private static JPanel TotalGCStats() {
        GridLayout layout = new GridLayout(7, 2);
        JPanel TotalGC = new JPanel(layout);
        TotalGC.setBackground(Color.WHITE);

        TotalGC.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        JLabel[] TextLable = new JLabel[14];
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        for(int i=0;i<14;i++){
            TextLable[i] = new JLabel("",JLabel.CENTER);
            TextLable[i].setPreferredSize(new Dimension(50,30));
            TextLable[i].setBorder(border);
            TextLable[i].setFont(TableStyle);
        }

        GCcount = LogReader.GCRecord.size();

        TextLable[0].setText("GC总数");
        TextLable[1].setText(String.valueOf(GCcount));

        TextLable[2].setText("GC总用时");
        for(int i=0;i<GCcount;i++){
            GC Judge =  LogReader.GCRecord.get(i);
            if(Judge instanceof FullGC){
                FullGCcount++;

            }
            GCtimesum += Judge.timeCost;
            totalReclaimed += Judge.cleanSize.valueForm;
        }
        TextLable[3].setText(df.format(GCtimesum) + " sec");

        TextLable[4].setText("GC平均用时");
        TextLable[5].setText(df.format(GCtimesum / GCcount)  + " sec" );

        for(int i=0;i<LogReader.ApplicationRecord.size();i++)
            Apptime += LogReader.ApplicationRecord.get(i).length;
        TextLable[6].setText("应用线程执行用时");
        TextLable[7].setText(df.format(Apptime) + " sec");

        TextLable[8].setText("GC平均触发间隔");
        TextLable[9].setText(df.format(Apptime / LogReader.ApplicationRecord.size()) + " sec");

        TextLable[10].setText(" GC清理对象总大小 ");
        TextLable[11].setText(String.valueOf(totalReclaimed) + " bytes");

        TextLable[12].setText(" 应用创建对象总大小 ");
        TextLable[13].setText(String.valueOf(totalReclaimed + LogReader.lastcreate) + " bytes");

        for(int i=0;i<14;i++)
            TotalGC.add(TextLable[i]);

        return TotalGC;
    }

}
