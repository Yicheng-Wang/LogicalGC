public class HeapSnapshot {
    public Utility.Number totalSize;
    public HeapSnapshot initial(String initialRow){
        HeapSnapshot init = new HeapSnapshot();
        init.totalSize = Utility.Number.parseNumber("-XX:InitialHeapSize=",initialRow);
        return init;
    }
}
