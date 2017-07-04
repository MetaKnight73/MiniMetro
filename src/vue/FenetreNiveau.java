package vue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.shape.Polyline;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Label;

import controleur.MiniMetro;
import modele.Liaison;
import modele.LiaisonSouterraine;
import modele.Ligne;
import modele.Reseau;
import modele.Station;
import modele.StationCarre;
import modele.StationRond;
import modele.StationTriangle;
import modele.ThreadAffichageJours;
import modele.ThreadAffichageVoyageurs;
import modele.ThreadControleVehicule;
import modele.ThreadDecompteSurchargeStation;
import modele.ThreadGenerationStations;
import modele.Vehicule;
import modele.VehiculeGraphique;
import modele.Wagon;

public class FenetreNiveau extends Scene {
    ////////////////////////////////Attributs///////////////////////////////////

    //Objets d'ordre général
    private final Fenetre fenetreParent;
    private MiniMetro m;
    private final MediaPlayer mp;

    //Booléens de contrôle des clics + mute et gameover
    private boolean aClique;
    private boolean extClique;
    private boolean wagonClique;
    private boolean locoClique;
    private boolean CEClique;
    private boolean isMuted;
    private boolean over;

    //Stations et ligne pour contrôle des clics également 
    private Station stFirstClick;
    private Station stSecondClick;
    private Station stExtClick;
    private Station stWagonClique;
    private Ligne stExtLine;

    //Objets graphiques
    private Polyline poly;
    private Image imgFond;
    private ImageView imgSound;
    private final ImageView imgBack;

    //Label pour l'affichage des jours
    private final Label labelJour;
    private final Label labelNbVoyageurs;

    //Les boutons des bonus
    private final Button btnLigne;
    private final Button btnCE;
    private final Button btnWagon;
    private final Button btnLoco;
    private final Button btnTunnel;

    //Threads
    private Thread threadGeneration;
    private Thread[] threadStations;
    private ThreadAffichageJours threadAfficheJour;
    private final ArrayList threadVehicules;
    private final ArrayList threadPosVehicules;
    private final ArrayList threadDecompte;

    //Les layers
    private final Pane p;
    private final Pane pFond;
    private final Pane pLignes;
    private final Pane pVehicules;
    private final Pane pTextes;
    private final Pane pStations;

    //////////////////////////////Fin attributs/////////////////////////////////
    //////////////////////////////Constructeur//////////////////////////////////
    public FenetreNiveau(Fenetre parent, MiniMetro m, String reseau) throws FileNotFoundException {
        super(new Pane(), 1280, 720);

        this.m = m;
        this.m.setFenetreNiveau(this);

        aClique = false;
        extClique = false;
        wagonClique = false;
        locoClique = false;
        CEClique = false;
        isMuted = false;
        over = false;

        //Ordre des layers: background + bonus, lignes, vehicules, textes des stations, stations
        pFond = new Pane();
        pLignes = new Pane();
        pVehicules = new Pane();
        pTextes = new Pane();
        pStations = new Pane();

        stExtLine = new Ligne();

        poly = new Polyline();
        imgFond = null;
        imgSound = null;
        imgBack = new ImageView("images/backArrow.png");
        imgBack.setLayoutX(0);
        imgBack.setLayoutY(685);
        pStations.getChildren().add(imgBack);

        threadGeneration = new Thread();
        threadVehicules = new ArrayList();
        threadPosVehicules = new ArrayList();
        threadDecompte = new ArrayList();

        btnLigne = new Button();
        btnCE = new Button();
        btnWagon = new Button();
        btnLoco = new Button();
        btnTunnel = new Button();

        labelJour = new Label("");
        labelNbVoyageurs = new Label("0");

        p = new Pane();
        setRoot(p);

        if (!this.m.isNoAudio()) {
            mp = new MediaPlayer(new Media(new File("src/sounds/Minimetro.mp3").toURI().toString()));
            mp.setCycleCount(MediaPlayer.INDEFINITE);
            mp.setVolume(0.5);
            mp.play();
        } else {
            mp = null;
        }

        initialisationNiveau(parent, reseau);
        parent.setScene(this);

        fenetreParent = parent;
    }

    ////////////////////////////Fin constructeur////////////////////////////////
    ////////////////////////Methodes d'initialisation///////////////////////////
    //Methode principale d'initialisation du niveau
    public void initialisationNiveau(Fenetre parent, String reseau) throws FileNotFoundException {
        //Initialisation des valeurs des bonus du jeu
        m.initElem();

        //Initialisation du reseau courant
        Reseau rCourant;
        if (m.isNightMode()) {
            rCourant = new Reseau(reseau, "images/" + reseau + "NM.png", m);
        } else {
            rCourant = new Reseau(reseau, "images/" + reseau + ".png", m);
        }

        m.setReseauActif(rCourant);

        //Initialisation des trois premières stations du jeu
        initStations(rCourant, reseau);

        //Initialisation des bonus (graphique)
        initBonus();

        //Initialisation du label pour l'affichage des jours
        initLabel();

        //Initialisation des threads pour chaque station et affichage des voyageurs à quai
        initVoyageur(rCourant);

        //On instancie les threads des stations pour la generation de voyageurs
        threadStations = new Thread[3];
        for (int i = 0; i < 3; i++) {
            Thread threadStation = new Thread((Runnable) rCourant.getListeStations().get(i));
            threadStation.start();
            threadStations[i] = threadStation;
        }

        //On instancie le thread qui genere les stations apres la 3e
        ThreadGenerationStations tgs = new ThreadGenerationStations(m, this);
        threadGeneration = new Thread((Runnable) tgs);
        threadGeneration.start();

        //On instancie le thread qui va afficher les jours de la semaine
        threadAfficheJour = new ThreadAffichageJours(labelJour, m, this);
        threadAfficheJour.start();

        //Gestion du clic sur l'image de back
        imgBack.setOnMouseClicked((MouseEvent e) -> {
            if (mp != null) {
                mp.stop();
            }

            arretAnimations();
            parent.setScene(new FenetreAccueil(parent, m));
        });

        //Gestion du clic sur ECHAP
        this.setOnKeyPressed((KeyEvent t) -> {
            if (t.getCode().equals(KeyCode.ESCAPE)) {
                if (mp != null) {
                    mp.stop();
                }

                arretAnimations();
                parent.setScene(new FenetreAccueil(parent, m));
            }
        });

        //Ajout des layers
        p.getChildren().add(pFond);
        p.getChildren().add(pLignes);
        p.getChildren().add(pVehicules);
        p.getChildren().add(pTextes);
        p.getChildren().add(pStations);
    }

