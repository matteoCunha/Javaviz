package controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.repository.UserRepository;
import model.user.Abonne;
import model.user.Admin;
import model.user.CompteConnecte;
import model.user.GestionConnexion;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginViewController {
    private Connection conn;
    private MainController mainController;

    @FXML private TextField pseudoField;
    @FXML private PasswordField passwordField;
    @FXML private javafx.scene.control.Label errorLabel;

    @FXML
    public void initialize() {

    }

    public void setMainController(MainController c) { this.mainController = c; }

    public void handleLogin() throws SQLException {
        String pseudoIn = pseudoField.getText();
        String passwordIn = passwordField.getText();

        if (pseudoIn.isEmpty() || passwordIn.isEmpty()) {
            sendError("Veuillez remplir tous les champs");
        }

        System.out.println("Tentative de connexion avec : user = " + pseudoIn + " | password = " + passwordIn);
        UserRepository u = new UserRepository(conn);
        GestionConnexion g = new GestionConnexion(conn, u);
        CompteConnecte user = g.connexion(pseudoIn, passwordIn);

        if (user == null) {
            sendError("Mauvais pseudo ou mot de passe");
        }
        if (user instanceof Abonne || user instanceof Admin) {
            mainController.updateSessionState(user);
            mainController.showHome();
        }

    }

    private void sendError(String str) {
        errorLabel.setText(str);
        errorLabel.setVisible(true);
    }

    public void setConnection(Connection c) {
        this.conn = c;
    }
}
