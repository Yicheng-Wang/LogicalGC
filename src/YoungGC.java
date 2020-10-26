public class YoungGC extends GC{
    boolean overflow = false;
    int newThreshold = 7;
    Utility.Number survivedSize = new Utility.Number();
    Utility.Number promotionSize = new Utility.Number();
}
