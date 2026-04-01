package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.PlaceBombRequest;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Threads.Thread_Jeu;

public class PlaceBombHandler implements MessageHandler<PlaceBombRequest> {
    @Override
    public void handle(PlaceBombRequest message, ClientHandler client) {
        Lobby lobby = LobbyManager.getInstance().getLobby(client.getLobbyId());
        
        if (lobby != null && client.getJoueur() != null) {
            Thread_Jeu gameThread = lobby.getThread();
            if (gameThread != null) {
                // On délègue la pose de bombe au BombManager du thread de jeu
                gameThread.getBombManager().placeBomb(client.getJoueur(), client.getJoueur().getExplosionRange(), lobby.getLabyrinthe());
            }
        }
    }
}
