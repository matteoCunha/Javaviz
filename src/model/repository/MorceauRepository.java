package model.repository;
import model.music.Artiste;
import model.music.Group;
import model.music.Morceau;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MorceauRepository {
    protected Connection conn;

    public MorceauRepository(Connection c) { this.conn = c;}

    public Morceau createMorceau(PreparedStatement q) throws SQLException {
        ResultSet rs = q.executeQuery();
        rs.next();
        java.sql.Date sqlDate = rs.getDate("date_sortie");
        LocalDate dateSortie = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        System.out.println("Morceau trouvé : \n\t-Titre : " + rs.getString(9)
                +"\n\t-Date sortie : " + dateSortie + "\n\t-Genre : " + rs.getString(4));
        Morceau m;
        if (rs.getInt("id") != 0) { m = new Morceau(rs.getInt("id"), dateSortie, (Artiste) null, rs.getInt("temps"), rs.getString("titre"), rs.getString(3)); }
        else { m = new Morceau(rs.getInt("id"), dateSortie, (Group) null, rs.getInt(3), rs.getString("titre"), rs.getString("titre")); }

        rs.close();
        return m;
    }

    public Morceau createMorceau(ResultSet rs) throws SQLException {
        java.sql.Date sqlDate = rs.getDate("date_sortie");
        LocalDate dateSortie = (sqlDate != null) ? sqlDate.toLocalDate() : null;
        Morceau m;
        if (rs.getInt("id") != 0) { m = new Morceau(rs.getInt("id"), dateSortie, (Artiste) null, rs.getInt("temps"), rs.getString("titre"), rs.getString(3)); }
        else { m = new Morceau(rs.getInt("id"), dateSortie, (Group) null, rs.getInt(3), rs.getString("titre"), rs.getString("titre")); }
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

    public List<Morceau> fetchByName(String name, int limit) throws SQLException{
        String query = "SELECT * FROM morceau WHERE titre ILIKE ? LIMIT ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setString(1, "%" + name + "%"); q.setInt(2, limit);
        ResultSet rs = q.executeQuery();

        List<Morceau> list = new ArrayList<>();
        while(rs.next()) { list.add(createMorceau(rs)); }
        return list;
    }
}


/*
TODO : implémenter une fonction recherche puis mettre en forme dans recherchable pour donner 2 morceaux 2 album et 2 artistes par exemple (les 2 sont arbitraires)
*/

//TODO test branch