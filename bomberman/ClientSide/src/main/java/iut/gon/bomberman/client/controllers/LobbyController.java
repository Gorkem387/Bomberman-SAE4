package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.MainApp;
import iut.gon.bomberman.client.network.NetworkManager;
import iut.gon.bomberman.common.model.Mess.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {

    @FXML private Label lobbyTitre;
    @FXML private ListView<String> listeJoueurs;
    @FXML private TextArea texteChat;
    @FXML private TextField texteMessage;
    @FXML private Button envoyerMessage;
    @FXML private Button quitterLobby;
    @FXML private Button validerLobby;

    private int currentLobbyId;
    private boolean isReady = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Récupérer l'ID du lobby actuel (stocké temporairement dans NetworkManager ou passé lors du setRoot)
        this.currentLobbyId = NetworkManager.getInstance().getCurrentLobbyId();

        // Écouter les détails du lobby
        NetworkManager.getInstance().addServerMessageListener(MessageType.LOBBY_DETAILS_RESPONSE, msg -> {
            LobbyDetailsResponse r = (LobbyDetailsResponse) msg;
            if (r.getLobbyId() == currentLobbyId) {
                Platform.runLater(() -> {
                    lobbyTitre.setText(r.getLobbyName() + " - " + r.getLobbyId());
                    listeJoueurs.getItems().clear();
                    for (LobbyDetailsResponse.PlayerDTO p : r.getPlayers()) {
                        listeJoueurs.getItems().add(p.toString());
                    }
                });
            }
        });

        // Écouter les messages de chat
        NetworkManager.getInstance().addServerMessageListener(MessageType.CHAT_MESSAGE, msg -> {
            ChatMessage chat = (ChatMessage) msg;
            Platform.runLater(() -> {
                texteChat.appendText(chat.getSenderName() + ": " + chat.getContent() + "\n");
            });
        });

        // Actions des boutons
        envoyerMessage.setOnAction(e -> handleSendMessage());
        texteMessage.setOnAction(e -> handleSendMessage());

        validerLobby.setOnAction(e -> {
            isReady = !isReady;
            validerLobby.setText(isReady ? "Prêt !" : "Prêt");
            NetworkManager.getInstance().send(new ReadyStatus(isReady, currentLobbyId));
        });

        quitterLobby.setOnAction(e -> {
            try {
                // Logique pour quitter le lobby côté serveur à implémenter
                MainApp.setRoot("fxml/attenteLobby");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Demander les détails initiaux
        refreshLobbyDetails();
    }

    private void handleSendMessage() {
        String content = texteMessage.getText().trim();
        if (!content.isEmpty()) {
            NetworkManager nm = NetworkManager.getInstance();
            nm.send(new ChatMessage(nm.getLocalPlayerName(), content, currentLobbyId));
            texteMessage.clear();
        }
    }

    private void refreshLobbyDetails() {
        NetworkManager.getInstance().send(new LobbyDetailsRequest(currentLobbyId));
    }
}
