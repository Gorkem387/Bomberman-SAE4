package iut.gon.serverside.Threads;

import iut.gon.bomberman.common.model.labyrinthe.Bomb;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.CellType;
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

    // "volatile" est nécessaire car "running" est lu par Thread_Jeu et écrit par ClientHandler,
    // qui tournent sur deux threads différents. Sans volatile, Java ne garantit pas que la valeur
    // mise à jour par un thread soit immédiatement visible par l'autre (le CPU peut garder
    // une copie en cache). Avec volatile, toute écriture est instantanément visible par tous les threads.
    private volatile boolean running = true;

    private final Lobby lobby;
    private final BombManager bombManager; // Gestionnaire de bombes côté serveur
    private boolean mapChanged = false;

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

            //Securité pour fin de game
            List<Joueur> joueurs = lobby.getJoueurs();
            if (joueurs == null || joueurs.isEmpty()) {
                stopGame();
                break;
            }
            bombManager.update(deltaTime, lobby.getLabyrinthe(), joueurs);
            checkBonuses();

            // Broadcast des positions des joueurs
            JoueurMisAJourDTO updateDTO = new JoueurMisAJourDTO();
            List<MinimDTO> positions = new ArrayList<>();
            for (Joueur j : joueurs) {
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
            if (!bombManager.getExplosionCells().isEmpty() || mapChanged) {
                currentLab = lobby.getLabyrinthe();
                mapChanged = false;
            }

            BombUpdate bombUpdate = new BombUpdate(bombDTOs, new ArrayList<>(bombManager.getExplosionCells()), currentLab);
            lobby.broadcast(bombUpdate);

            // 4. Vérification de la condition de victoire
            long nbSurvivants = lobby.getJoueurs().stream().filter(Joueur::isAlive).count();
            if (nbSurvivants <= 1 && lobby.getJoueurs().size() > 1) {
                // On arrête la boucle, le client détectera la victoire tout seul grâce aux PVs mis à jour
                stopGame();
            }

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

    private void checkBonuses() {
        for (Joueur j : lobby.getJoueurs()) {
            if (!j.isAlive()) continue;
            // Calcule la case centrale du joueur
            int x = (int) Math.round(j.getX());
            int y = (int) Math.round(j.getY());

            CellType type = lobby.getLabyrinthe().getCell(x, y);
            if (isBonus(type)) {
                appliquerBonus(j, type);
                // Vide la case sur la carte du serveur
                lobby.getLabyrinthe().setCell(x, y, CellType.EMPTY);
                // Debug log
                this.mapChanged = true;
                System.out.println("[BONUS] " + j.getNom() + " ramasse " + type);
            }
        }
    }

    private void appliquerBonus(Joueur j, CellType type) {
        switch (type) {
            case SPEED_BONUS -> j.setSpeed_multiplier(j.getSpeed_multiplier() + 0.2f);
            case FIRE_BONUS  -> j.addExplosionRange();
            case BOMB_BONUS  -> {
                if (j.getNb_bombes_max() < 6) {
                    j.setNb_bombes_max(j.getNb_bombes_max() + 1);
                    j.setNb_bombes(j.getNb_bombes() + 1);
                }
            }
            case HEAL_BONUS  -> {
                if (j.getPv() < 3) j.setPv(j.getPv() + 1);
            }
        }
    }

    private boolean isBonus(CellType type) {
        return type == CellType.SPEED_BONUS || type == CellType.FIRE_BONUS
                || type == CellType.BOMB_BONUS || type == CellType.HEAL_BONUS;
    }
}
