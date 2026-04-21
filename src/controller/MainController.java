package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.repository.DatabaseConnection;
import model.user.User;
import model.user.Visiteur;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MainController {
    Connection conn;
    User utilisateur; // utilisation si besoin utilisation du polymorphisme pour le passer en Abonne, Visiteur ou admin
    @FXML private StackPane contentArea;
    @FXML private VBox sideMenu;
    @FXML private Label userLabel;
    @FXML private HBox authContainer;
    @FXML private HBox userProfileContainer;

    public MainController() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
        utilisateur = new Visiteur();
    }

    public void updateSessionState(Object user) {
        if (user == null) {
            authContainer.setVisible(true);
            userProfileContainer.setVisible(false);

            updateSideMenu(null);
        } else {
            // Mode Connecté
            authContainer.setVisible(false);
            userProfileContainer.setVisible(true);

            userLabel.setText("Abonné Connecté");

            updateSideMenu(user);
        }
    }

    @FXML
    public void initialize() {
        showHome();
        updateSideMenu(null);
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
        System.out.println("Ouvrir la page d'inscription...");
    }

    @FXML
    public void logout() {
        updateSessionState(null);
        showHome();
    }

    public void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/" + fxmlFile));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);

            if (fxmlFile.equals("HomeView.fxml")) {
                HomeViewController controller = loader.getController();
                controller.setConn(conn);

            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + fxmlFile);
            e.printStackTrace();
        }
    }

    public void updateSideMenu(Object user) {
        sideMenu.getChildren().clear();

        if (utilisateur instanceof Visiteur) {
            Label info = new Label("S'abonner/Se connecter pour\nvoir vos playlists");
            info.setStyle("-fx-text-fill: #b3b3b3; -fx-padding: 10;");
            sideMenu.getChildren().add(info);
        } else {
            Label titre = new Label("VOS PLAYLISTS");
            titre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            sideMenu.getChildren().add(titre);
            //sideMenu.getChildren().add(new Button("Ma Playlist #1"));
        }
    }
}