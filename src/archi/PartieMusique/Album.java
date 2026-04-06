package archi.PartieMusique;

import archi.interfaces.Consultable;

import java.util.Date;

public class Album implements Consultable {
    int id;
    Date dateCreation;
    String description;
    String name;
    Artiste artiste;
    Group group;


    // ------------ Partie interface Consultable -------------------
    @Override
    public String getHeaderTitle(){ return this.name;}

    @Override
    public String getSubtitle(){
        if (this.artiste != null) { return "Artiste : " + this.artiste.getPseudo(); }
        else if (this.group != null) { return "Group : " + this.group.getName(); }

        return "Artiste/groupe inconnus";
    }

    @Override
    public String getDescription(){ return this.description;}

    @Override
    public SequenceDeMusique getElements(){ return new SequenceDeMusique();}
}
