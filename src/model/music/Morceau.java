package model.music;

import model.interfaces.Recherchable;

import java.time.LocalDate;
import java.util.Date;

public class Morceau implements Recherchable {
    private int id;
    private LocalDate dateSortie;
    private Artiste artiste;
    private Group group;
    private int temps;
    private String genre;
    private int nb_ecoutes;
    private int numero_piste;
    private String titre;
    private int position; // peut être null -> sert uniquement pour les playlists

    public Morceau(int id, LocalDate dateSortie, Artiste artiste, int temps, String titre,String genre, int numero_piste) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.artiste = artiste;
        this.temps = temps;
        this.genre = genre;
        this.titre = titre;
        this.numero_piste = numero_piste;
    }

    public Morceau(int id, LocalDate dateSortie, Artiste artiste, int temps, String titre,String genre, int numero_piste, int position) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.artiste = artiste;
        this.temps = temps;
        this.genre = genre;
        this.titre = titre;
        this.numero_piste = numero_piste;
        this.position = position;
    }

    public Morceau(int id, LocalDate dateSortie, Group group, int temps, String titre,String genre, int numero_piste) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.group = group;
        this.temps = temps;
        this.genre = genre;
        this.titre = titre;
        this.numero_piste = numero_piste;
    }

    public Morceau(int id, LocalDate dateSortie, Group group, int temps, String titre,String genre, int numero_piste, int position) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.group = group;
        this.temps = temps;
        this.genre = genre;
        this.titre = titre;
        this.numero_piste = numero_piste;
        this.position = position;
    }

    public void ajouterEcoute() { this.nb_ecoutes++; }
    public String getTitre() { return this.titre; }
    public int getId() { return this.id; }
    public int getNumeroPiste()  { return this.numero_piste;}
    public void setNumeroPiste(int n)  { this.numero_piste = n; }

    public int getPosition() { return this.position; }
    public void setPosition(int n) { this.position = n;}

    public int getNb_ecoutes() { return nb_ecoutes; }
    public void setNb_ecoutes(int n) { this.nb_ecoutes = n; }

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
    public String getSearchTitle() { return this.getTitre() + " - " + this.getAutorName() + " - " + this.getId(); }

    @Override
    public String getSearchSubtitle(){ return "Morceau"; }

    @Override
    public String getContent() {return getSearchTitle() + " - " + getSearchSubtitle(); }
}


/*
Dans la base de donnée :
1 - id
2 - date_sortie
3 - temps
4 - genre
5 - album_id (peut être nul)
6 - artiste_id
7 - nb_ecoutes
8 - numero_piste (sert pour les playlists, peut être null donc)
9 - titre (nom du morceau)
 */
