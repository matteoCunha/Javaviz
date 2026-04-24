package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.music.Playlist;
import model.music.SequenceDeMusique;
import model.repository.MorceauRepository;
import model.user.Abonne;
import model.repository.PlaylistRepository;

public class CreatePlaylistController {

    @FXML private TextField nameField;
    @FXML private CheckBox publicCheckBox;
    @FXML private Label errorLabel;

    private MainController mainController;

    public void setMainController(MainController main) {
        this.mainController = main;
    }

    @FXML
    public void createPlaylist() {
        String nom = nameField.getText().trim();

        if (nom.isEmpty()) {
            errorLabel.setVisible(true);
            nameField.setStyle("-fx-border-color: #d32f2f; -fx-border-radius: 5; -fx-background-color: #282828; -fx-text-fill: white;");
            return;
        }

        errorLabel.setVisible(false);
        nameField.setStyle("-fx-background-color: #282828; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5; -fx-font-size: 16;");

        Abonne createur = (Abonne) mainController.utilisateur;

        boolean isPublic = publicCheckBox.isSelected();

        try {
            MorceauRepository morceauRepository = new MorceauRepository(mainController.conn);
            PlaylistRepository playlistRepository = new PlaylistRepository(mainController.conn, morceauRepository);
            Playlist nouvellePlaylist = new Playlist(0, nom, isPublic, createur, new SequenceDeMusique());
            nouvellePlaylist = playlistRepository.insertNewPlaylist(nouvellePlaylist);
            System.out.println("Playlist créée en BDD : " + nom);

            mainController.updateSideMenu();
            mainController.showPlaylistDetail(nouvellePlaylist);

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors de la création.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void cancelCreation() {
        if (mainController != null) {
            mainController.showHome();
        }
    }
}