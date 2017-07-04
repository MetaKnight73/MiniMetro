package vue;

import controleur.MiniMetro;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FenetreOptions extends Scene {

    private final Pane p;
    private final MiniMetro m;

    public FenetreOptions(Fenetre parent, MiniMetro m) {
        super(new Pane(), 1280, 720);

        p = new Pane();
        setRoot(p);
        this.m = m;

        initialisationOptions(parent);
        parent.setScene(this);
    }

    public void initialisationOptions(Fenetre parent) {
        this.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode().equals(KeyCode.ESCAPE)) {
                parent.setScene(new FenetreAccueil(parent, m));
            }
        });

        p.getChildren().add(new ImageView("images/options.jpg"));

        ImageView imgBack = new ImageView("images/backArrow.png");
        imgBack.setLayoutX(0);
        imgBack.setLayoutY(0);
        imgBack.setOnMouseClicked((MouseEvent e) -> {
            parent.setScene(new FenetreAccueil(parent, m));
        });
        p.getChildren().add(imgBack);

        //Rules
        VBox boxRules = new VBox(10);
        Label labelRules = new Label();
        labelRules.setText("Règles");
        labelRules.setStyle("-fx-font: 16 verdana; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");

        Label textRules = new Label();
        textRules.setText("Bienvenue dans MiniMetro !\n"
                + "Cette application vise à simuler la gestion du traffic d'un réseau urbain de transports en commun à travers plusieurs actions. \n"
                + "Vous disposez de sept lignes et de bonus divers afin que les voyageurs du réseau puissent se rendre à la station de leur choix.\n"
                + "Reliez les stations entre elles, réarrangez le réseau à votre guise, ajoutez ou retirez des véhicules pour fluidifer le traffic, etc...\n"
                + "Mais attention, une erreur pourrait être fatale, et un réseau mal géré, c'est un mauvais réseau.\n");
        textRules.setStyle("-fx-font: 13 verdana; -fx-padding: 0 0 5 0;");

        boxRules.getChildren().add(labelRules);
        boxRules.getChildren().add(textRules);
        boxRules.setLayoutX(420);
        boxRules.setLayoutY(50);
        boxRules.setStyle("-fx-border-color: black; -fx-border-width: 2 0 2 0;");

        //IG explanations
        VBox boxExplanations = new VBox(10);
        Label labelExplanations = new Label();
        labelExplanations.setText("Explication des commandes");
        labelExplanations.setStyle("-fx-font: 16 verdana; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");

        Label textExplanations = new Label();
        textExplanations.setText("Création d'une liaison (nouvelle ligne) :\n    Clic gauche sur une station puis clic gauche sur une autre.\n\n"
                + "Extension d'une ligne à une autre station : \n   Clic droit sur un terminus de la ligne puis choisir l'option dans le menu ainsi que la ligne à étendre et enfin clic gauche sur la station où étendre la ligne.\n\n"
                + "Suppression d'une liaison : \n   Clic droit sur un terminus de la ligne souhaitée puis choisir l'option dans le menu.\n\n"
                + "Ajout de wagon(s) / locomotive(s) : \n   Clic gauche sur le bouton associé dans la partie droite du jeu puis clic gauche sur une station où passe la ligne souhaitée et choisir la ligne dans le menu.\n\n"
                + "Transformation d'une station en centre d'échange : \n    Clic gauche sur le bouton associé dans la partie droite du jeu puis clic gauche sur la station souhaitée.\n\n");
        textExplanations.setStyle("-fx-font: 13 verdana; -fx-padding: 0 0 5 0;");

        boxExplanations.getChildren().add(labelExplanations);
        boxExplanations.getChildren().add(textExplanations);
        boxExplanations.setLayoutX(232);
        boxExplanations.setLayoutY(200);
        boxExplanations.setStyle("-fx-border-color: black; -fx-border-width: 2 0 2 0;");

        //Switches
        VBox boxOptions = new VBox(10);

        Label labelOptions = new Label();
        labelOptions.setText("Autres options");
        labelOptions.setStyle("-fx-font: 16 verdana; -fx-font-weight: bold;");

        HBox boxHorizontale = new HBox(2);

        //Box left
        VBox boxLeft = new VBox(5);
        Button buttonLabelNM = new Button();
        buttonLabelNM.setPrefWidth(100);
        buttonLabelNM.setText("Mode nuit");

        Button buttonLabelAudio = new Button();
        buttonLabelAudio.setPrefWidth(100);
        buttonLabelAudio.setText("Audio");

        boxLeft.getChildren().add(buttonLabelNM);
        boxLeft.getChildren().add(buttonLabelAudio);

        boxHorizontale.getChildren().add(boxLeft);

        //Box right
        VBox boxRight = new VBox(5);
        boxRight.setPadding(new Insets(0, 0, 0, 10));
        ToggleSwitch buttonNM = new ToggleSwitch(m, "nightMode");
        ToggleSwitch buttonAudio = new ToggleSwitch(m, "audio");

        boxRight.getChildren().add(buttonNM);
        boxRight.getChildren().add(buttonAudio);

        boxHorizontale.getChildren().add(boxRight);

        boxOptions.getChildren().add(labelOptions);
        boxOptions.getChildren().add(boxHorizontale);
        boxOptions.setLayoutX(232);
        boxOptions.setLayoutY(500);

        //Equipe
        VBox boxEquipe = new VBox(10);
        Label labelEquipe = new Label();
        labelEquipe.setText("Équipe");
        labelEquipe.setStyle("-fx-font: 16 verdana; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");

        Label textEquipe = new Label();
        textEquipe.setText("Bouenba Ahmed\n"
                + "Hergenroether Nicolas\n"
                + "Perrot Mathilde\n"
                + "Sefiane Abdelhafid\n"
                + "\n"
                + "Merci à l'UTBM, à ses enseignants pour leurs\n"
                + "réponses et à CodePoint Limited pour le\n"
                + "jeu MiniMetro™ originel.\n");
        textEquipe.setStyle("-fx-font: 13 verdana; -fx-padding: 0 0 5 0;");

        boxEquipe.getChildren().add(labelEquipe);
        boxEquipe.getChildren().add(textEquipe);
        boxEquipe.setLayoutX(975);
        boxEquipe.setLayoutY(530);
        boxEquipe.setStyle("-fx-border-color: black; -fx-border-width: 0;");

        //Musiques
        VBox boxMusiques = new VBox(10);
        Label labelMusique = new Label();
        labelMusique.setText("Musiques");
        labelMusique.setStyle("-fx-font: 16 verdana; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        Label textMusique = new Label();
        textMusique.setText("Nicolai Heidlas Music - Pacific Sun, No Copyright Music\n"
                + "Super Mario Bros. Music - Lose a Life, Nintendo©");
        textMusique.setStyle("-fx-font: 13 verdana; -fx-padding: 0 0 5 0;");

        boxMusiques.getChildren().add(labelMusique);
        boxMusiques.getChildren().add(textMusique);
        boxMusiques.setLayoutX(232);
        boxMusiques.setLayoutY(600);
        boxMusiques.setStyle("-fx-border-color: black; -fx-border-width: 2 0 0 0;");

        p.getChildren().add(boxRules);
        p.getChildren().add(boxExplanations);
        p.getChildren().add(boxOptions);
        p.getChildren().add(boxEquipe);
        p.getChildren().add(boxMusiques);
    }
}