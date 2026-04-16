package model.repository;

import model.music.Album;
import model.music.Artiste;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AlbumRepository {
    protected Connection conn;

    public AlbumRepository(Connection c) { this.conn = c; }

    public Album createAlbum(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String desc = rs.getString("description");
        String name = rs.getString("name");
        java.sql.Date sqlDate = rs.getDate("date_creation");
        LocalDate date = (sqlDate != null) ? sqlDate.toLocalDate() : null;
        int artisteId = rs.getInt("artiste_id");
        if(!rs.wasNull()) {
            ArtistRepository art = new ArtistRepository(conn);
            Artiste artiste = art.fetchById(id);
            return new Album(id, date, desc, name, artiste);
        }

        int groupId = rs.getInt("group_id");
        //TODO : implémenter fonction avec GroupRepostory (pas encore fait)
        return null;
    }

    public Album fetchById(int id) throws SQLException {
        String query = "SELECT * FROM album WHERE id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, id);

        ResultSet rs = p.executeQuery();
        rs.next();

        return createAlbum(rs);
    }

    public Album fetchByName() throws SQLException {
        //TODO : a implémenter a voir si un seul résultat ou une liste (mais plutot résultat simple, la liste est gérer dans la classe search)
        return null;
    }
}
