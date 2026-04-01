package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.MainApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LauncherController {

    /**
     * Fonction redirigeant le joueur vers le jeu en local
     * @param actionEvent
     */
    @FXML
    public void handleLocalGame(ActionEvent actionEvent) {
            loadGameView(actionEvent);
            System.out.println("Mode Local lancé.");
    }

    /**
     * Fonction permettant de rediriger le joueur vers l'écran de connexion
     * @param actionEvent
     */
    @FXML
    public void handleOnlineGame(ActionEvent actionEvent) {
        try {
            // Redirige vers la saisie du pseudo avant toute connexion
            MainApp.setRoot("fxml/connexion");
        } catch (IOException e) {
            System.err.println("Erreur : Impossible de charger l'écran de connexion !");
            e.printStackTrace();
        }
    }

    /**
     * Fonction permettant de fermer l'application
     * @param actionEvent
     */
    @FXML
    public void handleExit(ActionEvent actionEvent) {
        Platform.exit();
    }

    /**
     * Fonction permettant de personnaliser le personnage
     * @param actionEvent
     */
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
