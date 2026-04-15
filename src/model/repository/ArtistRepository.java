package model.repository;

import model.music.Artiste;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepository {
    Connection conn;

    public ArtistRepository (Connection c) { this.conn = c; }

    public Artiste createArtist(ResultSet rs) throws SQLException{
        int id = rs.getInt("id");
        String pseudo = rs.getString("pseudo");
        String description = rs.getString("description");
        java.sql.Date sqlDate = rs.getDate("birth_date");
        LocalDate birthDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        return new Artiste(id, pseudo, description, birthDate);
    }

    public void fetchAll() throws SQLException {
        String query = "SELECT * FROM artiste";
        PreparedStatement p = conn.prepareStatement(query);
        ResultSet rs = p.executeQuery();
        ArrayList<Artiste> array = new ArrayList<Artiste>();
        boolean encore = rs.next();
        while(encore) {
            java.sql.Date sqlDate = rs.getDate("birth_date");
            LocalDate birthDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

            System.out.println("Artiste trouvé (ID " + rs.getInt(1) + "): \n\t-Pseudo : " + rs.getString(2)
                    +"\n\t-Date de naissance : " + birthDate + "\n\t-Description : " + rs.getString(3));
            array.add(createArtist(rs));
            encore = rs.next();
        }
        rs.close();
    }

    public Artiste fetchByI (int id) { return null; }

    public List<Artiste> searchByName (String name, int limit) throws SQLException{
        String query = "SELECT * FROM artiste WHERE pseudo ILIKE ? LIMIT ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setString(1, "%" + name + "%"); q.setInt(2, limit);

        ResultSet rs = q.executeQuery();
        List<Artiste> list = new ArrayList<>();

        while (rs.next()) { list.add(createArtist(rs)); }
        return list;
    }
}

//TODO conversion DATE, java ne comprends pas le format de la db (juste une conversion à faire

/*
TODO : implémenter une fonction recherche
 */