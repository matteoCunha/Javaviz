package archi.PartieMusique;

import archi.interfaces.Consultable;

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
