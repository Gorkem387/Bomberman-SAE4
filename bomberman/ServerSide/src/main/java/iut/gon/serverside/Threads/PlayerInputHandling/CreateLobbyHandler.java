package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.bomberman.common.model.Mess.CreateLobbyRequest;
import iut.gon.bomberman.common.model.Mess.CreateLobbyResponse;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Lob.Lobby;

/**
 * Gère la création d'un nouveau salon.
 */
public class CreateLobbyHandler implements MessageHandler<CreateLobbyRequest> {

    /**
     * Permet au joueur de créer un lobby
     * @param message
     * @param client
     */
    @Override
    public void handle(CreateLobbyRequest message, ClientHandler client) {
        // --- MISE À JOUR : On utilise le pseudo de la requête ---
        String playerName = message.getPlayerName();
        if (playerName == null || playerName.isEmpty()) {
            playerName = "Joueur_" + client.hashCode();
        }
        
        // Initialisation du joueur avec son vrai nom
        client.setJoueur(new Joueur(client.hashCode(), playerName)); 

        // Création du lobby avec cet owner
        Lobby newLobby = LobbyManager.getInstance().createLobby(
                client.getJoueur(),
                message.getLobbyName(),
                message.getMaxPlayers(),
                message.getLabyrintheType(),
                message.getSizeX(),
                message.getSizeY()
        );

        Logger logger = Logger.getInstance();
        if (newLobby != null) {
            client.setLobbyId(newLobby.getId());
            newLobby.addJoueur(client.getJoueur(), client);

            client.send(new CreateLobbyResponse(true, "Lobby créé avec succès", newLobby.getId()));
            logger.log(LogTypes.SUCCESS, "Lobby créé : " + newLobby.getNom() + " par " + playerName);
        } else {
            client.send(new CreateLobbyResponse(false, "Erreur de création", -1));
        }
    }
}
