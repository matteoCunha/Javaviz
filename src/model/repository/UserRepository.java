package model.repository;

import model.user.Abonne;
import model.user.Admin;
import model.user.CompteConnecte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    Connection conn;

    public UserRepository(Connection c) { this.conn = c; }

    public CompteConnecte createAbonneFromsql(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String pseudo = rs.getString("pseudo");
        String password = rs.getString("password");
        boolean isAdmin = rs.getBoolean("isadmin");

        if (isAdmin) {
            System.out.println("admin trouvé");
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

    public boolean addNewAbonne(String pseudo, String password) throws SQLException {
        String query = "INSERT INTO users (pseudo, password, isAdmin) VALUES (?, ?, FALSE)";
        try (PreparedStatement p = conn.prepareStatement(query)) {
            p.setString(1, pseudo);
            p.setString(2, password);

            int lignesAjoutees = p.executeUpdate();
            return lignesAjoutees > 0;
        }
    }

    public boolean pseudoExisteDeja(String pseudo) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE pseudo = ?";

        try (PreparedStatement p = conn.prepareStatement(query)) {
            p.setString(1, pseudo);
            ResultSet rs = p.executeQuery();

            if (rs.next()) {
                int n = rs.getInt(1);
                return n > 0;
            }
        }
        return false;
    }

    public List<Abonne> fetchAllAbonne() throws SQLException {
        String sql = "SELECT * FROM users WHERE isadmin = FALSE";
        PreparedStatement p = this.conn.prepareStatement(sql);

        List<Abonne> list = new ArrayList<>();
        ResultSet rs = p.executeQuery();
        while(rs.next()) {
            list.add((Abonne) createAbonneFromsql(rs));
        }
        return list;
    }
}

//TODO : fonction update
