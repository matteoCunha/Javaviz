package model.repository;

import model.music.Morceau;
import model.music.Playlist;
import model.music.SequenceDeMusique;
import model.user.Abonne;

import java.sql.*;
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
            boolean isPublic = rs.getBoolean("ispublic");
            SequenceDeMusique seq = creationSequenceFromId(id);
            list.add(new Playlist(id, name, isPublic, abonne, seq));
        }

        return list;
    }

    public void updatePlaylist(Playlist playlist) throws SQLException {
        SequenceDeMusique seq = playlist.getSequence();
        SequenceDeMusique.Node current = seq.getHead();
        conn.setAutoCommit(false);

        try {
            String delQuery = "DELETE FROM playlist_morceaux WHERE playlist_id = ?";
            PreparedStatement pDel = conn.prepareStatement(delQuery);
            pDel.setInt(1, playlist.getId());
            pDel.executeUpdate();

            String insertQuery = "INSERT INTO playlist_morceaux (playlist_id, morceaux_id, position) VALUES (?, ?, ?)";
            PreparedStatement pInsert = conn.prepareStatement(insertQuery);

            int positionAct = 1;
            while(current != null) {
                pInsert.setInt(1, playlist.getId());
                pInsert.setInt(2, seq.getMorceau(current).getId());
                pInsert.setInt(3, positionAct);

                pInsert.addBatch();

                current = seq.passToNext(current);
                positionAct++;
            }
            pInsert.executeBatch();
            conn.commit();

        } catch (SQLException e){
            conn.rollback(); throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public Playlist insertNewPlaylist(Playlist playlist) throws SQLException {
        String insertQuery = "INSERT INTO public.playlist (nom, ispublic, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, playlist.getName());
            pstmt.setBoolean(2, playlist.isPublic());
            pstmt.setLong(3, playlist.getAbonneId());

            int lignesModifiees = pstmt.executeUpdate();

            if (lignesModifiees == 0) {
                throw new SQLException("Échec de la création de la playlist, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int nouvelId = generatedKeys.getInt(1);
                    playlist.setId(nouvelId);
                } else {
                    throw new SQLException("Échec de la création, aucun ID retourné.");
                }
            }
        }

        return playlist;
    }

    public void ajouterMorceauxInPlaylist(int playlistId, int morceauId) throws SQLException {
        String sql = "INSERT INTO playlist_morceaux (playlist_id, morceaux_id, position) " +
                "VALUES (?, ?, (SELECT COALESCE(MAX(position), 0) + 1 FROM playlist_morceaux WHERE playlist_id = ?))";

        try (PreparedStatement p = this.conn.prepareStatement(sql)){
            p.setInt(1, playlistId);
            p.setInt(2, morceauId);
            p.setInt(3, playlistId);
            p.executeUpdate();
        }
    }
}
//TODO : fonction update le jeu est nul
