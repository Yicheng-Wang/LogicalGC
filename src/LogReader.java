import sun.rmi.runtime.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class LogReader {
    static ArrayList<HeapSnapshot> HeapRecord = new ArrayList<>();
    static ArrayList<YoungGC> youngRecord = new ArrayList<>();
    static Stack<Double> timeLine = new Stack<>();

    public static String[] LoadLog(String logPath) throws  IOException{
        File logFile = new File(logPath);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(logFile);
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在或者文件不可读或者文件是目录");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        while ((len = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }

        byte[] data = outputStream.toByteArray();
        fileInputStream.close();
        String content = new String(data);
        return content.split("\r\n");
    }

    public static void main(String[] args) throws IOException {
        //String logPath = args[0];
        String logPath = "C:\\Users\\DELL\\Desktop\\gc.log";
        String[] rows = LoadLog(logPath);
        HeapSnapshot initial = new HeapSnapshot().initial(rows[2]);
        int rowindex = 3;
        TimePeriod Application = new TimePeriod();

        //Parse
        while(rowindex < rows.length){
            if(rows[rowindex].contains("Heap before GC")){
                String[] HeapPrint;
                HeapPrint = Arrays.copyOfRange(rows,rowindex,rowindex+9);
                HeapSnapshot beforeGC = SentenceReader.parsePrintHeap(HeapPrint);
                beforeGC.phase = Application;
                beforeGC.complete = true;
                HeapSnapshot lastone = HeapRecord.remove(HeapRecord.size() - 1);
                if(!lastone.complete){
                    System.arraycopy(beforeGC.HeapPartition, 0, lastone.HeapPartition, 0, 5);
                    lastone.complete = true;
                }
                HeapRecord.add(lastone);
                HeapRecord.add(beforeGC);
                rowindex += 9;
            }

            else if(rows[rowindex].contains("Heap after GC")){
                String[] HeapPrint;
                HeapPrint = Arrays.copyOfRange(rows,rowindex,rowindex+9);
                HeapSnapshot afterGC = SentenceReader.parsePrintHeap(HeapPrint);
                HeapRecord.add(afterGC);
                rowindex += 10;
            }

            else if(rows[rowindex].contains("Application time")){
                //TODO:
                Utility.Number systemtime = Utility.Number.parseNumber("",rows[rowindex]);
                Utility.Number applicationtime = Utility.Number.parseNumber("Application time: ",rows[rowindex]);
                if(LogReader.timeLine.empty()){
                    TimePeriod warmup = new TimePeriod();
                    warmup.length = systemtime.valueDouble - applicationtime.valueDouble;
                    warmup.type = TimePeriod.usageType.Warmup;
                    initial.phase = warmup;
                    HeapRecord.add(initial);
                }
                LogReader.timeLine.push(systemtime.valueDouble);
                Application.type = TimePeriod.usageType.Application;
                Application.length = applicationtime.valueDouble;
                rowindex++;
            }

            else if(rows[rowindex].contains("GC (")){
                //TODO:
                String[] GCPrint;
                GCPrint = Arrays.copyOfRange(rows,rowindex,rowindex+2);
                YoungGC newGC = SentenceReader.ParseYoungGCcause(GCPrint);
                youngRecord.add(newGC);
                rowindex += 2;
            }

            else if(rows[rowindex].contains("AdaptiveSizePolicy::update_averages:")){
                //TODO:
                String[] survivePrint;
                survivePrint = Arrays.copyOfRange(rows,rowindex,rowindex+8);
                YoungGC lastGC = youngRecord.remove(youngRecord.size()-1);
                SentenceReader.ParseAdaptivePolicy(lastGC,survivePrint);
                youngRecord.add(lastGC);
                rowindex +=8;
            }

            else if(rows[rowindex].contains("threads were stopped: ")){
                //TODO:
                String stoppedMessage = rows[rowindex];
                HeapSnapshot afterGC = HeapRecord.remove(HeapRecord.size()-1);
                SentenceReader.ParseStopped(afterGC,stoppedMessage);
                rowindex ++;
            }
        }
    }
}
