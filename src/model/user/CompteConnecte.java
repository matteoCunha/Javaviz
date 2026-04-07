package model.PartieUtilisateur;

public abstract class CompteConnecte extends User{
    protected int id;
    protected String password;

    public CompteConnecte(String pseudo, int id, String password) {
        super(pseudo);
        this.id = id;
        this.password = password;
    }
}
