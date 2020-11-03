import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class LogReader {
    static ArrayList<HeapSnapshot> HeapRecord = new ArrayList<>();
    static ArrayList<TimePeriod> ApplicationRecord = new ArrayList<>();
    static ArrayList<GC> GCRecord = new ArrayList<>();
    static Stack<Double> timeLine = new Stack<>();
    static ArrayList<InstanceDistribution> distributions = new ArrayList<>();
    static long lastcreate = 0;

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


        //Parse
        while(rowindex < rows.length){
            if(rows[rowindex].contains("Heap before GC")){
                String[] HeapPrint;
                HeapPrint = Arrays.copyOfRange(rows,rowindex,rowindex+9);
                HeapSnapshot beforeGC = SentenceReader.parsePrintHeap(HeapPrint);
                if(rows[rowindex].contains("pre compact")){
                    double system = Utility.Number.parseNumber("",rows[rowindex]).valueDouble;
                    double applicationStop = timeLine.peek();
                    beforeGC.phase.length = system - applicationStop;
                    beforeGC.phase.type = TimePeriod.usageType.CollectInfo;
                    timeLine.push(system);
                }
                else{
                    TimePeriod Application =  ApplicationRecord.get(ApplicationRecord.size()-1);
                    beforeGC.phase.type = Application.type;
                    beforeGC.phase.length = Application.length;
                }
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
                TimePeriod Application = new TimePeriod();
                Application.type = TimePeriod.usageType.Application;
                Application.length = applicationtime.valueDouble;
                ApplicationRecord.add(Application);
                rowindex++;
            }

            else if(rows[rowindex].contains("[GC (")){
                //TODO:
                String[] GCPrint;
                GCPrint = Arrays.copyOfRange(rows,rowindex,rowindex+2);
                YoungGC newGC = SentenceReader.ParseYoungGCcause(GCPrint);
                GCRecord.add(newGC);
                rowindex += 2;
            }

            else if(rows[rowindex].contains("AdaptiveSizePolicy::update_averages:")){
                //TODO:
                String[] survivePrint;
                survivePrint = Arrays.copyOfRange(rows,rowindex,rowindex+8);
                YoungGC lastGC = (YoungGC) GCRecord.remove(GCRecord.size()-1);
                SentenceReader.ParseAdaptivePolicy(lastGC,survivePrint);
                GCRecord.add(lastGC);
                rowindex +=8;
            }

            else if(rows[rowindex].contains("threads were stopped: ")){
                //TODO:
                String stoppedMessage = rows[rowindex];
                HeapSnapshot afterGC = HeapRecord.remove(HeapRecord.size()-1);
                SentenceReader.ParseStopped(afterGC,stoppedMessage);
                HeapRecord.add(afterGC);
                rowindex ++;
            }


            //Full GC
            else if(rows[rowindex].contains("Class Histogram")) {
                double systemTime = Utility.Number.parseNumber("",rows[rowindex]).valueDouble;
                if(rows[rowindex].contains("(before full gc)")){
                    HeapSnapshot afterGC = HeapRecord.get(HeapRecord.size()-1);
                    TimePeriod youngGCTime = new TimePeriod();
                    youngGCTime.length = systemTime - timeLine.peek();
                    timeLine.push(systemTime);
                    youngGCTime.type = TimePeriod.usageType.YoungGC;
                    afterGC.phase = youngGCTime;
                }

                GC Last = GCRecord.get(LogReader.GCRecord.size()-1);
                Last.AdaptiveTime = systemTime - Last.AdaptiveTime;
                if(Last instanceof FullGC){
                    Last.timeCost = ((FullGC)Last).PreCompact + ((FullGC)Last).Markingphase + ((FullGC)Last).Summaryphase
                            + ((FullGC)Last).AdjustRoots + ((FullGC)Last).Compactionphase + ((FullGC)Last).PostCompact;
                    Last.CPUpercentage = Last.CPUpercentage / Last.timeCost / Last.threadNum;
                }

                rowindex += 3;
                InstanceDistribution beforeDistribution = new InstanceDistribution();
                while(!rows[rowindex].contains("Total      ")){
                    Integer cursor = rows[rowindex].indexOf(':') + 1;
                    Object[] giveBack = Utility.skipSpace(rows[rowindex],cursor);
                    String instances = (String) giveBack[0];
                    cursor = (Integer) giveBack[1];
                    giveBack = Utility.skipSpace(rows[rowindex],cursor);
                    String bytes = (String) giveBack[0];
                    cursor = (Integer) giveBack[1];
                    giveBack = Utility.skipSpace(rows[rowindex],cursor);
                    String className = (String) giveBack[0];
                    beforeDistribution.instances.add(Long.parseLong(instances));
                    beforeDistribution.bytes.add(Long.parseLong(bytes));
                    beforeDistribution.className.add(className);
                    rowindex++;
                }
                Integer index = 5;
                Object[] giveBack = Utility.skipSpace(rows[rowindex],index);
                String totalInstance = (String) giveBack[0];
                index = (Integer) giveBack[1];
                giveBack = Utility.skipSpace(rows[rowindex],index);
                String totalbytes = (String) giveBack[0];
                beforeDistribution.totalInstance = Long.parseLong(totalInstance);
                beforeDistribution.totalBytes = Long.parseLong(totalbytes);
                rowindex++;

                beforeDistribution.timecost = Utility.Number.parseNumber(", ",rows[rowindex]).valueDouble;
                distributions.add(beforeDistribution);
                rowindex ++;
            }

            else if(rows[rowindex].contains("[Full GC ")){
                String[] Fullcontent;
                Fullcontent = Arrays.copyOfRange(rows,rowindex,rowindex+6);
                FullGC newFull = new FullGC();
                newFull.threadNum = (int) Utility.Number.parseNumber("ParallelGCThreads ",rows[rowindex-2]).valueForm;
                SentenceReader.ParseFullGC(newFull,Fullcontent);
                GCRecord.add(newFull);
                rowindex += 6;
            }

            else if(rows[rowindex].contains("compaction phase")){
                FullGC unFinished = (FullGC) GCRecord.get(GCRecord.size()-1);
                double systemtime = Utility.Number.parseNumber("",rows[rowindex]).valueDouble;
                while (!rows[rowindex].contains("post compact"))
                    rowindex ++;
                double endtime = Utility.Number.parseNumber("",rows[rowindex]).valueDouble;
                unFinished.Compactionphase = endtime - systemtime;
                unFinished.PostCompact = Utility.Number.parseNumber("compact, ",rows[rowindex]).valueDouble;
                rowindex ++;
                unFinished.AdaptiveTime = Utility.Number.parseNumber("AdaptiveSizeStart: ",rows[rowindex]).valueDouble;
                rowindex ++;
            }

            else if(rows[rowindex].contains("[ParOldGen: ")){
                FullGC unFinished = (FullGC) GCRecord.get(GCRecord.size()-1);
                SentenceReader.ParseFullStop(unFinished,rows[rowindex]);
                rowindex++;
            }

            else if(rows[rowindex].contains("Heap") && rows[rowindex].length()<=5){
                String[] HeapPrint;
                HeapPrint = Arrays.copyOfRange(rows,rowindex,rowindex+9);
                HeapSnapshot finalShot = SentenceReader.parsePrintHeap(HeapPrint);
                lastcreate = finalShot.HeapPartition[0].usedSize.valueForm + finalShot.HeapPartition[1].usedSize.valueForm
                        + finalShot.HeapPartition[2].usedSize.valueForm + finalShot.HeapPartition[3].usedSize.valueForm;
                HeapRecord.add(finalShot);
                rowindex +=9;
            }
            else{
                rowindex ++;
            }
        }
        HeapSnapshot last = HeapRecord.get(HeapRecord.size()-1);
        last.phase.length = ApplicationRecord.get(ApplicationRecord.size()-1).length;
        last.phase.type = ApplicationRecord.get(ApplicationRecord.size()-1).type;
        last.complete = true;

        Showing.shows();
    }
}
