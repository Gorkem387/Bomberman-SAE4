package iut.gon.bomberman.client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class LauncherController {

    public void handleLocalGame(ActionEvent actionEvent) {
        //TODO A compléter
        // Renvoie vers les paramètres de la partie ( taille de carte, difficulté des bots, ... )
    }

    public void handleOnlineGame(ActionEvent actionEvent) {
        //TODO A compléter
        // Renvoie vers la liste des parties disponibles ou en cours
    }

    public void handleExit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void handleCustomize(ActionEvent actionEvent) {
        //TODO A compléter
        // Renvoie vers la personnalisation du personnage
    }

}
