package iut.gon.bomberman.common.model.Mess;

import iut.gon.bomberman.common.model.player.Joueur;

import java.io.Serializable;
import java.util.List;

/**
 * Message contenant l'état actuel de toutes les bombes et explosions du terrain.
 */
public class BombUpdate implements Message {
    private final List<BombDTO> activeBombs;
    private final List<int[]> activeExplosions; // [x, y] pour chaque cellule
    private final iut.gon.bomberman.common.model.labyrinthe.Labyrinthe labyrinthe; // null if unchanged

    public BombUpdate(List<BombDTO> activeBombs, List<int[]> activeExplosions, iut.gon.bomberman.common.model.labyrinthe.Labyrinthe labyrinthe) {
        this.activeBombs = activeBombs;
        this.activeExplosions = activeExplosions;
        this.labyrinthe = labyrinthe;
    }

    public BombUpdate(List<BombDTO> activeBombs, List<int[]> activeExplosions) {
        this.activeBombs = activeBombs;
        this.activeExplosions = activeExplosions;
        this.labyrinthe = null;
    }

    @Override
    public MessageType getType() {
        return MessageType.BOMB_UPDATE;
    }

    public List<BombDTO> getActiveBombs() {
        return activeBombs;
    }

    public List<int[]> getActiveExplosions() {
        return activeExplosions;
    }

    public iut.gon.bomberman.common.model.labyrinthe.Labyrinthe getLabyrinthe() {
        return labyrinthe;
    }

    public static class BombDTO implements Serializable {
        public final int x;
        public final int y;
        public final boolean isSolid;
        public final int playerId;

        public BombDTO(int x, int y, boolean isSolid, int playerId) {
            this.x = x;
            this.y = y;
            this.isSolid = isSolid;
            this.playerId = playerId;
        }
    }
}
