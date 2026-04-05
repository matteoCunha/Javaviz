package archi.PartieMusique;

public class Artiste {
    int id;
    String pseudo;
    String description;
    int dateNaissance;

    public Artiste(int id, String pseudo, String desc, int dateNaissance) {
        this.id = id;
        this.pseudo = pseudo;
        this.description = desc;
        this.dateNaissance = dateNaissance;
    }
}