    //Methode d'initialisation des stations du niveau avec lecture d'un fichier texte associe au niveau selectionne
    public void initStations(Reseau rCourant, String nomReseau) {
        final ImageView imgMap;

        imgMap = new ImageView(rCourant.getUrlImage());
        pFond.getChildren().add(imgMap);
        imgFond = new Image(rCourant.getUrlImage());
        imgFond = imgMap.getImage();

        try (BufferedReader buff = new BufferedReader(new FileReader("src/maps/" + nomReseau + ".txt"))) {
            Iterator<String> it = buff.lines().iterator();
            String tmp;

            //Tant que le fichier n'est pas lu en entier
            //on recupere chaque ligne, on separe la string selon un separateur qu'on aura convenu, ici,
            //et on cree l'objet selon la string recuperee
            String[] tab;
            Station sTmp;
            while (it.hasNext()) {
                tmp = it.next();
                tab = tmp.split(",");
                sTmp = createStation(tab, rCourant);
                rCourant.addStation(sTmp);
            }

            //On affiche les trois premieres stations de la liste de stations du niveau courant
            initNiveau(rCourant);
        } catch (IOException e) {
            System.out.println("FILE NOT FOUND!");
        }
    }

    //Methode d'initialisation du niveau avec affichage des trois premieres stations 
    public void initNiveau(Reseau rCourant) {
        ImageView img;
        for (int i = 0; i < 3; i++) {
            rCourant.getListeStations().get(i).setVisible();

            img = new ImageView(rCourant.getListeStations().get(i).getUrlImage());
            img.setLayoutX(rCourant.getListeStations().get(i).getPosX());
            img.setLayoutY(rCourant.getListeStations().get(i).getPosY());

            ajouterStationGraphique(img);
            creerListenerStation(img, rCourant, rCourant.getListeStations().get(i));
        }

        if (m.isNoAudio()) {
            imgSound = new ImageView("images/mute.png");
            imgSound.setLayoutX(5);
            imgSound.setLayoutY(75);
        } else {
            imgSound = new ImageView("images/sound.png");
            imgSound.setLayoutX(5);
            imgSound.setLayoutY(75);

            imgSound.setOnMouseClicked((MouseEvent e) -> {
                if (!isMuted) {
                    imgSound.setImage(new Image("images/mute.png"));
                    mp.setMute(true);
                    isMuted = true;
                } else {
                    imgSound.setImage(new Image("images/sound.png"));
                    mp.setMute(false);
                    isMuted = false;
                }
            });
        }

        pStations.getChildren().add(imgSound);

        ImageView imgVoyageurs = new ImageView("images/voyageur.png");
        imgVoyageurs.setLayoutX(5);
        imgVoyageurs.setLayoutY(5);

        pFond.getChildren().add(imgVoyageurs);
    }

    //Methode d'initialisation des bonus avec affichage des bonus sur le cote du jeu
    public void initBonus() {
        VBox box = new VBox(5);

        //Lignes
        refreshLigne();
        btnLigne.setStyle("-fx-font: 16 arial; -fx-base: #8b7b8b; -fx-border-radius: 0; -fx-text-fill: white;");
        btnLigne.setMaxWidth(Double.MAX_VALUE - 25);
        box.getChildren().add(btnLigne);

        //Centres d'échange
        refreshCE();
        btnCE.setStyle("-fx-font: 16 arial; -fx-base: #8b7b8b; -fx-border-radius: 0; -fx-text-fill: white;");
        btnCE.setMaxWidth(Double.MAX_VALUE - 25);
        btnCE.setOnMouseClicked((MouseEvent t) -> {
            if (m.getNbCEDispo() != 0) {
                CEClique = true;
            }
        });
        box.getChildren().add(btnCE);

        //Wagons
        refreshWagon();
        btnWagon.setStyle("-fx-font: 16 arial; -fx-base: #8b7b8b; -fx-border-radius: 0; -fx-text-fill: white;");
        btnWagon.setMaxWidth(Double.MAX_VALUE - 25);
        btnWagon.setOnMouseClicked((MouseEvent t) -> {
            if (m.getNbWagonsDispo() != 0) {
                wagonClique = true;
            }
        });
        box.getChildren().add(btnWagon);

        //Locomotives
        refreshLoco();
        btnLoco.setStyle("-fx-font: 16 arial; -fx-base: #8b7b8b; -fx-border-radius: 0; -fx-text-fill: white;");
        btnLoco.setMaxWidth(Double.MAX_VALUE - 25);
        btnLoco.setOnMouseClicked((MouseEvent t) -> {
            if (m.getNbLocomotivesDispo() != 0) {
                locoClique = true;
            }
        });
        box.getChildren().add(btnLoco);

        //Tunnels
        refreshTunnel();
        btnTunnel.setStyle("-fx-font: 16 arial; -fx-base: #8b7b8b; -fx-border-radius: 0; -fx-text-fill: white;");
        btnTunnel.setMaxWidth(Double.MAX_VALUE - 25);
        box.getChildren().add(btnTunnel);

        box.setPadding(new Insets(5, 5, 5, 5));
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(1135);
        box.setLayoutY(0);

        pFond.getChildren().add(box);
    }

