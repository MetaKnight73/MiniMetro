package modele;

import controleur.MiniMetro;
import javafx.util.Duration;

public class ThreadControleVehicule extends Thread {

    private final MiniMetro m;
    private final VehiculeGraphique vg;
    private boolean sestArrete;
    private Station lastStationStopped;
    private double valAjoutX, valAjoutY;

    public ThreadControleVehicule(MiniMetro m, VehiculeGraphique vg) {
        this.m = m;
        this.vg = vg;
        sestArrete = false;
        lastStationStopped = vg.getV().getLigneCirculation().getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0);
        valAjoutX = 0;
        valAjoutY = 0;
    }

    public synchronized void gestionVoyageur(Station s) throws InterruptedException {
        if (!vg.getV().isEmpty()) {
            vg.getV().sortir(s.getType());
        }

        while (!vg.getV().isFull() && !s.isEmpty()) {
            if (!s.isEmpty()) {
                Voyageur vtmp = s.removeVoyageur();
                vg.getV().addVoyageur(vtmp);
            }
        }
    }

    @Override
    public synchronized void run() {
        boolean lineSuppr = false;

        while (!lineSuppr) {
            if (vg.getV().getLigneCirculation() == null) {
                lineSuppr = true;
            } else {
                vg.setPosXDepart(vg.getV().getLigneCirculation().getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreX());
                vg.setPosYDepart(vg.getV().getLigneCirculation().getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreY());

                double posXCourante, posYCourante;

                if (vg.getV().getNbWagon() % 2 == 0) {
                    posXCourante = vg.getPosXDepart() + vg.getR().getTranslateX() + valAjoutX + ((vg.getV().getNbWagon() / 2) * 25);
                } else {
                    posXCourante = vg.getPosXDepart() + vg.getR().getTranslateX() + valAjoutX + (((int) vg.getV().getNbWagon() / 2) * 25) + 12.5;
                }

                posYCourante = vg.getPosYDepart() + vg.getR().getTranslateY() + valAjoutY + 9;

                Station res = null;
                res = m.findStation(posXCourante, posYCourante, vg.getV().getLigneCirculation(), vg);

                //Si on ne s'est pas encore arrêté sur cette station lors de l'aller ou retour
                if (!sestArrete) {
                    if (res != null) {
                        Duration tempsArret = vg.getTransition().getCurrentTime();

                        vg.getTransition().pause();

                        setLastStationStopped(res);
                        try {
                            if (res.getNbVoyageur() != 0) {
                                Thread.sleep(1500);
                                gestionVoyageur(res);
                            }
                        } catch (InterruptedException ex) {
                            System.out.println("Interrupted");
                        }

                        vg.getTransition().playFrom(tempsArret);

                        sestArrete = true;
                    }
                } //Sinon on check si on est assez loin pour ne pas s'arrêter plusieurs fois sur une même station
                else {
                    if (lastStationStopped != null) {
                        if (m.lookDistance(posXCourante, posYCourante, lastStationStopped.getCentreX(), lastStationStopped.getCentreY())) {
                            sestArrete = false;
                        }
                    }
                }

                //Pour ne pas surcharger avec le while
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }
            }
        }
    }

    public Station getLastStationStopped() {
        return lastStationStopped;
    }

    public void setLastStationStopped(Station lastStationStopped) {
        this.lastStationStopped = lastStationStopped;
    }

    public void setSestArrete(boolean s) {
        sestArrete = s;
    }

    public double getValAjoutX() {
        return valAjoutX;
    }

    public void setValAjoutX(double valAjoutX) {
        this.valAjoutX = valAjoutX;
    }

    public double getValAjoutY() {
        return valAjoutY;
    }

    public void setValAjoutY(double valAjoutY) {
        this.valAjoutY = valAjoutY;
    }
}