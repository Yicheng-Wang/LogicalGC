public class HeapSnapshot {
    Utility.Number totalSize = new Utility.Number();
    static Utility.Number maxSize = new Utility.Number();
    Generation[] HeapPartition = new Generation[5];
    boolean complete = false;
    TimePeriod phase = new TimePeriod();
    TimePeriod additionPhase = null;

    public HeapSnapshot initial(String initialRow){
        HeapSnapshot init = new HeapSnapshot();
        init.totalSize = Utility.Number.parseNumber("-XX:InitialHeapSize=",initialRow);
        HeapSnapshot.maxSize = Utility.Number.parseNumber("-XX:MaxHeapSize=",initialRow);
        return init;
    }

    public HeapSnapshot(){
        this.HeapPartition[0] = new Generation(Generation.generationType.Eden);
        this.HeapPartition[1] = new Generation(Generation.generationType.From);
        this.HeapPartition[2] = new Generation(Generation.generationType.To);
        this.HeapPartition[3] = new Generation(Generation.generationType.Old);
        this.HeapPartition[4] = new Generation(Generation.generationType.Meta);
    }
}
