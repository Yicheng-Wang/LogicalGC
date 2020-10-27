public class SentenceReader {

    public static HeapSnapshot parsePrintHeap(String[] rows){
        HeapSnapshot beforeGC = new HeapSnapshot();
        for(int i=0;i<3;i++){
            SentenceReader.parseHeapLine(beforeGC.HeapPartition[i], "space ", ", ", rows[i + 2]);
        }
        SentenceReader.parseHeapLine(beforeGC.HeapPartition[3], "total ", "used ", rows[5]);
        SentenceReader.parseHeapLine(beforeGC.HeapPartition[4], "capacity ", "used ", rows[7]);
        return  beforeGC;
    }

    public static void parseHeapLine(Generation input, String totalSymbol, String usedSymbol, String row){
        input.totalSize = Utility.Number.parseNumber(totalSymbol,row);
        input.usedSize = Utility.Number.parseNumber(usedSymbol,row);
        if(input.usedSize.isPercentage) {
            Utility.Number.dealingPercentage(input.usedSize, input.totalSize);
        }
    }

    public static YoungGC ParseYoungGCcause(String[] rows){
        YoungGC newGC = new YoungGC();
        newGC.Cause = Utility.parseString("[GC (",rows[0]);
        newGC.threadNum = Integer.parseInt(Utility.Number.parseNumber("ParallelGCThreads ",rows[0]).size);
        return newGC;
    }

    public static void ParseAdaptivePolicy(YoungGC lastGC, String[] rows){
        lastGC.survivedSize = Utility.Number.parseNumber("survived: ",rows[0]);
        lastGC.promotionSize = Utility.Number.parseNumber("promoted: ",rows[0]);
        lastGC.overflow = rows[0].contains("true");
        //Utility.Number policyStart = Utility.Number.parseNumber("AdaptiveSizeStart: ",rows[1]);
        //LogReader.timeLine.push(policyStart.valueDouble);
        lastGC.order = Integer.parseInt(Utility.Number.parseNumber("collection: ",rows[1]).size);
        lastGC.newThreshold = Integer.parseInt(Utility.Number.parseNumber("threshold ",rows[3]).size);
        lastGC.processSize = Utility.Number.parseNumber("[PSYoungGen: ",rows[7]);
        lastGC.cleanSize.valueForm = lastGC.processSize.valueForm - lastGC.survivedSize.valueForm - lastGC.promotionSize.valueForm;
        lastGC.cleanSize.size = Long.toString(lastGC.cleanSize.valueForm);
        lastGC.cleanSize.completeAllForm();
        lastGC.timeCost = Utility.Number.parseNumber("), ",rows[7]).valueDouble;
        lastGC.CPUpercentage = Utility.Number.parseNumber("Times: user=",rows[7]).valueDouble / lastGC.timeCost / lastGC.threadNum;
        lastGC.complete = true;
    }

    public static void ParseStopped(HeapSnapshot afterGC, String stoppedMessage) {
        Double systemTime = Utility.Number.parseNumber("",stoppedMessage).valueDouble;
        Double startStop = LogReader.timeLine.pop();
        TimePeriod FinishedGC = new TimePeriod();
        FinishedGC.type = TimePeriod.usageType.YoungGC;
        FinishedGC.length = systemTime - startStop;
        afterGC.phase = FinishedGC;
        afterGC.complete = true;
        LogReader.timeLine.push(startStop);
        LogReader.timeLine.push(systemTime);
    }
}
