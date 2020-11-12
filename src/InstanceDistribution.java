import java.util.ArrayList;

public class InstanceDistribution {
    ArrayList<Long> instances = new ArrayList<>();
    ArrayList<Long> bytes = new ArrayList<>();
    ArrayList<String> className = new ArrayList<>();
    long totalInstance = 0;
    long totalBytes = 0;
    double timecost = 0.0;

    public static String DealingName(String ClassName){
        String DealedName= "";
        boolean isArray = ClassName.contains("[");
        if(isArray){
            switch (ClassName){
                case("[I"):
                    return "int[ ]";
                case("[B"):
                    return "byte[ ]";
                case("[Z"):
                    return "boolean[ ]";
                case("[S"):
                    return "short[ ]";
                case("[J"):
                    return "long[ ]";
                case("[F"):
                    return "float[ ]";
                case("[D"):
                    return "double[ ]";
                case("[C"):
                    return "char[ ]";
                default:
                    int index = ClassName.lastIndexOf(".");
                    DealedName = ClassName.substring(index+1,ClassName.indexOf(";"));
                    DealedName += "[ ]";
                    return DealedName;
            }
        }
        int index = ClassName.lastIndexOf(".");
        DealedName = ClassName.substring(index+1);
        return DealedName;
    }
}
