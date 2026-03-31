package iut.gon.bomberman.client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LauncherController {

    // Pour l'instant handleLocalGame et handleOnlineGame lance directement la partie

    public void handleLocalGame(ActionEvent actionEvent) {
        //TODO A compléter
        // Renvoie vers les paramètres de la partie ( taille de carte, difficulté des bots, ... )
        System.out.println("Lancement du mode Local...");
        loadGameView(actionEvent);
    }

    public void handleOnlineGame(ActionEvent actionEvent) {
        //TODO A compléter
        // Renvoie vers la liste des parties disponibles ou en cours
        System.out.println("Lancement du mode Online...");
        loadGameView(actionEvent);
    }

    public void handleExit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void handleCustomize(ActionEvent actionEvent) {
        // Renvoie vers la personnalisation du personnage, ...
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customize.fxml"));
            Parent customizeRoot = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(customizeRoot);
            stage.setScene(scene);
            stage.setTitle("Bomberman - Personnalisation");
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur : Impossible de charger la vue de personnalisation.");
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour charger la vue du labyrinthe
     */
    private void loadGameView(ActionEvent event) {
        try {
            String fxmlPath = "/iut/gon/bomberman/client/game-view.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent gameRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene gameScene = new Scene(gameRoot);
            stage.setScene(gameScene);
            stage.setTitle("Bomberman - Partie en cours");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur : Impossible de charger la vue du jeu !");
            e.printStackTrace();
        }
    }
}
