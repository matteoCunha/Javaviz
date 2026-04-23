package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.music.*;
import model.repository.AlbumRepository;
import model.repository.ArtistRepository;
import model.repository.MorceauRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GroupViewController {

    @FXML private Label groupNameLabel;
    @FXML private Label listenerLabel;
    @FXML private VBox topTracksContainer;
    @FXML private FlowPane albumsContainer;
    @FXML private Label descriptionLabel;
    @FXML private FlowPane membersContainer;

    private Group currentGroup;
    private MainController mainController;

    public void setArtistData(Group group, MainController main) throws SQLException {
        this.currentGroup = group;
        this.mainController = main;

        groupNameLabel.setText(currentGroup.getName());
        // monthlyListenersLabel.setText(artiste.getFollowers() + " abonnés");

        MorceauRepository morceauRepository = new MorceauRepository(mainController.conn);
        List<Morceau> topTracks = morceauRepository.fetchTop5ByGroup(currentGroup);
        displayTopTracks(topTracks);

        int n = 0;
        for (Morceau m : topTracks) { n = n + m.getNb_ecoutes(); }
        listenerLabel.setText(n + " écoutes");
        descriptionLabel.setText(currentGroup.getDescription());

        AlbumRepository albumRepository = new AlbumRepository(mainController.conn);
        List<Album> albums = albumRepository.fetchByGroup(currentGroup);
        displayAlbums(albums);

        ArtistRepository artistRepository = new ArtistRepository(mainController.conn);
        List<Artiste> members = artistRepository.fetchByGroup(group.getId());
        displayMembers(members);
    }

    public void displayTopTracks(List<Morceau> topTracks) {
        topTracksContainer.getChildren().clear();

        if (topTracks == null || topTracks.isEmpty()) {
            Label emptyInfo = new Label("Aucun titre disponible.");
            emptyInfo.setStyle("-fx-text-fill: #b3b3b3;");
            topTracksContainer.getChildren().add(emptyInfo);
            return;
        }

        int index = 1;
        for (Morceau m : topTracks) {
            topTracksContainer.getChildren().add(createTrackRow(index++, m));
        }
    }

    private HBox createTrackRow(int index, Morceau m) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));

        Label lblIndex = new Label(String.valueOf(index));
        lblIndex.setStyle("-fx-text-fill: #b3b3b3;");
        lblIndex.setPrefWidth(30);

        Label lblTitre = new Label(m.getTitre());
        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        HBox.setHgrow(lblTitre, Priority.ALWAYS);

        row.getChildren().addAll(lblIndex, lblTitre);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 5;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent;"));

        row.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && mainController != null) {
                try {
                    mainController.lancerMusique(m);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        return row;
    }

    public void displayAlbums(List<Album> albums) {
        albumsContainer.getChildren().clear();
        if (albums == null || albums.isEmpty()) {
            Label emptyInfo = new Label("Aucun album disponible.");
            emptyInfo.setStyle("-fx-text-fill: #b3b3b3;");
            albumsContainer.getChildren().add(emptyInfo);
            return;
        }

        for (Album a : albums) {
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #181818; -fx-padding: 15; -fx-background-radius: 10; -fx-cursor: hand;");
            card.setPrefWidth(150);

            javafx.scene.shape.Rectangle cover = new javafx.scene.shape.Rectangle(120, 120);
            cover.setArcWidth(10); cover.setArcHeight(10);
            cover.setFill(javafx.scene.paint.Color.web("#d32f2f"));

            Label title = new Label(a.getName());
            title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Label year = new Label(a.getDateCreation().toString()); // Ou a.getYear()
            year.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12;");

            card.getChildren().addAll(cover, title, year);

            card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #282828; -fx-padding: 15; -fx-background-radius: 10; -fx-cursor: hand;"));
            card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #181818; -fx-padding: 15; -fx-background-radius: 10; -fx-cursor: hand;"));

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

    public void displayMembers(List<Artiste> membres) {
        membersContainer.getChildren().clear();

        if (membres == null || membres.isEmpty()) {
            Label emptyInfo = new Label("Aucun membre renseigné.");
            emptyInfo.setStyle("-fx-text-fill: #b3b3b3; -fx-font-style: italic;");
            membersContainer.getChildren().add(emptyInfo);
            return;
        }

        for (Artiste membre : membres) {
            VBox memberCard = new VBox(10);
            memberCard.setAlignment(Pos.CENTER);
            memberCard.setStyle("-fx-cursor: hand;");

            Circle avatar = new Circle(40);
            avatar.setFill(Color.web("#282828"));

            Label nameLabel = new Label(membre.getPseudo());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            memberCard.getChildren().addAll(avatar, nameLabel);

            memberCard.setOnMouseEntered(e -> avatar.setFill(Color.web("#d32f2f"))); // Passe en rouge
            memberCard.setOnMouseExited(e -> avatar.setFill(Color.web("#282828")));  // Revient en gris

            memberCard.setOnMouseClicked(e -> {
                if (mainController != null) {
                    mainController.showArtistDetail(membre);
                }
            });

            membersContainer.getChildren().add(memberCard);
        }
    }
}