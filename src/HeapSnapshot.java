public class HeapSnapshot {
    public Utility.Number totalSize;
    public static Utility.Number maxSize;
    Generation[] HeapPartition ;

    TimePeriod phase;
    public HeapSnapshot initial(String initialRow){
        HeapSnapshot init = new HeapSnapshot();
        init.totalSize = Utility.Number.parseNumber("-XX:InitialHeapSize=",initialRow);
        HeapSnapshot.maxSize = Utility.Number.parseNumber("-XX:MaxHeapSize=",initialRow);
        return init;
    }

    public HeapSnapshot(){
        this.HeapPartition = new Generation[5];
        this.HeapPartition[0].type = Generation.generationType.Eden;
        this.HeapPartition[1].type = Generation.generationType.Eden;
        this.HeapPartition[2].type = Generation.generationType.Eden;
        this.HeapPartition[3].type = Generation.generationType.Eden;
        this.HeapPartition[4].type = Generation.generationType.Eden;
    }



}
