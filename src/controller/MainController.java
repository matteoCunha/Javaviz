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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MainController {
    Connection conn;
    @FXML private StackPane contentArea; // C'est ici que le contenu change
    @FXML private VBox sideMenu;         // Ton menu de gauche
    @FXML private Label userLabel;       // Pour afficher "Visiteur" ou le Pseudo

    public MainController() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
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

        if (user == null) {
            Label info = new Label("Connectez-vous pour\nvoir vos playlists");
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