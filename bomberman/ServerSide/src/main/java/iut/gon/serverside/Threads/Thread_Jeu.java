package iut.gon.serverside.Threads;

import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Logger.Logger;
import iut.gon.bomberman.common.model.Mess.Message;
import iut.gon.bomberman.common.model.Mess.InitGame;
import iut.gon.bomberman.common.model.Mess.GameUpdate;
import iut.gon.bomberman.common.model.player.Joueur;

import java.util.HashMap;
import java.util.Map;

/**
 * Gère la boucle de jeu (60 FPS) pour un lobby spécifique.
 * Elle broadcast les mises à jour à tous les membres du lobby.
 */
public class Thread_Jeu extends Thread {

    private boolean running = true;
    private Lobby lobby;
    private Logger logger = Logger.getInstance();

    public Thread_Jeu(Lobby lobby) {
        this.lobby = lobby;
        lobby.setThread(this);
    }

    @Override
    public void run() {
        // Construire et envoyer un message d'initialisation global
        Map<Integer, InitGame.PlayerInitDTO> playersMap = new HashMap<>();
        for (Joueur j : lobby.getJoueurs()) {
            int id = j.getId();
            String pseudo = j.getNom();
            int skin = 0; // valeur par défaut, à adapter si vous avez un skin côté joueur
            int x = (int) j.getX();
            int y = (int) j.getY();
            playersMap.put(id, new InitGame.PlayerInitDTO(id, pseudo, skin, x, y));
        }

        // Note : les champs pseudoLocal/idLocal étaient initialement pris du lobby dans le code existant;
        // on conserve ce comportement pour rester compatible, mais idéalement ce serait les infos du joueur local.
        InitGame init = new InitGame(lobby.getNom(), lobby.getId(), 0, 0, 0, playersMap);
        broadcastUpdate(init);

        while (running) {
            // Construire une mise à jour légère (GameUpdate) et l'envoyer
            Map<Integer, GameUpdate.PlayerPositionDTO> posMap = new HashMap<>();
            for (Joueur j : lobby.getJoueurs()) {
                int id = j.getId();
                int x = (int) j.getX();
                int y = (int) j.getY();
                String dir = j.getDirection() != null ? j.getDirection().toString() : null;
                posMap.put(id, new GameUpdate.PlayerPositionDTO(x, y, dir));
            }

            GameUpdate update = new GameUpdate(posMap);
            broadcastUpdate(update);

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void broadcastUpdate(Message message) {
        // Envoie uniquement aux clients du lobby associé à ce Thread_Jeu
        ThreadPrincipal.broadcastToLobby(lobby, message);
    }

    public void stopGame() {
        this.running = false;
    }
}
