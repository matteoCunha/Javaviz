package model.music;

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

    public void printSequence() { sequence.printPlaylist(); }
    public SequenceDeMusique getSequence () { return this.sequence; }
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public Boolean isPublic() { return this.isPublic; }
}
