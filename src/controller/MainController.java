package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import model.music.*;
import model.repository.*;
import model.user.Abonne;
import model.user.Admin;
import model.user.User;
import model.user.Visiteur;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MainController {
    Connection conn;
    User utilisateur; // utilisation si besoin utilisation du polymorphisme pour le passer en Abonne, Visiteur ou admin
    //DONE : Recherche et renvoyer les albums/artistes/morceaux etc FAIT
    //DONE : ajouter d'autres morceaux albums et groupes (au moins 5 groupes de plus et 5 albums de plus)
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
    @FXML private TextField searchField;
    @FXML private VBox playlistsContainer;
    @FXML private Button buttonAddPlay;
    @FXML private Button btnHistorique;
    @FXML private Separator separatorAddPlay;
    @FXML private HBox visitorLimitContainer;
    @FXML private Label visitorLimitLabel;

    public MainController() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
        utilisateur = new Visiteur();
    }

    public void lancerMusique(Morceau m) throws SQLException {
        if (utilisateur instanceof Visiteur v) {
            if (!v.peutEcouter()) {
                System.out.println("BLOCAGE : Il ne reste plus aucune écoute pour cette session");
                afficherPopUpVisiteur();
                return;
            }

            v.incrementerEcoute();
            updateTopBar();
        }

        if (playerViewController != null) {
            playerViewController.jouerMorceau(m);
        }
    }

    public void afficherPopUpVisiteur() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Limite d'écoutes atteinte");
        alert.setHeaderText("Vous avez épuisé vos 5 écoutes gratuites !");
        alert.setContentText("Passez à la vitesse supérieure. Créez un compte pour écouter vos musiques en illimité et créer vos propres playlists.");

        ButtonType boutonInscription = new ButtonType("S'inscrire maintenant", ButtonBar.ButtonData.OK_DONE);
        ButtonType boutonAnnuler = new ButtonType("Plus tard", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(boutonInscription, boutonAnnuler);
        Optional<ButtonType> resultat = alert.showAndWait();
        if (resultat.isPresent() && resultat.get() == boutonInscription) {
            showSignUp();
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
        visitorLimitContainer.managedProperty().bind(visitorLimitContainer.visibleProperty());
        authContainer.managedProperty().bind(authContainer.visibleProperty());
        userProfileContainer.managedProperty().bind(userProfileContainer.visibleProperty());
        btnHistorique.managedProperty().bind(btnHistorique.visibleProperty());

        if (playerViewController != null) {
            playerViewController.setMainController(this);
        }
        searchField.setOnAction(event -> {
            String requete = searchField.getText();
            if (!requete.isEmpty()) {
                try {
                    lancerGlobalSearch(requete);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        updateSideMenu();
        showLogin();
    }

    @FXML
    public void showHome() {
        loadView("HomeView.fxml");
    }

    @FXML
    public void showLogin() {
        loadView("LoginView.fxml");
    }

    public void showHistorique() {
        if (utilisateur instanceof Abonne) {
            try {
                MorceauRepository m = new MorceauRepository(this.conn);
                PlaylistRepository playlistRepository = new PlaylistRepository(this.conn, m);
                Playlist historique = playlistRepository.getHistorique((Abonne) utilisateur);
                showPlaylistDetail(historique);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void showAdminCatalog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/AdminCatalogView.fxml"));
            Node view = loader.load();

            AdminCatalogController controller = loader.getController();
            controller.setMainController(this);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : sign up");
            e.printStackTrace();
        }
    }

    public void showAdminAddMorceau() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/AddMorceauView.fxml"));
            Node view = loader.load();

            AddMorceauController controller = loader.getController();
            controller.setMainController(this);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : sign up");
            e.printStackTrace();
        }
    }

    public void showAddAlbumView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/AddAlbumView.fxml"));
            Node view = loader.load();

            AddAlbumController controller = loader.getController();
            controller.setMainController(this);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : sign up");
            e.printStackTrace();
        }
    }

    public void showAddCreateurView() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/vue/AddCreateurView.fxml"));
            javafx.scene.layout.VBox view = loader.load();

            controller.AddCreateurController controller = loader.getController();
            controller.setMainController(this);

            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            System.err.println("Erreur lors du chargement de la vue : sign up");
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void showArtistDetail(Artiste artiste) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/ArtistView.fxml"));
            Node view = loader.load();

            ArtistViewController controller = loader.getController();
            controller.setArtistData(artiste, this);

            contentArea.getChildren().setAll(view);
        } catch (IOException | SQLException e) {
            System.err.println("Erreur chargement Artist view");
            e.printStackTrace();
        }
    }

    public void showGroupDetail(Group group) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/GroupView.fxml"));
            Node view = loader.load();

            GroupViewController controller = loader.getController();
            controller.setArtistData(group, this);

            contentArea.getChildren().setAll(view);
        } catch (IOException | SQLException e) {
            System.err.println("Erreur chargement Group View");
            e.printStackTrace();
        }
    }

    public void showSearchView(SearchResult results, String query) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/SearchView.fxml"));
            Node view = loader.load();

            SearchViewController controller = loader.getController();
            controller.setResults(results, query, this);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : search view");
            e.printStackTrace();
        }
    }

    @FXML
    public void showCreatePlaylist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/CreatePlaylistView.fxml"));
            Node view = loader.load();

            CreatePlaylistController controller = loader.getController();
            controller.setMainController(this);

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : search view");
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

    public void updateTopBar() {
        if (utilisateur instanceof Visiteur v) {

            visitorLimitContainer.setVisible(true);
            visitorLimitLabel.setVisible(true);

            int ecoute = 5 - v.getCompteurEcoute();

            visitorLimitLabel.setText("Écoutes restantes : " + ecoute);
            authContainer.setVisible(true);
            authContainer.setManaged(true);
            userProfileContainer.setVisible(false);
            userProfileContainer.setManaged(false);

            if (v.getCompteurEcoute() <= 1) {
                visitorLimitLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #d32f2f; -fx-padding: 5 15; -fx-background-radius: 15;");
            }
        } else {
            visitorLimitContainer.setVisible(false);
            visitorLimitLabel.setVisible(false);
            authContainer.setVisible(false);
            authContainer.setManaged(false);
            userProfileContainer.setVisible(true);
            userProfileContainer.setManaged(true);
        }
    }

    public void updateSideMenu() throws SQLException{
        playlistsContainer.getChildren().clear();

        if (utilisateur instanceof Visiteur) {

            Label info = new Label("S'abonner/Se connecter pour\nvoir vos playlists");
            info.setStyle("-fx-text-fill: #b3b3b3; -fx-padding: 10;");
            playlistsContainer.getChildren().add(info);
            buttonAddPlay.setVisible(false);
            separatorAddPlay.setVisible(false);
            if (btnHistorique != null) {
                btnHistorique.setVisible(false);
            }
        } else if (utilisateur instanceof Abonne){

            Label titre = new Label("VOS PLAYLISTS");
            titre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            playlistsContainer.getChildren().add(titre);
            buttonAddPlay.setVisible(true);
            separatorAddPlay.setVisible(true);

            if (btnHistorique != null) {
                btnHistorique.setVisible(true);
            }

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
                playlistsContainer.getChildren().add(playlistBtn);
            }
        } else if (utilisateur instanceof Admin) {
            Label titreAdmin = new Label("ADMINISTRATION");
            titreAdmin.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");

            Button btnUsers = new Button("👥 Gestion Users");
            btnUsers.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            //btnUsers.setOnAction(e -> showAdminUsersView());

            Button btnCatalog = new Button("🎵 Gestion Catalogue");
            btnCatalog.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            btnCatalog.setOnAction(e -> showAdminCatalog());

            playlistsContainer.getChildren().addAll(titreAdmin, btnUsers, btnCatalog);
            buttonAddPlay.setVisible(false);
            separatorAddPlay.setVisible(false);
        }
    }

    private void lancerGlobalSearch(String query) throws SQLException {
        try {
            MorceauRepository morceauRepository = new MorceauRepository(this.conn);
            ArtistRepository artistRepository = new ArtistRepository(this.conn);
            AlbumRepository albumRepository = new AlbumRepository(this.conn);
            GroupRepository groupRepository = new GroupRepository(this.conn);

            SearchResult result = new SearchResult(morceauRepository, artistRepository, albumRepository, groupRepository);
            result.globalSearch(query);
            showSearchView(result, query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}