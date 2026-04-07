package archi.PartieMusique;

import archi.interfaces.Consultable;
import archi.interfaces.Recherchable;

import java.util.Date;

public class Morceau implements Recherchable {
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

    public Morceau(int id, Date dateSortie, Group group, int temps, String genre) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.group = group;
        this.temps = temps;
        this.genre = genre;
    }

    public void ajouterEcoute() { this.nb_ecoutes++; }
    public String getTitre() { return this.titre; }
    public String getAutorName() {
        if (this.artiste != null) {
            return this.artiste.getPseudo();
        } else if (this.group != null) {
            return this.group.getName();
        }
        return "Inconnu";
    }


    //----- Interface Recherchable --------
    @Override
    public String getSearchTitle() { return this.getTitre() + "-" + this.getAutorName(); }

    @Override
    public String getSearchSubtitle(){ return "Morceau"; }
}
