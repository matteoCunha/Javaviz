package model.user;

import model.music.Playlist;
import model.music.SequenceDeMusique;
import model.repository.PlaylistRepository;

import java.sql.SQLException;
import java.util.List;

public class Abonne extends CompteConnecte {
    List<Playlist> playlist;

    public Abonne(String pseudo, String password, int id) {
        super(pseudo, id, pseudo);
    }

    @Override
    public boolean hasInfiniteStream() { return true; }

    @Override
    public boolean canCreatePlaylist() { return true; }

    @Override
    public int getId() { return this.id; }

    public void setPlaylist(List<Playlist> list) { this.playlist = list; }
    public void printPlay() {
        for(int i = 0; i < this.playlist.size(); i++) {
            this.playlist.get(i).printSequence();
        }
    }

    public SequenceDeMusique getFirstSequence() { return playlist.get(0).getSequence();}
    public void updatePlaylists(PlaylistRepository p) throws SQLException {
        for(int i = 0; i < this.playlist.size(); i++)
        {
            p.updatePlaylist(this.playlist.get(i));
        }
    }

    public String sePresenter() { return "Abonne -> name : " + this.getPseudo() + " - password : " + this.getPassword(); }
}
