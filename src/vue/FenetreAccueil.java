package vue;

import controleur.MiniMetro;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;

public class FenetreAccueil extends Scene {

    private final Pane p;
    private final MiniMetro m;

    public FenetreAccueil(Fenetre parent, MiniMetro m) {
        super(new Pane(), 1280, 720);

        System.gc();

        m.initTabCorrespondance();
        m.initReseaux();

        p = new Pane();
        this.setRoot(p);
        this.m = m;

        initialisationAccueil(parent, m);
        parent.setScene(this);
    }

    public void initialisationAccueil(final Fenetre parent, final MiniMetro m) {
        ImageView imgPlay = new ImageView("images/play.png");
        imgPlay.setLayoutX(455);
        imgPlay.setLayoutY(270);
        imgPlay.setOnMouseClicked((e) -> parent.setScene(new FenetreChoixNiveau(parent, m)));

        ImageView imgOptions = new ImageView("images/options.png");
        imgOptions.setLayoutX(455);
        imgOptions.setLayoutY(333);
        imgOptions.setOnMouseClicked((e) -> parent.setScene(new FenetreOptions(parent, m)));

        ImageView imgExit = new ImageView("images/exit.png");
        imgExit.setLayoutX(455);
        imgExit.setLayoutY(392);
        imgExit.setOnMouseClicked((e) -> System.exit(0));

        this.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode().equals(KeyCode.ESCAPE)) {
                System.exit(0);
            }
        });

        ImageView img;
        if (m.isNightMode()) {
            img = new ImageView("images/accueilNM.png");
        } else {
            img = new ImageView("images/accueil.png");
        }

        p.getChildren().add(img);
        p.getChildren().add(imgPlay);
        p.getChildren().add(imgOptions);
        p.getChildren().add(imgExit);
    }
}