    public void initVoyageur(Reseau rCourant) {
        ThreadAffichageVoyageurs t;
        Thread th;
        for (Station s : rCourant.getListeStations()) {
            if (s.IsShown()) {
                t = new ThreadAffichageVoyageurs(s);
                th = new Thread(t);
                th.start();
                pTextes.getChildren().add(t);
            }
        }
    }

    public void addText(ThreadAffichageVoyageurs t) {
        pFond.getChildren().add(t);
    }

    //Methode d'instanciation des stations à partir de chaque ligne du fichier texte lu
    private static Station createStation(String[] tab, Reseau reseau) {
        Station st = null;
        switch (tab[2]) {
            case "Rond":
                st = new StationRond(reseau, Integer.parseInt(tab[0]), Integer.parseInt(tab[1]));
                break;
            case "Carre":
                st = new StationCarre(reseau, Integer.parseInt(tab[0]), Integer.parseInt(tab[1]));
                break;
            case "Triangle":
                st = new StationTriangle(reseau, Integer.parseInt(tab[0]), Integer.parseInt(tab[1]));
                break;
        }

        return st;
    }

    //////////////////////Fin methodes d'initialisation/////////////////////////
    //////////////////Methodes de recuperation ou de calcul/////////////////////
    //Methode qui verifie l'existence d'une liaison entre deux stations
    public boolean verifExistenceLiaison(Reseau rCourant, Station st1, Station st2) {
        boolean res = false;
        for (Ligne l : rCourant.getListeLignes()) {
            for (Liaison li : l.getListeLiaisonsLigne()) {
                if ((li.getDeuxStationsLiaison().get(0).equals(st1) && li.getDeuxStationsLiaison().get(1).equals(st2)) || (li.getDeuxStationsLiaison().get(0).equals(st2) && li.getDeuxStationsLiaison().get(1).equals(st1))) {
                    res = true;
                }
            }
        }

        return res;
    }

    //Methode qui renvoie les lignes pour lesquelles la station donnee en parametre est un terminus
    public ArrayList<Ligne> getLignesTerminus(Reseau rCourant, Station st) {
        ArrayList<Ligne> listeLignes = new ArrayList<>();
        int cpt = 0;
        for (Ligne l : rCourant.getListeLignes()) {
            for (Liaison li : l.getListeLiaisonsLigne()) {
                if (li.getDeuxStationsLiaison().get(0).equals(st) || li.getDeuxStationsLiaison().get(1).equals(st)) {
                    cpt++;
                }
            }
            if (cpt == 1) {
                listeLignes.add(l);
            }
            cpt = 0;
        }
        return listeLignes;
    }

    public ArrayList<Ligne> getLignesStation(Reseau rCourant, Station s) {
        ArrayList<Ligne> res = new ArrayList<>();

        for (Ligne l : rCourant.getListeLignes()) {
            for (Liaison li : l.getListeLiaisonsLigne()) {
                if ((li.getDeuxStationsLiaison().get(0).equals(s) || li.getDeuxStationsLiaison().get(1).equals(s)) && !res.contains(l)) {
                    res.add(l);
                }
            }
        }

        return res;
    }

