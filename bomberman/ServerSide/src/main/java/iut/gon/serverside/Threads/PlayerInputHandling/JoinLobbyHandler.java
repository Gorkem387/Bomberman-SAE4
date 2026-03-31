package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.LobbyManager;
import iut.gon.bomberman.common.model.Mess.JoinLobbyRequest;
import iut.gon.bomberman.common.model.Mess.JoinLobbyResponse;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Lob.Lobby;

/**
 * Gère l'entrée d'un joueur dans un salon existant.
 */
public class JoinLobbyHandler implements MessageHandler<JoinLobbyRequest> {

    @Override
    public void handle(JoinLobbyRequest message, ClientHandler client) {
        Lobby lobby = LobbyManager.getInstance().getLobby(message.getLobbyId());

        if (lobby != null) {
            // Initialisation du joueur si nécessaire
            if (client.getJoueur() == null) {
                client.setJoueur(new Joueur(client.hashCode(), message.getPlayerName()));
            }

            // On délègue au lobby l'ajout du joueur
            boolean succes = lobby.rejoindreLobby(client);

            if (succes) {
                client.setLobbyId(lobby.getId());
                client.send(new JoinLobbyResponse(true, "Lobby rejoint : " + lobby.getNom(), lobby.getId()));
                
                // On notifie tout le monde dans le lobby qu'un joueur a rejoint
                // LobbyDetailsHandler sera appelé par le client pour rafraîchir la vue
            } else {
                client.send(new JoinLobbyResponse(false, "Lobby plein ou inaccessible", -1));
            }
        } else {
            client.send(new JoinLobbyResponse(false, "Lobby non trouvé", -1));
        }
    }
}
