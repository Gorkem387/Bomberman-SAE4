package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.MainApp;
import iut.gon.bomberman.client.controllers.OnlineGameController;
import iut.gon.bomberman.client.network.NetworkManager;
import iut.gon.bomberman.common.model.Mess.*;
import iut.gon.bomberman.common.model.player.EtatJoueur;
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
    @FXML private Button validerLobby; // Bouton "Prêt"
    @FXML private Button btnStartGame; // Bouton "LANCER LA PARTIE" (visible uniquement pour l'owner)

    private int currentLobbyId;
    private boolean isReady = false;
    private boolean isOwner = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.currentLobbyId = NetworkManager.getInstance().getCurrentLobbyId();

        // Écouter les détails du lobby
        NetworkManager.getInstance().addServerMessageListener(MessageType.LOBBY_DETAILS_RESPONSE, msg -> {
            LobbyDetailsResponse r = (LobbyDetailsResponse) msg;
            if (r.getLobbyId() == currentLobbyId) {
                Platform.runLater(() -> {
                    lobbyTitre.setText(r.getLobbyName() + " (ID: " + r.getLobbyId() + ")");
                    listeJoueurs.getItems().clear();
                    
                    boolean everyoneReady = true;
                    this.isOwner = false;
                    String localPlayerName = NetworkManager.getInstance().getLocalPlayerName();

                    for (LobbyDetailsResponse.PlayerDTO p : r.getPlayers()) {
                        String status = p.isReady ? " [PRÊT]" : " [PAS PRÊT]";
                        String prefix = p.isOwner ? "👑 " : "";
                        listeJoueurs.getItems().add(prefix + p.name + status);
                        
                        if (p.isOwner && p.name.equals(localPlayerName)) {
                            this.isOwner = true;
                        }
                        
                        if (!p.isReady) {
                            everyoneReady = false;
                        }
                    }

                    btnStartGame.setVisible(this.isOwner);
                    btnStartGame.setDisable(!(everyoneReady && r.getPlayers().size() >= 2));
                    validerLobby.setText(isReady ? "Prêt !" : "Prêt");
                });
            }
        });

        // --- ÉCOUTE DU DÉCOMPTE ---
        NetworkManager.getInstance().addServerMessageListener(MessageType.COUNTDOWN_UPDATE, msg -> {
            CountdownUpdate countdown = (CountdownUpdate) msg;
            Platform.runLater(() -> {
                if (countdown.getRemainingSeconds() > 0) {
                    lobbyTitre.setText("LANCEMENT DANS : " + countdown.getRemainingSeconds() + " s");
                    // On grise le bouton de lancement pendant le décompte
                    btnStartGame.setDisable(true);
                } else if (countdown.getRemainingSeconds() == -1) {
                    lobbyTitre.setText("LANCEMENT ANNULÉ");
                }
            });
        });

        // --- ÉCOUTE DU MESSAGE D'INITIALISATION DE LA PARTIE ---
        NetworkManager.getInstance().addServerMessageListener(MessageType.INIT_GAME, msg -> {
            Platform.runLater(() -> {
                try {
                    System.out.println("Partie lance ! Chargement de la vue du jeu...");
                    javafx.fxml.FXMLLoader loader = MainApp.setRootAndGetLoader("iut/gon/bomberman/client/game-view");
                    OnlineGameController gameController = loader.getController();
                    InitGameMessage initMsg = (InitGameMessage) msg;
                    gameController.setLabyrinthe(initMsg.getLabyrinthe());
                    gameController.initPlayers(initMsg.getPlayers());
                } catch (IOException e) {
                    System.err.println("Erreur : Impossible de charger la vue du jeu !");
                    e.printStackTrace();
                }
            });
        });

        // Écouter les messages de chat
        NetworkManager.getInstance().addServerMessageListener(MessageType.CHAT_MESSAGE, msg -> {
            ChatMessage chat = (ChatMessage) msg;
            Platform.runLater(() -> {
                texteChat.appendText(chat.getSenderName() + ": " + chat.getContent() + "\n");
            });
        });

        envoyerMessage.setOnAction(e -> handleSendMessage());
        texteMessage.setOnAction(e -> handleSendMessage());

        validerLobby.setOnAction(e -> {
            isReady = !isReady;
            NetworkManager.getInstance().send(new ReadyStatus(isReady, currentLobbyId));
        });

        btnStartGame.setOnAction(e -> {
            System.out.println("L'owner demande le lancement du décompte...");
            NetworkManager.getInstance().send(new StartGameRequest(currentLobbyId));
        });

        quitterLobby.setOnAction(e -> handleLeaveLobby());

        NetworkManager.getInstance().send(new LobbyDetailsRequest(currentLobbyId));
    }

    private void handleSendMessage() {
        String content = texteMessage.getText().trim();
        if (!content.isEmpty()) {
            NetworkManager nm = NetworkManager.getInstance();
            nm.send(new ChatMessage(nm.getLocalPlayerName(), content, currentLobbyId));
            texteMessage.clear();
        }
    }

    @FXML
    public void handleLeaveLobby() {
        try {
            NetworkManager.getInstance().setCurrentLobbyId(-1);
            MainApp.setRoot("fxml/AttenteLobby");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
