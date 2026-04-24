package model.repository;
import model.music.Artiste;
import model.music.Group;
import model.music.Morceau;
import model.user.Abonne;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MorceauRepository {
    protected Connection conn;

    public MorceauRepository(Connection c) { this.conn = c;}

    public Morceau createMorceauFromsql(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        java.sql.Date sqlDate = rs.getDate("date_sortie");
        LocalDate dateSortie = (sqlDate != null) ? sqlDate.toLocalDate() : null;
        int artisteId = rs.getInt("artiste_id");
        if(!rs.wasNull()) {
            ArtistRepository art = new ArtistRepository(conn);
            Artiste artiste = art.fetchById(artisteId);
            return new Morceau(id, dateSortie, artiste, rs.getInt("temps"), rs.getString("titre"), rs.getString("genre"), rs.getInt("numero_piste"), rs.getInt("nb_ecoutes"));
        }

        int groupId = rs.getInt("group_id");
        GroupRepository groupRepository = new GroupRepository(conn);
        Group group = groupRepository.fetchById(groupId);
        return new Morceau(id, dateSortie, group, rs.getInt("temps"), rs.getString("titre"), rs.getString("genre"), rs.getInt("numero_piste"), rs.getInt("nb_ecoutes"));
    }

    public Morceau createFromSQLPlaylist(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        java.sql.Date sqlDate = rs.getDate("date_sortie");
        LocalDate dateSortie = (sqlDate != null) ? sqlDate.toLocalDate() : null;
        int artisteId = rs.getInt("artiste_id");
        if(!rs.wasNull()) {
            ArtistRepository art = new ArtistRepository(conn);
            Artiste artiste = art.fetchById(artisteId);
            int position = rs.getInt("position");
            if (!rs.wasNull()) {
                return new Morceau(id, dateSortie, artiste, rs.getInt("temps"), rs.getString("titre"), rs.getString("genre"), rs.getInt("numero_piste"), position);
            }
            return new Morceau(id, dateSortie, artiste, rs.getInt("temps"), rs.getString("titre"), rs.getString("genre"), rs.getInt("numero_piste"), 0);
        }

        int groupId = rs.getInt("group_id");
        GroupRepository groupRepository = new GroupRepository(conn);
        Group group = groupRepository.fetchById(groupId);
        int position = rs.getInt("position");
        if (!rs.wasNull()) {
            return new Morceau(id, dateSortie, group, rs.getInt("temps"), rs.getString("titre"), rs.getString("genre"), rs.getInt("numero_piste"), position);
        }
        return new Morceau(id, dateSortie, group, rs.getInt("temps"), rs.getString("titre"), rs.getString("genre"), rs.getInt("numero_piste"), 0);

    }

    public Morceau fetchByArtist(Artiste a) throws SQLException{
        String query = "SELECT * FROM morceau WHERE artiste_id = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setInt(1, a.getId());
        ResultSet rs = q.executeQuery();
        rs.next();
        return createMorceauFromsql(rs);
    }

    public Morceau fetchById(int id) throws SQLException {
        String query = "SELECT * FROM morceau WHERE id = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setInt(1, id);
        ResultSet rs = q.executeQuery();
        rs.next();
        return createMorceauFromsql(rs);
    }

    public Morceau fetchByName (String name) throws SQLException {
        String query = "SELECT * FROM morceau WHERE titre = ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setString(1, name);
        ResultSet rs = q.executeQuery();
        rs.next();
        return createMorceauFromsql(rs);
    }

    public List<Morceau> fetchByName(String name, int limit) throws SQLException{
        String query = "SELECT * FROM morceau WHERE titre ILIKE ? LIMIT ?";
        PreparedStatement q = conn.prepareStatement(query);
        q.setString(1, "%" + name + "%"); q.setInt(2, limit);
        ResultSet rs = q.executeQuery();

        List<Morceau> list = new ArrayList<>();
        while(rs.next()) { list.add(createMorceauFromsql(rs)); }
        return list;
    }

    public List<Morceau> fetchByAlbum(int album_id) throws SQLException {
        String query = "SELECT * FROM morceau WHERE album_id = ? ORDER BY numero_piste";
        PreparedStatement q = conn.prepareStatement(query);
        q.setInt(1, album_id);

        ResultSet rs = q.executeQuery();

        List<Morceau> list = new ArrayList<>();
        while(rs.next()) { list.add(createMorceauFromsql(rs)); }
        return list;
    }

    public List<Morceau> fetchAllMorceaux() throws SQLException {
        String query = "SELECT * FROM morceau";
        PreparedStatement p = this.conn.prepareStatement(query);

        ResultSet rs = p.executeQuery();
        List<Morceau> list = new ArrayList<>();
        while(rs.next()) { list.add(createMorceauFromsql(rs)); }

        return list;
    }

    public List<Morceau> fetchHomeMorceaux() throws SQLException {
        String query = "SELECT * FROM morceau ORDER BY nb_ecoutes DESC LIMIT ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, 5);

        ResultSet rs = p.executeQuery();

        List<Morceau> list = new ArrayList<>();
        while(rs.next()) { list.add(createMorceauFromsql(rs)); }
        return list;
    }

    public List<Morceau> fetchTop5ByArtist(Artiste artiste) throws SQLException {
        String query = "SELECT * FROM morceau WHERE artiste_id = ? ORDER BY nb_ecoutes DESC LIMIT ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, artiste.getId());
        p.setInt(2, 5);

        ResultSet rs = p.executeQuery();

        List<Morceau> list = new ArrayList<>();
        while(rs.next()) { list.add(createMorceauFromsql(rs)); }
        return list;
    }

    public List<Morceau> fetchTop5ByGroup(Group group) throws SQLException {
        String query = "SELECT * FROM morceau WHERE group_id = ? ORDER BY nb_ecoutes DESC LIMIT ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, group.getId());
        p.setInt(2, 5);

        ResultSet rs = p.executeQuery();

        List<Morceau> list = new ArrayList<>();
        while(rs.next()) { list.add(createMorceauFromsql(rs)); }
        return list;
    }

    public void updateMorceau(Morceau m) throws SQLException {
        String query = "UPDATE morceau SET nb_ecoutes = ?, numero_piste = ? WHERE id = ? ";
        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, m.getNb_ecoutes());
        p.setInt(2, m.getNumeroPiste());
        p.setInt(3, m.getId());

        int rs = p.executeUpdate();
    }

    public void updateHistorique(Abonne abonne, Morceau m) throws SQLException{
        String sqlInsert = "INSERT INTO historique_ecoutes (user_id, morceau_id, date_ecoute) VALUES (?, ?, NOW())";
        PreparedStatement p = conn.prepareStatement(sqlInsert);
        p.setInt(1, abonne.getId());
        p.setInt(2, m.getId());

        int r = p.executeUpdate();
    }
}

/*
TODO : implémenter une fonction recherche puis mettre en forme dans recherchable pour donner 2 morceaux 2 album et 2 artistes par exemple (les 2 sont arbitraires)
*/
//fonction updateMorceau fonctionne
//TODO: a implémenter -> logique d'update de la base de donnée a la fermeture de l'appli pour enregistrer les possibles modifs
//TODO: faire les fonctions update pour tous les autres éléments de la DB