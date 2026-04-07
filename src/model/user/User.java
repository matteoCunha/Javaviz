package model.PartieUtilisateur;

import model.interfaces.Consultable;

public abstract class User {
    protected String pseudo;

    public User(String pseudo) {
        this.pseudo = pseudo;
    }

    public void rechercher(String query) {
        System.out.println(this.pseudo + "recherche : " + query);
    }

    public void consulter(Consultable contenu) { System.out.println(this.pseudo + "consulte : " + contenu); }

    public boolean hasInfiniteStream() { return false; }
    public boolean canCreatePlaylist() { return false; }
    public boolean isAdmin() { return false; }

    public String getPseudo() {
        return pseudo;
    }
}