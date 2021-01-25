public class YoungGC extends GC{
    TLAB allocation = new TLAB();
    boolean overflow = false;
    int newThreshold = 7;
    Utility.Number survivedSize = new Utility.Number();
    Utility.Number promotionSize = new Utility.Number();
    Utility.Number desiredSize = new Utility.Number();
}
