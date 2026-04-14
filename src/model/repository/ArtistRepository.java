package model.repository;

import model.music.Artiste;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistRepository {
    Connection conn;

    public ArtistRepository (Connection c) { this.conn = c; }

    public void fetchAll() throws SQLException {
        String query = "SELECT * FROM artiste";
        PreparedStatement p = conn.prepareStatement(query);
        ResultSet rs = p.executeQuery();

        boolean encore = rs.next();
        while(encore) {
            System.out.println("Artiste trouvé (ID " + rs.getInt(1) + "): \n\t-Pseudo : " + rs.getString(2)
                    +"\n\t-Date de naissance : " + rs.getDate(5) + "\n\t-Description : " + rs.getString(3));
            encore = rs.next();
        }
    }
    public Artiste fetchByI (int id) { return null; }
}

//TODO conversion DATE, java ne comprends pas le format de la db (juste une conversion à faire

