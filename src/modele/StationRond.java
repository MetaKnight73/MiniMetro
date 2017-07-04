package modele;

public class StationRond extends Station {

    public StationRond(Reseau reseau, int posX, int posY) {
        super(reseau, posX, posY);

        centreX = posX + 15;
        centreY = posY + 15;
        type = StationType.Rond;
        urlImage = "images/StationRond.png";
    }
}