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

    public BombUpdate(List<BombDTO> activeBombs, List<int[]> activeExplosions) {
        this.activeBombs = activeBombs;
        this.activeExplosions = activeExplosions;
    }

    @Override
    public MessageType getType() {
        return MessageType.BOMB_UPDATE;
    }

    public List<BombDTO> getActiveBombs() { return activeBombs; }
    public List<int[]> getActiveExplosions() { return activeExplosions; }

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
