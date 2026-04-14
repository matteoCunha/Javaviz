import model.music.Album;
import model.music.Morceau;
import model.repository.ArtistRepository;
import model.repository.DatabaseConnection;
import model.repository.MorceauRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            System.out.println("La base de données est prête à recevoir des requêtes SQL.");
            MorceauRepository m = new MorceauRepository(conn);
            Morceau gims = m.fetchByName("PARISIENNE");
            System.out.println("Test morceau " + gims.getAutorName()); //doit renvoyer inconnu car dans l'implémentaiton quand artiste ou group n'est pas trouvé dans la classe cela renvoie inconnu par défaut

            ArtistRepository a = new ArtistRepository(conn);
            a.fetchAll();

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la connexion :");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de configuration (variables d'environnement) :");
            System.err.println(e.getMessage());
        }
    }
}