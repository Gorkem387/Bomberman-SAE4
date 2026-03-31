package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.network.NetworkManager;
import iut.gon.bomberman.common.model.Mess.ChatMessage;
import iut.gon.bomberman.common.model.Mess.MessageType;
import iut.gon.bomberman.common.model.Mess.ReadyStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LobbyControlleur {

    @FXML private Label lobbyTitre;
    @FXML private ListView<String> listeJoueurs;
    @FXML private TextArea texteChat;
    @FXML private TextField texteMessage;
    @FXML private Button envoyerMessage;
    @FXML private Button quitterLobby;
    @FXML private Button validerLobby;

    private NetworkManager networkManager = NetworkManager.getInstance();

    private int lobbyId;

    @FXML
    public void initialize() {


        NetworkManager.getInstance().addServerMessageListener(
                MessageType.CHAT_MESSAGE, msg -> {
                    ChatMessage chat = (ChatMessage) msg;
                    texteChat.appendText(chat.getSenderName() + " : " + chat.getContent() + "\n");
                }
        );

        envoyerMessage.setOnAction(e -> envoyerMessage());
        validerLobby.setOnAction(e ->
                NetworkManager.getInstance().send(new ReadyStatus(true, lobbyId))
        );
    }

    private void envoyerMessage() {
        String message = texteMessage.getText().trim();
        if (!message.isEmpty()) {
            NetworkManager.getInstance().send(new ChatMessage("moi", message, lobbyId));
            texteMessage.clear();
        }
    }

    public void setLobbyId(int id) {
        lobbyId = id;
        lobbyTitre.setText("Arena - " + id);
    }


}
