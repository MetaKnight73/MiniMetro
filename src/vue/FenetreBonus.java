package vue;

import controleur.MiniMetro;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FenetreBonus {

    public FenetreBonus(MiniMetro m, FenetreNiveau fn) {

        m.addSemaine();
        int nbSemaine = m.getNbSemaine();

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Distribution de bonus");
        Group root = new Group();

        VBox box = new VBox(5);
        Label l1 = new Label("Semaine " + (nbSemaine - 1) + " terminée.");
        Label l2 = new Label("Vous recevez une locomotive et : ");
        Button btn1 = new Button();
        Button btn2 = new Button();
        btn1.setText("Un wagon");
        btn2.setText("Un tunnel");

        btn1.setOnAction((ActionEvent event) -> {
            m.addWagon();
            fn.refreshWagon();
            primaryStage.close();
            m.addLocomotive();
            fn.refreshLoco();
        });
        btn1.setStyle("-fx-font: 16 arial; -fx-base: #d8bfd8; -fx-border-radius: 0; -fx-text-fill: white;");
        btn1.setMaxWidth(Double.MAX_VALUE - 25);

        btn2.setOnAction((ActionEvent event) -> {
            m.setNbTunnelsDispo(m.getNbTunnelsDispo() + 1);
            fn.refreshTunnel();
            primaryStage.close();
            m.addLocomotive();
            fn.refreshLoco();
        });
        btn2.setStyle("-fx-font: 16 arial; -fx-base: #d8bfd8; -fx-border-radius: 0; -fx-text-fill: white;");
        btn2.setMaxWidth(Double.MAX_VALUE - 25);

        box.getChildren().addAll(l1, l2, btn1, btn2);

        if (nbSemaine >= 4) {
            Button btn3 = new Button();
            btn3.setText("Un centre d'échange");
            btn3.setOnAction((ActionEvent event) -> {
                m.addCE();
                fn.refreshCE();
                primaryStage.close();
                m.addLocomotive();
                fn.refreshLoco();
            });
            btn3.setStyle("-fx-font: 16 arial; -fx-base: #d8bfd8; -fx-border-radius: 0; -fx-text-fill: white;");
            btn3.setMaxWidth(Double.MAX_VALUE - 25);
            box.getChildren().add(btn3);
        }

        if (m.getReseauActif().getListeLignes().size() + m.getNbLignesDispo() < 7) {
            Button btn4 = new Button();
            btn4.setText("Une ligne");
            btn4.setOnAction((ActionEvent event) -> {
                m.setNbLignesDispo(m.getNbLignesDispo() + 1);
                m.getNumCouleursDispo().add(m.getNextStationDispo());
                m.setNextStationDispo(m.getNextStationDispo() + 1);
                fn.refreshLigne();
                m.addLocomotive();
                fn.refreshLoco();
                primaryStage.close();
            });
            btn4.setStyle("-fx-font: 16 arial; -fx-base: #d8bfd8; -fx-border-radius: 0; -fx-text-fill: white;");
            btn4.setMaxWidth(Double.MAX_VALUE - 25);
            box.getChildren().add(btn4);
        }

        box.setPadding(new Insets(20, 20, 20, 20));
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(25);
        box.setLayoutY(0);

        Scene scene = new Scene(root, 260, 220);
        primaryStage.setScene(scene);

        root.getChildren().add(box);
        primaryStage.requestFocus();
        primaryStage.getIcons().add(new Image("images/icon.png"));
        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            e.consume();
        });
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}