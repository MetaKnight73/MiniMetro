package modele;

public class StationTriangle extends Station {

    public StationTriangle(Reseau reseau, int posX, int posY) {
        super(reseau, posX, posY);

        centreX = posX + 15;
        centreY = posY + 20;
        type = StationType.Triangle;
        urlImage = "images/StationTriangle.png";
    }
}