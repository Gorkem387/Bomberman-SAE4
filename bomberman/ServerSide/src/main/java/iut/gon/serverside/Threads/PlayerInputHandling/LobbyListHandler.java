package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.LobbyListRequest;
import iut.gon.bomberman.common.model.Mess.LobbyListResponse;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Threads.ClientHandler;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyListHandler implements MessageHandler<LobbyListRequest> {
    /**
     * Permet au client de recevoir la liste des lobby
     * @param message
     * @param client
     */
    @Override
    public void handle(LobbyListRequest message, ClientHandler client) {
        List<LobbyListResponse.LobbyDTO> lobbyDTOs = LobbyManager.getInstance().getLobbies().values().stream()
                .map(l -> new LobbyListResponse.LobbyDTO(l.getId(), l.getNom(), l.getJoueurs().size(), l.getNbJMax()))
                .collect(Collectors.toList());
        
        client.send(new LobbyListResponse(lobbyDTOs));
    }
}
