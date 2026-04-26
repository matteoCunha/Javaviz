package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import model.music.Album;
import model.music.Artiste;
import model.music.Group;
import model.music.Morceau;
import model.repository.AlbumRepository;
import model.repository.ArtistRepository;
import model.repository.GroupRepository;
import model.repository.MorceauRepository;

public class AddAlbumController {

    private MainController mainController;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private TextArea descriptionArea;

    @FXML private RadioButton radioArtiste;
    @FXML private RadioButton radioGroupe;
    @FXML private ToggleGroup createurToggleGroup;

    @FXML private ComboBox<Artiste> artisteComboBox;
    @FXML private ComboBox<Group> groupeComboBox;
    @FXML private ListView<Morceau> tracksListView;

    @FXML private Label errorLabel;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());

        configurerInterface();
        chargerDonnees();
    }

    private void configurerInterface() {
        createurToggleGroup = new ToggleGroup();
        radioArtiste.setToggleGroup(createurToggleGroup);
        radioGroupe.setToggleGroup(createurToggleGroup);

        datePicker.setValue(LocalDate.now());
        tracksListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        createurToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isArtiste = radioArtiste.isSelected();
            artisteComboBox.setVisible(isArtiste);
            groupeComboBox.setVisible(!isArtiste);
        });
    }

    private void chargerDonnees() {
        try {
            ArtistRepository aRepo = new ArtistRepository(mainController.conn);
            artisteComboBox.getItems().addAll(aRepo.fetchAll());

            GroupRepository gRepo = new GroupRepository(mainController.conn);
            groupeComboBox.getItems().addAll(gRepo.fetchAll());

            MorceauRepository mRepo = new MorceauRepository(mainController.conn);
            tracksListView.getItems().addAll(mRepo.fetchAllMorceaux());

        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur lors du chargement des données.");
        }
    }

    @FXML
    public void handleSave() {
        String name = nameField.getText().trim();
        LocalDate date = datePicker.getValue();
        String description = descriptionArea.getText().trim();

        Artiste artisteSelectionne = radioArtiste.isSelected() ? artisteComboBox.getValue() : null;
        Group groupeSelectionne = radioGroupe.isSelected() ? groupeComboBox.getValue() : null;

        if (name.isEmpty() || date == null) {
            afficherErreur("Le nom et la date sont obligatoires.");
            return;
        }
        if (artisteSelectionne == null && groupeSelectionne == null) {
            afficherErreur("Veuillez sélectionner un artiste ou un groupe.");
            return;
        }

        try {
            MorceauRepository m = new MorceauRepository(mainController.conn);
            Album nouvelAlbum;
            if (artisteSelectionne != null) {
                nouvelAlbum = new Album(0, date, description, name, artisteSelectionne, m);
            } else {
                nouvelAlbum = new Album(0, date, description, name, groupeSelectionne, m);

            }
            List<Morceau> morceauxCoches = tracksListView.getSelectionModel().getSelectedItems();

            AlbumRepository albumRepo = new AlbumRepository(mainController.conn);
            albumRepo.insertAlbum(nouvelAlbum, morceauxCoches);
            mainController.showAdminCatalog();
        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de la sauvegarde de l'album.");
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