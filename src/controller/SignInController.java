package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class SignInController {

    @FXML private TextField pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private MainController mainController;
    private Connection conn;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    @FXML
    public void handleSignUp() throws SQLException {
        String pseudo = pseudoField.getText();
        String mdp = passwordField.getText();
        String confirmMdp = confirmPasswordField.getText();

        if (pseudo.isEmpty() || mdp.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs.");
            return;
        }

        if (!mdp.equals(confirmMdp)) {
            afficherErreur("Les mots de passe ne correspondent pas.");
            return;
        }

        UserRepository u = new UserRepository(conn);
        if (u.pseudoExisteDeja(pseudo)) {
            afficherErreur("Ce pseudo est déjà utilisé");
        } else {
            if (u.addNewAbonne(pseudo, mdp)) {
                afficherSucces("Comptre créé, vous pouvez vous connecter.");
            } else {
                afficherErreur("Erreur lors de la création du compte.\nVeuillez réessayer.");
            }
        }
    }

    @FXML
    public void switchToLogin() {
        mainController.showLogin();
    }

    private void afficherErreur(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void afficherSucces(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
    }
}