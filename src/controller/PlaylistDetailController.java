package controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.music.Morceau;
import model.music.Playlist;
import model.music.SequenceDeMusique;
import model.repository.MorceauRepository;
import model.repository.PlaylistRepository;

import java.sql.SQLException;
import java.util.List;

public class PlaylistDetailController {

    @FXML private Label statusLabel;
    @FXML private Label playlistTitle;
    @FXML private Label creatorName;
    @FXML private Label trackCount;
    @FXML private VBox trackListContainer;
    @FXML private ComboBox<Morceau> trackComboBox;
    @FXML private Button deleteButton;

    private Playlist currentPlaylist;
    private MainController mainController;

    private PauseTransition autoSaveTimer = new PauseTransition(Duration.seconds(1));

    public void initialize() {
        autoSaveTimer.setOnFinished(event -> {
            try {
                executeUpdateBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setPlaylistData(Playlist playlist, MainController main) throws SQLException {
        this.currentPlaylist = playlist;
        this.mainController = main;

        playlistTitle.setText(playlist.getName());
        statusLabel.setText(playlist.isPublic() ? "PLAYLIST PUBLIQUE" : "PLAYLIST PRIVÉE");
        creatorName.setText("Créé par " + playlist.getCreateur().getName());

        loadTracks();
        chargerCatalogue();
    }

    public void handleDeletePlaylist() throws SQLException {
        MorceauRepository m = new MorceauRepository(mainController.conn);
        PlaylistRepository repository = new PlaylistRepository(mainController.conn, m);

        repository.deletePlaylist(currentPlaylist);
        mainController.updateSideMenu();
        mainController.showHome();
    }

    public void chargerCatalogue() throws SQLException {
        try {
            MorceauRepository morceauRepository = new MorceauRepository(this.mainController.conn);
            List<Morceau> catalogue = morceauRepository.fetchAllMorceaux();

            trackComboBox.getItems().setAll(catalogue);
            trackComboBox.setConverter(new StringConverter<Morceau>() {
                @Override
                public String toString(Morceau morceau) {
                    return (morceau != null) ? morceau.getTitre() + " - " + morceau.getAutorName() : "";
                }

                @Override
                public Morceau fromString(String s) {
                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addSelectedTrack() {
        Morceau choisi = trackComboBox.getValue();

        if (choisi == null) {
            System.out.println("Veuillez choisir un morceau d'abord.");
            return;
        }

        try {
            PlaylistRepository playlistRepository = new PlaylistRepository(this.mainController.conn, new MorceauRepository(this.mainController.conn));
            playlistRepository.ajouterMorceauxInPlaylist(currentPlaylist.getId(), choisi.getId());

            currentPlaylist.getSequence().pushBack(choisi);
            loadTracks();
            trackComboBox.setValue(null);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

        Label lblTitre = new Label(m.getTitre() + " - " + m.getAutorName());
        lblTitre.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        HBox.setHgrow(lblTitre, Priority.ALWAYS);

        HBox actionsBox = new HBox(5);
        actionsBox.setPrefWidth(80);
        actionsBox.setAlignment(Pos.CENTER);

        Button btnUp = new Button("▲");
        Button btnDown = new Button("▼");
        btnUp.setStyle("-fx-background-color: transparent; -fx-text-fill: #b3b3b3; -fx-cursor: hand;");
        btnDown.setStyle("-fx-background-color: transparent; -fx-text-fill: #b3b3b3; -fx-cursor: hand;");

        Button btnDelete = new Button();
        btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        SVGPath trashIcon = new SVGPath();
        trashIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        trashIcon.setFill(javafx.scene.paint.Color.web("#b3b3b3"));

        btnDelete.setGraphic(trashIcon);
        btnDelete.setVisible(false);

        btnDelete.setOnMouseEntered(e -> trashIcon.setFill(javafx.scene.paint.Color.web("#d32f2f")));
        btnDelete.setOnMouseExited(e -> trashIcon.setFill(javafx.scene.paint.Color.web("#b3b3b3")));

        btnUp.setVisible(false);
        btnDown.setVisible(false);

        actionsBox.getChildren().addAll(btnUp, btnDown, btnDelete);
        row.getChildren().addAll(lblIndex, lblTitre, actionsBox);

        btnUp.setOnAction(event -> {
            currentPlaylist.getSequence().moveUp(noeud);
            loadTracks();

            autoSaveTimer.playFromStart();
        });

        btnDown.setOnAction(event -> {
            currentPlaylist.getSequence().moveDown(noeud);
            loadTracks();

            autoSaveTimer.playFromStart();
        });

        btnDelete.setOnAction(event -> {
            currentPlaylist.getSequence().remove(noeud);
            loadTracks();
            autoSaveTimer.playFromStart();
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
            btnDelete.setVisible(true);
        });

        row.setOnMouseExited(e -> {
            row.setStyle("-fx-background-color: transparent;");
            btnUp.setVisible(false);
            btnDown.setVisible(false);
            btnDelete.setVisible(false);
        });

        return row;
    }

    public void executeUpdateBatch() throws SQLException {
        MorceauRepository morceauRepository = new MorceauRepository(mainController.conn);
        PlaylistRepository playlistRepository = new PlaylistRepository(mainController.conn, morceauRepository);

        playlistRepository.updatePlaylist(currentPlaylist);
    }
}