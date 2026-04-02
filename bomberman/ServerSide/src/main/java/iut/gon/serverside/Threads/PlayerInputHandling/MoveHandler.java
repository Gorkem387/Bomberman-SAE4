package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.MoveRequest;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Threads.Thread_Jeu;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;

/**
 * Gère les demandes de déplacement des joueurs pendant la partie.
 */
public class MoveHandler implements MessageHandler<MoveRequest> {
    /**
     * Met à jour la partie dans un lobby spécifique
     * @param message
     * @param client
     */
    @Override
    public void handle(MoveRequest message, ClientHandler client) {
        Lobby lobby = LobbyManager.getInstance().getLobby(client.getLobbyId());

        if (client.getJoueur() != null && lobby != null) {
            // Le serveur applique le mouvement sur son modèle de données
            // (La logique de collision est incluse dans la méthode move du Joueur)
            // Delta de 1/60eme de seconde
            Thread_Jeu threadJeu = lobby.getThread();
            BombManager bombManager = (threadJeu != null) ? threadJeu.getBombManager() : null;

            client.getJoueur().move(message.getDx(), message.getDy(), 1.0 / 60.0, lobby.getLabyrinthe(), bombManager);

            // Le Thread_Jeu (60 FPS) se chargera de diffuser cette nouvelle position
            // à tous les membres du lobby via GAME_UPDATE.
        }
    }
}
