public class HeapSnapshot {
    public Utility.Number totalSize;
    public static Utility.Number maxSize;
    TimePeriod phase;
    public HeapSnapshot initial(String initialRow){
        HeapSnapshot init = new HeapSnapshot();
        init.totalSize = Utility.Number.parseNumber("-XX:InitialHeapSize=",initialRow);
        HeapSnapshot.maxSize = Utility.Number.parseNumber("-XX:MaxHeapSize=",initialRow);
        return init;
    }
}
