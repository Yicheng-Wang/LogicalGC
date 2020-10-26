public class SentenceReader {

    public static HeapSnapshot parsePrintHeap(String[] rows){
        HeapSnapshot beforeGC = new HeapSnapshot();

        beforeGC.HeapPartition[0].totalSize = Utility.Number.parseNumber("space ",rows[2]);
        beforeGC.HeapPartition[0].usedSize = Utility.Number.parseNumber(", ",rows[2]);
        beforeGC.HeapPartition[0].usedSize = Utility.Number.dealingPercentage(beforeGC.HeapPartition[0].usedSize,
                beforeGC.HeapPartition[0].totalSize);

        beforeGC.HeapPartition[1].totalSize = Utility.Number.parseNumber("space ",rows[3]);
        beforeGC.HeapPartition[1].usedSize = Utility.Number.parseNumber(", ",rows[3]);
        beforeGC.HeapPartition[1].usedSize = Utility.Number.dealingPercentage(beforeGC.HeapPartition[1].usedSize,
                beforeGC.HeapPartition[1].totalSize);

        beforeGC.HeapPartition[2].totalSize = Utility.Number.parseNumber("space ",rows[4]);
        beforeGC.HeapPartition[2].usedSize = Utility.Number.parseNumber(", ",rows[4]);
        beforeGC.HeapPartition[2].usedSize = Utility.Number.dealingPercentage(beforeGC.HeapPartition[2].usedSize,
                beforeGC.HeapPartition[2].totalSize);

        beforeGC.HeapPartition[3].totalSize = Utility.Number.parseNumber("total ",rows[5]);
        beforeGC.HeapPartition[3].usedSize = Utility.Number.parseNumber("used ",rows[5]);

        beforeGC.HeapPartition[4].totalSize = Utility.Number.parseNumber("capacity ",rows[7]);
        beforeGC.HeapPartition[4].usedSize = Utility.Number.parseNumber("used ",rows[7]);
        return  beforeGC;
    }
}
