package view;

import model.music.Artiste;
import model.music.Group;
import model.music.Morceau;
import model.music.Playlist;
import model.repository.*;
import model.user.Abonne;
import model.user.CompteConnecte;
import model.user.GestionConnexion;
import model.user.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainConsole {

    private Connection conn;
    private Scanner scanner;
    private User utilisateurConnecte = null;

    public MainConsole() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        MainConsole app = new MainConsole();
        app.demarrer();
    }

    public void demarrer() {
        System.out.println("=========================================");
        System.out.println("  BIENVENUE SUR JAVAVIZ (Version Console)");
        System.out.println("=========================================");

        try {
            this.conn = DatabaseConnection.getConnection();
            UserRepository u = new UserRepository(conn);
            GestionConnexion g = new GestionConnexion(conn, u);

            boolean running = true;

            while (running) {
                afficherMenu();
                int choix = lireChoix();

                switch (choix) {
                    case 1:
                        afficherCatalogue();
                        break;
                    case 2:
                        listerArtistes();
                        break;
                    case 3:
                        listerGroupes();
                    case 4:
                        if (utilisateurConnecte == null) seConnecter();
                        else seDeconnecter();
                        break;
                    case 5:
                        if (utilisateurConnecte != null) afficherHistorique();
                        else System.out.println("Commande invalide.");
                        break;

                    case 6:
                        if (utilisateurConnecte != null) listerPlaylist();
                        else System.out.println("Commande invalide");
                        break;
                    case 0:
                        running = false;
                        System.out.println("Au revoir !");
                        break;
                    default:
                        System.out.println("Choix invalide, veuillez réessayer.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        } finally {
            scanner.close();
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void afficherMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        if (utilisateurConnecte != null) {
            System.out.println("Connecté en tant que : " + utilisateurConnecte.getPseudo());
        } else {
            System.out.println("Statut : Visiteur");
        }

        System.out.println("1. Voir le catalogue musical");
        System.out.println("2. Voir les artistes");
        System.out.println("3. Voir les groupes");


        if (utilisateurConnecte == null) {
            System.out.println("4. Se connecter");

        } else {
            System.out.println("4. Se déconnecter");
            System.out.println("5. Mon historique");
            System.out.println("6. Mes playlists");

        }
        System.out.println("0. Quitter");
        System.out.print("\tVotre choix : ");
    }

    private void listerArtistes() throws SQLException {
        ArtistRepository artistRepository = new ArtistRepository(this.conn);
        List<Artiste> list = artistRepository.fetchAll();// print fait lors de la récupération des données
        System.out.println("\nAffichage des Artistes : ");
    }

    private void listerGroupes() throws SQLException {
        GroupRepository groupRepository = new GroupRepository(this.conn);
        List<Group> list = groupRepository.fetchAll(); // print fait lors de la récupération des données
        System.out.println("Affichage des Groupes : ");
    }

    private void afficherCatalogue() {
        System.out.println("\n--- CATALOGUE MUSICAL ---");
        try {
            MorceauRepository repo = new MorceauRepository(this.conn);
            List<Morceau> morceaux = repo.fetchAllMorceaux();

            if (morceaux.isEmpty()) {
                System.out.println("Le catalogue est vide.");
            } else {
                for (Morceau m : morceaux) {
                    System.out.printf("ID: %d | %s - %s [%s]\n",
                            m.getId(), m.getAutorName(), m.getTitre(), formaterTemps(m.getTime()));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du catalogue : " + e.getMessage());
        }
    }

    private void listerPlaylist() throws SQLException {
        System.out.println("\n--- PLAYLISTS ---");
        PlaylistRepository playlistRepository = new PlaylistRepository(this.conn, new MorceauRepository(this.conn));
        List<Playlist> listPlaylists = playlistRepository.fetchAllPlaylistFromsql((Abonne) utilisateurConnecte);
        for (int i = 0; i < listPlaylists.size(); i++) {
            System.out.printf("Playlist n°%d : %s\n", i+1, listPlaylists.get(i).getName());
        }
        System.out.println("Afficher playlist : (entrez le numéro, sinon 0) :");
        int choix = lireChoix();
        if (choix != 0 && choix - 1 < listPlaylists.size()) {
            System.out.println("Affichage playlist : " + listPlaylists.get(choix - 1).getName());
            listPlaylists.get(choix - 1).printSequence();
        }

        else System.out.println("Retour à l'acceuil ou mauvais numéros de playlist entré...");
    }

    private void seConnecter() {
        System.out.println("\n--- CONNEXION ---");
        System.out.print("Pseudo : ");
        String pseudo = scanner.nextLine();

        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        try {
            UserRepository repo = new UserRepository(this.conn);
            GestionConnexion gestionConnexion = new GestionConnexion(this.conn, repo);
            CompteConnecte user = gestionConnexion.connexion(pseudo, password);

            if (user != null) {
                this.utilisateurConnecte = user;
                System.out.println("Connexion réussie ! Bienvenue " + user.getPseudo());
            } else {
                System.out.println("Pseudo ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion : " + e.getMessage());
        }
    }

    private void seDeconnecter() {
        this.utilisateurConnecte = null;
        System.out.println("Vous avez été déconnecté.");
    }

    private void afficherHistorique() throws SQLException {

        PlaylistRepository playlistRepository = new PlaylistRepository(this.conn, new MorceauRepository(this.conn));
        Playlist historique = playlistRepository.getHistorique((Abonne) utilisateurConnecte);
        historique.printSequence();
    }

    private int lireChoix() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String formaterTemps(int totalSecondes) {
        int minutes = totalSecondes / 60;
        int secondes = totalSecondes % 60;
        return String.format("%d:%02d", minutes, secondes);
    }
}