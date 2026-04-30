package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Duration;
import model.music.Morceau;
import model.music.SequenceDeMusique;
import model.repository.MorceauRepository;
import model.user.Abonne;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

import java.sql.SQLException;

public class PlayerViewController {

    @FXML private Label trackTitle;
    @FXML private Label artistName;
    @FXML private Button playPauseBtn;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Slider timeSlider;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Slider volumeSlider;

    private MediaPlayer mediaPlayer;

    private Morceau currentTrack;
    private SequenceDeMusique.Node  currentPlayingNode = null;
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
        actualiserBoutons();

        volumeSlider.setMin(0);
        volumeSlider.setMax(100);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue() / 100.0);
            }
        });
    }

    public void jouerMorceau(Morceau morceau) throws SQLException {
        this.currentPlayingNode = null;
        actualiserBoutons();
        demarrerLecture(morceau);
    }

    public void jouerDepuisPlaylist(SequenceDeMusique.Node noeud) throws SQLException {
        this.currentPlayingNode = noeud;
        actualiserBoutons();
        demarrerLecture(noeud.getMorceaux());
    }

    private void demarrerLecture(Morceau morceau) throws SQLException {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        try {
            String path = "src/music.mp3";
            File audioFile = new File(path);
            if (audioFile.exists()) {
                Media media = new Media(audioFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                mediaPlayer.play();
            } else {
                System.out.println("Fichier audio introuvable !");
            }
        } catch (Exception e) {
            System.out.println("Erreur de lecture audio : " + e.getMessage());
        }

        this.currentTrack = morceau;
        this.tempsTotal = morceau.getTime();
        MorceauRepository morceauRepository = new MorceauRepository(mainController.conn);

        trackTitle.setText(morceau.getTitre());
        artistName.setText(morceau.getAutorName());
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

    private void actualiserBoutons() {
        boolean estPlaylist = (currentPlayingNode != null);

        boolean hasPrev = estPlaylist && currentPlayingNode.getPrev() != null;
        boolean hasNext = estPlaylist && currentPlayingNode.getNext() != null;

        btnPrev.setDisable(!hasPrev);
        btnNext.setDisable(!hasNext);

        btnPrev.setOpacity(hasPrev ? 1.0 : 0.3);
        btnNext.setOpacity(hasNext ? 1.0 : 0.3);
    }

    @FXML
    public void jouerPrecedent() throws SQLException {
        if (currentPlayingNode != null && currentPlayingNode.getPrev() != null) {
            jouerDepuisPlaylist(currentPlayingNode.getPrev());
        }
    }

    @FXML
    public void jouerSuivant() throws SQLException {
        if (currentPlayingNode != null && currentPlayingNode.getNext() != null) {
            jouerDepuisPlaylist(currentPlayingNode.getNext());
        }
    }

    @FXML
    public void togglePlayPause() {
        if (currentTrack == null) return;

        if (isPlaying) {
            timeLine.pause();
            if (mediaPlayer != null) mediaPlayer.pause();
            playPauseBtn.setText("▶");
            isPlaying = false;
        } else {
            timeLine.play();
            if (mediaPlayer != null) mediaPlayer.play();
            playPauseBtn.setText("⏸");
            isPlaying = true;
        }
    }

    private void avancerTemps() {
        if (tempEcoule >= tempsTotal) {
            timeLine.stop();
            if (mediaPlayer != null) mediaPlayer.stop();

            if (currentPlayingNode != null && currentPlayingNode.getNext() != null) {
                try {
                    jouerSuivant();
                } catch (SQLException e) { e.printStackTrace(); }
            } else {
                isPlaying = false;
                playPauseBtn.setText("▶");
            }
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