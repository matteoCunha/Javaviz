package model.user;

public abstract class CompteConnecte extends User{
    protected int id;
    private String password;

    public CompteConnecte(String pseudo, int id, String password) {
        super(pseudo);
        this.id = id;
        this.password = password;
    }

    public boolean comparePass(String other) { return this.password.equals(other); }

    public int getId() { return this.id; }
    public String getPassword() { return this.password; }
}
