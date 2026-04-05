package archi.PartieUtilisateur;

public class Visiteur extends User{
    private int compteurEcoute = 0;

    public Visiteur() {
        super("Invité");
    }

    public void incrementerEcoute() { this.compteurEcoute++; }
    public int getCompteurEcoute() { return compteurEcoute; }
}
