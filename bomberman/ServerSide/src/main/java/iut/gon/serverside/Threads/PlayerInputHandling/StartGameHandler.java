package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.StartGameRequest;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Threads.ClientHandler;

/**
 * Gère le lancement manuel de la partie par le propriétaire.
 */
public class StartGameHandler implements MessageHandler<StartGameRequest> {

    @Override
    public void handle(StartGameRequest message, ClientHandler client) {
        Lobby lobby = LobbyManager.getInstance().getLobby(message.getLobbyId());
        
        if (lobby != null && lobby.getProprietaire() != null) {
            // Seul l'owner peut lancer
            if (lobby.getProprietaire().equals(client.getJoueur())) {
                System.out.println("Le propriétaire lance la partie !");
                lobby.startGame();
            }
        }
    }
}
