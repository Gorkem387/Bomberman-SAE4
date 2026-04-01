package iut.gon.bomberman.common.model.Mess;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
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

    /**
     * Lit un GameUpdate depuis un DataInputStream en assumant le format compact utilisé par
     * le DTO léger : int size; pour i=0..size-1 { int id; int x; int y; }
     * NB : la lecture suppose que le code appelant a déjà lu l'entier type si présent.
     */
    public static GameUpdate read(DataInputStream in) throws IOException {
        int size = in.readInt();
        Map<Integer, PlayerPositionDTO> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            int id = in.readInt();
            int x = in.readInt();
            int y = in.readInt();
            map.put(id, new PlayerPositionDTO(x, y, null));
        }
        return new GameUpdate(map);
    }

    /**
     * Écrit ce GameUpdate dans un DataOutputStream en utilisant le format compact : int size; puis pour
     * chaque couple id -> position : int id, int x, int y
     */
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(playerPositions.size());
        for (Map.Entry<Integer, PlayerPositionDTO> e : playerPositions.entrySet()) {
            int id = e.getKey();
            PlayerPositionDTO p = e.getValue();
            out.writeInt(id);
            out.writeInt(p.x);
            out.writeInt(p.y);
        }
        out.flush();
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
