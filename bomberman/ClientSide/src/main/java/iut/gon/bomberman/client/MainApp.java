package iut.gon.bomberman.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Au démarrage, on lance le launcher (Menu principal)
        Parent root = loadFXML("fxml/launcher");
        scene = new Scene(root, 650, 450);
        stage.setScene(scene);
        stage.setTitle("Bomberman - Menu Principal");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static FXMLLoader setRootAndGetLoader(String fxml) throws IOException {
        URL resource = MainApp.class.getResource("/" + fxml + ".fxml");
        if (resource == null) {
            resource = MainApp.class.getResource(fxml + ".fxml");
        }
        if (resource == null) {
            throw new IOException("Fichier FXML non trouvé : " + fxml);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Parent root = fxmlLoader.load();
        scene.setRoot(root);
        return fxmlLoader;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        URL resource = MainApp.class.getResource("/" + fxml + ".fxml");
        if (resource == null) {
            // Fallback si le chemin commence déjà par /
            resource = MainApp.class.getResource(fxml + ".fxml");
        }
        if (resource == null) {
            throw new IOException("Fichier FXML non trouvé : " + fxml);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
