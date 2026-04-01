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
            // Initialisation du joueur si nécessaire
            if (client.getJoueur() == null) {
                client.setJoueur(new Joueur(client.hashCode(), message.getPlayerName()));
            }

            // On délègue au lobby l'ajout du joueur
            boolean succes = lobby.rejoindreLobby(client);

            if (succes) {
                client.setLobbyId(lobby.getId());
                client.send(new JoinLobbyResponse(true, "Lobby rejoint : " + lobby.getNom(), lobby.getId()));
                
                // --- MISE À JOUR TEMPS RÉEL ---
                // On notifie TOUS les joueurs du lobby qu'un nouveau est arrivé
                // On réutilise LobbyDetailsHandler pour envoyer les nouvelles infos à tout le monde
                LobbyDetailsHandler detailsHandler = new LobbyDetailsHandler();
                LobbyDetailsRequest updateReq = new LobbyDetailsRequest(lobby.getId());
                
                for (Joueur j : lobby.getJoueurs()) {
                    // On retrouve le ClientHandler associé à chaque joueur (via son ID/HashCode ou stocké dans Joueur)
                    // Pour simplifier ici, on peut faire un broadcast via le LobbyManager si les handlers sont liés
                    // Mais la méthode la plus propre est que le lobby notifie ses membres
                }
                
                // On simule le broadcast via le dispatcher vers tous les membres du lobby
                lobby.broadcast(new LobbyDetailsRequest(lobby.getId())); // On ajoute une méthode broadcast dans Lobby
            } else {
                client.send(new JoinLobbyResponse(false, "Lobby plein ou inaccessible", -1));
            }
        } else {
            client.send(new JoinLobbyResponse(false, "Lobby non trouvé", -1));
        }
    }
}