    //Méthode de vérification de la présence de véhicules sur une liaison à supprimer
    public boolean vehiculesSurLiaison(Liaison liaison) {
        boolean res = false;
        Ligne lCourante = liaison.getLigne();
        ArrayList<Vehicule> listeVehiculesLigne = lCourante.getListeVehiculesCirculationLigne();

        for (Vehicule v : listeVehiculesLigne) {
            VehiculeGraphique vg = v.getVg();
            Station lastStop = vg.getThreadControle().getLastStationStopped();
            Station firstStLiaison = liaison.getDeuxStationsLiaison().get(0);
            Station secondStLiaison = liaison.getDeuxStationsLiaison().get(1);

            if ((lastStop.equals(firstStLiaison) && !lastStop.equals(lCourante.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1)) && !lastStop.equals(lCourante.getListeLiaisonsLigne().get(lCourante.getListeLiaisonsLigne().size() - 1).getDeuxStationsLiaison().get(0))) || (lastStop.equals(secondStLiaison) && !lastStop.equals(lCourante.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1)) && !lastStop.equals(lCourante.getListeLiaisonsLigne().get(lCourante.getListeLiaisonsLigne().size() - 1).getDeuxStationsLiaison().get(0)))) {
                res = true;
            }
        }

        return res;
    }

    //Methode de creation d'un listener sur chaque station
    public void creerListenerStation(ImageView img, Reseau rCourant, Station s) {
        img.setOnMouseClicked((MouseEvent t) -> {
            int positionX = (int) t.getSceneX();
            int positionY = (int) t.getSceneY();

            //Clic gauche
            if (t.getButton() == MouseButton.PRIMARY) {
                //Deuxieme clic gauche = creation nouvelle ligne
                if (isaClique()) {
                    extClique = false;
                    locoClique = false;
                    wagonClique = false;
                    CEClique = false;

                    stSecondClick = s;
                    boolean passeSurEau = verifTunnel(stFirstClick, stSecondClick);

                    if ((m.getNbLignesDispo() > 0 && !passeSurEau) || (m.getNbTunnelsDispo() > 0 && passeSurEau)) {
                        poly = new Polyline();

                        poly.getPoints().addAll((double) stFirstClick.getCentreX(), (double) stFirstClick.getCentreY(), (double) stSecondClick.getCentreX(), (double) stSecondClick.getCentreY());
                        poly.setStrokeWidth(5);

                        //On verifie l'existence ou non d'une liaison entre les deux stations cliquees
                        boolean liaisonExistante = verifExistenceLiaison(rCourant, stFirstClick, stSecondClick);

                        //Si une liaison n'existe pas deja et qu'on ne double clique pas sur une station
                        if (!liaisonExistante && !stFirstClick.equals(stSecondClick)) {
                            poly.setStroke(m.getTabNomColor().get(m.getTabPosNomCouleur().get(m.getNumCouleursDispo().get(0))));

                            refreshLayersLiaisonAjoutee(poly);

                            //Creation de la ligne
                            Ligne ligne = new Ligne();
                            ligne.setReseauAppartenance(rCourant);
                            ligne.setListeLiaisonsLigne(new ArrayList<>());
                            ligne.setListeVehiculesCirculationLigne(new ArrayList<>());
                            ligne.setCouleur(m.getTabPosNomCouleur().get(m.getNumCouleursDispo().get(0)));

                            m.getNumCouleursDispo().remove(0);

                            //Creation de la liaison entre les deux stations
                            Liaison liaison;
                            if (passeSurEau) {
                                liaison = new LiaisonSouterraine();
                                m.setNbTunnelsDispo(m.getNbTunnelsDispo() - 1);
                                refreshTunnel();
                            } else {
                                liaison = new Liaison();
                            }

                            m.setNbLignesDispo(m.getNbLignesDispo() - 1);
                            refreshLigne();

                            liaison.getDeuxStationsLiaison().add(stFirstClick);
                            liaison.getDeuxStationsLiaison().add(stSecondClick);

                            //Association de la ligne et de la liaison
                            ligne.getListeLiaisonsLigne().add(liaison);
                            liaison.setLigne(ligne);
                            rCourant.getListeLignes().add(ligne);

                            //Creation d'un vehicule sur la nouvelle ligne
                            Vehicule v = new Vehicule();
                            v.setLigneCirculation(ligne);
                            ligne.getListeVehiculesCirculationLigne().add(v);

                            //Creation de l'objet graphique associe au vehicule cree
                            Rectangle r = new Rectangle(ligne.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreX(), ligne.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreY(), 25, 18);
                            r.setFill(poly.getStroke());

                            VehiculeGraphique vg = new VehiculeGraphique(r, v, poly);
                            v.setVg(vg);

                            //On demarre le thread du vehicule graphique pour son deplacement
                            Thread threadVG = new Thread((Runnable) vg);
                            threadVG.start();
                            threadVehicules.add(threadVG);

                            //On fait un petit sleep pour laisser le temps au thread ci-dessus de s'initialiser correctement
                            //avant de lancer celui ci-dessous
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException ex) {
                                System.out.println("Interrupted");
                            }

                            //On demarre le thread de controle de la position du vehicule
                            ThreadControleVehicule tcv = new ThreadControleVehicule(m, vg);
                            Thread threadCV = new Thread((Runnable) tcv);
                            threadCV.start();
                            threadPosVehicules.add(threadCV);

                            vg.setThreadControle(tcv);

                            pVehicules.getChildren().add(r);
                        }

                        setaClique(false);
                    }
                } //Clic extension d'une ligne
                else if (isExtClique()) {
                    aClique = false;
                    locoClique = false;
                    wagonClique = false;
                    CEClique = false;

                    boolean liaisonExistante = verifExistenceLiaison(rCourant, s, stExtClick);
                    boolean passeSurEau = verifTunnel(stExtClick, s);
                    poly = stExtLine.getListeVehiculesCirculationLigne().get(0).getVg().getP();

                    //La liaison n'existe pas encore et les deux stations selectionnees sont differentes
                    if (!liaisonExistante && !stExtClick.equals(s) && !isAlreadyOnLine(stExtLine, s) && (!passeSurEau || (passeSurEau && m.getNbTunnelsDispo() != 0))) {
                        //Creation de la nouvelle liaison entre les deux stations selectionnees
                        Liaison liaison;

                        //Gestion tunnel
                        if (passeSurEau) {
                            liaison = new LiaisonSouterraine();
                            m.setNbTunnelsDispo(m.getNbTunnelsDispo() - 1);
                            refreshTunnel();
                        } else {
                            liaison = new Liaison();
                        }

                        //Gestion de l'ajout des points graphiques
                        if (stExtClick.equals(stExtLine.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0))) {
                            poly.getPoints().add(0, (double) s.getCentreX());
                            poly.getPoints().add(1, (double) s.getCentreY());
                            liaison.getDeuxStationsLiaison().add(0, s);
                            liaison.getDeuxStationsLiaison().add(1, stExtClick);
                            stExtLine.getListeLiaisonsLigne().add(0, liaison);

                            for (int i = 0; i < stExtLine.getListeVehiculesCirculationLigne().size(); i++) {
                                VehiculeGraphique vg = stExtLine.getListeVehiculesCirculationLigne().get(i).getVg();
                                vg.setPosDepart(s.getCentreX(), s.getCentreY());

                                if (s.getPosX() > stExtClick.getPosX()) {
                                    stExtLine.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().setValAjoutX(stExtLine.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().getValAjoutX() - Math.abs(s.getCentreX() - stExtClick.getCentreX()));
                                } else if (s.getPosX() < stExtClick.getPosX()) {
                                    stExtLine.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().setValAjoutX(stExtLine.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().getValAjoutX() + Math.abs(s.getCentreX() - stExtClick.getCentreX()));
                                }

                                if (s.getPosY() > stExtClick.getPosY()) {
                                    stExtLine.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().setValAjoutY(stExtLine.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().getValAjoutY() - Math.abs(s.getCentreY() - stExtClick.getCentreY()));
                                } else if (s.getPosY() < stExtClick.getPosY()) {
                                    stExtLine.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().setValAjoutY(stExtLine.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().getValAjoutY() + Math.abs(s.getCentreY() - stExtClick.getCentreY()));
                                }
                            }
                        } else {
                            poly.getPoints().addAll((double) s.getCentreX(), (double) s.getCentreY());
                            liaison.getDeuxStationsLiaison().add(stExtClick);
                            liaison.getDeuxStationsLiaison().add(s);
                            stExtLine.getListeLiaisonsLigne().add(liaison);
                        }

                        liaison.setLigne(stExtLine);

                        for (int i = 0; i < stExtLine.getListeVehiculesCirculationLigne().size(); i++) {
                            VehiculeGraphique vg = stExtLine.getListeVehiculesCirculationLigne().get(i).getVg();

                            Duration tCourant = vg.getTransition().getCurrentTime();
                            Duration newDuration = Duration.seconds(vg.getV().getLigneCirculation().getListeLiaisonsLigne().size() * vg.getV().getSpeed());
                            Duration oldDuration = newDuration.subtract(Duration.seconds(vg.getV().getSpeed()));

                            vg.getTransition().stop();
                            vg.getTransition().setDuration(newDuration);
                            vg.getTransition().setPath(poly);
                            vg.getTransition().playFrom(tCourant.multiply(newDuration.toSeconds()).divide(oldDuration.toSeconds()));
                        }
                    }

                    setExtClique(false);
                } else if (isWagonClique()) {
                    aClique = false;
                    extClique = false;
                    locoClique = false;
                    CEClique = false;

                    //Creation d'un menu contextuel
                    ContextMenu wagons = new ContextMenu();

                    //Recuperation des lignes passant par la station
                    ArrayList<Ligne> listeLignes = getLignesStation(rCourant, s);
                    if (listeLignes.size() > 0) {

                        for (Ligne l : listeLignes) {
                            MenuItem item = new MenuItem("Wagon sur ligne " + l.getCouleur().toLowerCase());
                            item.setOnAction((ActionEvent e) -> {
                                Vehicule vAjout = null;
                                boolean aTrouve = false;

                                if (l.getListeVehiculesCirculationLigne().size() < 5) {
                                    for (Vehicule v : l.getListeVehiculesCirculationLigne()) {
                                        if (v.getWagonsVehicule().size() < 4 && !aTrouve) {
                                            vAjout = v;
                                            aTrouve = true;
                                        }
                                    }

                                    if (aTrouve) {
                                        Wagon w = new Wagon(vAjout);
                                        vAjout.getWagonsVehicule().add(w);
                                        vAjout.getVg().getR().setWidth(vAjout.getVg().getR().getWidth() + 25);

                                        m.setNbWagonsDispo(m.getNbWagonsDispo() - 1);
                                        refreshWagon();
                                    }
                                }
                            });
                            wagons.getItems().add(item);
                        }
                    }

                    wagonClique = false;

                    Bounds bounds = pStations.getBoundsInLocal();
                    Bounds screenBounds = pStations.localToScreen(bounds);
                    int x1 = (int) screenBounds.getMinX();
                    int y1 = (int) screenBounds.getMinY();
                    wagons.show(pStations, x1 + positionX, y1 + positionY);
                } else if (isLocoClique()) {
                    aClique = false;
                    extClique = false;
                    wagonClique = false;
                    CEClique = false;

                    //Creation d'un menu contextuel
                    ContextMenu loco = new ContextMenu();

                    //Recuperation des lignes passant par la station
                    ArrayList<Ligne> listeLignes = getLignesStation(rCourant, s);
                    if (listeLignes.size() > 0) {
                        for (Ligne l : listeLignes) {
                            MenuItem item = new MenuItem("Locomotive sur ligne " + l.getCouleur().toLowerCase());
                            item.setOnAction((ActionEvent e) -> {
                                if (l.getListeVehiculesCirculationLigne().size() < 5) {
                                    poly = l.getListeVehiculesCirculationLigne().get(0).getVg().getP();

                                    //Creation d'un vehicule sur la ligne
                                    Vehicule v = new Vehicule();
                                    v.setLigneCirculation(l);
                                    l.getListeVehiculesCirculationLigne().add(v);

                                    //Creation de l'objet graphique associe au vehicule cree
                                    Rectangle r = new Rectangle(l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreX(), l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreY(), 25, 18);
                                    r.setFill(poly.getStroke());

                                    VehiculeGraphique vg = new VehiculeGraphique(r, v, poly, l.getListeVehiculesCirculationLigne().get(0).getVg());
                                    v.setVg(vg);

                                    //On demarre le thread du vehicule graphique pour son deplacement
                                    Thread threadVG = new Thread((Runnable) vg);
                                    threadVG.start();
                                    threadVehicules.add(threadVG);

                                    //On fait un petit sleep pour laisser le temps au thread ci-dessus de s'initialiser correctement
                                    //avant de lancer celui ci-dessous
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException ex) {
                                        System.out.println("Interrupted");
                                    }

                                    //On demarre le thread de controle de la position du vehicule
                                    ThreadControleVehicule tcv = new ThreadControleVehicule(m, vg);
                                    Thread threadCV = new Thread((Runnable) tcv);
                                    threadCV.start();
                                    threadPosVehicules.add(threadCV);

                                    vg.setThreadControle(tcv);

                                    pVehicules.getChildren().add(r);

                                    m.setNbLocomotivesDispo(m.getNbLocomotivesDispo() - 1);
                                    refreshLoco();
                                }
                            });
                            loco.getItems().add(item);
                        }
                    }

                    locoClique = false;

                    Bounds bounds = pStations.getBoundsInLocal();
                    Bounds screenBounds = pStations.localToScreen(bounds);
                    int x1 = (int) screenBounds.getMinX();
                    int y1 = (int) screenBounds.getMinY();
                    loco.show(pStations, x1 + positionX, y1 + positionY);
                } else if (isCEClique()) {
                    aClique = false;
                    extClique = false;
                    wagonClique = false;
                    locoClique = false;

                    if (!s.isExchangeCenter()) {
                        s.setIsExchangeCenter(true);
                        s.setTailleEncombrement(s.getTailleEncombrement() + 5);
                        m.setNbCEDispo(m.getNbCEDispo() - 1);
                        refreshCE();
                    }

                    CEClique = false;
                } //Premier clic
                else {
                    setaClique(true);
                    stFirstClick = s;
                }
            } //Clic droit
            else if (t.getButton() == MouseButton.SECONDARY) {
                setaClique(false);

                //Creation d'un menu contextuel
                ContextMenu actionLigne = new ContextMenu();

                //Recuperation des lignes dont la station est un terminus
                ArrayList<Ligne> listeLignes = getLignesTerminus(rCourant, s);
                if (listeLignes.size() > 0) {
                    //Creation des items du menu
                    MenuItem supprimerItem = new MenuItem("Supprimer une liaison");

                    supprimerItem.setOnAction((ActionEvent e) -> {
                        //Creation d'un nouveau menu qui remplace le précédent
                        ContextMenu choixLigne = new ContextMenu();

                        //Creation des items : un item par ligne disponible
                        for (Ligne l : listeLignes) {
                            MenuItem numLigne = new MenuItem("Ligne " + l.getCouleur().toLowerCase());
                            choixLigne.getItems().add(numLigne);

                            numLigne.setOnAction((ActionEvent e1) -> {
                                //Recuperation des liaisons de la ligne selectionnee
                                ArrayList<Liaison> listeLiaisons = l.getListeLiaisonsLigne();
                                Station st2 = null;
                                Liaison res = null;

                                //On identifie la liaison visee pour la suppression en recuperant la station qui suit celle selectionnee
                                for (Liaison lia : listeLiaisons) {
                                    if ((lia.getDeuxStationsLiaison().get(0) == s) || (lia.getDeuxStationsLiaison().get(1) == s)) {
                                        if (lia.getDeuxStationsLiaison().get(0) == s) {
                                            st2 = lia.getDeuxStationsLiaison().get(1);
                                        } else {
                                            st2 = lia.getDeuxStationsLiaison().get(0);
                                        }
                                        res = lia;
                                    }
                                }
                                
                                poly = l.getListeVehiculesCirculationLigne().get(0).getVg().getP();

                                if (!vehiculesSurLiaison(res) || res.getLigne().getListeLiaisonsLigne().size() == 1) {
                                    
                                    int cranAEnlever = 0;
                                    for (int k = 0; k < l.getListeLiaisonsLigne().size(); k++) {
                                        if (l.getListeLiaisonsLigne().get(k).getDeuxStationsLiaison().get(0).getCentreX() == s.getCentreX() && l.getListeLiaisonsLigne().get(k).getDeuxStationsLiaison().get(0).getCentreY() == s.getCentreY()) {
                                            cranAEnlever = 0;
                                        } else if (l.getListeLiaisonsLigne().get(k).getDeuxStationsLiaison().get(1).getCentreX() == s.getCentreX() && l.getListeLiaisonsLigne().get(k).getDeuxStationsLiaison().get(1).getCentreY() == s.getCentreY()) {
                                            cranAEnlever = 2 * (k + 1);
                                        }
                                    }

                                    if (cranAEnlever == 0) {
                                        for (int i = 0; i < l.getListeVehiculesCirculationLigne().size(); i++) {
                                            VehiculeGraphique vg = l.getListeVehiculesCirculationLigne().get(i).getVg();

                                            if (l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getPosX() > l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1).getPosX()) {
                                                vg.getThreadControle().setValAjoutX(l.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().getValAjoutX() + Math.abs(l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreX() - l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1).getCentreX()));
                                            } else if (l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getPosX() < l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1).getPosX()) {
                                                vg.getThreadControle().setValAjoutX(l.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().getValAjoutX() - Math.abs(l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreX() - l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1).getCentreX()));
                                            }

                                            if (l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getPosY() > l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1).getPosY()) {
                                                vg.getThreadControle().setValAjoutY(l.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().getValAjoutY() + Math.abs(l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreY() - l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1).getCentreY()));
                                            } else if (l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getPosY() < l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1).getPosY()) {
                                                vg.getThreadControle().setValAjoutY(l.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().getValAjoutY() - Math.abs(l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(0).getCentreY() - l.getListeLiaisonsLigne().get(0).getDeuxStationsLiaison().get(1).getCentreY()));
                                            }
                                        }
                                    }

                                    //La liaison est retiree de la liste de la ligne
                                    listeLiaisons.remove(res);
                                    res.setLigne(null);

                                    //A modifier pour que ce soit plus propre par la suite
                                    if ("class modele.LiaisonSouterraine".equals(res.getClass().toString())) {
                                        m.setNbTunnelsDispo(m.getNbTunnelsDispo() + 1);
                                        refreshTunnel();
                                    }

                                    //On verifie si la ligne possede d'autres liaisons
                                    if (listeLiaisons.isEmpty()) {
                                        //On recupere la liste des vehicules de la ligne
                                        ArrayList<Vehicule> vehicules = l.getListeVehiculesCirculationLigne();

                                        if (vehicules.size() > 0) {
                                            int cpt = 0;

                                            for (Vehicule v : vehicules) {
                                                cpt++;
                                                //On recupere la liste des wagons pour chaque vehicule
                                                ArrayList<Wagon> wagons = v.getWagonsVehicule();

                                                if (wagons.size() > 0) {
                                                    for (Wagon w : wagons) {
                                                        w.setVehiculeAppartenance(null);
                                                        //On incremente le bonus wagon pour chaque wagon supprime
                                                        m.addWagon();
                                                    }

                                                    refreshWagon();
                                                }

                                                if (cpt > 1) {
                                                    //On incremente le bonus locomotive s'il y en a plus d'une sur la ligne
                                                    m.addLocomotive();
                                                }
                                                v.setLigneCirculation(null);
                                            }

                                            refreshLoco();

                                            //La ligne est retiree du reseau
                                            ArrayList<Ligne> listeLignes1 = rCourant.getListeLignes();
                                            listeLignes1.remove(l);

                                            //Les objets graphiques representant les vehicules sont supprimes
                                            ArrayList<Rectangle> r = new ArrayList<>();
                                            for (Node obj : pVehicules.getChildren()) {
                                                Rectangle v1 = (Rectangle) obj;
                                                if (v1.getFill() == m.getTabNomColor().get(l.getCouleur())) {
                                                    r.add(v1);
                                                }
                                            }

                                            pVehicules.getChildren().removeAll(r);
                                        }

                                        int pos = m.getTabColorPos().get(m.getTabNomColor().get(l.getCouleur()));
                                        int posInsertion = 0;
                                        if (m.getNumCouleursDispo().isEmpty()) {
                                            m.getNumCouleursDispo().add(0, pos);
                                        } else {
                                            while (pos > m.getNumCouleursDispo().get(posInsertion)) {
                                                posInsertion++;
                                            }
                                            m.getNumCouleursDispo().add(posInsertion, pos);
                                        }

                                        //On incremente le bonus ligne
                                        m.setNbLignesDispo(m.getNbLignesDispo() + 1);
                                        refreshLigne();

                                        for (int i = 0; i < l.getListeVehiculesCirculationLigne().size(); i++) {
                                            l.getListeVehiculesCirculationLigne().get(i).getVg().getThreadControle().interrupt();
                                        }
                                    }

                                    if (cranAEnlever != 0) {
                                        poly.getPoints().remove(cranAEnlever + 1);
                                        poly.getPoints().remove(cranAEnlever);
                                    } else {
                                        poly.getPoints().remove(0);
                                        poly.getPoints().remove(0);
                                    }
                                    
                                    if (!l.getListeLiaisonsLigne().isEmpty()) {
                                        for (int i = 0; i < l.getListeVehiculesCirculationLigne().size(); i++) {
                                            VehiculeGraphique vg = l.getListeVehiculesCirculationLigne().get(i).getVg();

                                            Duration tCourant = vg.getTransition().getCurrentTime();
                                            Duration newDuration = Duration.seconds(vg.getV().getLigneCirculation().getListeLiaisonsLigne().size() * vg.getV().getSpeed());
                                            Duration oldDuration = newDuration.subtract(Duration.seconds(vg.getV().getSpeed()));

                                            vg.getTransition().stop();
                                            vg.getTransition().setDuration(newDuration);
                                            vg.getTransition().setPath(poly);
                                            vg.getTransition().playFrom(tCourant.multiply(newDuration.toSeconds()).divide(oldDuration.toSeconds()));
                                        }
                                    }
                                }
                            });
                        }

                        //Affichage du menu
                        Bounds b = pStations.getBoundsInLocal();
                        Bounds screenBounds = pStations.localToScreen(b);
                        int x1 = (int) screenBounds.getMinX();
                        int y1 = (int) screenBounds.getMinY();

                        choixLigne.show(pStations, x1 + positionX, y1 + positionY);
                    });

                    //2nd item du premier menu
                    MenuItem ajouterItem = new MenuItem("Etendre une liaison");
                    ajouterItem.setOnAction((ActionEvent e) -> {
                        //Creation du menu qui remplace le 1er
                        ContextMenu choixLigne = new ContextMenu();

                        //Creation d'un item pour chaque ligne disponible
                        for (Ligne l : listeLignes) {
                            MenuItem numLigne = new MenuItem("Ligne " + l.getCouleur().toLowerCase());
                            numLigne.setOnAction((ActionEvent e1) -> {
                                setExtClique(true);
                                stExtClick = s;
                                stExtLine = l;
                            });

                            choixLigne.getItems().add(numLigne);
                        }

                        //Affichage du menu
                        Bounds b = pStations.getBoundsInLocal();
                        Bounds screenBounds = pStations.localToScreen(b);
                        int x1 = (int) screenBounds.getMinX();
                        int y1 = (int) screenBounds.getMinY();

                        choixLigne.show(pStations, x1 + positionX, y1 + positionY);
                    });

                    actionLigne.getItems().addAll(supprimerItem, ajouterItem);
                } //La station n'est pas un terminus, aucune action ne peut etre effectuee sur les lignes
                else {
                    MenuItem listeVideItem = new MenuItem("<Aucune ligne n'est disponible>");
                    actionLigne.getItems().add(listeVideItem);
                }

                Bounds bounds = pStations.getBoundsInLocal();
                Bounds screenBounds = pStations.localToScreen(bounds);
                int x1 = (int) screenBounds.getMinX();
                int y1 = (int) screenBounds.getMinY();
                actionLigne.show(pStations, x1 + positionX, y1 + positionY);
            }
        });
    }

    //Methode qui verifie si la station s est deja sur la ligne sExtLine
    public boolean isAlreadyOnLine(Ligne sExtLine, Station s) {
        boolean res = false;
        for (Liaison l : sExtLine.getListeLiaisonsLigne()) {
            if (l.getDeuxStationsLiaison().get(0).equals(s) || l.getDeuxStationsLiaison().get(1).equals(s)) {
                res = true;
            }
        }

        return res;
    }

    //Methode qui verifie si un tunnel est necessaire entre deux stations avant la creation d'une liaison
    public boolean verifTunnel(Station stFirstClick, Station stSecondClick) {
        double posXDepart = (double) stFirstClick.getCentreX();
        double posXArrivee = (double) stSecondClick.getCentreX();
        double posYDepart = (double) stFirstClick.getCentreY();
        double posYArrivee = (double) stSecondClick.getCentreY();
        Color c;
        double yCourant;
        boolean res = false;

        if (posXDepart > posXArrivee || ((posXDepart == posXArrivee) && posYDepart > posYArrivee)) {
            double tempX = posXDepart;
            double tempY = posYDepart;

            posXDepart = posXArrivee;
            posXArrivee = tempX;

            posYDepart = posYArrivee;
            posYArrivee = tempY;
        }

        double coeffDirecteur = ((posYArrivee - posYDepart) / (posXArrivee - posXDepart));

        if (posXDepart == posXArrivee) {
            for (double i = posYDepart; i < posYArrivee; i++) {
                c = imgFond.getPixelReader().getColor((int) posXDepart, (int) i);
                if (c.getBlue() > c.getGreen() && c.getBlue() > c.getRed()) {
                    res = true;
                }
            }
        } else if (posYDepart == posYArrivee) {
            for (double i = posXDepart; i < posXArrivee; i++) {
                c = imgFond.getPixelReader().getColor((int) i, (int) posYDepart);
                if (c.getBlue() > c.getGreen() && c.getBlue() > c.getRed()) {
                    res = true;
                }
            }
        } else {
            for (double i = posXDepart; i < posXArrivee; i++) {
                yCourant = posYDepart + (coeffDirecteur * (i - posXDepart));
                c = imgFond.getPixelReader().getColor((int) i, (int) yCourant);
                if (c.getBlue() > c.getGreen() && c.getBlue() > c.getRed()) {
                    res = true;
                }
            }
        }

        return res;
    }

    ////////////////Fin methodes de recuperation ou de calcul///////////////////
    //////////////////////////////Methodes graphiques///////////////////////////
    //Methode d'ajout de l'image d'une station au layer des stations
    public void ajouterStationGraphique(ImageView img) {
        pStations.getChildren().add(img);
    }

    //Initialisation du label pour l'affichage des jours
    public void initLabel() {
        labelJour.setPadding(new Insets(5, 0, 0, 5));
        labelJour.setStyle("-fx-font: 16 arial;");

        labelJour.setAlignment(Pos.CENTER);
        labelJour.setLayoutX(0);
        labelJour.setLayoutY(25);

        labelNbVoyageurs.setPadding(new Insets(7, 0, 0, 35));
        labelNbVoyageurs.setStyle("-fx-font: 16 arial;");

        labelNbVoyageurs.setAlignment(Pos.CENTER);
        labelNbVoyageurs.setLayoutX(0);
        labelNbVoyageurs.setLayoutY(0);

        pFond.getChildren().addAll(labelNbVoyageurs, labelJour);
    }

    //Méthode pour créer des labels à côté de chaque nouvelle station (pour le décompte avant game over)
    public Label addLabelStation(int posX, int posY) {
        Label l = new Label();

        l.setPadding(new Insets(5, 5, 5, 5));

        l.setAlignment(Pos.CENTER);
        l.setLayoutX(posX - 25);
        l.setLayoutY(posY);

        pTextes.getChildren().add(l);

        return l;
    }

    //Methode appelee pour refresh l'affichage graphique a chaque ajout d'element graphique
    public void refreshLayersLiaisonAjoutee(Polyline poly) {
        p.getChildren().clear();
        pLignes.getChildren().add(poly);

        p.getChildren().add(pFond);
        p.getChildren().add(pLignes);
        p.getChildren().add(pVehicules);
        p.getChildren().add(pTextes);
        p.getChildren().add(pStations);
    }

    //Méthodes appelées pour mettre à jour les boutons bonus
    public void refreshLoco() {
        btnLoco.setText("Locomotives : " + m.getNbLocomotivesDispo());
    }

    public void refreshWagon() {
        btnWagon.setText("Wagons : " + m.getNbWagonsDispo());
    }

    public void refreshCE() {
        btnCE.setText("CE : " + m.getNbCEDispo());
    }

    public void refreshLigne() {
        btnLigne.setText("Lignes : " + m.getNbLignesDispo());
    }

    public void refreshTunnel() {
        btnTunnel.setText("Tunnels : " + m.getNbTunnelsDispo());
    }

    public void refreshNbVoyageurs() {
        labelNbVoyageurs.setText(String.valueOf(m.getNbVoyageurs()));
    }

    public void refreshCompteurStation(int t, Label l) {
        l.setText("" + t);
    }

    public void hideCompteurStation(Label l) {
        l.setText("");
    }

    ////////////////////////////Fin methodes graphiques/////////////////////////
    ////////////////////////////////Getters/Setters/////////////////////////////
    public Pane getP() {
        return p;
    }

    public MiniMetro getM() {
        return m;
    }

    public void setM(MiniMetro m) {
        this.m = m;
    }

    public boolean isaClique() {
        return aClique;
    }

    public void setaClique(boolean aClique) {
        this.aClique = aClique;
    }

    public boolean isExtClique() {
        return extClique;
    }

    public void setExtClique(boolean extClique) {
        this.extClique = extClique;
    }

    public boolean isWagonClique() {
        return wagonClique;
    }

    public boolean isLocoClique() {
        return locoClique;
    }

    public boolean isCEClique() {
        return CEClique;
    }

    public void setOver() {
        over = true;
        if (mp != null) {
            mp.stop();
        }
    }

    public boolean isOver() {
        return over;
    }

    ///////////////////Fin des getters/setters//////////////////////////////////
    public void arretAnimations() {

        Thread th;

        for (Object t : threadDecompte) {
            th = (ThreadDecompteSurchargeStation) t;
            th.interrupt();
        }

        threadGeneration.interrupt();
        threadAfficheJour.interrupt();

        for (Thread t : threadStations) {
            t.interrupt();
        }

        for (Object t : threadVehicules) {
            th = (Thread) t;
            th.interrupt();
        }

        for (Object t : threadPosVehicules) {
            th = (Thread) t;
            th.interrupt();
        }
    }

    public void addThreadDecompte(ThreadDecompteSurchargeStation tdss) {
        threadDecompte.add(tdss);
    }

    public void removeThreadDecompte(ThreadDecompteSurchargeStation tdss) {
        threadDecompte.remove(tdss);
    }

    public Fenetre getParent() {
        return fenetreParent;
    }
}