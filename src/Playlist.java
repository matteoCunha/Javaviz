package modele;

import java.util.List;
import java.util.LinkedList;
import java.io.Serializable;

public class Playlist implements Serializable {
    private int id;
    private String nom;
    private List<Morceau> morceaux;

    public Playlist(int id, String nom, List<Morceau> morceaux) {
        this.id = id;
        this.nom = nom;
        this.morceaux = morceaux;
    }
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Morceau> getMorceaux() {
        return morceaux;
    }
}