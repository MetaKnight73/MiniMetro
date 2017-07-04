package modele;

import java.util.Random;

import controleur.MiniMetro;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import vue.FenetreNiveau;

public class ThreadGenerationStations extends Thread {

    private final MiniMetro m;
    private ImageView img;
    private final FenetreNiveau fn;
    private ThreadAffichageVoyageurs t;

    public ThreadGenerationStations(MiniMetro m, FenetreNiveau fn) {
        this.m = m;
        this.fn = fn;
        img = new ImageView();
    }

    @Override
    public synchronized void run() {
        int i = 2;  //Position de départ dans la liste des stations
        Reseau rCourant = m.getReseauActif();
        Random rand;
        Thread th;
        while (i != m.getReseauActif().getListeStations().size() - 1) {
            i++;
            rand = new Random();
            int nombreAleatoire = rand.nextInt(10000) + 10000;

            try {
                Thread.sleep(nombreAleatoire);
            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
            }

            rCourant.getListeStations().get(i).setVisible();
            t = new ThreadAffichageVoyageurs(rCourant.getListeStations().get(i));
            th = new Thread(t);
            th.start();

            img = new ImageView(rCourant.getListeStations().get(i).getUrlImage());
            img.setLayoutX(rCourant.getListeStations().get(i).getPosX());
            img.setLayoutY(rCourant.getListeStations().get(i).getPosY());

            Platform.runLater(
                    () -> {
                        fn.addText(t);
                        fn.ajouterStationGraphique(img);
                    }
            );
            fn.creerListenerStation(img, m.getReseauActif(), rCourant.getListeStations().get(i));

            Thread threadStation = new Thread((Runnable) rCourant.getListeStations().get(i));
            threadStation.start();
        }

        this.interrupt();
    }
}