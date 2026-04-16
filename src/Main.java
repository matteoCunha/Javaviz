import model.music.Album;
import model.music.Morceau;
import model.repository.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("La base de données est prête à recevoir des requêtes SQL.");
            MorceauRepository m = new MorceauRepository(conn);
            ArtistRepository a = new ArtistRepository(conn);
            AlbumRepository alb = new AlbumRepository(conn);
            Morceau gims = m.fetchByName("PARISIENNE");
            System.out.println("Test morceau " + gims.getAutorName()); //doit renvoyer inconnu car dans l'implémentaiton quand artiste ou group n'est pas trouvé dans la classe cela renvoie inconnu par défaut

            PreparedStatement p = conn.prepareStatement("SELECT * FROM morceau WHERE titre ILIKE ?");
            p.setString(1, "%pari%");
            ResultSet rs = p.executeQuery(); rs.next();

            System.out.println(rs.getString("titre") + " - " + rs.getInt("id"));
            boolean encore = rs.next();
            while(encore) {
                System.out.println(rs.getString("titre") + " - " + rs.getInt("id"));
                encore = rs.next();
            }

            SearchResult s = new SearchResult(m, a, alb);
            s.globalSearch("Bo");

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la connexion :");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de configuration (variables d'environnement) :");
            System.err.println(e.getMessage());
        }
    }
}