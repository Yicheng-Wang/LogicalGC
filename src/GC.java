public abstract class GC {
    int threadNum;
    int order;
    String Cause;
    Double timeCost;
    Double CPUpercentage;
    boolean complete = false;
    Utility.Number processSize = new Utility.Number();
    Utility.Number cleanSize = new Utility.Number();
}