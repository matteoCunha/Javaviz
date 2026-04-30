package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.music.Morceau;
import model.repository.MorceauRepository;

import java.sql.SQLException;
import java.util.List;

public class AdvancedSearchViewController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreCombo;
    @FXML private ComboBox<String> periodeCombo;
    @FXML private ComboBox<String> triCombo;
    @FXML private VBox tracksContainer;
    @FXML private Label resultCountLabel;

    private MainController mainController;
    private MorceauRepository morceauRepository;

    public void setMainController(MainController mc) {
        this.mainController = mc;
        this.morceauRepository = new MorceauRepository(mc.conn);
        initialiserFiltres();
        lancerRecherche();
    }

    private void initialiserFiltres() {
        try {
            genreCombo.getItems().add("Tous les genres");
            genreCombo.getItems().addAll(morceauRepository.getDistinctGenres());
            genreCombo.setValue("Tous les genres");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        periodeCombo.getItems().addAll("Toutes les époques", "Années 70", "Années 80", "Années 90", "Années 2000+");
        periodeCombo.setValue("Toutes les époques");

        triCombo.getItems().addAll("A-Z", "Plus écoutés", "Plus récents", "Plus anciens");
        triCombo.setValue("A-Z");

        searchField.textProperty().addListener((obs, oldV, newV) -> lancerRecherche());
        genreCombo.valueProperty().addListener((obs, oldV, newV) -> lancerRecherche());
        periodeCombo.valueProperty().addListener((obs, oldV, newV) -> lancerRecherche());
        triCombo.valueProperty().addListener((obs, oldV, newV) -> lancerRecherche());
    }

    @FXML
    private void lancerRecherche() {
        if (morceauRepository == null) return;

        try {
            String recherche = searchField.getText();
            String genre = genreCombo.getValue();
            String periode = periodeCombo.getValue();
            String tri = triCombo.getValue();

            List<Morceau> resultats = morceauRepository.rechercheAvancee(recherche, genre, periode, tri);
            afficherResultats(resultats);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resetFiltres() {
        searchField.clear();
        genreCombo.setValue("Tous les genres");
        periodeCombo.setValue("Toutes les époques");
        triCombo.setValue("A-Z");
    }

    private void afficherResultats(List<Morceau> morceaux) {
        tracksContainer.getChildren().clear();
        resultCountLabel.setText(morceaux.size() + " résultat(s)");

        for (int i = 0; i < morceaux.size(); i++) {
            tracksContainer.getChildren().add(createTrackRow(i + 1, morceaux.get(i)));
        }
    }

    private HBox createTrackRow(int index, Morceau m) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));

        Label lblIndex = new Label(String.valueOf(index));
        lblIndex.setPrefWidth(30);
        lblIndex.setStyle("-fx-text-fill: #b3b3b3;");

        Label lblTitre = new Label(m.getTitre());
        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblDetails = new Label(" • " + m.getGenre() + " • " + formaterTemps(m.getTime()) + " • " + m.getAutorName());
        lblDetails.setStyle("-fx-text-fill: #b3b3b3;");

        HBox titleBox = new HBox(5, lblTitre, lblDetails);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        row.getChildren().addAll(lblIndex, titleBox);

        row.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && mainController != null) {
                try {
                    mainController.lancerMusique(m);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 5;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent;"));

        return row;
    }

    private String formaterTemps(int totalSecondes) {
        int minutes = totalSecondes / 60;
        int secondes = totalSecondes % 60;
        return String.format("%d:%02d", minutes, secondes);
    }
}