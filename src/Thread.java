import java.util.ArrayList;

public class Thread {
    String ID = "";
    double TotalCreateSize = 0;
    double TotalUsedSize = 0;
    double FinalBufferSize = 0;
    double FinalWasteLimit = 0;
    ArrayList<Double> UsedSizeList = new ArrayList<>();
    ArrayList<Double> CreateSizeList = new ArrayList<>();
    ArrayList<Double> TLABSizeList = new ArrayList<>();
    ArrayList<Double> WasteSizeList = new ArrayList<>();

    public Thread(String threadID) {
        this.ID = threadID;
    }
}
