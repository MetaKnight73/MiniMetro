package modele;

import java.util.ArrayList;

public class Liaison {

    private Ligne ligne;
    private ArrayList<Station> deuxStationsLiaison;

    public Liaison() {
        ligne = new Ligne();
        deuxStationsLiaison = new ArrayList<>();
    }

    public Ligne getLigne() {
        return ligne;
    }

    public void setLigne(Ligne ligne) {
        this.ligne = ligne;
    }

    public ArrayList<Station> getDeuxStationsLiaison() {
        return deuxStationsLiaison;
    }

    public void setDeuxStationsLiaison(ArrayList<Station> deuxStationsLiaison) {
        this.deuxStationsLiaison = deuxStationsLiaison;
    }
}