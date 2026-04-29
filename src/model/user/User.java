package model.user;

public abstract class User {
    protected String pseudo;

    public User(String pseudo) {
        this.pseudo = pseudo;
    }

    public boolean hasInfiniteStream() { return false; }
    public boolean canCreatePlaylist() { return false; }
    public boolean isAdmin() { return false; }

    public String getPseudo() {
        return pseudo;
    }
}