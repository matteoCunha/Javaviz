package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import model.music.Morceau;
import model.music.Artiste;
import model.repository.MorceauRepository;
import model.repository.ArtistRepository;

public class AddMorceauController {

    private MainController mainController;

    @FXML private TextField titreField;
    @FXML private TextField genreField;
    @FXML private TextField dureeField;
    @FXML private ComboBox<Artiste> artisteComboBox;
    @FXML private Label errorLabel;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
        chargerArtistes();
    }

    private void chargerArtistes() {
        try {
            ArtistRepository aRepo = new ArtistRepository(mainController.conn);
            List<Artiste> listeArtistes = aRepo.fetchAll();

            artisteComboBox.getItems().addAll(listeArtistes);

        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Impossible de charger la liste des artistes depuis la base.");
        }
    }

    @FXML
    public void handleSave() {
        String titre = titreField.getText().trim();
        String genre = genreField.getText().trim();
        String dureeStr = dureeField.getText().trim();

        Artiste artisteSelectionne = artisteComboBox.getValue();

        if (titre.isEmpty() || genre.isEmpty() || dureeStr.isEmpty() || artisteSelectionne == null) {
            afficherErreur("Veuillez remplir tous les champs et sélectionner un artiste.");
            return;
        }

        int duree;
        try {
            duree = Integer.parseInt(dureeStr);
            if (duree <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            afficherErreur("La durée doit être un nombre entier positif.");
            return;
        }

        try {
            MorceauRepository mRepo = new MorceauRepository(mainController.conn);
            Morceau newMorceau = new Morceau(0, LocalDate.now(), artisteSelectionne, duree, titre, genre, 1, 0);

            mRepo.insertMorceau(newMorceau);
            mainController.showAdminCatalog();

        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de l'enregistrement en base de données.");
        }
    }

    @FXML
    public void handleCancel() {
        mainController.showAdminCatalog();
    }

    private void afficherErreur(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}