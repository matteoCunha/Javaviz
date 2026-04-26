package model.repository;

import model.music.Album;
import model.music.Artiste;
import model.music.Group;
import model.music.Morceau;

import javax.xml.transform.Result;
import java.sql.*;
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

    public List<Album> fetchByArtist(Artiste artiste) throws SQLException {
        String query = "SELECT * FROM album WHERE artiste_id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, artiste.getId());

        ResultSet rs = p.executeQuery();
        List<Album> list = new ArrayList<>();
        while(rs.next()) { list.add(createAlbumFromsql(rs)); }
        return list;
    }

    public List<Album> fetchByGroup(Group group) throws SQLException {
        String query = "SELECT * FROM album WHERE artiste_id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, group.getId());

        ResultSet rs = p.executeQuery();
        List<Album> list = new ArrayList<>();
        while(rs.next()) { list.add(createAlbumFromsql(rs)); }
        return list;
    }

    public List<Album> fetchAll() throws SQLException {
        String query = "SELECT * FROM album";
        PreparedStatement p = conn.prepareStatement(query);

        ResultSet rs = p.executeQuery();
        List<Album> list = new ArrayList<>();
        while(rs.next()) { list.add(createAlbumFromsql(rs)); }
        return list;
    }

    public Album fetchByName(String name) throws SQLException {
        String query = "SELECT * FROM album WHERE name = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, name);

        ResultSet rs = p.executeQuery();
        rs.next();
        return createAlbumFromsql(rs);
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

    public List<Album> fetchHomeAlbum() throws SQLException {
        String query = "SELECT * FROM album ORDER BY date_creation DESC LIMIT ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, 5);

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

    public void deleteAlbum(Album album) throws SQLException {
        String sql = "UPDATE morceau SET album_id = NULL WHERE album_id = ?";
        PreparedStatement p = this.conn.prepareStatement(sql);
        p.setInt(1, album.getId());
        p.executeUpdate();

        String del = "DELETE FROM album WHERE id = ?";
        PreparedStatement q = this.conn.prepareStatement(del);
        q.setInt(1, album.getId());
        q.executeUpdate();
    }

    public void insertAlbum(Album album, List<Morceau> morceauxSelectionnes) throws SQLException {
        boolean autoCommitPrecedent = this.conn.getAutoCommit();
        this.conn.setAutoCommit(false);

        try {
            String sql = "INSERT INTO album (name, date_creation, description, artiste_id, group_id) VALUES (?, ?, ?, ?, ?)";

            long nouvelAlbumId;
            try (PreparedStatement p = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                p.setString(1, album.getName());
                p.setDate(2, java.sql.Date.valueOf(album.getDateCreation()));

                if (album.getDescription() != null && !album.getDescription().isEmpty()) {
                    p.setString(3, album.getDescription());
                } else {
                    p.setNull(3, Types.VARCHAR);
                }

                if (album.getArtiste() != null) p.setLong(4, album.getArtiste().getId());
                else p.setNull(4, Types.BIGINT);

                if (album.getGroup() != null) p.setLong(5, album.getGroup().getId());
                else p.setNull(5, Types.BIGINT);

                p.executeUpdate();

                try (ResultSet rs = p.getGeneratedKeys()) {
                    if (rs.next()) nouvelAlbumId = rs.getLong(1);
                    else throw new SQLException("Échec lors de la récupération de l'ID de l'album.");
                }
            }

            if (morceauxSelectionnes != null && !morceauxSelectionnes.isEmpty()) {
                String sqlUpdateMorceau = "UPDATE morceau SET album_id = ? WHERE id = ?";
                try (PreparedStatement pTrack = this.conn.prepareStatement(sqlUpdateMorceau)) {
                    for (Morceau m : morceauxSelectionnes) {
                        pTrack.setLong(1, nouvelAlbumId);
                        pTrack.setLong(2, m.getId());
                        pTrack.addBatch();
                    }
                    pTrack.executeBatch();
                }
            }

            this.conn.commit();

        } catch (SQLException e) {
            this.conn.rollback();
            throw e;
        } finally {
            this.conn.setAutoCommit(autoCommitPrecedent);
        }
    }
}
