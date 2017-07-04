package modele;

public class StationCarre extends Station {

    public StationCarre(Reseau reseau, int posX, int posY) {
        super(reseau, posX, posY);

        centreX = posX + 15;
        centreY = posY + 15;
        type = StationType.Carre;
        urlImage = "images/StationCarre.png";
    }
}