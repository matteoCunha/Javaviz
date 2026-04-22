package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import model.music.Album;
import model.music.Morceau;
import model.music.Playlist;
import model.repository.DatabaseConnection;
import model.repository.MorceauRepository;
import model.repository.PlaylistRepository;
import model.user.Abonne;
import model.user.Admin;
import model.user.User;
import model.user.Visiteur;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MainController {
    Connection conn;
    User utilisateur; // utilisation si besoin utilisation du polymorphisme pour le passer en Abonne, Visiteur ou admin
    //TODO : Recherche et renvoyer les albums/artistes/morceaux etc
    //TODO : ajouter d'autres morceaux albums et groupes (au moins 5 groupes de plus et 5 albums de plus)
    //TODO : Pouvoir afficher l'historique d'écoute, a voir si sous forme de playlist, album on un autre view
    //TODO : Ajouter les deux fonctions supplémentaire a voir quoi encore
    //TODO : ajouter les nombre d'écoutes pour chaques morceaux pour les stats coté admin
    //TODO : faire l'interface coté administrateur avec ajout/suppression de morceaux albums etc...
    //TODO : ajouter un son lors du lancement d'une musique (peut être toujours le même) et un son non copyright
    @FXML private StackPane contentArea;
    @FXML private VBox sideMenu;
    @FXML private Label userLabel;
    @FXML private HBox authContainer;
    @FXML private HBox userProfileContainer;
    @FXML private PlayerViewController playerViewController;

    public MainController() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
        utilisateur = new Visiteur();
    }

    public void lancerMusique(Morceau m) throws SQLException {
        if (playerViewController != null) {
            playerViewController.jouerMorceau(m);
        }
    }

    public void setUtilisateur(User user) { this.utilisateur = user; }

    public void updateSessionState(Object user) throws SQLException {
        if (user == null) {
            this.utilisateur = new Visiteur();
            authContainer.setVisible(true);
            userProfileContainer.setVisible(false);
        } else {
            authContainer.setVisible(false);
            userProfileContainer.setVisible(true);
            if (user instanceof Abonne) {
                userLabel.setText(((Abonne) user).getPseudo());
                setUtilisateur((Abonne) user);
            } else {
                setUtilisateur((Admin) user);
                userLabel.setText("Administrateur Mode");
            }
        }
        try {
            updateSideMenu();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() throws SQLException {
        if (playerViewController != null) {
            playerViewController.setMainController(this);
        }
        showHome();
        updateSideMenu();
    }

    @FXML
    public void showHome() {
        loadView("HomeView.fxml");
    }

    @FXML
    public void showLogin() {
        loadView("LoginView.fxml");
    }

    @FXML
    public void showSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/SignInView.fxml"));
            Node view = loader.load();

            SignInController controller = loader.getController();
            controller.setMainController(this);
            controller.setConnection(conn);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : consult album");
            e.printStackTrace();
        }
    }

    public void showPlaylistDetail(Playlist playlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/PlaylistDetailView.fxml"));
            Node view = loader.load();

            PlaylistDetailController controller = loader.getController();
            controller.setPlaylistData(playlist, this);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur chargement PlaylistDetailView");
            e.printStackTrace();
        }
    }

    @FXML
    public void logout() throws SQLException {
        updateSessionState(null);
        showHome();
    }

    public void consultAlbum(Album album) throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/ConsultAlbumView.fxml"));
            Node view = loader.load();

            ConsultAlbumController controller = loader.getController();
            controller.setMainController(this);
            controller.setAlbumData(album, this.conn);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : consult album");
            e.printStackTrace();
        }
    }

    public void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/" + fxmlFile));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);

            if (fxmlFile.equals("HomeView.fxml")) {
                HomeViewController controller = loader.getController();
                controller.setConn(conn);
                controller.setMainController(this);
            } else if (fxmlFile.equals("LoginView.fxml")) {
                LoginViewController controller = loader.getController();
                controller.setConnection(conn);
                controller.setMainController(this);
            }

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + fxmlFile);
            e.printStackTrace();
        }
    }

    public void updateSideMenu() throws SQLException{
        sideMenu.getChildren().clear();

        if (utilisateur instanceof Visiteur) {
            Label info = new Label("S'abonner/Se connecter pour\nvoir vos playlists");
            info.setStyle("-fx-text-fill: #b3b3b3; -fx-padding: 10;");
            sideMenu.getChildren().add(info);
        } else if (utilisateur instanceof Abonne){
            Label titre = new Label("VOS PLAYLISTS");
            titre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            sideMenu.getChildren().add(titre);

            MorceauRepository m = new MorceauRepository(conn);
            PlaylistRepository p = new PlaylistRepository(conn, m);
            List<Playlist> userPlaylists = p.fetchAllPlaylistFromsql((Abonne) this.utilisateur);

            for(Playlist pl : userPlaylists) {
                Button playlistBtn = new Button(pl.getName());
                playlistBtn.setUserData(pl);

                String styleNormal = "-fx-background-color: #1a1a1a; " +
                        "-fx-text-fill: #b3b3b3; " +
                        "-fx-alignment: CENTER_LEFT; " +
                        "-fx-padding: 10 15 10 15; " +
                        "-fx-background-radius: 5; " +
                        "-fx-pref-width: 170; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;";

                String styleHover = "-fx-background-color: #2a2a2a; " +
                        "-fx-text-fill: white; " +
                        "-fx-alignment: CENTER_LEFT; " +
                        "-fx-padding: 10 15 10 15; " +
                        "-fx-background-radius: 5; " +
                        "-fx-pref-width: 170; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;";

                playlistBtn.setStyle(styleNormal);
                playlistBtn.setOnMouseEntered(e -> playlistBtn.setStyle(styleHover));
                playlistBtn.setOnMouseExited(e -> playlistBtn.setStyle(styleNormal));

                playlistBtn.setOnAction(event -> {
                    Playlist clickedPlaylist = (Playlist) playlistBtn.getUserData();
                    showPlaylistDetail(clickedPlaylist);
                });
                sideMenu.getChildren().add(playlistBtn);
            }
        }
    }
}