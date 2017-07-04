package modele;

public enum StationType {
    Rond("Rond"),
    Carre("Carre"),
    Triangle("Triangle");

    private String name = "";

    StationType(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }
}