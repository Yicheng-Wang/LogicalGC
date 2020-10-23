public class TimePeriod {
    double length = 0.0;
    static enum usageType {Application,YoungGC,OldGC,Warmup};
    usageType type;
}
