import java.util.ArrayList;

public class TLAB {
    ArrayList<Double> desired_size_set = new ArrayList<>();
    ArrayList<Double> refill_waste_set = new ArrayList<>();
    long threadNum = 0;
    long refillTotal = 0;
    long slowAlloc = 0;
    String wastePercent = "";
    double gc_waste = 0;
    double slow_waste = 0;
    double fast_waste = 0;
    double average_size = 0;
    double average_refill_waste = 0;

    public void ParseTLABsummary(String row) {
        this.threadNum = Utility.Number.parseNumber("thrds: ", row).valueForm;
        this.refillTotal = Utility.Number.parseNumber("refills: ", row).valueForm;
        this.slowAlloc = Utility.Number.parseNumber("slow allocs: ", row).valueForm;
        this.wastePercent = Utility.Number.parseNumber("waste: ", row).size;
        this.gc_waste = Utility.Number.parseNumber("gc: ", row).valueForm;
        this.slow_waste = Utility.Number.parseNumber("slow: ", row).valueForm;
        this.fast_waste = Utility.Number.parseNumber("fast: ", row).valueForm;
        for(int i=0;i<LogReader.LastTLAB.threadNum;i++){
            this.average_size += this.desired_size_set.get(i);
            this.average_refill_waste += this.refill_waste_set.get(i);
        }
        this.average_size /= this.threadNum;
        this.average_refill_waste /= this.threadNum;
    }
}
