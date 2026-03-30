package iut.gon.bomberman;

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

    //private Lobby lobby;

    @FXML
    public void initialize() {
        // bind listeJoueurs et lobby.listeJoueurs

        // bind lobbyTitre et lobby.titre

        envoyerMessage.setOnAction(e -> {
            envoyerMessage();
            texteChat.clear();
        });

        texteMessage.setOnAction(e -> {
            envoyerMessage();
            texteChat.clear();
        });

        quitterLobby.setOnAction(e -> {
            // retour page choix du lobby
        });

        validerLobby.setOnAction(e -> {
            //lobby.joueur est prêt
        });
    }

    private void envoyerMessage() {
        String message = texteMessage.getText().trim();
        if (!message.isEmpty()) {
            texteChat.appendText("Moi : " + message + "\n");
            texteMessage.clear();
        }
    }

    public void recevoirMessage(String message) {
        texteChat.appendText(message + "\n");
    }
}
