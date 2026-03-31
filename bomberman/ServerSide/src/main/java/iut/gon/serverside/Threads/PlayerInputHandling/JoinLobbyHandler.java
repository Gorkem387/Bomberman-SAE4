package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.serverside.LobbyManager;
import iut.gon.bomberman.common.model.Message.JoinLobbyRequest;
import iut.gon.bomberman.common.model.Message.JoinLobbyResponse;
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
            // Mise à jour de l'ID du joueur dans le handler
            client.playerId = message.getLobbyId(); // Ou un ID unique global si nécessaire
            
            // On délègue au lobby l'ajout du joueur (pour gérer le nombre max, l'état, etc.)
            boolean succes = lobby.rejoindreLobby(client);


            //reponse
            String text_message = "";
            if(succes) text_message = "Vous avez rejoint le lobby" + lobby.getNom();
            else text_message = "Probleme avec lobby " + lobby.getNom();

            JoinLobbyResponse response = new JoinLobbyResponse(succes, text_message, lobby.getId());


            client.send(response);
            // Broadcast : On pourrait ici prévenir les autres joueurs qu'un nouveau est arrivé
        } else {
            // Réponse négative (Lobby plein ou inexistant)
            client.send(new JoinLobbyResponse(false, "Lobby non trouvé ou plein", -1));
        }
    }
}
