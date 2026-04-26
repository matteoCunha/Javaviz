package model.user;

public class Visiteur extends User{
    private int compteurEcoute;

    public Visiteur() {
        super("Invité");
        this.compteurEcoute = 0;
    }

    public void incrementerEcoute() { this.compteurEcoute++; }
    public int getCompteurEcoute() { return compteurEcoute; }
    public boolean peutEcouter() {
        return this.compteurEcoute < 5;
    }
}
