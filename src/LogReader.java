import java.io.*;
import java.util.ArrayList;

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
        while(rowindex < rows.length){
            if (rows[rowindex].contains("Application time")){
                TimePeriod warmup = new TimePeriod();
                Utility.Number systemtime = Utility.Number.parseNumber("",rows[rowindex]);
                Utility.Number applicationtime = Utility.Number.parseNumber("Application time: ",rows[rowindex]);
                warmup.length = systemtime.valueDouble - applicationtime.valueDouble;
                warmup.type = TimePeriod.usageType.Warmup;
                initial.phase = warmup;
                HeapRecord.add(initial);
            }
            else{
                rowindex ++;
                continue;
            }
        }
    }
}
