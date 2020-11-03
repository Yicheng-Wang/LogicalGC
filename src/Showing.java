import com.frontangle.ichart.pie.PieChart;
import com.frontangle.ichart.pie.Segment;
import sun.rmi.runtime.Log;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

public class Showing {

    public static void shows() {

        JFrame Mainframe = new JFrame();
        Mainframe.setLayout(null);
        Mainframe.setSize(1800,1000);

        JPanel MainPanel = new JPanel();
        MainPanel.setLayout(null);
        MainPanel.setBounds(0,0,1800, 1000);
        MainPanel.setPreferredSize(new Dimension(800, 600));

        JLabel Title = new JLabel("Overall Statics",JLabel.CENTER);
        Font set = new Font("Dialog", 1, 20);
        Title.setFont(set);
        Title.setBounds(10,10,550,50);
        MainPanel.add(Title);

        JPanel TotalGCStats = Showing.TotalGCStats();
        TotalGCStats.setBounds(10,60,550,150);
        MainPanel.add(TotalGCStats);

        JScrollPane jsp = new JScrollPane(MainPanel);
        jsp.setBounds(10,10,800, 800);
        jsp.setBackground(Color.WHITE);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //jsp.getViewport().add(panel2);

        /*
        JPanel TotalGCStats = Showing.TotalGCStats();
        TotalGCStats.setBounds(10,10,280,80);
        TotalGCStats.setPreferredSize(new Dimension(600, 600));
        jsp.getViewport().add(TotalGCStats);
        TotalGCStats.setVisible(true);

        ArrayList<Segment> values = new ArrayList<Segment>();
        values.add(new Segment(35, "something", Color.RED));
        values.add(new Segment(32, "something else", Color.BLUE));
        values.add(new Segment(5, "something or other", Color.BLUE.brighter()));
        values.add(new Segment(3, "this", Color.BLUE.darker()));
        values.add(new Segment(7, "that", Color.ORANGE));
        values.add(new Segment(5, "and", Color.CYAN));
        values.add(new Segment(13, "the other", Color.GREEN));
        PieChart pieChart = new PieChart(values, "Simple Pie");
        pieChart.setSize(800, 700);
        pieChart.setBounds(50,200,800,700);
        jsp.getViewport().add(pieChart);
        pieChart.setVisible(true);*/

        Mainframe.add(jsp);
        Mainframe.setVisible(true);

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
            TextLable[i].setPreferredSize(new Dimension(50,20));
            TextLable[i].setBorder(border);
            Font set = new Font("Dialog", 0, 18);
            TextLable[i].setFont(set);
        }

        DecimalFormat df = new DecimalFormat("#0.000");

        int GCcount = LogReader.GCRecord.size();
        TextLable[0].setText(" Total GC Count: ");
        TextLable[1].setText(String.valueOf(GCcount));

        double GCtimesum = 0;
        long totalReclaimed = 0;
        TextLable[2].setText(" Total GC Time: ");
        for(int i=0;i<GCcount;i++){
            GCtimesum += LogReader.GCRecord.get(i).timeCost;
            totalReclaimed += LogReader.GCRecord.get(i).cleanSize.valueForm;
        }

        TextLable[3].setText(df.format(GCtimesum) + " sec");

        TextLable[4].setText(" Average GC Time: ");
        TextLable[5].setText(df.format(GCtimesum / GCcount)  + " sec" );

        double Apptime = 0;
        for(int i=0;i<LogReader.ApplicationRecord.size();i++)
            Apptime += LogReader.ApplicationRecord.get(i).length;
        TextLable[6].setText(" Total Application Time: ");
        TextLable[7].setText(df.format(Apptime) + " sec");

        TextLable[8].setText(" Average GC Interval Time: ");
        TextLable[9].setText(df.format(Apptime / LogReader.ApplicationRecord.size()) + " sec");

        TextLable[10].setText(" Total Reclaimed Size ");
        TextLable[11].setText(String.valueOf(totalReclaimed) + " bytes");

        TextLable[12].setText(" Total Create Size ");
        TextLable[13].setText(String.valueOf(totalReclaimed + LogReader.lastcreate) + " bytes");

        for(int i=0;i<14;i++)
            TotalGC.add(TextLable[i]);

        return TotalGC;
    }

}
