package model.repository;
import model.music.Artiste;
import model.music.Group;
import model.music.Morceau;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;

public class MorceauRepository {
    protected Connection conn;

    public MorceauRepository(Connection c) { this.conn = c;}

    public Morceau createMorceau(PreparedStatement q) throws SQLException {
        ResultSet rs = q.executeQuery();
        rs.next();
        System.out.println("Morceau trouvé : \n\t-Titre : " + rs.getString(9)
                +"\n\t-Date sortie : " + rs.getDate(2) + "\n\t-Genre : " + rs.getString(4));
        Morceau m;
        if (rs.getInt(6) != 0) { m = new Morceau(rs.getInt(1), (Date) rs.getDate(2), (Artiste) null, rs.getInt(3), rs.getString(3)); }
        else { m = new Morceau(rs.getInt(1), (Date) rs.getDate(2), (Group) null, rs.getInt(3), rs.getString(3)); }

        rs.close();
        return m;
    }

    public Morceau fetchByArtist(Artiste a) throws SQLException{
        String query = "SELECT * FROM morceau WHERE artiste_id = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setInt(1, a.getId());
        return createMorceau(q);
    }

    public Morceau fetchById(int id) throws SQLException {
        String query = "SELECT * FROM morceau WHERE id = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setInt(1, id);
        return createMorceau(q);
    }

    public Morceau fetchByName (String name) throws SQLException {
        String query = "SELECT * FROM morceau WHERE titre = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setString(1, name);
        return createMorceau(q);
    }
}