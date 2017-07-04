package modele;

import controleur.MiniMetro;
import javafx.application.Platform;
import javafx.scene.control.Label;
import vue.FenetreBonus;
import vue.FenetreNiveau;

public class ThreadAffichageJours extends Thread {

    private final Label lJour;
    private int jour;
    private static final String[] SEMAINE = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
    private final MiniMetro m;
    private final FenetreNiveau fn;

    public ThreadAffichageJours(Label l, MiniMetro m, FenetreNiveau fn) {
        this.m = m;
        this.fn = fn;
        lJour = l;
        jour = 0;
    }

    @Override
    public synchronized void run() {
        while (true) {
            Platform.runLater(() -> {
                lJour.setText("Semaine " + m.getNbSemaine() + "\n" + SEMAINE[jour]);
                jour++;
                if (jour > 6) {
                    jour = 0;
                    FenetreBonus fenetreBonus = new FenetreBonus(m, fn);
                }
            });
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
            }
        }
    }
}