import java.util.ArrayList;

/*TLAB: gc thread: 0x00007fc910116000 [id: 746] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 99.8% gc: 5346624B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc822800 [id: 525] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 92.1% gc: 4935928B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc820000 [id: 524] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 99.9% gc: 5352464B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc799000 [id: 516] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 98.1% gc: 5258960B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc797800 [id: 515] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 97.0% gc: 5197304B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc796800 [id: 514] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 3 waste 20.9% gc: 3283176B slow: 76704B fast: 0B
        TLAB: gc thread: 0x00007fc918002000 [id: 513] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 81.4% gc: 4361144B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc7a5800 [id: 512] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 77.1% gc: 4132752B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc662000 [id: 511] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 100.0% gc: 5358056B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc225800 [id: 509] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 86.9% gc: 4658392B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc1a7000 [id: 508] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 95.6% gc: 5124952B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc0cb000 [id: 505] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 99.4% gc: 5326176B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc0c9000 [id: 504] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 100.0% gc: 5357408B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc0c7000 [id: 503] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 1 waste 100.0% gc: 5358136B slow: 0B fast: 0B
        TLAB: gc thread: 0x00007fc9bc00d000 [id: 489] desired_size: 5232KB slow allocs: 0  refill waste: 83720B alloc: 1.00000   214674KB refills: 25 waste  3.6% gc: 4742752B slow: 9544B fast: 42904B
        TLAB totals: thrds: 15  refills: 41 max: 25 slow allocs: 0 max 0 waste: 33.6% gc: 73794224B max: 5358136B slow: 86248B max: 76704B fast: 42904B max: 42904B*/
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
