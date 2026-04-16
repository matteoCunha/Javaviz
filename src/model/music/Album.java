package model.music;

import model.interfaces.Consultable;
import model.interfaces.Recherchable;
import model.repository.MorceauRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Album implements Consultable, Recherchable {
    private int id;
    private LocalDate dateCreation;
    private String description;
    private String name;
    private Artiste artiste;
    private Group group;
    private List<Morceau> morceauList;

    public Album(int id, LocalDate dateCreation, String description, String name, Artiste artiste, MorceauRepository m) throws SQLException {
        this.id = id;
        this.dateCreation = dateCreation;
        this.description = description;
        this.name = name;
        this.artiste = artiste;
        this.morceauList = m.fetchByAlbum(this.id);
    }

    public Album(int id, LocalDate dateCreation, String description, String name, Group group, MorceauRepository m) throws SQLException {
        this.id = id;
        this.dateCreation = dateCreation;
        this.description = description;
        this.name = name;
        this.group = group;
        this.morceauList = m.fetchByAlbum(this.id);
    }

    public void printAlbum() {
        System.out.println("\nAlbum de " + this.artiste.getPseudo() + " -> " + this.name + "\n\tDescription : " + this.getDescription());
        for(Morceau m : morceauList) {
            System.out.println("\t- " + m.getNumeroPiste() + " " + m.getContent());
        }
    }

    public String getName() { return this.name; }
    public String getArtistName() { if(this.artiste != null) { return this.artiste.getPseudo(); } return ""; }
    public String getGroupName() { if(this.group != null) { return this.group.getName(); } return ""; }



    // ------------ Partie interface Consultable -------------------
    @Override
    public String getHeaderTitle(){ return this.name;}

    @Override
    public String getSubtitle(){
        if (this.artiste != null) { return "Artiste : " + this.artiste.getPseudo(); }
        else if (this.group != null) { return "Group : " + this.group.getName(); }

        return "Inconnu";
    }

    @Override
    public String getDescription(){ return this.description;}

    @Override
    public SequenceDeMusique getElements(){ return new SequenceDeMusique();}


    //----- Interface Recherchable --------
    @Override
    public String getSearchTitle() {
        if (artiste != null) {
            return this.getName() + " - " + this.getArtistName();
        } else if (group != null) {
            return this.getName() + " - " + this.getGroupName();
        }
        return "";
    }

    @Override
    public String getSearchSubtitle(){ return "Album"; }

    @Override
    public String getContent() { return getSearchTitle() + " - " + getSearchSubtitle(); }
}


/*
Dans la base de donnée (index des colonnes commence à 1 pas a 0) :
1 - id
2 - date_creation
3 - description
4 - name
5 - artiste_id (peut être nul) soit un artiste, soit un groupe
6 - group_id (peut être nul).
 */

//TODO : idée pour les playlist garder les listes chainées (simple pour utiliser le bouton suivant/précédent plus tard dans l'inteface graphique) et utilisation de arrayLists pour les albums (rapide pour display avec une itération).