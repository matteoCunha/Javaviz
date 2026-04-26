package model.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatsRepository {
    private Connection conn;

    public StatsRepository(Connection conn) {
        this.conn = conn;
    }

    private int getCount(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement p = conn.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countAbonnes() throws SQLException {
        return getCount("users");
    }

    public int countMorceaux() throws SQLException {
        return getCount("morceau");
    }

    public int countAlbums() throws SQLException {
        return getCount("album");
    }

    public int countGroupes() throws SQLException {
        return getCount("groupe");
    }

    public int countArtistes() throws SQLException {
        return getCount("artiste");
    }

    public long countTotalEcoutes() throws SQLException {
        String sql = "SELECT COALESCE(SUM(nb_ecoutes), 0) FROM morceau";

        try (PreparedStatement p = conn.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    public List<TopItem> getTopMorceaux() throws SQLException {
        List<TopItem> top = new ArrayList<>();
        String sql = "SELECT titre, nb_ecoutes FROM morceau ORDER BY nb_ecoutes DESC LIMIT 5";

        try (PreparedStatement p = conn.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                top.add(new TopItem(rs.getString("titre"), rs.getLong("nb_ecoutes")));
            }
        }
        return top;
    }

    public List<TopItem> getTopCreateurs() throws SQLException {
        List<TopItem> top = new ArrayList<>();

        String sql =
                "SELECT name, SUM(total_ecoutes) AS final_total FROM (" +
                        "  SELECT a.pseudo AS name, SUM(m.nb_ecoutes) AS total_ecoutes " +
                        "  FROM morceau m " +
                        "  JOIN artiste a ON m.artiste_id = a.id " +
                        "  GROUP BY a.id, a.pseudo " +
                        "  UNION ALL " +
                        "  SELECT g.name AS name, SUM(m.nb_ecoutes) AS total_ecoutes " +
                        "  FROM morceau m " +
                        "  JOIN groupe g ON m.group_id = g.id " +
                        "  GROUP BY g.id, g.name " +
                        ") AS combined_stats " +
                        "GROUP BY name " +
                        "ORDER BY final_total DESC " +
                        "LIMIT 5";

        try (PreparedStatement p = conn.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                top.add(new TopItem(rs.getString("name"), rs.getLong("final_total")));
            }
        }
        return top;
    }

    public static class TopItem {
        private String nom;
        private long valeur;

        public TopItem(String nom, long valeur) {
            this.nom = nom;
            this.valeur = valeur;
        }
        public String getNom() { return nom; }
        public long getValeur() { return valeur; }
    }

    public long getEcoutesMorceau(long id) throws SQLException {
        String sql = "SELECT nb_ecoutes FROM morceau WHERE id = ?";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    public long getEcoutesAlbum(long id) throws SQLException {
        String sql = "SELECT COALESCE(SUM(nb_ecoutes), 0) FROM morceau WHERE album_id = ?";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    public long getEcoutesArtiste(long id) throws SQLException {
        String sql = "SELECT COALESCE(SUM(nb_ecoutes), 0) FROM morceau WHERE artiste_id = ?";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    public long getEcoutesGroupe(long id) throws SQLException {
        String sql = "SELECT COALESCE(SUM(nb_ecoutes), 0) FROM morceau WHERE group_id = ?";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }
}