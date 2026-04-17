package model.user;

public abstract class CompteConnecte extends User{
    private int id;
    private String password;

    public CompteConnecte(String pseudo, int id, String password) {
        super(pseudo);
        this.id = id;
        this.password = password;
    }

    public boolean comparePass(String other) { return other == this.password; }

    public int getId() { return this.id; }
}
