package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.CreateLobbyRequest;
import iut.gon.bomberman.common.model.Mess.CreateLobbyResponse;
import iut.gon.bomberman.common.model.Mess.LobbyListResponse;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Threads.ThreadPrincipal;
import iut.gon.serverside.Lob.Lobby;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Gère la création d'un nouveau salon.
 */
public class CreateLobbyHandler implements MessageHandler<CreateLobbyRequest> {

    /**
     * Permet au joueur de créer un lobby, puis notifie tous les clients connectés
     * de la nouvelle liste de lobbies disponibles.
     * @param message Le message de création de lobby
     * @param client  Le client qui crée le lobby
     */
    @Override
    public void handle(CreateLobbyRequest message, ClientHandler client) {
        String playerName = message.getPlayerName();
        if (playerName == null || playerName.isEmpty()) {
            playerName = "Joueur_" + client.hashCode();
        }

        // Initialisation du joueur avec son vrai nom
        Joueur j = client.getJoueur();
        int uniqueId = Math.abs(client.hashCode());
        if (j == null) {
            j = new Joueur(uniqueId, playerName);
            client.setJoueur(j);
        } else {
            j.setId(uniqueId);
            j.setNom(playerName);
        }

        // Création du lobby avec cet owner
        Lobby newLobby = LobbyManager.getInstance().createLobby(
                j,
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

            // Notifier tous les clients de la nouvelle liste de lobbies
            broadcastLobbyList();
        } else {
            client.send(new CreateLobbyResponse(false, "Erreur de création", -1));
        }
    }

    /**
     * Envoie la liste des lobbies à jour à tous les clients connectés.
     */
    private void broadcastLobbyList() {
        List<LobbyListResponse.LobbyDTO> lobbyDTOs = LobbyManager.getInstance().getLobbies().values().stream()
                .map(l -> new LobbyListResponse.LobbyDTO(l.getId(), l.getNom(), l.getJoueurs().size(), l.getNbJMax()))
                .collect(Collectors.toList());
        ThreadPrincipal.broadcast(new LobbyListResponse(lobbyDTOs));
    }
}