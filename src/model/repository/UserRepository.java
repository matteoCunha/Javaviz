package model.repository;

import model.music.Playlist;
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

    public void deleteAbonne(Abonne a) throws SQLException {
        boolean autoCommitPrecedent = this.conn.getAutoCommit();
        this.conn.setAutoCommit(false);

        try {
            if (a.getPlaylists() != null) {
                PlaylistRepository playlistRepository = new PlaylistRepository(this.conn, new MorceauRepository(this.conn));
                for (Playlist p : a.getPlaylists()) {
                    playlistRepository.deletePlaylist(p);
                }
            }

            String sqlHist = "DELETE FROM historique_ecoutes WHERE user_id = ?";
            try (PreparedStatement pHist = this.conn.prepareStatement(sqlHist)) {
                pHist.setLong(1, a.getId());
                pHist.executeUpdate();
            }

            String sqlDel = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement pDel = this.conn.prepareStatement(sqlDel)) {
                pDel.setLong(1, a.getId());
                pDel.executeUpdate();
            }

            this.conn.commit();
            System.out.println("Abonné supprimé avec succès !");

        } catch (SQLException e) {
            this.conn.rollback();
            System.err.println("Erreur lors de la suppression de l'abonné. Annulation.");
            throw e;
        } finally {
            this.conn.setAutoCommit(autoCommitPrecedent);
        }
    }
}