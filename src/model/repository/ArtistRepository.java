package model.repository;

import com.sun.scenario.effect.impl.prism.ps.PPSBlend_REDPeer;
import model.music.Album;
import model.music.Artiste;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepository {
    protected Connection conn;

    public ArtistRepository (Connection c) { this.conn = c; }

    public Artiste createArtistFromsql(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String pseudo = rs.getString("pseudo");
        String description = rs.getString("description");
        java.sql.Date sqlDate = rs.getDate("birth_date");
        LocalDate birthDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        return new Artiste(id, pseudo, description, birthDate);
    }

    public List<Artiste> fetchAll() throws SQLException {
        String query = "SELECT * FROM artiste";
        PreparedStatement p = conn.prepareStatement(query);
        ResultSet rs = p.executeQuery();
        ArrayList<Artiste> list = new ArrayList<Artiste>();
        boolean encore = rs.next();
        while(encore) {
            java.sql.Date sqlDate = rs.getDate("birth_date");
            LocalDate birthDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

            System.out.println("Artiste trouvé (ID " + rs.getInt(1) + "): \n\t-Pseudo : " + rs.getString(2)
                    +"\n\t-Date de naissance : " + birthDate + "\n\t-Description : " + rs.getString(3));
            list.add(createArtistFromsql(rs));
            encore = rs.next();
        }
        rs.close();
        return list;
    }

    public Artiste fetchById(int id) throws SQLException {
        String query = "SELECT * FROM artiste WHERE id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, id);

        ResultSet rs = p.executeQuery();
        rs.next();

        return createArtistFromsql(rs);
    }

    public List<Artiste> searchByName (String name, int limit) throws SQLException{
        String query = "SELECT * FROM artiste WHERE pseudo ILIKE ? LIMIT ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setString(1, "%" + name + "%"); q.setInt(2, limit);

        ResultSet rs = q.executeQuery();
        List<Artiste> list = new ArrayList<>();

        while (rs.next()) { list.add(createArtistFromsql(rs)); }
        return list;
    }

    public List<Artiste> fetchHomeArtists() throws SQLException {
        String query = "SELECT * FROM artiste ORDER BY id LIMIT ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, 5);

        ResultSet rs = p.executeQuery();
        List<Artiste> list = new ArrayList<>();

        while(rs.next()) { list.add(createArtistFromsql(rs)); }
        return list;
    }

    public List<Artiste> fetchByGroup(int groupId) throws SQLException {
        String query = "SELECT * FROM artiste WHERE groupe_id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, groupId);

        ResultSet rs = p.executeQuery();
        List<Artiste> list = new ArrayList<>();

        while (rs.next()) { list.add(createArtistFromsql(rs)); }
        return list;
    }
}

//TODO conversion DATE, java ne comprends pas le format de la db (juste une conversion à faire

/*
TODO : implémenter une fonction recherche -> fait mais manque l'ajout de la fonction des albums
*/