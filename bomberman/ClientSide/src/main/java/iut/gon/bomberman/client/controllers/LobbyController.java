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
                        
                        // Détection de l'owner local
                        if (p.isOwner && p.name.equals(localPlayerName)) {
                            this.isOwner = true;
                        }
                        
                        // Si au moins un joueur n'est pas prêt, on ne peut pas lancer
                        if (!p.isReady) {
                            everyoneReady = false;
                        }
                    }

                    // --- LOGIQUE DE LANCEMENT ---
                    // Le bouton "Lancer" n'est visible QUE si l'utilisateur est l'owner
                    btnStartGame.setVisible(this.isOwner);
                    
                    // Il n'est activé QUE si tout le monde (y compris l'owner) est prêt
                    // Et s'il y a au moins 2 joueurs (minimum requis pour lancer)
                    btnStartGame.setDisable(!(everyoneReady && r.getPlayers().size() >= 2));
                    
                    // Mise à jour de l'affichage du bouton "Prêt" local
                    validerLobby.setText(isReady ? "Prêt !" : "Prêt");
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

        // Bouton Prêt/Pas Prêt (utilisé par tout le monde, même l'owner)
        validerLobby.setOnAction(e -> {
            isReady = !isReady;
            NetworkManager.getInstance().send(new ReadyStatus(isReady, currentLobbyId));
        });

        // Bouton Lancer la partie (utilisé uniquement par l'owner)
        btnStartGame.setOnAction(e -> {
            System.out.println("Lancement de la partie par l'owner...");
            NetworkManager.getInstance().send(new StartGameRequest(currentLobbyId));
        });

        quitterLobby.setOnAction(e -> handleLeaveLobby());

        // Demander les détails initiaux dès l'arrivée
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
