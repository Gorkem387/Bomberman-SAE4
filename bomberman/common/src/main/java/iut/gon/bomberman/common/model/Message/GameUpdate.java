package iut.gon.bomberman.common.model.message;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO (Data Transfer Object) léger pour la synchronisation en temps réel (UDP ou TCP).
 * Contient typiquement les positions des joueurs, l'état des bombes, etc.
 */
public class GameUpdate implements Message {
    private final Map<Integer, PlayerPositionDTO> playerPositions;

    public GameUpdate(Map<Integer, PlayerPositionDTO> playerPositions) {
        this.playerPositions = playerPositions;
    }

    @Override
    public MessageType getType() {
        return MessageType.GAME_UPDATE;
    }

    public Map<Integer, PlayerPositionDTO> getPlayerPositions() {
        return playerPositions;
    }

    // Classe interne pour le DTO de position
    public static class PlayerPositionDTO implements Serializable {
        public final int x;
        public final int y;
        public final String direction;

        public PlayerPositionDTO(int x, int y, String direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }
}
