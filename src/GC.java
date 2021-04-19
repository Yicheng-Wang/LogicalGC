public abstract class GC {
    int threadNum;
    int order;
    String Cause;
    double timeCost;
    double CPUpercentage;
    double AdaptiveTime;
    boolean complete = false;
    Utility.Number processSize = new Utility.Number();
    Utility.Number cleanSize = new Utility.Number();
}
