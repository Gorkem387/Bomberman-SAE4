package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.ChatMessage;
import iut.gon.bomberman.common.model.Mess.LeaveLobbyRequest;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Threads.ClientHandler;

public class LeaveLobbyHandler implements MessageHandler<LeaveLobbyRequest> {
    @Override
    public void handle(LeaveLobbyRequest message, ClientHandler client) {
        Lobby lobby = LobbyManager.getInstance().getLobby(message.getLobbyId());
        if (lobby == null) return;

        boolean isOwner = lobby.getProprietaire() != null &&
                lobby.getProprietaire().getId() == client.getJoueur().getId();

        if (isOwner) {
            lobby.broadcast(new ChatMessage("SERVEUR",
                    "Le propriétaire a quitté. Le lobby est fermé.", message.getLobbyId()));
            LobbyManager.getInstance().removeLobby(message.getLobbyId());
        } else {
            lobby.removeJoueur(client.getJoueur());
            client.setLobbyId(-1);
            if (lobby.getJoueurs().isEmpty()) {
                LobbyManager.getInstance().removeLobby(message.getLobbyId());
            }
        }
    }
}
