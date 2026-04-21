package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {

    // On déclare les éléments définis dans le FXML
    @FXML private StackPane contentArea; // C'est ici que le contenu change
    @FXML private VBox sideMenu;         // Ton menu de gauche
    @FXML private Label userLabel;       // Pour afficher "Visiteur" ou le Pseudo

    @FXML
    public void initialize() {
        // Au démarrage, on affiche la page d'accueil par défaut
        showHome();

        // Exemple : on initialise le menu de gauche en mode visiteur
        updateSideMenu(null);
    }

    @FXML
    public void showHome() {
        loadView("HomeView.fxml");
    }

    // Fonction générique pour changer de page
    public void loadView(String fxmlFile) {
        try {
            // Attention au chemin : "/vue/" doit correspondre à ton dossier resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/" + fxmlFile));
            Node view = loader.load();

            // On vide le centre et on met la nouvelle vue
            contentArea.getChildren().setAll(view);

            // Si on vient de charger HomeView, on peut remplir les morceaux
            if (fxmlFile.equals("HomeView.fxml")) {
                // Ici, on pourrait appeler une fonction pour remplir les HBox de HomeView
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + fxmlFile);
            e.printStackTrace();
        }
    }

    // Logique pour adapter le menu latéral
    public void updateSideMenu(Object user) {
        sideMenu.getChildren().clear(); // On vide tout

        if (user == null) {
            Label info = new Label("Connectez-vous pour\nvoir vos playlists");
            info.setStyle("-fx-text-fill: #b3b3b3; -fx-padding: 10;");
            sideMenu.getChildren().add(info);
        } else {
            // Ici tu boucleras sur les playlists de l'abonné
            Label titre = new Label("VOS PLAYLISTS");
            titre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            sideMenu.getChildren().add(titre);
            // sideMenu.getChildren().add(new Button("Ma Playlist #1"));
        }
    }
}