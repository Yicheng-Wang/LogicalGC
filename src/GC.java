public abstract class GC {
    int threadNum;
    int order;
    String Cause;
    Double timeCost;
    Double CPUpercentage;
    double AdaptiveTime;
    boolean complete = false;
    long lastCreate = 0;
    Utility.Number processSize = new Utility.Number();
    Utility.Number cleanSize = new Utility.Number();
}
