package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.SQLException;
import model.repository.StatsRepository;

public class AdminStatsController {

    private MainController mainController;

    @FXML private Label lblUsers;
    @FXML private Label lblEcoutes;
    @FXML private Label lblMorceaux;
    @FXML private Label lblAlbums;
    @FXML private Label lblArtistes;
    @FXML private Label lblGroupes;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        chargerStatistiques();
    }

    @FXML
    public void chargerStatistiques() {
        try {
            StatsRepository statsRepo = new StatsRepository(mainController.conn);

            int users = statsRepo.countAbonnes();
            long ecoutes = statsRepo.countTotalEcoutes();
            int morceaux = statsRepo.countMorceaux();
            int albums = statsRepo.countAlbums();
            int artistes = statsRepo.countArtistes();
            int groupes = statsRepo.countGroupes();

            lblUsers.setText(String.valueOf(users));
            lblEcoutes.setText(formaterNombre(ecoutes));
            lblMorceaux.setText(String.valueOf(morceaux));
            lblAlbums.setText(String.valueOf(albums));
            lblArtistes.setText(String.valueOf(artistes));
            lblGroupes.setText(String.valueOf(groupes));

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des statistiques.");
        }
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