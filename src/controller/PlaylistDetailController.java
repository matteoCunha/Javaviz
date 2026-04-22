package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.music.Morceau;
import model.music.Playlist;
import model.music.SequenceDeMusique;

import java.sql.SQLException;

public class PlaylistDetailController {

    @FXML private Label statusLabel;
    @FXML private Label playlistTitle;
    @FXML private Label creatorName;
    @FXML private Label trackCount;
    @FXML private VBox trackListContainer;

    private Playlist currentPlaylist;
    private MainController mainController;

    public void setPlaylistData(Playlist playlist, MainController main) {
        this.currentPlaylist = playlist;
        this.mainController = main;

        playlistTitle.setText(playlist.getName());
        statusLabel.setText(playlist.isPublic() ? "PLAYLIST PUBLIQUE" : "PLAYLIST PRIVÉE");

        loadTracks();
    }

    private void loadTracks() {
        trackListContainer.getChildren().clear();

        if (currentPlaylist.getSequence() == null || currentPlaylist.getSequence().getHead() == null) {
            trackCount.setText("0 titre");
            return;
        }

        var noeudCourant = currentPlaylist.getSequence().getHead();
        int index = 1;

        while (noeudCourant != null) {
            trackListContainer.getChildren().add(createTrackRow(index, noeudCourant.getNode()));

            noeudCourant = currentPlaylist.getSequence().passToNext(noeudCourant);
            index++;
        }

        trackCount.setText((index - 1) + " titres");
    }

    private HBox createTrackRow(int index, SequenceDeMusique.Node noeud) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));

        Morceau m = noeud.getMorceaux();

        Label lblIndex = new Label(String.valueOf(index));
        lblIndex.setPrefWidth(30);
        lblIndex.setStyle("-fx-text-fill: #b3b3b3;");

        Label lblTitre = new Label(m.getTitre());
        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        HBox.setHgrow(lblTitre, Priority.ALWAYS);

        HBox actionsBox = new HBox(5);
        actionsBox.setPrefWidth(80);
        actionsBox.setAlignment(Pos.CENTER);

        Button btnUp = new Button("▲");
        Button btnDown = new Button("▼");
        btnUp.setStyle("-fx-background-color: transparent; -fx-text-fill: #b3b3b3; -fx-cursor: hand;");
        btnDown.setStyle("-fx-background-color: transparent; -fx-text-fill: #b3b3b3; -fx-cursor: hand;");

        btnUp.setVisible(false);
        btnDown.setVisible(false);

        actionsBox.getChildren().addAll(btnUp, btnDown);
        row.getChildren().addAll(lblIndex, lblTitre, actionsBox);

        btnUp.setOnAction(event -> {
            currentPlaylist.getSequence().moveUp(noeud);
            loadTracks();
        });

        btnDown.setOnAction(event -> {
            currentPlaylist.getSequence().moveDown(noeud);
            loadTracks();
        });

        row.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && mainController != null) {
                try {
                    mainController.lancerMusique(m);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        row.setOnMouseEntered(e -> {
            row.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 5;");
            btnUp.setVisible(true);
            btnDown.setVisible(true);
        });
        row.setOnMouseExited(e -> {
            row.setStyle("-fx-background-color: transparent;");
            btnUp.setVisible(false);
            btnDown.setVisible(false);
        });

        return row;
    }
}