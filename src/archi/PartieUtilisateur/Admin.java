package archi.PartieUtilisateur;

public class Admin extends CompteConnecte {

    public Admin(String pseudo, String password, int id) { super(pseudo, id, password); }

    @Override
    public boolean isAdmin() { return true; }

    public void supprimerUser() {}
    public void suspendreUser() {}

    public void ajouterMorceau() {}
    public void supprimerMorceau() {}

    public void ajouterArtiste() {}
    public void supprimerartiste() {}
    public void modifierArtiste() {}

    public void ajouterGroup() {}
    public void supprimerGroup() {}
    public void modifierGroup() {}
}
