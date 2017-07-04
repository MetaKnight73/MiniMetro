package modele;

import controleur.MiniMetro;
import java.io.File;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import vue.Fenetre;
import vue.FenetreAccueil;
import vue.FenetreGameOver;

public class ThreadDecompteSurchargeStation extends Thread {

    private final Station st;
    private int timer;
    private boolean sortie;
    private final MiniMetro m;
    private final Label decompteStation;

    public ThreadDecompteSurchargeStation(Station s, MiniMetro m, Label l) {
        st = s;
        timer = 30;
        sortie = false;
        this.m = m;
        decompteStation = l;
        decompteStation.setStyle("-fx-font: 13 arial");
        m.getFenetreNiveau().addThreadDecompte(this);
    }

    @Override
    public synchronized void run() {
        while (timer != 0 && sortie == false) {
            Platform.runLater(() -> {
                timer -= 1;
                if (st.getNbVoyageur() < st.getTailleEncombrement()) {
                    sortie = true;
                }
                m.getFenetreNiveau().refreshCompteurStation(timer, decompteStation);
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
            }
        }

        if (timer <= 0 && !m.getFenetreNiveau().isOver()) {
            Platform.runLater(() -> {
                m.getFenetreNiveau().setOver();

                MediaPlayer mp = new MediaPlayer(new Media(new File("src/sounds/loss.wav").toURI().toString()));
                mp.setVolume(0.5);
                mp.play();

                Fenetre parent = m.getFenetreNiveau().getParent();

                FenetreGameOver fenetreGameOver = new FenetreGameOver(m);
                parent.setScene(new FenetreAccueil(parent, m));
                fenetreGameOver.getStage().toFront();
            });
        }

        timer = 30;
        Platform.runLater(() -> {
            m.getFenetreNiveau().hideCompteurStation(decompteStation);
        });

        sortie = false;
        m.getFenetreNiveau().removeThreadDecompte(this);
        st.setActive(false);
    }
}