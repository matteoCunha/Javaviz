package model.repository;

import model.music.Album;
import model.music.Artiste;
import model.music.Group;
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
    private GroupRepository groupRepository;

    public SearchResult(MorceauRepository m, ArtistRepository a, AlbumRepository alb, GroupRepository g) {
        this.morceauRepository = m; this.artistRepository = a;
        this.albumRepository = alb; this.groupRepository = g;
        //TODO : reste a ajouter la logique pour albums (et playlist peut-être plus tard)
    }

    public SearchResult globalSearch(String query) throws SQLException {

        List<Morceau> morceaux = morceauRepository.fetchByName(query, 5);
        List<Artiste> artistes = artistRepository.searchByName(query, 3);
        List<Album> albums = albumRepository.searchByName(query, 2);
        List<Group> groups = groupRepository.searchByName(query, 2);
        for (Morceau morceau : morceaux) {
            System.out.println(morceau.getContent());
        }

        for (Artiste artiste : artistes) {
            System.out.println(artiste.getContent());
        }

        for (Album album : albums) {
            System.out.println(album.getContent());
        }

        for (Group group : groups) {
            System.out.println(group.getContent());
        }

        return null;
    }
}
