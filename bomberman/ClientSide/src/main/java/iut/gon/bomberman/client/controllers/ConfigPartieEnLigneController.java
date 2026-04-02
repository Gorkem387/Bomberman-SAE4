package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.MainApp;
import iut.gon.bomberman.client.ai.AISTRATEGIES;
import iut.gon.bomberman.client.network.NetworkManager;
import iut.gon.bomberman.client.network.ServerMessageListener;
import iut.gon.bomberman.common.model.Mess.CreateLobbyRequest;
import iut.gon.bomberman.common.model.Mess.CreateLobbyResponse;
import iut.gon.bomberman.common.model.Mess.Message;
import iut.gon.bomberman.common.model.Mess.MessageType;
import iut.gon.bomberman.common.model.labyrinthe.TypeLab;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigPartieEnLigneController {

    @FXML private ToggleButton cartePetite;
    @FXML private ToggleButton carteMoyenne;
    @FXML private ToggleButton carteGrande;
    @FXML private Slider nombreJoueurs;
    @FXML private Button creerLobby;
    @FXML private Text nbJoueur;

    @FXML
    public void initialize() {
        final int[] lastNb = {1};

        nombreJoueurs.valueProperty().addListener((obs, oldVal, newVal) -> {
            int nb = (int) Math.round(newVal.doubleValue());

            nbJoueur.setText(String.valueOf(nb));

            if (nb != lastNb[0]) {
                lastNb[0] = nb;
            }
        });

        int depart = (int) Math.round(nombreJoueurs.getValue());
        nbJoueur.setText(String.valueOf(depart));

        creerLobby.setOnAction(this::handleCreateLobbyRequest);
        setupNetworkListener();
    }

    public int getTailleMap() {
        if (cartePetite.isSelected()) return 15;
        if (carteGrande.isSelected()) return 23;
        return 19;
    }

    private void handleCreateLobbyRequest(ActionEvent event) {
        NetworkManager nm = NetworkManager.getInstance();
        int taille = getTailleMap();
        int nbJoueurs = (int) Math.round(nombreJoueurs.getValue());

        nm.send(new CreateLobbyRequest(
                nm.getLocalPlayerName() + "'s Lobby",
                nm.getLocalPlayerName(),
                nbJoueurs,
                TypeLab.DEEPSEARCH,
                taille,
                taille
        ));
    }

    private void setupNetworkListener() {
        NetworkManager nm = NetworkManager.getInstance();

        nm.addServerMessageListener(MessageType.CREATE_LOBBY_RESPONSE, new ServerMessageListener() {
            @Override
            public void onServerMessage(Message msg) {
                CreateLobbyResponse r = (CreateLobbyResponse) msg;
                if (r.isSuccess()) {
                    nm.setCurrentLobbyId(r.getLobbyId());
                    nm.removeServerMessageListener(MessageType.CREATE_LOBBY_RESPONSE, this);

                    Platform.runLater(() -> {
                        try {
                            MainApp.setRoot("fxml/lobby");
                        } catch (IOException e) { e.printStackTrace(); }
                    });
                }
            }
        });
    }

}