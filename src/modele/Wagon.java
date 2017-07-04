package modele;

import java.util.ArrayList;
import java.util.Iterator;

public class Wagon {
    
    private Vehicule vehiculeAppartenance;
    private final int nbPlaces = 6;
    private final ArrayList<Voyageur> voyageurs;
    
    public Wagon(Vehicule v) {
        vehiculeAppartenance = v;
        voyageurs = new ArrayList<>();
    }
    
    public int nbVoyageur() {
        return voyageurs.size();
    }
    
    public boolean isEmpty() {
        return voyageurs.isEmpty();
    }
    
    public boolean isFull() {
        return voyageurs.size() == nbPlaces;
    }
    
    public ArrayList<Voyageur> getVoyageurs() {
        return voyageurs;
    }
    
    public Vehicule getVehiculeAppartenance() {
        return vehiculeAppartenance;
    }
    
    public void setVehiculeAppartenance(Vehicule vehiculeAppartenance) {
        this.vehiculeAppartenance = vehiculeAppartenance;
    }
    
    public void addVoyageur(Voyageur v) {
        voyageurs.add(v);
    }
    
    public void vider(StationType st) {
        Iterator<Voyageur> it = voyageurs.iterator();
        while (it.hasNext()) {
            if (it.next().getTypeStationDestination() == st) {
                it.remove();
            } 
        }
    }
}