package model.music;

import model.interfaces.Consultable;
import model.interfaces.Recherchable;

import java.util.Date;

public class Album implements Consultable, Recherchable {
    private int id;
    private Date dateCreation;
    private String description;
    private String name;
    private Artiste artiste;
    private Group group;

    public Album(int id, Date dateCreation, String description, String name, Artiste artiste) {
        this.id = id;
        this.dateCreation = dateCreation;
        this.description = description;
        this.name = name;
        this.artiste = artiste;
    }

    public Album(int id, Date dateCreation, String description, String name, Group group) {
        this.id = id;
        this.dateCreation = dateCreation;
        this.description = description;
        this.name = name;
        this.group = group;
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
            return this.getName() + "-" + this.getArtistName();
        } else if (group != null) {
            return this.getName() + "-" + this.getGroupName();
        }
        return "";
    }

    @Override
    public String getSearchSubtitle(){ return "Morceau"; }
}
