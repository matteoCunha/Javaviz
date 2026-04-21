package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;

public class HomeViewController {

    @FXML private HBox tracksContainer;
    @FXML private HBox albumsContainer;

    @FXML
    public void initialize() {
        for (int i = 1; i <= 5; i++) {
            VBox card = createMusicCard("Morceau " + i, "Artiste Inconnu");
            tracksContainer.getChildren().add(card);
        }

        for (int i = 1; i <= 3; i++) {
            VBox card = createMusicCard("Album " + i, "Genre Rouge");
            albumsContainer.getChildren().add(card);
        }
    }

    private VBox createMusicCard(String titre, String artiste) {
        VBox card = new VBox(10);
        card.getStyleClass().add("music-card");
        card.setPrefWidth(150);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #181818; -fx-background-radius: 10;");

        Rectangle cover = new Rectangle(130, 130);
        cover.setArcHeight(15);
        cover.setArcWidth(15);
        cover.setFill(Color.web("#d32f2f"));

        Label lblTitre = new Label(titre);
        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblArtiste = new Label(artiste);
        lblArtiste.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");

        card.getChildren().addAll(cover, lblTitre, lblArtiste);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #282828; -fx-background-radius: 10;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #181818; -fx-background-radius: 10;"));

        card.setOnMouseClicked(e -> {
            System.out.println("Lecture de : " + titre);
        });

        return card;
    }
}