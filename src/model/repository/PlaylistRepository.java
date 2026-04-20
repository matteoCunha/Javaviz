package model.repository;

import model.music.Morceau;
import model.music.Playlist;
import model.music.SequenceDeMusique;
import model.user.Abonne;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistRepository {
    Connection conn;
    MorceauRepository morceauRepository;

    public PlaylistRepository(Connection c, MorceauRepository m) { this.conn = c; this.morceauRepository = m; }

    public SequenceDeMusique creationSequenceFromId(int playlistId) throws SQLException {
        String query = "SELECT m.*, pm.position " +
                    "FROM morceau m " +
                    "JOIN playlist_morceaux pm ON m.id = pm.morceaux_id " +
                    "WHERE pm.playlist_id = ? " +
                    "ORDER BY pm.position";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, playlistId);
        ResultSet rs = p.executeQuery();
        SequenceDeMusique seq = new SequenceDeMusique();

        while(rs.next()) {
            Morceau m = morceauRepository.createFromSQLPlaylist(rs);
            seq.pushBack(m);
        }

        return seq;
    }

    public List<Playlist> fetchAllPlaylistFromsql(Abonne abonne) throws SQLException {
        String query = "SELECT * FROM playlist WHERE user_id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, abonne.getId());

        ResultSet rs = p.executeQuery();
        List<Playlist> list = new ArrayList<>();

        while(rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("nom");
            boolean isPublic = rs.getBoolean("isPublic");
            SequenceDeMusique seq = creationSequenceFromId(id);
            list.add(new Playlist(id, name, isPublic, abonne, seq));
        }

        return list;
    }
}
