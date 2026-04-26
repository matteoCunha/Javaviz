package model.music;

import model.interfaces.Consultable;
import model.interfaces.Recherchable;

import java.time.LocalDate;

public class Artiste implements Consultable, Recherchable {
    private int id;
    private String pseudo;
    private String description;
    private LocalDate dateNaissance;

    public Artiste(int id, String pseudo, String desc, LocalDate dateNaissance) {
        this.id = id;
        this.pseudo = pseudo;
        this.description = desc;
        this.dateNaissance = dateNaissance;
    }

    public String getPseudo() { return this.pseudo; }
    public int getId() { return this.id; }
    public LocalDate getDateNaissance() { return this.dateNaissance; }

    // ------------ Partie interface Consultable -------------------
    @Override
    public String getHeaderTitle(){ return this.getPseudo();}

    @Override
    public String getSubtitle(){ return "Artiste";}

    @Override
    public String getDescription(){ return this.description;}

    @Override
    public SequenceDeMusique getElements(){ return new SequenceDeMusique();}


    // ------------ Partie interface Recherchable -------------------
    @Override
    public String getSearchTitle() { return getHeaderTitle(); }

    @Override
    public String getSearchSubtitle() { return "Artiste"; }

    @Override
    public String getContent() { return getSearchTitle() + " - " + getSearchSubtitle(); }

    @Override
    public String toString() {
        return this.pseudo; // ou peu importe comment s'appelle ta variable "nom"
    }

}


/*
Dans la base de donnée (index des colonnes commence à 1 pas a 0) :
1 - id
2 - pseudo
3 - description
4 - group_id (peut être nul)
5 - birth_date (peut être nul)
 */