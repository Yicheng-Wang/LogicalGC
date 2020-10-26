public class Generation {
    Utility.Number totalSize = new Utility.Number();
    Utility.Number usedSize = new Utility.Number();
    generationType type;
    enum generationType {Eden,From,To,Old,Meta}

    public Generation(generationType type){
        this.type = type;
    }
}
