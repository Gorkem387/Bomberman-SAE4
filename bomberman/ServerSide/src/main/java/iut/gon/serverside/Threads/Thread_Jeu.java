package iut.gon.serverside.Threads;

import iut.gon.bomberman.common.model.labyrinthe.Bomb;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.bomberman.common.model.DTO.JoueurMisAJourDTO;
import iut.gon.bomberman.common.model.DTO.MinimDTO;
import iut.gon.bomberman.common.model.Mess.BombUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gère la boucle de jeu (60 FPS) pour un lobby spécifique.
 * Elle broadcast les mises à jour à tous les membres du lobby.
 */
public class Thread_Jeu extends Thread {

    private boolean running = true;
    private final Lobby lobby;
    private final BombManager bombManager; // Gestionnaire de bombes côté serveur

    public Thread_Jeu(Lobby lobby) {
        this.lobby = lobby;
        this.bombManager = new BombManager();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();

        // Boucle de jeu (Synchronisation temps réel)
        while (running) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            // 1. Mise à jour de la physique (bombes, explosions, dégâts) sur le serveur
            bombManager.update(deltaTime, lobby.getLabyrinthe(), lobby.getJoueurs());

            // 2. Broadcast des positions des joueurs
            JoueurMisAJourDTO updateDTO = new JoueurMisAJourDTO();
            List<MinimDTO> positions = new ArrayList<>();
            for (Joueur j : lobby.getJoueurs()) {
                MinimDTO m = new MinimDTO(
                    j.getId(),
                    (int)(j.getX() * 100),
                    (int)(j.getY() * 100),
                    j.getPv(),
                    j.getNb_bombes(),
                    j.getExplosionRange(),
                    j.getSpeed_multiplier()
                );

                positions.add(m);
            }
            updateDTO.positionsAll = positions;
            lobby.broadcast(updateDTO);

            // 3. Broadcast de l'état des bombes et explosions
            List<BombUpdate.BombDTO> bombDTOs = bombManager.getBombs().stream()
                    .map(b -> new BombUpdate.BombDTO(b.getX(), b.getY(), b.isSolid(), b.getJoueur() != null ? b.getJoueur().getId() : -1))
                    .collect(Collectors.toList());
            
            // Envoyer le labyrinthe actualisé seulement s'il y a des explosions actives (il a pu changer)
            iut.gon.bomberman.common.model.labyrinthe.Labyrinthe currentLab = null;
            if (!bombManager.getExplosionCells().isEmpty()) {
                currentLab = lobby.getLabyrinthe();
            }

            BombUpdate bombUpdate = new BombUpdate(bombDTOs, new ArrayList<>(bombManager.getExplosionCells()), currentLab);
            lobby.broadcast(bombUpdate);

            try {
                // Pause pour maintenir environ 60 FPS (16ms)
                Thread.sleep(16);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void stopGame() {
        this.running = false;
    }

    public BombManager getBombManager() {
        return bombManager;
    }
}
