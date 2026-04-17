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

    public Morceau createMorceauFromsql(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        java.sql.Date sqlDate = rs.getDate("date_sortie");
        LocalDate dateSortie = (sqlDate != null) ? sqlDate.toLocalDate() : null;
        int artisteId = rs.getInt("artiste_id");
        if(!rs.wasNull()) {
            ArtistRepository art = new ArtistRepository(conn);
            Artiste artiste = art.fetchById(artisteId);
            return new Morceau(id, dateSortie, artiste, rs.getInt("temps"), rs.getString("titre"), rs.getString("genre"), rs.getInt("numero_piste"));
        }

        int groupId = rs.getInt("group_id");
        GroupRepository groupRepository = new GroupRepository(conn);
        Group group = groupRepository.fetchById(groupId);
        return new Morceau(id, dateSortie, group, rs.getInt("temps"), rs.getString("titre"), rs.getString("genre"), rs.getInt("numero_piste"));
    }

    public Morceau fetchByArtist(Artiste a) throws SQLException{
        String query = "SELECT * FROM morceau WHERE artiste_id = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setInt(1, a.getId());
        ResultSet rs = q.executeQuery();
        rs.next();
        return createMorceauFromsql(rs);
    }

    public Morceau fetchById(int id) throws SQLException {
        String query = "SELECT * FROM morceau WHERE id = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setInt(1, id);
        ResultSet rs = q.executeQuery();
        rs.next();
        return createMorceauFromsql(rs);
    }

    public Morceau fetchByName (String name) throws SQLException {
        String query = "SELECT * FROM morceau WHERE titre = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setString(1, name);
        ResultSet rs = q.executeQuery();
        rs.next();
        return createMorceauFromsql(rs);
    }

    public List<Morceau> fetchByName(String name, int limit) throws SQLException{
        String query = "SELECT * FROM morceau WHERE titre ILIKE ? LIMIT ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setString(1, "%" + name + "%"); q.setInt(2, limit);
        ResultSet rs = q.executeQuery();

        List<Morceau> list = new ArrayList<>();
        while(rs.next()) { list.add(createMorceauFromsql(rs)); }
        return list;
    }

    public List<Morceau> fetchByAlbum(int album_id) throws SQLException {
        String query = "SELECT * FROM morceau WHERE album_id = ? ORDER BY numero_piste";
        PreparedStatement q = conn.prepareStatement(query);
        q.setInt(1, album_id);

        ResultSet rs = q.executeQuery();

        List<Morceau> list = new ArrayList<>();
        while(rs.next()) { list.add(createMorceauFromsql(rs)); }
        return list;
    }
}

/*
TODO : implémenter une fonction recherche puis mettre en forme dans recherchable pour donner 2 morceaux 2 album et 2 artistes par exemple (les 2 sont arbitraires)
*/