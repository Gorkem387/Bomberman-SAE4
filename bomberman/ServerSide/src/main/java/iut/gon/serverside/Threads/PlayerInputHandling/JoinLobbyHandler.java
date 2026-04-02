package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.LobbyManager;
import iut.gon.bomberman.common.model.Mess.JoinLobbyRequest;
import iut.gon.bomberman.common.model.Mess.JoinLobbyResponse;
import iut.gon.bomberman.common.model.Mess.LobbyDetailsRequest;
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
            // Définir l'id du lobby sur le client (important pour broadcastToLobby)
            client.setLobbyId(lobby.getId());

            // Mettre à jour le nom du joueur côté serveur
            if (client.getJoueur() == null) {
                Joueur j = new Joueur(Math.abs(client.hashCode()), message.getPlayerName());
                client.setJoueur(j);
            } else {
                client.getJoueur().setNom(message.getPlayerName());
                // On lui donne un ID unique temporaire (positif)
                client.getJoueur().setId(Math.abs(client.hashCode()));
            }
            // On délègue au lobby l'ajout du joueur (pour gérer le nombre max, l'état, etc.)
            boolean succes = lobby.rejoindreLobby(client);

            // Réponse au client
            String text_message;
            if (succes) text_message = "Vous avez rejoint le lobby " + lobby.getNom();
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
