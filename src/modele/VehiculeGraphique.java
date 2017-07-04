package modele;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class VehiculeGraphique extends Thread {

    private Rectangle r;
    private Vehicule v;
    private Polyline p;
    private PathTransition transition;
    private ThreadControleVehicule threadControle;
    private final VehiculeGraphique vgAssocie;
    private double posXDepart;
    private double posYDepart;

    public VehiculeGraphique(Rectangle r, Vehicule v, Polyline poly) {
        this.r = r;
        this.v = v;
        p = poly;
        transition = null;
        threadControle = null;
        vgAssocie = null;
    }

    public VehiculeGraphique(Rectangle r, Vehicule v, Polyline poly, VehiculeGraphique vg) {
        this.r = r;
        this.v = v;
        p = poly;
        transition = null;
        threadControle = null;
        vgAssocie = vg;
    }

    @Override
    public synchronized void run() {
        setTransition(new PathTransition());
        transition.setNode(getR());
        transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setAutoReverse(true);
        transition.setPath(getP());

        if (vgAssocie == null) {
            transition.setDuration(Duration.seconds(getV().getSpeed()));
        } else {
            transition.setDuration(vgAssocie.getTransition().getDuration());
        }

        transition.play();
    }

    public Rectangle getR() {
        return r;
    }

    public void setR(Rectangle r) {
        this.r = r;
    }

    public Vehicule getV() {
        return v;
    }

    public void setV(Vehicule v) {
        this.v = v;
    }

    public Polyline getP() {
        return p;
    }

    public void setP(Polyline p) {
        this.p = p;
    }

    public PathTransition getTransition() {
        return transition;
    }

    public void setTransition(PathTransition transition) {
        this.transition = transition;
    }

    public ThreadControleVehicule getThreadControle() {
        return threadControle;
    }

    public void setThreadControle(ThreadControleVehicule threadControle) {
        this.threadControle = threadControle;
    }

    public double getPosXDepart() {
        return posXDepart;
    }

    public void setPosXDepart(double posXDepart) {
        this.posXDepart = posXDepart;
    }

    public double getPosYDepart() {
        return posYDepart;
    }

    public void setPosYDepart(double posYDepart) {
        this.posYDepart = posYDepart;
    }

    public void setPosDepart(double x, double y) {
        this.setPosXDepart(x);
        this.setPosYDepart(y);
    }
}