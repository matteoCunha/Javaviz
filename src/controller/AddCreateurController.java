package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.time.LocalDate;

import model.music.Artiste;
import model.music.Group;
import model.repository.ArtistRepository;
import model.repository.GroupRepository;

public class AddCreateurController {

    private MainController mainController;

    @FXML private RadioButton radioArtiste;
    @FXML private RadioButton radioGroupe;
    @FXML private ToggleGroup createurToggleGroup;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Group> groupeComboBox;

    @FXML private Label nomLabel;
    @FXML private Label dateLabel;
    @FXML private Label groupeLabel;
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

        createurToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isArtiste = radioArtiste.isSelected();

            groupeComboBox.setVisible(isArtiste);
            groupeLabel.setVisible(isArtiste);

            if (isArtiste) {
                nomLabel.setText("Pseudo :");
                dateLabel.setText("Date de naissance :");
            } else {
                nomLabel.setText("Nom du groupe :");
                dateLabel.setText("Date de création :");
            }
        });
    }

    private void chargerDonnees() {
        try {
            GroupRepository gRepo = new GroupRepository(mainController.conn);
            groupeComboBox.getItems().addAll(gRepo.fetchAll());
        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur lors du chargement des groupes.");
        }
    }

    @FXML
    public void handleSave() {
        String name = nameField.getText().trim();
        LocalDate date = datePicker.getValue();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || date == null) {
            afficherErreur("Le nom/pseudo et la date sont obligatoires.");
            return;
        }

        try {
            if (radioArtiste.isSelected()) {
                Artiste nouvelArtiste = new Artiste(0, name, description, date);
                ArtistRepository aRepo = new ArtistRepository(mainController.conn);
                aRepo.insertArtiste(nouvelArtiste);

            } else {
                Group nouveauGroupe = new Group(0, date, description, name);
                GroupRepository gRepo = new GroupRepository(mainController.conn);
                gRepo.insertGroup(nouveauGroupe);
            }

            mainController.showAdminCatalog();

        } catch (SQLException e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de la sauvegarde dans la base de données.");
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