package vue;

import controleur.MiniMetro;
import java.io.FileNotFoundException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class FenetreChoixNiveau extends Scene {

    private final Pane p;
    private final MiniMetro m;

    public FenetreChoixNiveau(Fenetre parent, MiniMetro m) {
        super(new Pane(), 1280, 720);

        p = new Pane();
        setRoot(p);
        this.m = m;

        initialisationChoixNiveau(parent);
        parent.setScene(this);
    }

    public void initialisationChoixNiveau(final Fenetre parent) {
        ImageView img;
        if (m.isNightMode()) {
            img = new ImageView("images/Choix_niveauNM.png");
        } else {
            img = new ImageView("images/Choix_niveau.png");
        }
        p.getChildren().add(img);

        VBox box = new VBox(5);

        this.setOnKeyPressed((javafx.scene.input.KeyEvent t) -> {
            if (t.getCode().equals(KeyCode.ESCAPE)) {
                parent.setScene(new FenetreAccueil(parent, m));
            }
        });

        ImageView imgBack = new ImageView("images/backArrow.png");
        imgBack.setLayoutX(0);
        imgBack.setLayoutY(0);
        imgBack.setOnMouseClicked((MouseEvent e) -> {
            parent.setScene(new FenetreAccueil(parent, m));
        });
        p.getChildren().add(imgBack);

        ImageView imgButton;
        int i = 0;
        for (String s : m.getListeReseauxDispo()) {
            imgButton = new ImageView("images/" + s + "_button.png");
            imgButton.setOnMouseClicked((e) -> {
                try {
                    parent.setScene(new FenetreNiveau(parent, m, s));
                } catch (FileNotFoundException ex) {
                    System.out.println("FILE NOT FOUND !");
                }
            });
            i++;
            box.getChildren().add(imgButton);
        }

        box.setLayoutX(520);
        box.setLayoutY(275);
        box.setPadding(new Insets(5, 5, 5, 5));
        box.setAlignment(Pos.CENTER);
        p.getChildren().add(box);
    }
}