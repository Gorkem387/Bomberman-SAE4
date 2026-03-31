package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.bomberman.common.model.Message.CreateLobbyRequest;
import iut.gon.bomberman.common.model.Message.CreateLobbyResponse;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Lob.Lobby;

/**
 * Gère la création d'un nouveau salon.
 */
public class CreateLobbyHandler implements MessageHandler<CreateLobbyRequest> {

    @Override
    public void handle(CreateLobbyRequest message, ClientHandler client) {
        // Le joueur qui crée le lobby en devient le propriétaire
        // Note: L'ID du skin ou le type de labyrinthe pourrait être dans la requête
        Lobby newLobby = LobbyManager.getInstance().createLobby(
                client.joueur, //owner du lobby (celui qui créé le lobby)
                message.getLobbyName(),
                message.getMaxPlayers(),
                message.getLabyrintheType(),
                message.getSizeX(),
                message.getSizeY()
        );

        Logger logger = Logger.getInstance();
        if (newLobby != null) {
            client.send(new CreateLobbyResponse(true, "Lobby créé avec succès", newLobby.getId()));
            logger.log(LogTypes.SUCCESS, "Lobby créé avec succès " + newLobby.getNom() + " avec id : " + newLobby.getId());
        } else {
            client.send(new CreateLobbyResponse(false, "Erreur lors de la création du lobby", -1));

            logger.log(LogTypes.ERROR, "Erreur de création de lobby");
        }
    }
}
