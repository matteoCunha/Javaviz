package controller;

import com.sun.tools.javac.Main;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import model.repository.MorceauRepository;
import model.repository.UserRepository;
import model.user.Abonne;



import java.sql.SQLException;

public class AdminUserController {
    private MainController mainController;

    @FXML private TableView<Abonne> userTable;
    @FXML private TableColumn<Abonne, Long> colId;
    @FXML private TableColumn<Abonne, String> colPseudo;
    @FXML private TableColumn<Abonne, String> colPassword;
    @FXML private TableColumn<Abonne, Void> colActions;


    public void setMainController(MainController main) {
        this.mainController = main;
        configurerColonnes();
        chargerAbonne();
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPseudo.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));

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
                    Abonne a = getTableView().getItems().get(getIndex());
                    try {
                        supprimerAbonne(a);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    private void chargerAbonne() {
        try {
            UserRepository u = new UserRepository(this.mainController.conn);
            userTable.setItems(FXCollections.observableArrayList(u.fetchAllAbonne()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void supprimerAbonne(Abonne a) throws SQLException {
        try {
            new UserRepository(this.mainController.conn).deleteAbonne(a);
            chargerAbonne();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
