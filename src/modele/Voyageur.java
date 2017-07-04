package modele;

import java.util.ArrayList;

public class Voyageur {

    private Station stationAQuai;
    private Vehicule vehiculeEnTransit;
    protected StationType typeStationDestination;
    protected StationType typeStationDepart;
    protected String urlImg = "";

    public Voyageur(Station s) {
        stationAQuai = s;
        vehiculeEnTransit = new Vehicule();
        typeStationDepart = s.getType();
        typeStationDestination = pickDest();

        switch (typeStationDestination) {
            case Carre:
                urlImg = "images/voyageurCarre.png";
                break;
            case Rond:
                urlImg = "images/voyageurRond.png";
                break;
            default:
                urlImg = "images/voyageurTriangle.png";
                break;
        }
    }

    public Voyageur(Voyageur v) {
        this.urlImg = v.urlImg;
        stationAQuai = v.stationAQuai;
        vehiculeEnTransit = v.vehiculeEnTransit;
        typeStationDepart = v.typeStationDepart;
        typeStationDestination = v.typeStationDestination;
    }

    protected StationType pickDest() {
        ArrayList<StationType> stype = new ArrayList<>();

        stype.add(StationType.Rond);
        stype.add(StationType.Carre);
        stype.add(StationType.Triangle);

        return stype.get(rand());
    }

    private static int rand() {
        double random = Math.random();
        if (random < 0.33) {
            return 0;
        } else if (random < 0.66) {
            return 1;
        } else {
            return 2;
        }
    }

    public Station getStationAQuai() {
        return stationAQuai;
    }

    public void setStationAQuai(Station stationAQuai) {
        this.stationAQuai = stationAQuai;
    }

    public Vehicule getVehiculeEnTransit() {
        return vehiculeEnTransit;
    }

    public void setVehiculeEnTransit(Vehicule vehiculeEnTransit) {
        this.vehiculeEnTransit = vehiculeEnTransit;
    }

    public StationType getTypeStationDestination() {
        return typeStationDestination;
    }

    public StationType getTypeStationDepart() {
        return this.typeStationDepart;
    }
}