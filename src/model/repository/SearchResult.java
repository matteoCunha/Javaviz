package model.repository;

import model.music.*;

import java.sql.SQLException;
import java.util.List;

public class SearchResult {
    private List<Morceau> morceaux;
    private List<Artiste> artistes;
    private List<Album> albums;
    private List<Group> groups;
    private MorceauRepository morceauRepository;
    private ArtistRepository artistRepository;
    private AlbumRepository albumRepository;
    private GroupRepository groupRepository;

    public SearchResult(MorceauRepository m, ArtistRepository a, AlbumRepository alb, GroupRepository g) {
        this.morceauRepository = m; this.artistRepository = a;
        this.albumRepository = alb; this.groupRepository = g;
    }

    public void globalSearch(String query) throws SQLException {
        List<Morceau> morceaux = morceauRepository.fetchByName(query, 5);
        List<Artiste> artistes = artistRepository.searchByName(query, 3);
        List<Album> albums = albumRepository.searchByName(query, 2);
        List<Group> groups = groupRepository.searchByName(query, 2);

        this.morceaux = morceaux;
        this.artistes = artistes;
        this.albums = albums;
        this.groups = groups;
    }

    public List<Morceau> getMorceaux() { return morceaux; }
    public List<Artiste> getArtistes() { return artistes; }
    public List<Album> getAlbums() { return albums; }
    public List<Group> getGroups() { return groups; }
}
