package model.user;

import model.repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class GestionConnexion {
    Connection conn;
    UserRepository userRepository;

    public GestionConnexion(Connection c, UserRepository u) {
        this.conn = c;
        this.userRepository = u;
    }

    public CompteConnecte connexion(String pseudo, String password) throws SQLException {
        CompteConnecte user = userRepository.fetchByPseudo(pseudo);
        if (user != null && !user.comparePass(password)) {
            return user;
        }
        return null;
    }

    public Visiteur sessionVisiteur() {
        return new Visiteur();
    }
}
