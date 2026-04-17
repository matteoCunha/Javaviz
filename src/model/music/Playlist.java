package model.music;

import model.repository.SearchResult;
import model.user.Abonne;

public class Playlist {
    int id;
    String name;
    boolean isPublic;
    Abonne createur;
    SequenceDeMusique sequence;

    public Playlist(int id, String name, boolean isPublic, Abonne a, SequenceDeMusique h)  {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.createur = a;
        this.sequence = h;
    }


}
