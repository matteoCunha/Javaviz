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
    private MorceauRepository morceauRepository;
    private ArtistRepository artistRepository;
    private AlbumRepository albumRepository;

    public SearchResult(MorceauRepository m, ArtistRepository a, AlbumRepository alb) {
        this.morceauRepository = m; this.artistRepository = a;
        this.albumRepository = alb;
        //TODO : reste a ajouter la logique pour albums (et playlist peut-être plus tard)
    }

    public SearchResult globalSearch(String query) throws SQLException {

        List<Morceau> morceaux = morceauRepository.fetchByName(query, 5);
        List<Artiste> artistes = artistRepository.searchByName(query, 3);
        List<Album> albums = albumRepository.searchByName(query, 2);

        for (Morceau morceau : morceaux) {
            System.out.println(morceau.getContent());
        }

        for (Artiste artiste : artistes) {
            System.out.println(artiste.getContent());
        }

        for (Album album : albums) {
            System.out.println(album.getContent());
        }

        return null;
    }
}
