package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import java.sql.SQLException;
import java.util.List;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import model.music.Album;
import model.music.Artiste;
import model.music.Group;
import model.music.Morceau;
import model.repository.*;

public class AdvancedStatsController {

    private MainController mainController;
    private StatsRepository statsRepo;

    @FXML private BarChart<String, Number> morceauxChart;
    @FXML private PieChart artistesChart;

    @FXML private ComboBox<Morceau> comboMorceau;
    @FXML private ComboBox<Album> comboAlbum;
    @FXML private ComboBox<Artiste> comboArtiste;
    @FXML private ComboBox<Group> comboGroupe;
    @FXML private Label resMorceau, resAlbum, resArtiste, resGroupe;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.statsRepo = new StatsRepository(this.mainController.conn);
        chargerGraphiques();
        chargerListes();
        configurerListeners();
    }

    private void chargerListes() {
        try {
            comboMorceau.setItems(FXCollections.observableArrayList(new MorceauRepository(mainController.conn).fetchAllMorceaux()));
            comboAlbum.setItems(FXCollections.observableArrayList(new AlbumRepository(mainController.conn).fetchAll()));
            comboArtiste.setItems(FXCollections.observableArrayList(new ArtistRepository(mainController.conn).fetchAll()));
            comboGroupe.setItems(FXCollections.observableArrayList(new GroupRepository(mainController.conn).fetchAll()));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void chargerGraphiques() {
        try {
            StatsRepository repo = new StatsRepository(mainController.conn);

            List<StatsRepository.TopItem> topMorceaux = repo.getTopMorceaux();
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            for (StatsRepository.TopItem item : topMorceaux) {
                series.getData().add(new XYChart.Data<>(item.getNom(), item.getValeur()));
            }
            morceauxChart.getData().clear();
            morceauxChart.getData().add(series);

            List<StatsRepository.TopItem> topCreateurs = repo.getTopCreateurs();
            artistesChart.getData().clear();

            for (StatsRepository.TopItem item : topCreateurs) {
                artistesChart.getData().add(new PieChart.Data(item.getNom(), item.getValeur()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Impossible de charger les statistiques évoluées.");
        }
    }

    private void configurerListeners() {
        comboMorceau.setOnAction(e -> {
            Morceau m = comboMorceau.getValue();
            if(m != null) {
                try { resMorceau.setText(formaterNombre(statsRepo.getEcoutesMorceau(m.getId())) + " écoutes"); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        comboAlbum.setOnAction(e -> {
            Album a = comboAlbum.getValue();
            if(a != null) {
                try { resAlbum.setText(formaterNombre(statsRepo.getEcoutesAlbum(a.getId())) + " écoutes"); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        comboArtiste.setOnAction(e -> {
            Artiste art = comboArtiste.getValue();
            if(art != null) {
                try { resArtiste.setText(formaterNombre(statsRepo.getEcoutesArtiste(art.getId())) + " écoutes"); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        comboGroupe.setOnAction(e -> {
            Group g = comboGroupe.getValue();
            if(g != null) {
                try { resGroupe.setText(formaterNombre(statsRepo.getEcoutesGroupe(g.getId())) + " écoutes"); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        });
    }

    private String formaterNombre(long nombre) {
        if (nombre >= 1_000_000_000) {
            return String.format("%.1f Md", nombre / 1_000_000_000.0); // Milliards
        } else if (nombre >= 1_000_000) {
            return String.format("%.1f M", nombre / 1_000_000.0);       // Millions
        } else if (nombre >= 1_000) {
            return String.format("%.1f k", nombre / 1_000.0);           // Milliers
        } else {
            return String.valueOf(nombre);                              // Moins de 1000
        }
    }

}