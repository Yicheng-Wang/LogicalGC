import com.frontangle.ichart.pie.PieChart;
import com.frontangle.ichart.pie.Segment;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

public class Showing {

    public static void shows() {
        JFrame Mainframe = new JFrame();
        Mainframe.setSize(1500, 1500);
        Mainframe.setBackground(Color.WHITE);
        Mainframe.setLayout(null);
        JPanel TotalGCStats = Showing.TotalGCStats();
        JPanel test = Showing.TotalGCStats();
        TotalGCStats.setBounds(10,10,280,80);
        Mainframe.add(TotalGCStats);

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
        pieChart.setBounds(50,300,800,700);
        Mainframe.getContentPane().add(pieChart);


        Mainframe.setVisible(true);
    }

    private static JPanel TotalGCStats() {
        GridLayout layout = new GridLayout(2, 2);
        JPanel TotalGC = new JPanel(layout);
        TotalGC.setBackground(Color.WHITE);

        TotalGC.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        JLabel[] TextLable = new JLabel[10];
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        for(int i=0;i<10;i++){
            TextLable[i] = new JLabel("",JLabel.CENTER);
            TextLable[i].setBorder(border);
        }
        TextLable[0].setText(" Total GC Count: ");
        TextLable[1].setText(String.valueOf(LogReader.GCRecord.size()));
        TextLable[2].setText(" Total GC Time: ");
        TextLable[3].setText(String.valueOf(LogReader.GCRecord.size()));

        for(int i=0;i<10;i++)
            TotalGC.add(TextLable[i]);

        return TotalGC;
    }

}
