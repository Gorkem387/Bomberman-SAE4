package iut.gon.serverside.Threads;

import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Logger.Logger;
import iut.gon.serverside.Player.DTO.IDTO;
import iut.gon.serverside.Player.DTO.InitGameDTO;
import iut.gon.serverside.Player.DTO.JoueurMisAJourDTO;

/**
 * Gère la boucle de jeu (60 FPS) pour un lobby spécifique.
 * Elle broadcast les mises à jour à tous les membres du lobby.
 */
public class Thread_Jeu extends Thread {

    private boolean running = true;
    private final Lobby lobby;
    private final Logger logger = Logger.getInstance();

    public Thread_Jeu(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public void run() {
        // 1. Initialisation de la partie pour tous les clients
        InitGameDTO initDTO = new InitGameDTO();
        initDTO.id = lobby.getId();
        initDTO.pseudo = lobby.getNom();
        initDTO.skin = 0;
        initDTO.x = 0;
        initDTO.y = 0;

        // Utilisation du broadcast du lobby (Remplace j.getClientHandler().send())
        lobby.broadcast(initDTO);

        // 2. Boucle de jeu (Synchronisation temps réel)
        while (running) {
            // Dans cette architecture, chaque itération envoie l'état du monde
            // (Pour l'instant on simule l'envoi pour le premier joueur)
            if (!lobby.getJoueurs().isEmpty()) {
                JoueurMisAJourDTO updateDTO = new JoueurMisAJourDTO(
                        lobby.getJoueurs().get(0).getId(),
                        (int) lobby.getJoueurs().get(0).getX(),
                        (int) lobby.getJoueurs().get(0).getY()
                );
                
                // Diffusion automatique à tout le monde via le Lobby
                lobby.broadcast(updateDTO);
            }

            try {
                // Pause pour maintenir 60 FPS
                Thread.sleep(16);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void stopGame() {
        this.running = false;
    }
}
