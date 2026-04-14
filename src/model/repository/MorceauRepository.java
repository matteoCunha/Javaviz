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
    Connection conn;

    public MorceauRepository(Connection c) { this.conn = c;}

    public Morceau fetchByArtist(Artiste a) {
        return null;
    }

    public Morceau fetchById(int id) {
        return null;
    }

    public Morceau fetchByName (String name) throws SQLException {
        String query = "SELECT * FROM morceau WHERE titre = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setString(1, name);
        ResultSet result = q.executeQuery();
        result.next();
        System.out.println("Morceau trouvé : \n\t-Titre : " + result.getString(9)
        +"\n\t-Date sortie : " + result.getDate(2) + "\n\t-Genre : " + result.getString(4));
        if (result.getInt(6) != 0) {
            return new Morceau(result.getInt(1), (Date) result.getDate(2), (Artiste) null, result.getInt(3), result.getString(3));
        } else {
            return new Morceau(result.getInt(1), (Date) result.getDate(2), (Group) null, result.getInt(3), result.getString(3));
        }
    }
}

//TODO reste des fonctions utiles pour cette classe