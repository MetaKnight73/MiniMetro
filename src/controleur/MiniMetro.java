package controleur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import modele.Ligne;
import modele.Reseau;
import modele.Station;
import modele.VehiculeGraphique;
import vue.Fenetre;
import vue.FenetreNiveau;

public class MiniMetro extends Application {

    //Liste des reseaux disponibles dans l'application (sous forme de string)
    private ArrayList<String> listeReseauxDispo;
    private Reseau reseauActif;
    private FenetreNiveau fn;

    //Bonus
    private int nbCEDispo;
    private int nbWagonsDispo;
    private int nbLocomotivesDispo;
    private int nbTunnelsDispo;
    private int nbLignesDispo;
    private int nbSemaine;
    private int nbVoyageurs;

    //Options de jeu
    private boolean nightMode;
    private boolean noAudio;

    //Autres attributs
    private int nextStationDispo;
    private ArrayList<Integer> numCouleursDispo;
    private HashMap<Integer, String> tabPosNomCouleur;
    private HashMap<String, Color> tabNomColor;
    private HashMap<Color, Integer> tabColorPos;

    //Le launch du main lance la methode start
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        setNightMode(false);
        setNoAudio(false);
        Fenetre f = new Fenetre(this);
    }

    //Initialisation des réseaux de l'application à partir du fichier texte de maps
    public void initReseaux() {
        listeReseauxDispo = new ArrayList<>();
        try (BufferedReader buff = new BufferedReader(new FileReader("src/maps/Maps.txt"))) {
            Iterator<String> it = buff.lines().iterator();
            while (it.hasNext()) {
                listeReseauxDispo.add(it.next());
            }
        } catch (IOException e) {
            System.out.println("FILE NOT FOUND!");
        }
    }

    //Initialisation des tableaux de correspondance pour les couleurs et les lignes disponibles à l'utilisation
    public void initTabCorrespondance() {
        tabPosNomCouleur = new HashMap<>();
        tabPosNomCouleur.put(0, "Rouge");
        tabPosNomCouleur.put(1, "Bleu");
        tabPosNomCouleur.put(2, "Vert");
        tabPosNomCouleur.put(3, "Violet");
        tabPosNomCouleur.put(4, "Rose");
        tabPosNomCouleur.put(5, "Orange");
        tabPosNomCouleur.put(6, "Gris");

        tabNomColor = new HashMap<>();
        tabNomColor.put("Rouge", Color.RED);
        tabNomColor.put("Bleu", Color.BLUE);
        tabNomColor.put("Vert", Color.GREEN);
        tabNomColor.put("Violet", Color.PURPLE);
        tabNomColor.put("Rose", Color.PINK);
        tabNomColor.put("Orange", Color.ORANGE);
        tabNomColor.put("Gris", Color.GRAY);

        tabColorPos = new HashMap<>();
        tabColorPos.put(Color.RED, 0);
        tabColorPos.put(Color.BLUE, 1);
        tabColorPos.put(Color.GREEN, 2);
        tabColorPos.put(Color.PURPLE, 3);
        tabColorPos.put(Color.PINK, 4);
        tabColorPos.put(Color.ORANGE, 5);
        tabColorPos.put(Color.GRAY, 6);

        numCouleursDispo = new ArrayList<>();
        numCouleursDispo.add(0);
        numCouleursDispo.add(1);
        numCouleursDispo.add(2);
        nextStationDispo = 3;
    }

    //Initialisation des valeurs des bonus
    public void initElem() {
        nbLignesDispo = 3;
        nbCEDispo = 0;
        nbLocomotivesDispo = 3;
        nbTunnelsDispo = 3;
        nbWagonsDispo = 0;
        nbLignesDispo = 3;
        nbSemaine = 1;
        nbVoyageurs = 0;
    }

    //Méthode pour trouver une station avec la position courante
    public Station findStation(double posXCourante, double posYCourante, Ligne l, VehiculeGraphique vg) {
        Station res = null;
        boolean found = false;
        int numLiaison = 0;
        double posX1, posY1, posX2, posY2;
        double posXDepart, posYDepart, posXArrivee, posYArrivee;
        double valSepX, valSepY;

        posXDepart = vg.getP().getPoints().get(0);
        posYDepart = vg.getP().getPoints().get(1);
        posXArrivee = vg.getP().getPoints().get(vg.getP().getPoints().size() - 2);
        posYArrivee = vg.getP().getPoints().get(vg.getP().getPoints().size() - 1);

        valSepX = 1;
        valSepY = 1;

        if (vg.getP().getPoints().size() == 4) {
            if (Math.abs(posXCourante - posXDepart) <= valSepX && Math.abs(posYCourante - posYDepart) <= valSepY) {
                res = l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0);
            } else if (Math.abs(posXCourante - posXArrivee) <= valSepX && Math.abs(posYCourante - posYArrivee) <= valSepY) {
                res = l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1);
            }
        } else {
            for (int i = 0; i < vg.getP().getPoints().size() - 3; i += 2) {
                if (!found) {
                    posX1 = vg.getP().getPoints().get(i);
                    posY1 = vg.getP().getPoints().get(i + 1);
                    posX2 = vg.getP().getPoints().get(i + 2);
                    posY2 = vg.getP().getPoints().get(i + 3);

                    if (Math.abs(posXCourante - posX1) <= valSepX && Math.abs(posYCourante - posY1) <= valSepY) {
                        res = l.getListeLiaisonsLigne().get(numLiaison).getDeuxStationsLiaison().get(0);
                        found = true;
                    } else if (Math.abs(posXCourante - posX2) <= valSepX && Math.abs(posYCourante - posY2) <= valSepY) {
                        res = l.getListeLiaisonsLigne().get(numLiaison).getDeuxStationsLiaison().get(1);
                        found = true;
                    }
                }

                numLiaison++;
            }
        }

        return res;
    }

    //Méthode pour regarder la distance entre la position courante d'un véhicule et la dernière station où elle est passée
    public boolean lookDistance(double posXCourante, double posYCourante, double posDernierArretX, double posDernierArretY) {
        boolean isFarEnough = false;

        if (Math.abs(posXCourante - posDernierArretX) >= 50 || Math.abs(posYCourante - posDernierArretY) >= 50) {
            isFarEnough = true;
        }

        return isFarEnough;
    }

    //Getters/setters
    public ArrayList<String> getListeReseauxDispo() {
        return listeReseauxDispo;
    }

    public Reseau getReseauActif() {
        return reseauActif;
    }

    public int getNbCEDispo() {
        return nbCEDispo;
    }

    public int getNbWagonsDispo() {
        return nbWagonsDispo;
    }

    public int getNbLocomotivesDispo() {
        return nbLocomotivesDispo;
    }

    public int getNbTunnelsDispo() {
        return nbTunnelsDispo;
    }

    public HashMap<Integer, String> getTabPosNomCouleur() {
        return tabPosNomCouleur;
    }

    public ArrayList<Integer> getNumCouleursDispo() {
        return numCouleursDispo;
    }

    public HashMap<Color, Integer> getTabColorPos() {
        return tabColorPos;
    }

    public void setTabColorPos(HashMap<Color, Integer> tabColorPos) {
        this.tabColorPos = tabColorPos;
    }

    public int getNextStationDispo() {
        return nextStationDispo;
    }

    public void setNextStationDispo(int n) {
        nextStationDispo = n;
    }

    public void addLocomotive() {
        this.nbLocomotivesDispo++;
    }

    public void addWagon() {
        this.setNbWagonsDispo(this.getNbWagonsDispo() + 1);
    }

    public void addCE() {
        this.setNbCEDispo(this.getNbCEDispo() + 1);
    }

    public void setNbCEDispo(int nbCEDispo) {
        this.nbCEDispo = nbCEDispo;
    }

    public int getNbLignesDispo() {
        return nbLignesDispo;
    }

    public void setNbLignesDispo(int i) {
        this.nbLignesDispo = i;
    }

    public HashMap<String, Color> getTabNomColor() {
        return tabNomColor;
    }

    public void setReseauActif(Reseau reseauActif) {
        this.reseauActif = reseauActif;
    }

    public void setNbTunnelsDispo(int nbTunnelsDispo) {
        this.nbTunnelsDispo = nbTunnelsDispo;
    }

    public void setNbWagonsDispo(int nbWagonsDispo) {
        this.nbWagonsDispo = nbWagonsDispo;
    }

    public void setNbLocomotivesDispo(int nbLocomotivesDispo) {
        this.nbLocomotivesDispo = nbLocomotivesDispo;
    }

    public void addSemaine() {
        nbSemaine++;
    }

    public int getNbSemaine() {
        return nbSemaine;
    }

    public void addVoyageur() {
        nbVoyageurs++;
    }

    public int getNbVoyageurs() {
        return nbVoyageurs;
    }

    public void setFenetreNiveau(FenetreNiveau f) {
        fn = f;
    }

    public FenetreNiveau getFenetreNiveau() {
        return fn;
    }

    public boolean isNightMode() {
        return nightMode;
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public boolean isNoAudio() {
        return noAudio;
    }

    public void setNoAudio(boolean noAudio) {
        this.noAudio = noAudio;
    }
}