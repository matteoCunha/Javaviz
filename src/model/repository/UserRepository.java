package model.repository;

import model.user.Abonne;
import model.user.Admin;
import model.user.CompteConnecte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    Connection conn;

    public UserRepository(Connection c) { this.conn = c; }

    public CompteConnecte createAbonneFromsql(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String pseudo = rs.getString("pseudo");
        String password = rs.getString("password");
        boolean isAdmin = rs.getBoolean("isAdmin");

        if (isAdmin) {
            return new Admin(pseudo, password, id);
        }

        return new Abonne(pseudo, password, id);
    }

    public CompteConnecte fetchByPseudo(String pseudo) throws SQLException{
        String query = "SELECT * FROM User WHERE pseudoo = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, pseudo);

        ResultSet rs = p.executeQuery();
        rs.next();

        return createAbonneFromsql(rs);
    }
}
