import model.repository.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            System.out.println("La base de données est prête à recevoir des requêtes SQL.");
            PreparedStatement queryAllMorceaux = conn.prepareStatement("SELECT * FROM morceau");
            ResultSet resultats = queryAllMorceaux.executeQuery();
            System.out.println(resultats);

            boolean encore = resultats.next();
            while(encore) {
                System.out.println(resultats.getString(1) + ":" + resultats.getString(2) + ":" + resultats.getString(3) + "-" + resultats.getString(9));
                encore = resultats.next();
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la connexion :");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de configuration (variables d'environnement) :");
            System.err.println(e.getMessage());
        }
    }
}