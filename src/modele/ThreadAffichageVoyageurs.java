package modele;

import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ThreadAffichageVoyageurs extends Text implements Runnable {

    private final Station station;

    public ThreadAffichageVoyageurs(Station s) {
        this.station = s;
        this.setVisible(true);
        this.setX(s.getCentreX() + 15);
        this.setY(s.getCentreY() - 15);
    }

    @Override
    public synchronized void run() {
        while (true) {
            try {
                Platform.runLater(() -> {
                    if (station.isExchangeCenter()) {
                        this.setText("" + this.station.getNbVoyageur() + " - CE");
                    } else {
                        this.setText("" + this.station.getNbVoyageur());
                    }
                    this.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
                });

                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
        }
    }
}