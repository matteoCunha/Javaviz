import model.music.Album;
import model.music.Morceau;
import model.music.Playlist;
import model.music.SequenceDeMusique;
import model.repository.*;
import model.user.Abonne;
import model.user.GestionConnexion;

import javax.sound.midi.Sequence;
import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("La base de données est prête à recevoir des requêtes SQL.");
            MorceauRepository m = new MorceauRepository(conn);
            ArtistRepository a = new ArtistRepository(conn);
            AlbumRepository alb = new AlbumRepository(conn);
            GroupRepository g = new GroupRepository(conn);
            UserRepository u = new UserRepository(conn);
            PlaylistRepository play = new PlaylistRepository(conn, m);

            GestionConnexion gestionConnexion = new GestionConnexion(conn, u);
            Abonne matt = (Abonne) gestionConnexion.connexion("matteo", "root");
            matt.setPlaylist(play.fetchAllPlaylistFromsql(matt));
            matt.printPlay();
            SequenceDeMusique seq = matt.getFirstSequence();
            seq.moveDown(seq.getHead());
            seq.printPlaylist();
            matt.updatePlaylists(play);

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la connexion :");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de configuration (variables d'environnement) :");
            System.err.println(e.getMessage());
        }
    }
}