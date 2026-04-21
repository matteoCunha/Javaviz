package model.repository;

import model.music.Album;
import model.music.Artiste;
import model.music.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlbumRepository {
    protected Connection conn;
    public AlbumRepository(Connection c) { this.conn = c; }

    public Album createAlbumFromsql(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String desc = rs.getString("description");
        String name = rs.getString("name");
        java.sql.Date sqlDate = rs.getDate("date_creation");
        LocalDate date = (sqlDate != null) ? sqlDate.toLocalDate() : null;
        int artisteId = rs.getInt("artiste_id");
        if (!rs.wasNull()) {
            ArtistRepository art = new ArtistRepository(conn);
            Artiste artiste = art.fetchById(artisteId);
            return new Album(id, date, desc, name, artiste, new MorceauRepository(conn));
        }

        int groupId = rs.getInt("group_id");
        GroupRepository groupRepository = new GroupRepository(conn);
        Group group = groupRepository.fetchById(groupId);
        return new Album(id, date, desc, name, group, new MorceauRepository(conn));
    }

    public Album fetchById(int id) throws SQLException {
        String query = "SELECT * FROM album WHERE id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, id);

        ResultSet rs = p.executeQuery();
        rs.next();

        return createAlbumFromsql(rs);
    }

    public Album fetchByName(String name) throws SQLException {
        String query = "SELECT * FROM album WHERE name = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, name);

        ResultSet rs = p.executeQuery();
        rs.next();
        return createAlbumFromsql(rs);
        //TODO : a implémenter a voir si un seul résultat ou une liste (mais plutot résultat simple, la liste est gérer dans la classe search)
    }

    public List<Album> searchByName(String name, int limit) throws SQLException {
        String query = "SELECT * FROM album WHERE name ILIKE ? LIMIT ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, "%" + name + "%");
        p.setInt(2, limit);

        ResultSet rs = p.executeQuery();
        List<Album> list = new ArrayList<>();

        while(rs.next()) { list.add(createAlbumFromsql(rs)); }
        return list;
    }

    public void updateAlbum(Album a) throws SQLException {
        String query = "UPDATE album SET date_creation = ?, description = ?, name = ? WHERE id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setDate(1, a.getSqlDate());
        p.setString(2, a.getDescription());
        p.setString(3, a.getName());
        p.setInt( 4, a.getId());

        int rs = p.executeUpdate();
    }
}
//TODO : fonction update