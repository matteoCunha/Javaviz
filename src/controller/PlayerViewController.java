package controller;

import com.sun.tools.javac.Main;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Duration;
import model.music.Morceau;
import model.repository.MorceauRepository;
import model.user.Abonne;

import java.sql.SQLException;

public class PlayerViewController {

    @FXML private Label trackTitle;
    @FXML private Label artistName;
    @FXML private Button playPauseBtn;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Slider timeSlider;

    private Morceau currentTrack;
    private boolean isPlaying = false;
    private MainController mainController;

    private Timeline timeLine;
    private int tempEcoule = 0;
    private int tempsTotal = 210;

    public void setMainController(MainController m) {
        this.mainController = m;
    }

    @FXML
    public void initialize() {
        timeLine = new Timeline(new KeyFrame(Duration.seconds(1), event -> avancerTemps()));
        timeLine.setCycleCount(Timeline.INDEFINITE);

        timeSlider.setDisable(true);
    }

    public void jouerMorceau(Morceau morceau) throws SQLException {
        this.currentTrack = morceau;
        this.tempsTotal = morceau.getTime();
        MorceauRepository morceauRepository = new MorceauRepository(mainController.conn);

        trackTitle.setText(morceau.getTitre());
        tempEcoule = 0;

        timeSlider.setMin(0);
        timeSlider.setMax(tempsTotal);
        timeSlider.setValue(0);

        totalTimeLabel.setText(formaterTemps(tempsTotal));
        currentTimeLabel.setText("0:00");

        isPlaying = true;
        playPauseBtn.setText("⏸");
        timeLine.play();

        if(mainController.utilisateur instanceof Abonne) {
            morceauRepository.updateHistorique((Abonne) mainController.utilisateur, morceau);
        }
    }

    @FXML
    public void togglePlayPause() {
        if (currentTrack == null) return;

        if (isPlaying) {
            timeLine.pause();
            playPauseBtn.setText("▶");
            isPlaying = false;
        } else {
            timeLine.play();
            playPauseBtn.setText("⏸");
            isPlaying = true;
        }
    }

    private void avancerTemps() {
        if (tempEcoule >= tempsTotal) {
            timeLine.stop();
            isPlaying = false;
            playPauseBtn.setText("▶");
            return;
        }

        tempEcoule++;

        timeSlider.setValue(tempEcoule);
        currentTimeLabel.setText(formaterTemps(tempEcoule));
    }

    private String formaterTemps(int totalSecondes) {
        int minutes = totalSecondes / 60;
        int secondes = totalSecondes % 60;
        return String.format("%d:%02d", minutes, secondes);
    }
}