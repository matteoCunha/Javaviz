package archi.PartieMusique;

public class Artiste {
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

}
