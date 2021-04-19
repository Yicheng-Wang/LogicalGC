import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class LogReader {
    static ArrayList<HeapSnapshot> HeapRecord = new ArrayList<>();
    static ArrayList<TimePeriod> ApplicationRecord = new ArrayList<>();
    static ArrayList<GC> GCRecord = new ArrayList<>();
    static Stack<Double> timeLine = new Stack<>();
    static ArrayList<InstanceDistribution> distributions = new ArrayList<>();
    static ArrayList<TimePeriod> SafePoints = new ArrayList<>();
    static long lastcreate = 0;
    static TLAB LastTLAB = new TLAB();
    static HashMap<String,Thread> AllThread = new HashMap<>();

    public static String[] LoadLog(String logPath) throws IOException{
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
        return content.split("\n");
    }

    public static void main(String[] args) throws IOException {
        /*
         * Test Java Arguments:
         * -XX:+PrintGCDetails -XX:+UnlockDiagnosticVMOptions
         * -XX:+PrintClassHistogramAfterFullGC -XX:+PrintClassHistogramBeforeFullGC
         * -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCApplicationStoppedTime
         * -XX:+PrintHeapAtGC -XX:+PrintHeapAtGCExtended
         * -XX:+PrintOldPLAB -XX:+PrintPLAB -XX:+PrintTLAB
         * -XX:+PrintParallelOldGCPhaseTimes -XX:+PrintReferenceGC
         * -XX:+PrintTenuringDistribution -XX:+PrintPromotionFailure
         * -XX:+PrintStringDeduplicationStatistics -XX:+PrintAdaptiveSizePolicy
         * -XX:+TraceDynamicGCThreads -XX:+TraceMetadataHumongousAllocation
         */
        String logPath = args[0];
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
                    for(int i=0;i<5;i++){
                        lastone.HeapPartition[i].totalSize = beforeGC.HeapPartition[i].totalSize;
                        lastone.HeapPartition[i].usedSize.size = "0";
                        lastone.HeapPartition[i].usedSize.completeAllForm();
                    }
                    lastone.complete = true;
                }
                HeapRecord.add(lastone);
                HeapRecord.add(beforeGC);
                rowindex += 9;
            }

            else if(rows[rowindex].contains("TLAB: gc thread:")){
                YoungGC newGC = new YoungGC();
                while(rows[rowindex].contains("TLAB: gc thread:")){
                    String ThreadID = Utility.Number.parseNumber("id: ", rows[rowindex]).size;
                    Thread thisRowThread;
                    if(!AllThread.containsKey(ThreadID)){
                        thisRowThread = new Thread(ThreadID);
                        AllThread.put(ThreadID,thisRowThread);
                    }
                    else
                        thisRowThread = AllThread.get(ThreadID);
                    Utility.Number SingleBufferSize = Utility.Number.parseNumber("desired_size: ", rows[rowindex]);
                    Utility.Number WasteLimit = Utility.Number.parseNumber("refill waste: ", rows[rowindex]);
                    long BufferNum = Utility.Number.parseNumber("refills: ", rows[rowindex]).valueForm;
                    String WasteString = Utility.Number.parseNumber("waste ", rows[rowindex]).size;
                    double WastePer = Double.parseDouble(WasteString.substring(0,WasteString.length()-1));
                    double UsedSize = (100-WastePer) * SingleBufferSize.valueFormK * BufferNum / 100;
                    double CreateSize = SingleBufferSize.valueFormK * BufferNum;
                    thisRowThread.FinalBufferSize = SingleBufferSize.valueFormK;
                    thisRowThread.FinalWasteLimit = WasteLimit.valueFormK;
                    thisRowThread.TotalCreateSize += CreateSize;
                    thisRowThread.TotalUsedSize += UsedSize;
                    thisRowThread.CreateSizeList.add(CreateSize);
                    thisRowThread.UsedSizeList.add(UsedSize);
                    thisRowThread.TLABSizeList.add(SingleBufferSize.valueFormK);
                    thisRowThread.WasteSizeList.add(WasteLimit.valueFormK);

                    newGC.allocation.thread_alive.add(ThreadID);
                    newGC.allocation.desired_size_set.add(SingleBufferSize.valueFormK);
                    newGC.allocation.refill_waste_set.add(WasteLimit.valueFormK);
                    rowindex++;
                }
                newGC.allocation.ParseTLABsummary(rows[rowindex]);
                GCRecord.add(newGC);
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
                if(ApplicationRecord.isEmpty()){
                    TimePeriod warmup = new TimePeriod();
                    warmup.length = systemtime.valueDouble - applicationtime.valueDouble;
                    warmup.type = TimePeriod.usageType.Warmup;
                    initial.phase = warmup;
                    HeapRecord.add(initial);
                }
                if(! rows[rowindex-2].contains("Application time")){
                    TimePeriod Application = new TimePeriod();
                    Application.type = TimePeriod.usageType.Application;
                    Application.length = applicationtime.valueDouble;
                    ApplicationRecord.add(Application);
                }
                else{
                    TimePeriod Application = ApplicationRecord.get(ApplicationRecord.size() -1 );
                    Application.length += applicationtime.valueDouble;
                }
                if((rowindex == rows.length-1) || ! rows[rowindex+1].contains("threads were stopped"))
                    LogReader.timeLine.push(systemtime.valueDouble);
                rowindex++;
            }

            else if(rows[rowindex].contains("[GC (")){
                //TODO:
                String[] GCPrint;
                GCPrint = Arrays.copyOfRange(rows,rowindex,rowindex+2);
                SentenceReader.ParseYoungGCcause(((YoungGC)GCRecord.get(GCRecord.size()-1)),GCPrint);
                rowindex += 2;
            }

            else if(rows[rowindex].contains("AdaptiveSizePolicy::update_averages:")){
                //TODO:
                String[] survivePrint;
                int rowLength = 1;
                while(!rows[rowindex+rowLength].contains("PSYoungGen"))
                    rowLength++;
                survivePrint = Arrays.copyOfRange(rows,rowindex,rowindex+rowLength+1);
                YoungGC lastGC = (YoungGC) GCRecord.remove(GCRecord.size()-1);
                SentenceReader.ParseAdaptivePolicy(lastGC,survivePrint);
                GCRecord.add(lastGC);
                rowindex += rowLength+1;
            }

            else if(rows[rowindex].contains("threads were stopped: ")){
                //TODO:
                String stoppedMessage = rows[rowindex];
                if(rows[rowindex-1].contains("Application time")){
                    TimePeriod Safepoint = new TimePeriod();
                    Safepoint.type = TimePeriod.usageType.SafePoint;
                    Safepoint.length = Utility.Number.parseNumber("threads were stopped: ",stoppedMessage).valueDouble;
                    SafePoints.add(Safepoint);
                }
                else{
                    HeapSnapshot afterGC = HeapRecord.remove(HeapRecord.size()-1);
                    SentenceReader.ParseStopped(afterGC,stoppedMessage);
                    HeapRecord.add(afterGC);
                }
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
                    afterGC.complete = true;
                }

                GC Last = GCRecord.get(LogReader.GCRecord.size()-1);
                Last.AdaptiveTime = systemTime - Last.AdaptiveTime;
                if(Last instanceof FullGC){
                    Last.timeCost = ((FullGC)Last).PreCompact + ((FullGC)Last).Markingphase + ((FullGC)Last).Summaryphase
                            + ((FullGC)Last).AdjustRoots + ((FullGC)Last).Compactionphase + ((FullGC)Last).PostCompact
                            + Last.AdaptiveTime;
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
                beforeDistribution.totalBytes = Long.parseLong(totalbytes.trim());
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

        String title = logPath;
        if (logPath.lastIndexOf('/') != -1) {
            title = logPath.substring(logPath.lastIndexOf('/')+1);
        }
        Showing.shows(title);
    }
}
