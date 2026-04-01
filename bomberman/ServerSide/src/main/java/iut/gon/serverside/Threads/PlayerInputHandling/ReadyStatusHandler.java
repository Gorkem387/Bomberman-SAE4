package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.LobbyDetailsRequest;
import iut.gon.bomberman.common.model.Mess.ReadyStatus;
import iut.gon.bomberman.common.model.player.EtatJoueur;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Threads.ClientHandler;

/**
 * Gère le changement de statut "Prêt / Pas prêt" d'un joueur.
 */
public class ReadyStatusHandler implements MessageHandler<ReadyStatus> {
    /**
     * Met à jour l'état du joueur et informe tous les autres joueurs du lobby
     * @param message
     * @param client
     */
    @Override
    public void handle(ReadyStatus message, ClientHandler client) {
        Lobby lobby = LobbyManager.getInstance().getLobby(message.getLobbyId());
        
        if (lobby != null && client.getJoueur() != null) {
            // Mise à jour de l'état du joueur
            client.getJoueur().setEtat(message.isReady() ? EtatJoueur.PRET : EtatJoueur.PAS_PRET);
            
            // Le serveur notifie TOUS les membres du lobby du changement d'état
            // On délègue à LobbyDetailsHandler pour générer et diffuser les détails mis à jour
            LobbyDetailsHandler detailsHandler = new LobbyDetailsHandler();
            lobby.broadcast(detailsHandler.createResponse(lobby));
            
            System.out.println("Joueur " + client.getJoueur().getNom() + " est désormais : " + client.getJoueur().getEtat());
        }
    }
}
