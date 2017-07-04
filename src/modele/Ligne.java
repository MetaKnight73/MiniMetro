package modele;

import java.util.ArrayList;

public class Ligne {

    private Reseau reseauAppartenance;
    private ArrayList<Vehicule> listeVehiculesCirculationLigne;
    private ArrayList<Liaison> listeLiaisonsLigne;
    private String couleur;

    public Ligne(Reseau reseau, String color, int numero) {
        reseauAppartenance = reseau;
        listeLiaisonsLigne = new ArrayList<>();
        listeVehiculesCirculationLigne = new ArrayList<>();
        couleur = color;
    }

    public Ligne() {
    }

    public Reseau getReseauAppartenance() {
        return reseauAppartenance;
    }

    public void setReseauAppartenance(Reseau reseauAppartenance) {
        this.reseauAppartenance = reseauAppartenance;
    }

    public ArrayList<Vehicule> getListeVehiculesCirculationLigne() {
        return listeVehiculesCirculationLigne;
    }

    public void setListeVehiculesCirculationLigne(ArrayList<Vehicule> listeVehiculesCirculationLigne) {
        this.listeVehiculesCirculationLigne = listeVehiculesCirculationLigne;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public ArrayList<Liaison> getListeLiaisonsLigne() {
        return listeLiaisonsLigne;
    }

    public void setListeLiaisonsLigne(ArrayList<Liaison> listeLiaisonsLigne) {
        this.listeLiaisonsLigne = listeLiaisonsLigne;
    }
}