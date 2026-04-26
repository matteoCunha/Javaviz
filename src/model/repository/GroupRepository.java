package model.repository;

import model.music.Group;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository {
    private Connection conn;

    public GroupRepository(Connection c) { this.conn = c; }

    public Group createGroupFromsql(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        java.sql.Date sqlDate = rs.getDate("date_creation");
        LocalDate date = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        return new Group(id, date, description, name);
    }

    public Group fetchById(int id) throws SQLException {
        String query = "SELECT * FROM groupe WHERE id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, id);

        ResultSet rs = p.executeQuery();
        rs.next();

        return createGroupFromsql(rs);
    }

    public List<Group> searchByName(String name, int limit) throws SQLException {
        String query = "SELECT * FROM groupe WHERE name ILIKE ? LIMIT ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, "%" + name + "%");
        p.setInt(2, limit);

        List<Group> list = new ArrayList<>();
        ResultSet rs = p.executeQuery();

        while(rs.next()) { list.add(createGroupFromsql(rs)); }
        return list;
    }

    public List<Group> fetchHomeGroups() throws SQLException {
        String query = "SELECT * FROM groupe ORDER BY date_creation DESC LIMIT ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, 5);

        List<Group> list = new ArrayList<>();
        ResultSet rs = p.executeQuery();

        while(rs.next()) { list.add(createGroupFromsql(rs)); }
        return list;
    }

    public void updateGroup(Group g) throws SQLException {
        String query = "UPDATE groupe SET date_creation = ?, name = ?, description = ? WHERE id = ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setDate(1, g.getSqlDate());
        p.setString(2, g.getName());
        p.setString(3, g.getDescription());
        p.setInt(4, g.getId());

        int rs = p.executeUpdate();
    }

    public void deleteGroupe(Group groupe) throws SQLException {
        boolean autoCommitPrecedent = this.conn.getAutoCommit();
        this.conn.setAutoCommit(false);
        try {
            String updateMorceaux = "UPDATE morceau SET group_id = NULL WHERE group_id = ?";
            try (PreparedStatement p1 = this.conn.prepareStatement(updateMorceaux)) {
                p1.setLong(1, groupe.getId());
                p1.executeUpdate();
            }

            String updateAlbums = "UPDATE album SET group_id = NULL WHERE group_id = ?";
            try (PreparedStatement p2 = this.conn.prepareStatement(updateAlbums)) {
                p2.setLong(1, groupe.getId());
                p2.executeUpdate();
            }

            String deleteGroupe = "DELETE FROM groupe WHERE id = ?";
            try (PreparedStatement p3 = this.conn.prepareStatement(deleteGroupe)) {
                p3.setLong(1, groupe.getId());
                p3.executeUpdate();
            }

            this.conn.commit();
            System.out.println("Groupe '" + groupe.getName() + "' supprimé avec succès !");

        } catch (SQLException e) {
            this.conn.rollback();
            System.err.println("Erreur lors de la suppression du groupe. Annulation des modifications.");
            throw e;
        } finally {
            this.conn.setAutoCommit(autoCommitPrecedent);
        }
    }

    public void insertGroup(Group groupe) throws SQLException {
        String sql = "INSERT INTO groupe (name, description, date_creation) VALUES (?, ?, ?)";
        try (PreparedStatement p = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, groupe.getName());
            if (groupe.getDescription() != null && !groupe.getDescription().isEmpty()) {
                p.setString(2, groupe.getDescription());
            } else {
                p.setNull(2, Types.VARCHAR);
            }
            p.setDate(3, java.sql.Date.valueOf(groupe.getDateCreation()));
            p.executeUpdate();

            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) {
                    int nouvelId = rs.getInt(1);
                    groupe.setId(nouvelId);
                    System.out.println("Groupe créé avec succès avec l'ID : " + nouvelId);
                }
            }
        }
    }

    public List<Group> fetchAll() throws SQLException {
        String sql = "SELECT * FROM groupe";
        PreparedStatement p = this.conn.prepareStatement(sql);

        ResultSet rs = p.executeQuery();
        List<Group> list = new ArrayList<>();
        while(rs.next()) { list.add(createGroupFromsql(rs)); }

        return list;
    }
}

//TODO : fonction update
