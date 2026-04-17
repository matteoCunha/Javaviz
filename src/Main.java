import model.music.Album;
import model.music.Morceau;
import model.music.Playlist;
import model.repository.*;
import model.user.Abonne;
import model.user.GestionConnexion;

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
            GestionConnexion gestionConn = new GestionConnexion(conn, u);
            Abonne matt = (Abonne) gestionConn.connexion("matteo", "root");



            Album test = alb.fetchById(1);
            test.printAlbum();
            List<Playlist> l = play.fetchAllPlaylistFromsql(matt);
            matt.setPlaylist(l);
            matt.printPlay();
            System.out.println(matt.sePresenter());
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la connexion :");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de configuration (variables d'environnement) :");
            System.err.println(e.getMessage());
        }
    }
}