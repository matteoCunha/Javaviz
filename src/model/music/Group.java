package model.music;

import model.interfaces.Consultable;
import model.interfaces.Recherchable;

import java.sql.Date;
import java.time.LocalDate;

public class Group implements Consultable, Recherchable {
    private int id;
    private LocalDate dateCreation;
    private String description;
    private String name;

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
}
/*
champ de group :
- id
- date_creation
- name
- description
 */