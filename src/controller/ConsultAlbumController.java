package controller;

import com.sun.tools.javac.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.music.Album;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javafx.fxml.FXML;
import model.music.Morceau;


public class ConsultAlbumController {
    @FXML private Label albumTitle, artistName, albumYear, trackCount;
    @FXML private VBox trackListContainer;
    private Connection conn;
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setAlbumData(Album album, Connection connection) {
        this.conn = connection;

        albumTitle.setText(album.getName());
        artistName.setText(album.getArtistName());
        albumYear.setText(String.valueOf(album.getDateCreation()));

        loadTracks(album);
    }

    private void loadTracks(Album album) {
        trackListContainer.getChildren().clear();

        List<Morceau> morceaux = album.getMorceauList();

        int index = 1;
        for (Morceau m : morceaux) {
            trackListContainer.getChildren().add(createTrackRow(index++, m));
        }
        trackCount.setText(morceaux.size() + " titres");
    }

    private HBox createTrackRow(int index, Morceau m) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.getStyleClass().add("track-row");

        Label lblIndex = new Label(String.valueOf(index));
        lblIndex.setPrefWidth(30);
        lblIndex.setStyle("-fx-text-fill: #b3b3b3;");

        VBox infoTitre = new VBox(2);
        Label lblTitre = new Label(m.getTitre());
        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        infoTitre.getChildren().add(lblTitre);

        int time = m.getTime();
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        Label lblDuration = new Label(String.format("%02d:%02d", minutes, seconds));
        lblDuration.setPrefWidth(80);
        lblDuration.setAlignment(Pos.CENTER_RIGHT);
        lblDuration.setStyle("-fx-text-fill: #b3b3b3;");

        HBox.setHgrow(infoTitre, Priority.ALWAYS);
        row.getChildren().addAll(lblIndex, infoTitre, lblDuration);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #282828; -fx-background-radius: 5;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent;"));
        row.setOnMouseClicked(e -> {
            try {
                mainController.lancerMusique(m);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        return row;
    }
}