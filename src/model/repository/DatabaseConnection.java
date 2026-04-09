package model.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver PostgreSQL introuvable : " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        String host     = getEnvSecurise("DB_HOST");
        String port     = getEnvSecurise("DB_PORT");
        String database = getEnvSecurise("DB_DATABASE");
        String user     = getEnvSecurise("DB_USER");
        String password = getEnvSecurise("DB_PASSWORD");

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("sslmode", "require");
        props.setProperty("connectTimeout", "10");

        System.out.println("Tentative de connexion à : " + url);

        Connection conn = DriverManager.getConnection(url, props);
        System.out.println("Connexion établie avec succès !");
        return conn;
    }

    private static String getEnvSecurise(String cle) {
        String valeur = System.getenv(cle);
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new IllegalArgumentException("Variable d'environnement manquante : " + cle);
        }
        return valeur.trim();
    }
}