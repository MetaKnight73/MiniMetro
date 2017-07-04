package vue;

import controleur.MiniMetro;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Fenetre extends Stage {

    public Fenetre(MiniMetro m) {
        super();

        this.setTitle("MiniMetro");
        this.setResizable(false);
        this.getIcons().add(new Image("images/icon.png"));
        //Prevent la fermeture par la croix
        this.setOnCloseRequest((WindowEvent e) -> {
            e.consume();
        });
        this.setScene(new FenetreAccueil(this, m));
        this.show();
    }
}