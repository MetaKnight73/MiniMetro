package vue;

import controleur.MiniMetro;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FenetreGameOver {

    private final Stage primaryStage;

    public FenetreGameOver(MiniMetro m) {

        m.getFenetreNiveau().arretAnimations();
        m.initTabCorrespondance();
        m.initReseaux();

        primaryStage = new Stage();
        primaryStage.setTitle("Game Over");
        primaryStage.getIcons().add(new Image("images/icon.png"));
        Group root = new Group();

        VBox box = new VBox(2);
        Label l1 = new Label("Game Over !");
        Label l2 = new Label("Votre réseau a été opérationnel " + m.getNbSemaine() + " semaines et a accueilli " + m.getNbVoyageurs() + " voyageurs.");
        box.getChildren().addAll(l1, l2);

        box.setPadding(new Insets(20, 20, 20, 20));
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(0);
        box.setLayoutY(0);

        Scene scene = new Scene(root, 420, 100);
        primaryStage.setScene(scene);

        root.getChildren().add(box);
        primaryStage.show();
    }

    public Stage getStage() {
        return primaryStage;
    }
}