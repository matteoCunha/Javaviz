package archi.PartieUtilisateur;

import archi.PartieMusique.Playlist;

public class Abonne extends CompteConnecte {
    Playlist[] playlist;

    public Abonne(String pseudo, String password, int id) {
        super(pseudo, id, pseudo);
    }

    @Override
    public boolean hasInfiniteStream() { return true; }

    @Override
    public boolean canCreatePlaylist() { return true; }
}
