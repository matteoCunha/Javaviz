package model.repository;

import model.user.Abonne;
import model.user.Admin;
import model.user.CompteConnecte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    Connection conn;

    public UserRepository(Connection c) { this.conn = c; }

    public CompteConnecte createAbonneFromsql(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String pseudo = rs.getString("pseudo");
        String password = rs.getString("password");
        boolean isAdmin = rs.getBoolean("isAdmin");

        if (isAdmin) {
            return new Admin(pseudo, password, id);
        }

        return new Abonne(pseudo, password, id);
    }

    public CompteConnecte fetchByPseudo(String pseudo) throws SQLException {
        System.out.println("--- 1. DEBUT DU FETCH ---");
        System.out.println("Recherche du pseudo : [" + pseudo + "]");

        String query = "SELECT * FROM users  WHERE pseudo = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, pseudo);

        ResultSet rs = p.executeQuery();

        if (rs.next()) {
            System.out.println("--- 2. PSEUDO TROUVÉ EN BASE ! ---");
            return createAbonneFromsql(rs);
        } else {
            System.out.println("--- 2. ERREUR : PSEUDO INTROUVABLE EN BASE ---");
            return null;
        }
    }
}

//TODO : fonction update
