package iut.gon.bomberman.client.controllers;

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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AttenteLobbyController implements Initializable {

    @FXML private ListView<String> listeLobby;
    @FXML private MenuBar menu;
    @FXML private Pane pane;
    private final List<Integer> lobbyIds = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Redimensionnement dynamique
        menu.prefWidthProperty().bind(pane.widthProperty());
        listeLobby.prefWidthProperty().bind(pane.widthProperty());
        listeLobby.prefHeightProperty().bind(pane.heightProperty());

        // Événement double-clic pour rejoindre un lobby
        listeLobby.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                int selectedIndex = listeLobby.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    int selectedLobbyId = lobbyIds.get(selectedIndex);
                    NetworkManager nm = NetworkManager.getInstance();

                    // Si on est déjà dedans, on affiche juste la page
                    if (nm.getCurrentLobbyId() == selectedLobbyId) {
                        try {
                            MainApp.setRoot("fxml/lobby");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        // Sinon on demande à rejoindre
                        nm.send(new JoinLobbyRequest(selectedLobbyId, nm.getLocalPlayerName()));
                    }
                }
            }
        });

        // Listeners de messages
        NetworkManager nm = NetworkManager.getInstance();

        nm.addServerMessageListener(MessageType.JOIN_LOBBY_RESPONSE, msg -> {
            JoinLobbyResponse r = (JoinLobbyResponse) msg;
            if (r.isSuccess()) {
                nm.setCurrentLobbyId(r.getLobbyId());
                Platform.runLater(() -> {
                    try { MainApp.setRoot("fxml/lobby"); } catch (IOException ex) { ex.printStackTrace(); }
                });
            }
        });

        nm.addServerMessageListener(MessageType.LOBBY_LIST_RESPONSE, msg -> {
            LobbyListResponse r = (LobbyListResponse) msg;
            Platform.runLater(() -> {
                listeLobby.getItems().clear();
                lobbyIds.clear();
                for (LobbyListResponse.LobbyDTO lobby : r.getLobbies()) {
                    listeLobby.getItems().add(lobby.id + " - " + lobby.name + " (" + lobby.currentPlayers + "/" + lobby.maxPlayers + ")");
                    lobbyIds.add(lobby.id);
                }
            });
        });

        nm.addServerMessageListener(MessageType.CREATE_LOBBY_RESPONSE, msg -> {
            CreateLobbyResponse r = (CreateLobbyResponse) msg;
            if (r.isSuccess()) {
                nm.setCurrentLobbyId(r.getLobbyId());
                Platform.runLater(() -> {
                    try { MainApp.setRoot("fxml/lobby"); } catch (IOException ex) { ex.printStackTrace(); }
                });
            }
        });

        handleRefresh();
    }

    @FXML
    public void handleCreateLobby() {
        NetworkManager nm = NetworkManager.getInstance();
        // --- MISE À JOUR : On passe maintenant le pseudo du joueur local ---
        nm.send(new CreateLobbyRequest(
                nm.getLocalPlayerName() + "'s Lobby", 
                nm.getLocalPlayerName(), // Pseudo de l'owner
                4, 
                TypeLab.DEEPSEARCH,
                21, 
                21
        ));
    }

    @FXML
    public void handleRefresh() {
        NetworkManager.getInstance().send(new LobbyListRequest());
    }
}
