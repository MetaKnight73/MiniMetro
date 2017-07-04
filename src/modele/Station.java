package modele;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class Station implements Runnable {

    //Attributs principaux
    private final Reseau reseauAppartenance;
    private ArrayList<Liaison> liaisonsTransit;
    private ArrayList<Voyageur> voyageurs;
    protected String urlImage;
    protected StationType type;

    //Attributs graphiques et booléens de contrôle
    private int posX, posY;
    protected int centreX, centreY;
    private int capaciteAvantTimer;
    private boolean isShown;
    private boolean isExchangeCenter;
    private boolean active;
    private final Label decompteStation;

    public Station(Reseau reseau, int posX, int posY) {
        reseauAppartenance = reseau;
        liaisonsTransit = new ArrayList<>();
        voyageurs = new ArrayList<>();
        isExchangeCenter = false;
        this.posX = posX;
        this.posY = posY;
        capaciteAvantTimer = 10;
        isShown = false;
        active = false;
        decompteStation = reseau.getMiniM().getFenetreNiveau().addLabelStation(posX, posY);
    }

    public boolean containsX(double x) {
        return (posX <= (x + 15) && posX >= (x - 15));
    }

    public boolean containsY(double y) {
        return (posY <= (y + 15) && posY >= (y - 15));
    }

    @Override
    public synchronized void run() {
        Voyageur vTmp;
        setActive(false);

        while (true) {
            try {
                Random rand = new Random();
                int nombreAleatoire = rand.nextInt(5000) + 7500;
                if (reseauAppartenance.getMiniM().getNbSemaine() < 15 && reseauAppartenance.getMiniM().getNbSemaine() > 4) {
                    nombreAleatoire -= ((reseauAppartenance.getMiniM().getNbSemaine() - 4) * 500);
                } else if(15 <= reseauAppartenance.getMiniM().getNbSemaine()) {
                    nombreAleatoire -= 7500;
                }
                Thread.sleep(nombreAleatoire);

                vTmp = new Voyageur(this);
                voyageurs.add(vTmp);

                reseauAppartenance.getMiniM().addVoyageur();
                Platform.runLater(() -> {
                    reseauAppartenance.getMiniM().getFenetreNiveau().refreshNbVoyageurs();
                    if (capaciteAvantTimer - voyageurs.size() <= 0 && !isActive()) {
                        setActive(true);
                        ThreadDecompteSurchargeStation tdss = new ThreadDecompteSurchargeStation(this, reseauAppartenance.getMiniM(), decompteStation);
                        tdss.start();
                    }
                });
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
        }
    }

    public Reseau getReseauAppartenance() {
        return reseauAppartenance;
    }

    public int getCentreX() {
        return centreX;
    }

    public int getCentreY() {
        return centreY;
    }

    public ArrayList<Voyageur> getVoyageursAQuai() {
        return voyageurs;
    }

    public void setVoyageurs(ArrayList<Voyageur> voyageursAQuai) {
        voyageurs = voyageursAQuai;
    }

    public void addVoyageurs(Voyageur v) {
        voyageurs.add(v);
    }

    public Voyageur removeVoyageur() {
        Iterator<Voyageur> itmp = this.voyageurs.iterator();
        Voyageur vtmp = new Voyageur(itmp.next());
        itmp.remove();
        return vtmp;
    }

    public boolean isEmpty() {
        return voyageurs.isEmpty();
    }

    public boolean isExchangeCenter() {
        return isExchangeCenter;
    }

    public void setIsExchangeCenter(boolean isExchangeCenter) {
        this.isExchangeCenter = isExchangeCenter;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public int getTailleEncombrement() {
        return capaciteAvantTimer;
    }

    public void setTailleEncombrement(int tailleEncombrement) {
        capaciteAvantTimer = tailleEncombrement;
    }

    public StationType getType() {
        return type;
    }

    public ArrayList<Liaison> getLiaisonsTransit() {
        return liaisonsTransit;
    }

    public void setLiaisonsTransit(ArrayList<Liaison> liaisonsTransit) {
        this.liaisonsTransit = liaisonsTransit;
    }

    public boolean IsShown() {
        return isShown;
    }

    public void setVisible() {
        isShown = true;
    }

    public int getNbVoyageur() {
        return voyageurs.size();
    }

    public void setActive(boolean a) {
        active = a;
    }

    public boolean isActive() {
        return active;
    }
}