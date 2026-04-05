package archi.PartieUtilisateur;

import java.util.Date;

public class User {
    String pseudo;
    String password;
    Date lastConnection;

    public User(String pseudo, String p) { this.pseudo = pseudo; this.password = p;}
}
