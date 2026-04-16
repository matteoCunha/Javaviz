package model.repository;

import model.music.Album;
import model.music.Artiste;
import model.music.Morceau;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SearchResult {
    private List<Morceau> morceaux;
    private List<Artiste> artistes;
    private List<Album> albums; //pas encore pret pour implémentation
    private MorceauRepository m;
    private ArtistRepository a;

    public SearchResult(MorceauRepository m, ArtistRepository a) {
        this.m = m; this.a = a;
        //TODO : reste a ajouter la logique pour albums (et playlist peut-être plus tard)
    }

    public SearchResult globalSearch(String query) throws SQLException {

        List<Morceau> morceaux = m.fetchByName(query, 5);
        List<Artiste> artistes = a.searchByName(query, 3);

        for (Morceau morceau : morceaux) {
            System.out.println(morceau.getContent());
        }

        for (Artiste artiste : artistes) {
            System.out.println(artiste.getContent());
        }
        return null;
    }
}
