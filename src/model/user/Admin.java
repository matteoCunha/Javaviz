package model.user;

public class Admin extends CompteConnecte {

    public Admin(String pseudo, String password, int id) { super(pseudo, id, password); }

    @Override
    public boolean isAdmin() { return true; }

}
