package archi.PartieMusique;

import archi.interfaces.Consultable;

import java.util.Date;

public class Morceau implements Consultable {
    private int id;
    private Date dateSortie;
    private Artiste artiste;
    private Group group;
    private int temps;
    private String genre;
    private int nb_ecoutes;

    public Morceau(int id, Date dateSortie, Artiste artiste, int temps, String genre) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.artiste = artiste;
        this.temps = temps;
        this.genre = genre;
    }

    public void consulterElement() {
        if(this.group == null){
            System.out.println("Morceau: \n\t- Artiste : " + this.artiste.pseudo + "\n\t- Date de publication : " + this.dateSortie + "\n\t- Genre : " + this.genre + "\n\t- Duree : " + this.temps);
        } else if (this.artiste == null) {
            System.out.println("Morceau: \n\t- Group : " + this.group.name + "\n\t- Date de publication : " + this.dateSortie + "\n\t- Genre : " + this.genre + "\n\t- Duree : " + this.temps);
        }
    }
}
