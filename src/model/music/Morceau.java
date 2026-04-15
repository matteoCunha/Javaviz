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

    public Morceau(int id, LocalDate dateSortie, Artiste artiste, int temps, String titre,String genre) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.artiste = artiste;
        this.temps = temps;
        this.genre = genre;
        this.titre = titre;
    }

    public Morceau(int id, LocalDate dateSortie, Group group, int temps, String titre,String genre) {
        this.id = id;
        this.dateSortie = dateSortie;
        this.group = group;
        this.temps = temps;
        this.genre = genre;
        this.titre = titre;
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


/*
Dans la base de donnée (index des colonnes commence à 1 pas a 0) :
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
