package model.repository;

import model.music.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
