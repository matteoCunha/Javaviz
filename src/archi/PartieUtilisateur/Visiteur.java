package archi.PartieUtilisateur;

public class Visiteur extends User{
    int compteurEcoute;
    int maxEcoute;

    public Visiteur(String pseudo) {
        super(pseudo);
        this.compteurEcoute = 0;
        this.maxEcoute = 5;
    }
}
