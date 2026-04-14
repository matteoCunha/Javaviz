package model.music;

import model.interfaces.Consultable;

public class Artiste implements Consultable {
    private int id;
    private String pseudo;
    private String description;
    private int dateNaissance;

    public Artiste(int id, String pseudo, String desc, int dateNaissance) {
        this.id = id;
        this.pseudo = pseudo;
        this.description = desc;
        this.dateNaissance = dateNaissance;
    }

    public String getPseudo() { return pseudo; }

    // ------------ Partie interface Consultable -------------------
    @Override
    public String getHeaderTitle(){ return this.getPseudo();}

    @Override
    public String getSubtitle(){ return "Artiste";}

    @Override
    public String getDescription(){ return this.description;}

    @Override
    public SequenceDeMusique getElements(){ return new SequenceDeMusique();}
}


/*
Dans la base de donnée (index des colonnes commence à 1 pas a 0) :
1 - id
2 - pseudo
3 - description
4 - group_id (peut être nul)
5 - birth_date (peut être nul)
 */