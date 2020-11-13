public class TimePeriod {
    double length = 0.0;
    enum usageType {Application,YoungGC,OldGC,Warmup,Adaptive,SafePoint,CollectInfo};
    usageType type;
}
