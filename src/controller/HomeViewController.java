package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import model.music.Album;
import model.music.Artiste;
import model.music.Morceau;
import model.repository.AlbumRepository;
import model.repository.ArtistRepository;
import model.repository.MorceauRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HomeViewController {
    private Connection conn;
    private MainController mainController;
    @FXML private HBox tracksContainer;
    @FXML private HBox albumsContainer;
    @FXML private HBox artistsContainer;

    public void setMainController(MainController c) { this.mainController = c; }

    @FXML
    public void initialize() {
        tracksContainer.getChildren().clear();
        albumsContainer.getChildren().clear();
        artistsContainer.getChildren().clear();

        for (int i = 1; i <= 3; i++) {
            VBox card = createMusicCard(null);
            albumsContainer.getChildren().add(card);
        }
    }

    public void setConn(Connection c) {
        this.conn = c;
        chargerVraisMorceaux();
        chargerVraiAlbum();
        chargerVraiArtists();
    }

    private void chargerVraisMorceaux() {
        try {
            MorceauRepository m = new MorceauRepository(conn);
            List<Morceau> top5 = m.fetchHomeMorceaux();

            for (Morceau mo : top5) {
                VBox card = createMusicCard(mo);
                tracksContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des morceaux : " + e.getMessage());
        }
    }

    private void chargerVraiAlbum() {
        try {
            albumsContainer.getChildren().clear();
            AlbumRepository a = new AlbumRepository(conn);
            List<Album> top5 = a.fetchHomeAlbum();

            for(Album ab : top5) {
                VBox card = createAlbumCard(ab);
                albumsContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des albums : " + e.getMessage());
        }
    }

    private void chargerVraiArtists() {
        try {
            artistsContainer.getChildren().clear();
            ArtistRepository a = new ArtistRepository(conn);
            List<Artiste> top5 = a.fetchHomeArtists();

            for (Artiste art : top5) {
                VBox card = createArtistCard(art);
                artistsContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des albums : " + e.getMessage());
        }
    }

    private VBox createMusicCard(Morceau morceau) {
        VBox card = new VBox(10);
        card.getStyleClass().add("music-card");
        card.setPrefWidth(150);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #181818; -fx-background-radius: 10;");

        Rectangle cover = new Rectangle(130, 130);
        cover.setArcHeight(15);
        cover.setArcWidth(15);
        cover.setFill(Color.web("#d32f2f"));

        Label lblTitre;
        Label lblArtiste;
        if(morceau != null) {
            lblTitre = new Label(morceau.getTitre());
            lblArtiste = new Label(morceau.getAutorName());

        } else {
            lblTitre = new Label("Inconnu");
            lblArtiste = new Label("Inconnu");
        }

        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        lblArtiste.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");

        card.getChildren().addAll(cover, lblTitre, lblArtiste);
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #282828; -fx-background-radius: 10;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #181818; -fx-background-radius: 10;"));
        card.setUserData(morceau);
        card.setOnMouseClicked(e -> {
            try {
                mainController.lancerMusique((Morceau) card.getUserData());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        return card;
    }

    private VBox createAlbumCard(Album album) {
        VBox card = new VBox(5);
        card.getStyleClass().add("music-card");
        card.setPrefWidth(150);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #181818; -fx-background-radius: 10;");

        Rectangle cover = new Rectangle(130, 130);
        cover.setArcHeight(15);
        cover.setArcWidth(15);
        cover.setFill(Color.web("#d32f2f"));

        Label lblTitre;
        Label lblArtiste;
        if(album != null) {
            lblTitre = new Label(album.getName());
            lblArtiste = new Label(album.getSubtitle());
        } else {
            lblTitre = new Label("Inconnu");
            lblArtiste = new Label("Inconnu");
        }

        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        lblArtiste.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");

        card.getChildren().addAll(cover, lblTitre, lblArtiste);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #282828; -fx-background-radius: 10;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #181818; -fx-background-radius: 10;"));
        card.setUserData(album);
        card.setOnMouseClicked(e -> {
            try {
                mainController.consultAlbum((Album) card.getUserData());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        return card;
    }

    private VBox createArtistCard(Artiste artiste) {
        VBox card = new VBox(10);
        card.getStyleClass().add("music-card");
        card.setPrefWidth(150);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #181818; -fx-background-radius: 10;");

        Rectangle cover = new Rectangle(130, 130);
        cover.setArcHeight(15);
        cover.setArcWidth(15);
        cover.setFill(Color.web("#d32f2f"));

        Label lblTitre;
        Label lblArtiste;
        if(artiste != null) {
            lblTitre = new Label(artiste.getPseudo());
            lblArtiste = new Label(artiste.getDescription());

        } else {
            lblTitre = new Label("Inconnu");
            lblArtiste = new Label("Inconnu");
        }

        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        lblArtiste.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");

        card.getChildren().addAll(cover, lblTitre, lblArtiste);
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #282828; -fx-background-radius: 10;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #181818; -fx-background-radius: 10;"));
        card.setUserData(artiste);
        card.setOnMouseClicked(e -> {
            System.out.println("Lecture de : " + lblTitre.getText());
        });

        return card;
    }
}