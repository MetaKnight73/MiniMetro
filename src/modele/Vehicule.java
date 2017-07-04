package modele;

import java.util.ArrayList;

public class Vehicule {

    private Ligne ligneCirculation;
    private ArrayList<Wagon> wagonsVehicule;
    private int speed;
    private VehiculeGraphique vg;

    public Vehicule() {
        ligneCirculation = new Ligne();
        wagonsVehicule = new ArrayList<>();
        wagonsVehicule.add(new Wagon(this));
        speed = 5;
        vg = null;
    }

    public int getTotalOccupancy() {
        int totalOccupancy = 0;
        totalOccupancy = wagonsVehicule.stream().map((w) -> w.nbVoyageur()).reduce(totalOccupancy, Integer::sum);

        return totalOccupancy;
    }

    public int getNbVoyageurs() {
        int nbV = 0;
        nbV = wagonsVehicule.stream().map((w) -> w.nbVoyageur()).reduce(nbV, Integer::sum);

        return nbV;
    }

    public void sortir(StationType st) {
        wagonsVehicule.forEach((Wagon w) -> {
            w.vider(st);
        });
    }

    public Ligne getLigneCirculation() {
        return ligneCirculation;
    }

    public void setLigneCirculation(Ligne ligneCirculation) {
        this.ligneCirculation = ligneCirculation;
    }

    public ArrayList<Wagon> getWagonsVehicule() {
        return wagonsVehicule;
    }

    public void setWagonsVehicule(ArrayList<Wagon> wagonsVehicule) {
        this.wagonsVehicule = wagonsVehicule;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getNbWagon() {
        return this.wagonsVehicule.size();
    }

    public boolean isEmpty() {
        int index = 0;
        boolean empty = true;
        while (empty && index < wagonsVehicule.size()) {
            if (!wagonsVehicule.get(index).isEmpty()) {
                empty = false;
            }
            index++;
        }

        return empty;
    }

    public boolean isFull() {
        int index = 0;
        boolean full = true;
        while (full && index < wagonsVehicule.size()) {
            if (!wagonsVehicule.get(index).isFull()) {
                full = false;
            }
            index++;
        }

        return full;
    }

    public void addVoyageur(Voyageur v) {
        wagonsVehicule.stream().filter((w) -> (!w.isFull())).forEachOrdered((w) -> {
            w.addVoyageur(v);
        });
    }

    public VehiculeGraphique getVg() {
        return vg;
    }

    public void setVg(VehiculeGraphique vg) {
        this.vg = vg;
    }
}