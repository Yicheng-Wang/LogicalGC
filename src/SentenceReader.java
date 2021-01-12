public class SentenceReader {

    public static HeapSnapshot parsePrintHeap(String[] rows){
        HeapSnapshot beforeGC = new HeapSnapshot();
        for(int i=0;i<3;i++){
            SentenceReader.parseHeapLine(beforeGC.HeapPartition[i], "space ", ", ", rows[i + 2]);
        }
        SentenceReader.parseHeapLine(beforeGC.HeapPartition[3], "total ", "used ", rows[5]);
        SentenceReader.parseHeapLine(beforeGC.HeapPartition[4], "capacity ", "used ", rows[7]);
        beforeGC.totalSize.size = String.valueOf(beforeGC.HeapPartition[0].totalSize.valueForm + beforeGC.HeapPartition[1].totalSize.valueForm
         + beforeGC.HeapPartition[2].totalSize.valueForm + beforeGC.HeapPartition[3].totalSize.valueForm + beforeGC.HeapPartition[4].totalSize.valueForm);
        beforeGC.totalSize.completeAllForm();
        return  beforeGC;
    }

    public static void parseHeapLine(Generation input, String totalSymbol, String usedSymbol, String row){
        input.totalSize = Utility.Number.parseNumber(totalSymbol,row);
        input.usedSize = Utility.Number.parseNumber(usedSymbol,row);
        if(input.usedSize.isPercentage) {
            Utility.Number.dealingPercentage(input.usedSize, input.totalSize);
        }
    }

    public static void ParseYoungGCcause(YoungGC newGC, String[] rows){
        newGC.Cause = Utility.parseString("[GC (",rows[0]);
        newGC.threadNum = Integer.parseInt(Utility.Number.parseNumber("ParallelGCThreads ",rows[0]).size);
    }

    public static void ParseAdaptivePolicy(YoungGC lastGC, String[] rows){

        lastGC.survivedSize = Utility.Number.parseNumber("survived: ",rows[0]);
        lastGC.promotionSize = Utility.Number.parseNumber("promoted: ",rows[0]);
        lastGC.overflow = rows[0].contains("true");
        lastGC.AdaptiveTime = Utility.Number.parseNumber("AdaptiveSizeStart: ",rows[1]).valueDouble;
        //Utility.Number policyStart = Utility.Number.parseNumber("AdaptiveSizeStart: ",rows[1]);
        //LogReader.timeLine.push(policyStart.valueDouble);
        lastGC.order = Integer.parseInt(Utility.Number.parseNumber("collection: ",rows[1]).size);
        int offset = 2;
        while(!rows[offset].contains("threshold "))
            offset++;
        lastGC.newThreshold = Integer.parseInt(Utility.Number.parseNumber("threshold ",rows[offset]).size);

        lastGC.processSize = Utility.Number.parseNumber("[PSYoungGen: ",rows[ rows.length -1]);
        lastGC.cleanSize.valueForm = lastGC.processSize.valueForm - lastGC.survivedSize.valueForm - lastGC.promotionSize.valueForm;
        lastGC.cleanSize.size = Long.toString(lastGC.cleanSize.valueForm);
        lastGC.cleanSize.completeAllForm();
        lastGC.timeCost = Utility.Number.parseNumber("), ",rows[rows.length -1]).valueDouble;
        lastGC.CPUpercentage = Utility.Number.parseNumber("Times: user=",rows[rows.length -1]).valueDouble / lastGC.timeCost / lastGC.threadNum;
        lastGC.complete = true;
    }

    public static void ParseStopped(HeapSnapshot afterGC, String stoppedMessage) {
        Double systemTime = Utility.Number.parseNumber("",stoppedMessage).valueDouble;
        Double startStop = LogReader.timeLine.pop();
        TimePeriod FinishedGC = new TimePeriod();
        GC Temp;
        if((Temp = LogReader.GCRecord.get(LogReader.GCRecord.size()-1)) instanceof FullGC){
            FinishedGC.type = TimePeriod.usageType.OldGC;
            InstanceDistribution last = LogReader.distributions.get(LogReader.distributions.size() - 1);
            FinishedGC.length = systemTime - startStop - last.timecost;
            TimePeriod collectInfo = new TimePeriod();
            collectInfo.type = TimePeriod.usageType.CollectInfo;
            collectInfo.length = last.timecost;
            afterGC.additionPhase = collectInfo;
        }
        else{
            FinishedGC.type = TimePeriod.usageType.YoungGC;
            FinishedGC.length = systemTime - startStop;
            Temp.AdaptiveTime = systemTime - Temp.AdaptiveTime;
        }

        afterGC.phase = FinishedGC;
        afterGC.complete = true;
        LogReader.timeLine.push(startStop);
        LogReader.timeLine.push(systemTime);
    }

    public static void ParseFullGC(FullGC newFull, String[] rows) {
        double systemTime = Utility.Number.parseNumber("",rows[0]).valueDouble;
        double GCstart = LogReader.timeLine.peek();
        newFull.PreCompact = systemTime - GCstart;
        newFull.Cause = Utility.parseString("[Full GC (",rows[0]);
        newFull.Markingphase = Utility.Number.parseNumber(", ",rows[3]).valueDouble;
        newFull.Summaryphase = Utility.Number.parseNumber(", ",rows[4]).valueDouble;
        newFull.AdjustRoots = Utility.Number.parseNumber(", ",rows[5]).valueDouble;
    }

    public static void ParseFullStop(FullGC unFinished, String row) {
        row = row.substring(row.indexOf(']',row.indexOf(']')+1));
        unFinished.processSize = Utility.Number.parseNumber("] ",row);
        Utility.Number afterSize = Utility.Number.parseNumber("->",row);
        unFinished.cleanSize.size = String.valueOf(unFinished.processSize.valueForm - afterSize.valueForm);
        unFinished.cleanSize.completeAllForm();
        unFinished.reportStopTime = Utility.Number.parseNumber(")], ",row).valueDouble;
        unFinished.CPUpercentage = Utility.Number.parseNumber("Times: user=",row).valueDouble;
        unFinished.complete = true;
    }
}
