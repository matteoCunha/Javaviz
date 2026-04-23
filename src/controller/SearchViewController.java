package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import model.music.*;
import model.repository.SearchResult;

import java.sql.SQLException;
import java.util.List;

public class SearchViewController {

    @FXML private Label searchTitleLabel;
    @FXML private VBox tracksContainer;
    @FXML private FlowPane albumsContainer;
    @FXML private FlowPane artistsContainer;

    private MainController mainController;

    public void setResults(SearchResult results, String query, MainController main) {
        this.mainController = main;
        searchTitleLabel.setText("Résultats pour \"" + query + "\"");

        displayTracks(results.getMorceaux());
        displayAlbums(results.getAlbums());
        displayArtists(results.getArtistes(), results.getGroups());
    }

    private void displayTracks(List<Morceau> morceaux) {
        tracksContainer.getChildren().clear();
        if (morceaux.isEmpty()) {
            tracksContainer.getChildren().add(createEmptyLabel("Aucun morceau trouvé."));
            return;
        }

        int index = 1;
        for (Morceau m : morceaux) {
            tracksContainer.getChildren().add(createSimpleTrackRow(index++, m));
        }
    }

    private void displayAlbums(List<Album> albums) {
        albumsContainer.getChildren().clear();
        if (albums.isEmpty()) {
            albumsContainer.getChildren().add(createEmptyLabel("Aucun album trouvé."));
            return;
        }

        for (Album a : albums) {
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #181818; -fx-padding: 15; -fx-background-radius: 10;");
            Label title = new Label(a.getName());
            title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            card.getChildren().add(title);

            card.setOnMouseClicked(e -> {
                try {
                    mainController.consultAlbum(a);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            albumsContainer.getChildren().add(card);
        }
    }

    private void displayArtists(List<Artiste> artistes, List<Group> groups) {
        artistsContainer.getChildren().clear();

        if(artistes.isEmpty()) {
            artistsContainer.getChildren().add(createEmptyLabel("Aucun artiste trouvé"));
            return;
        }

        for (Artiste a : artistes) {
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #181818; -fx-padding: 15; -fx-background-radius: 10;");
            Label title = new Label(a.getPseudo());
            title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            card.getChildren().add(title);

            card.setOnMouseClicked(e -> {
                mainController.showArtistDetail(a);
            });
            artistsContainer.getChildren().add(card);
        }
        for (Group g : groups) {
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #181818; -fx-padding: 15; -fx-background-radius: 10;");
            Label title = new Label(g.getName());
            title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            card.getChildren().add(title);

            card.setOnMouseClicked(e -> {
                mainController.showGroupDetail(g);
            });
            artistsContainer.getChildren().add(card);
        }
    }

    private HBox createSimpleTrackRow(int index, Morceau m) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-cursor: hand;");

        Label lblIndex = new Label(String.valueOf(index));
        lblIndex.setStyle("-fx-text-fill: #b3b3b3;");
        lblIndex.setPrefWidth(30);

        Label lblTitre = new Label(m.getTitre() + " - " + m.getAutorName());
        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        HBox.setHgrow(lblTitre, Priority.ALWAYS);

        row.getChildren().addAll(lblIndex, lblTitre);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 5;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent;"));

        row.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                try {
                    mainController.lancerMusique(m);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        return row;
    }

    private Label createEmptyLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #b3b3b3; -fx-font-italic: true;");
        return l;
    }
}