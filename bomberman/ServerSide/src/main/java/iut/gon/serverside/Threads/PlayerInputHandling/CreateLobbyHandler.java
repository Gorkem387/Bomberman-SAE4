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

    @Override
    public void handle(CreateLobbyRequest message, ClientHandler client) {
        // Initialisation du joueur si nécessaire (le créateur est un joueur)
        if (client.getJoueur() == null) {
            // Le nom vient de l'initialisation côté client dans le NetworkManager
            client.setJoueur(new Joueur(client.hashCode(), "Créateur")); 
        }

        // Le joueur qui crée le lobby en devient le propriétaire
        Lobby newLobby = LobbyManager.getInstance().createLobby(
                client.getJoueur(), //owner du lobby
                message.getLobbyName(),
                message.getMaxPlayers(),
                message.getLabyrintheType(),
                message.getSizeX(),
                message.getSizeY()
        );

        Logger logger = Logger.getInstance();
        if (newLobby != null) {
            client.setLobbyId(newLobby.getId());
            // Le créateur rejoint automatiquement son propre lobby
            newLobby.addJoueur(client.getJoueur());

            client.send(new CreateLobbyResponse(true, "Lobby créé avec succès", newLobby.getId()));
            logger.log(LogTypes.SUCCESS, "Lobby créé avec succès " + newLobby.getNom() + " avec id : " + newLobby.getId());
        } else {
            client.send(new CreateLobbyResponse(false, "Erreur lors de la création du lobby", -1));
            logger.log(LogTypes.ERROR, "Erreur de création de lobby");
        }
    }
}
