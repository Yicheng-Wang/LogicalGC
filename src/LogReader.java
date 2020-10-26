import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class LogReader {
    static ArrayList<HeapSnapshot> HeapRecord = new ArrayList<>();
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
        int len = 0;

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
        //First Stop
        while(rowindex < rows.length){
            if (rows[rowindex].contains("Application time")){
                TimePeriod warmup = new TimePeriod();
                Utility.Number systemtime = Utility.Number.parseNumber("",rows[rowindex]);
                Utility.Number applicationtime = Utility.Number.parseNumber("Application time: ",rows[rowindex]);
                warmup.length = systemtime.valueDouble - applicationtime.valueDouble;
                warmup.type = TimePeriod.usageType.Warmup;
                Application.type = TimePeriod.usageType.Application;
                Application.length = applicationtime.valueDouble;
                initial.phase = warmup;
                HeapRecord.add(initial);
                rowindex++;
                break;
            }
            rowindex ++;
        }

        //Parse Later
        while(rowindex < rows.length){
            if(rows[rowindex].contains("Heap before GC")){
                //TODO:
                String[] HeapPrint = new String[9];
                HeapPrint = Arrays.copyOfRange(rows,rowindex,rowindex+9);
                HeapSnapshot beforeGC = SentenceReader.parsePrintHeap(HeapPrint);
                beforeGC.phase = Application;
                beforeGC.complete = true;
                HeapSnapshot lastone = HeapRecord.remove(HeapRecord.size() - 1);
                if(!lastone.complete){

                }
            }
            else if(rows[rowindex].contains("Heap after GC")){
                //TODO:
            }
            else if(rows[rowindex].contains("GC (")){
                //TODO:
            }
            else if(rows[rowindex].contains("[SoftReference")){
                //TODO:
            }
            else if(rows[rowindex].contains("AdaptiveSizeStart:")){
                //TODO:
            }
            else if(rows[rowindex].contains("AdaptiveSizeStart:")){
                //TODO:
            }
        }
    }
}
