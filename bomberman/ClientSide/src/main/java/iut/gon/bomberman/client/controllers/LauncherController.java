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

    @FXML
    public void handleLocalGame(ActionEvent actionEvent) {
            loadCustomizeView(actionEvent);
            System.out.println("Mode Local lancé.");
    }

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

    @FXML
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

    private void loadCustomizeView(ActionEvent event){
        try {
            String fxmlPath = "/iut/gon/bomberman/client/configPartieLocale.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent gameRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene gameScene = new Scene(gameRoot);
            stage.setScene(gameScene);
            stage.setTitle("Bomberman - Configuration Partie");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur : Impossible de charger la vue du jeu !");
            e.printStackTrace();
        }
    }
}
