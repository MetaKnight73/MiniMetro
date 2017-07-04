package modele;

import controleur.MiniMetro;
import java.util.ArrayList;

public class Reseau {

    private ArrayList<Station> listeStations;
    private ArrayList<Ligne> listeLignes;
    private final String nom;
    private final String urlImage;
    private final MiniMetro m;

    public Reseau(String nom, String urlImage, MiniMetro m) {
        listeStations = new ArrayList<>();
        listeLignes = new ArrayList<>();
        this.nom = nom;
        this.urlImage = urlImage;
        this.m = m;
    }

    public ArrayList<Station> getListeStations() {
        return listeStations;
    }

    public void addStation(Station s) {
        this.listeStations.add(s);
    }

    public void setListeStations(ArrayList<Station> listeStations) {
        this.listeStations = listeStations;
    }

    public ArrayList<Ligne> getListeLignes() {
        return listeLignes;
    }

    public void setListeLignes(ArrayList<Ligne> listeLignes) {
        this.listeLignes = listeLignes;
    }

    public String getNom() {
        return nom;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public MiniMetro getMiniM() {
        return m;
    }
}