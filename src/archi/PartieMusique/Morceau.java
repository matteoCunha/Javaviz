package archi.PartieMusique;

import archi.interfaces.Consultable;

import java.util.Date;

public class Morceau {
    private int id;
    private Date dateSortie;
    private Artiste artiste;
    private Group group;
    private int temps;
    private String genre;
    private int nb_ecoutes;
    private String titre;

    public Morceau(int id, Date dateSortie, Artiste artiste, int temps, String genre) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.artiste = artiste;
        this.temps = temps;
        this.genre = genre;
    }

    public void ajouterEcoute() { this.nb_ecoutes++; }

}
