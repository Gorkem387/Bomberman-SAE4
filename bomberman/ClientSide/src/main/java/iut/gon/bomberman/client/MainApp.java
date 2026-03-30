package iut.gon.bomberman.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL fxmlLocation = getClass().getResource("/iut/gon/bomberman/client/game-view.fxml");
        // Debug car le chemin ne fonctionné pas
        if (fxmlLocation == null) {
            System.err.println("ERREUR : Le fichier FXML n'a pas été trouvé dans les ressources !");
            System.err.println("Chemin testé : /iut/gon/bomberman/client/game-view.fxml");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Bomberman IUT");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
