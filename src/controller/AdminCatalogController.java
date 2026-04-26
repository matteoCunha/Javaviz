package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import java.sql.SQLException;

import model.music.Album;
import model.music.Artiste;
import model.music.Group;
import model.music.Morceau;
import model.repository.AlbumRepository;
import model.repository.ArtistRepository;
import model.repository.GroupRepository;
import model.repository.MorceauRepository;

public class AdminCatalogController {

    private MainController mainController;

    @FXML private TableView<Morceau> morceauxTable;
    @FXML private TableColumn<Morceau, Long> colId;
    @FXML private TableColumn<Morceau, String> colTitre;
    @FXML private TableColumn<Morceau, String> colGenre;
    @FXML private TableColumn<Morceau, Integer> colTemps;
    @FXML private TableColumn<Morceau, Void> colActions;

    @FXML private TableView<Album> albumTable;
    @FXML private TableColumn<Album, Long> colAlbumId;
    @FXML private TableColumn<Album, String> colAlbumName;
    @FXML private TableColumn<Album, java.time.LocalDate> colAlbumDate;
    @FXML private TableColumn<Album, String> colAlbumCreateur;
    @FXML private TableColumn<Album, Void> colAlbumActions;

    @FXML private TableView<CreateurItem> createursTable;
    @FXML private TableColumn<CreateurItem, Long> colCreateurId;
    @FXML private TableColumn<CreateurItem, String> colCreateurNom;
    @FXML private TableColumn<CreateurItem, String> colCreateurType;
    @FXML private TableColumn<CreateurItem, Void> colCreateurActions;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        configurerColonnes();
        configurerColonnesAlbums();
        configurerColonnesCreateurs();
        chargerMorceaux();
        chargerAlbums();
        chargerCreateurs();
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colTemps.setCellValueFactory(new PropertyValueFactory<>("temps"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button();
            private final SVGPath trashIcon = new SVGPath();
            {
                trashIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
                trashIcon.setFill(Color.web("#b3b3b3"));
                btnDelete.setGraphic(trashIcon);
                btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                btnDelete.setOnMouseEntered(e -> trashIcon.setFill(Color.web("#d32f2f")));
                btnDelete.setOnMouseExited(e -> trashIcon.setFill(Color.web("#b3b3b3")));
                btnDelete.setOnAction(event -> {
                    Morceau m = getTableView().getItems().get(getIndex());
                    supprimerMorceau(m);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    private void configurerColonnesAlbums() {
        colAlbumId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAlbumName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAlbumDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        colAlbumCreateur.setCellValueFactory(cellData -> {
            Album album = cellData.getValue();
            if (album.getArtiste() != null) {
                return new SimpleStringProperty("👤 " + album.getArtiste().toString());
            } else if (album.getGroup() != null) {
                return new SimpleStringProperty("👥 " + album.getGroup().toString());
            }
            return new SimpleStringProperty("Inconnu");
        });

        colAlbumActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button();
            private final SVGPath trashIcon = new SVGPath();
            {
                trashIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
                trashIcon.setFill(Color.web("#b3b3b3"));
                btnDelete.setGraphic(trashIcon);
                btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                btnDelete.setOnMouseEntered(e -> trashIcon.setFill(Color.web("#d32f2f")));
                btnDelete.setOnMouseExited(e -> trashIcon.setFill(Color.web("#b3b3b3")));
                btnDelete.setOnAction(event -> {
                    Album album = getTableView().getItems().get(getIndex());
                    supprimerAlbum(album);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    private void configurerColonnesCreateurs() {
        colCreateurId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCreateurNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCreateurType.setCellValueFactory(new PropertyValueFactory<>("type"));

        colCreateurActions.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();
            private final SVGPath icon = new SVGPath();
            {
                icon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
                icon.setFill(Color.web("#b3b3b3"));
                btn.setGraphic(icon);
                btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                btn.setOnAction(e -> supprimerCreateur(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void chargerCreateurs() {
        try {
            ObservableList<CreateurItem> listeAffichage = FXCollections.observableArrayList();

            ArtistRepository aRepo = new ArtistRepository(mainController.conn);
            for (Artiste a : aRepo.fetchAll()) {
                listeAffichage.add(new CreateurItem(a.getId(), a.getPseudo(), "Artiste", a));
            }

            GroupRepository gRepo = new GroupRepository(mainController.conn);
            for (Group g : gRepo.fetchAll()) {
                listeAffichage.add(new CreateurItem(g.getId(), g.getName(), "Groupe", g));
            }

            createursTable.setItems(listeAffichage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void supprimerCreateur(CreateurItem item) {
        try {
            if (item.getType().equals("Artiste")) {
                ArtistRepository aRepo = new ArtistRepository(mainController.conn);
                aRepo.deleteArtiste((Artiste) item.getVraiObjet());
            } else {
                GroupRepository gRepo = new GroupRepository(mainController.conn);
                gRepo.deleteGroupe((Group) item.getVraiObjet());
            }
            chargerCreateurs();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Impossible de supprimer. Ce créateur est sûrement lié à des albums ou morceaux !");
        }
    }

    @FXML public void showAddCreateurForm() {
        mainController.showAddCreateurView();
    }

    private void chargerMorceaux() {
        try {
            MorceauRepository mRepo = new MorceauRepository(mainController.conn);
            morceauxTable.setItems(FXCollections.observableArrayList(mRepo.fetchAllMorceaux()));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void chargerAlbums() {
        try {
            AlbumRepository aRepo = new AlbumRepository(mainController.conn);
            albumTable.setItems(FXCollections.observableArrayList(aRepo.fetchAll()));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void supprimerMorceau(Morceau m) {
        try {
            new MorceauRepository(mainController.conn).deleteMorceau(m);
            chargerMorceaux();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void supprimerAlbum(Album a) {
        try {
            AlbumRepository albumRepository = new AlbumRepository(mainController.conn);
            albumRepository.deleteAlbum(a);
            chargerAlbums();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML public void showAddArtisteForm() { mainController.showAddCreateurView(); }
    @FXML public void showAddAlbumForm() { mainController.showAddAlbumView(); }
    @FXML public void showAddMorceauForm() { mainController.showAdminAddMorceau(); }


    public static class CreateurItem {
        private final long id;
        private final String nom;
        private final String type;
        private final Object vraiObjet;

        public CreateurItem(long id, String nom, String type, Object vraiObjet) {
            this.id = id;
            this.nom = nom;
            this.type = type;
            this.vraiObjet = vraiObjet;
        }

        public long getId() { return id; }
        public String getNom() { return nom; }
        public String getType() { return type; }
        public Object getVraiObjet() { return vraiObjet; }
    }
}