package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainGUI extends Application {

    @Override
    public void start(Stage fenetrePrincipale) {
        Button btn = new Button("Lancer ma musique !");
        StackPane racine = new StackPane();
        racine.getChildren().add(btn);

        Scene scene = new Scene(racine, 400, 300);

        fenetrePrincipale.setTitle("Mon Lecteur Audio");
        fenetrePrincipale.setScene(scene);
        fenetrePrincipale.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}