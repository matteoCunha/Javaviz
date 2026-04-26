package model.music;

import model.interfaces.Consultable;
import model.interfaces.Recherchable;
import model.repository.ArtistRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class Group implements Consultable, Recherchable {
    private int id;
    private LocalDate dateCreation;
    private String description;
    private String name;
    private ArtistRepository artistsRepo;
    private List<Artiste> artisteList;

    public Group(int id, LocalDate dateCreation, String description, String name) {
        this.id = id;
        this.dateCreation = dateCreation;
        this.description = description;
        this.name = name;
    }

    public String getName() { return name; }
    public Date getSqlDate() { return Date.valueOf(this.dateCreation); }
    public int getId() { return this.id; }

    // ------------ Partie interface Consultable -------------------
    @Override
    public String getHeaderTitle(){ return this.getName();}

    @Override
    public String getSubtitle(){ return "Artistes :";}

    @Override
    public String getDescription(){ return this.description;}

    @Override
    public SequenceDeMusique getElements(){ return new SequenceDeMusique();}


    //----- Interface Recherchable --------
    @Override
    public String getSearchTitle() { return this.getHeaderTitle() + " - " + this.getDescription(); }

    @Override
    public String getSearchSubtitle(){ return "Groupe"; }

    @Override
    public String getContent() {return getSearchTitle() + " - " + getSearchSubtitle(); }

    @Override
    public String toString() { return getHeaderTitle(); }

    public LocalDate getDateCreation() { return this.dateCreation; }

    public void setId(int nouvelId) { this.id = nouvelId; }
}
/*
champ de group :
- id
- date_creation
- name
- description
 */