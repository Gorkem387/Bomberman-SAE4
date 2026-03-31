package iut.gon.bomberman.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import iut.gon.bomberman.client.MainApp;
import iut.gon.bomberman.client.network.NetworkManager;
import iut.gon.bomberman.common.model.Mess.*;
import iut.gon.bomberman.common.model.labyrinthe.TypeLab;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;

public class AttenteLobbyController implements Initializable{

    @FXML private ListView<String> listeLobby;
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
                    int lobbyId = lobbyIds.get(selectedIndex);

                    NetworkManager networkManager = NetworkManager.getInstance();
                    networkManager.send(new JoinLobbyRequest(lobbyId, networkManager.getLocalPlayerName()));
                }
            }
        });

        NetworkManager.getInstance().addServerMessageListener(
                MessageType.JOIN_LOBBY_RESPONSE, msg -> {
                    JoinLobbyResponse r = (JoinLobbyResponse) msg;
                    if (r.isSuccess()) {
                        NetworkManager.getInstance().setCurrentLobbyId(r.getLobbyId());
                        Platform.runLater(() -> {
                            try {
                                MainApp.setRoot("fxml/lobby"); 
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        System.err.println("Erreur : " + r.getMessage());
                    }
                }
        );

        NetworkManager.getInstance().addServerMessageListener(
                MessageType.LOBBY_LIST_RESPONSE, msg -> {
                    LobbyListResponse r = (LobbyListResponse) msg;
                    Platform.runLater(() -> {
                        listeLobby.getItems().clear();
                        lobbyIds.clear();
                        for (LobbyListResponse.LobbyDTO lobby : r.getLobbies()) {
                            listeLobby.getItems().add(lobby.id + " - " + lobby.name + " (" + lobby.currentPlayers + "/" + lobby.maxPlayers + ")");
                            lobbyIds.add(lobby.id);
                        }
                    });
                }
        );

        NetworkManager.getInstance().addServerMessageListener(
                MessageType.CREATE_LOBBY_RESPONSE, msg -> {
                    CreateLobbyResponse r = (CreateLobbyResponse) msg;
                    if (r.isSuccess()) {
                        NetworkManager.getInstance().setCurrentLobbyId(r.getLobbyId());
                        Platform.runLater(() -> {
                            try {
                                MainApp.setRoot("fxml/lobby");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
        );

        // Rafraîchir au chargement
        handleRefresh();
    }

    @FXML
    private void handleCreateLobby() {
        NetworkManager networkManager = NetworkManager.getInstance();
        networkManager.send(new CreateLobbyRequest(networkManager.getLocalPlayerName() + "'s Lobby", 4, TypeLab.DEEPSEARCH, 20, 20));
    }

    @FXML
    private void handleRefresh() {
        NetworkManager.getInstance().send(new LobbyListRequest());
    }
}
