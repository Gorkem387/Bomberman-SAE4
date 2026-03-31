package iut.gon.bomberman.client.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import iut.gon.bomberman.client.network.NetworkManager;
import iut.gon.bomberman.common.model.message.JoinLobbyRequest;
import iut.gon.bomberman.common.model.message.JoinLobbyResponse;
import iut.gon.bomberman.common.model.message.MessageType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;

public class AttenteLobbyController implements Initializable{

    @FXML private ListView listeLobby;
    @FXML private MenuBar menu;
    @FXML private Pane pane;
    private final List<Integer> lobbyIds = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menu.prefWidthProperty().bind(pane.widthProperty());
        listeLobby.prefWidthProperty().bind(pane.widthProperty());
        listeLobby.prefHeightProperty().bind(pane.heightProperty());
        listeLobby.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                int selectedIndex = listeLobby.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    int selectedId = lobbyIds.get(selectedIndex);
                    NetworkManager.getInstance().send(new JoinLobbyRequest(selectedId, "monPseudo"));
                }
            }
        });

        NetworkManager.getInstance().addServerMessageListener(
                MessageType.JOIN_LOBBY_RESPONSE, msg -> {
                    JoinLobbyResponse r = (JoinLobbyResponse) msg;
                    listeLobby.getItems().add(r.getLobbyId() + " - " + r.getMessage());
                    lobbyIds.add(r.getLobbyId());
                }
        );
    }


}
