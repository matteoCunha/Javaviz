package archi.PartieMusique;

import java.util.Date;

public class Morceau {
    int id;
    Date dateSortie;
    Artiste artiste; //a voir si on mets un tableau
    int temps;
    String genre;
    int nb_ecoutes;

    public Morceau(int id, Date dateSortie, Artiste artiste, int temps, String genre) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.artiste = artiste;
        this.temps = temps;
        this.genre = genre;
    }
}
