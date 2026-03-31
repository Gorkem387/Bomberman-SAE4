package iut.gon.bomberman.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.net.URL;

public class MainApp extends Application {

    // MENU
    /*
    @Override
    public void start(Stage primaryStage) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/launcher.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Bomberman - Menu Principal");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            System.err.println("Erreur lors du chargement de l'appliaction.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/
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
