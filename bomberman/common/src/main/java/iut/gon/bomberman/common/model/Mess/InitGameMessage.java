package iut.gon.bomberman.common.model.Mess;

import java.io.Serializable;
import java.util.List;

public class InitGameMessage implements Message {
    private final int lobbyId;
    private final String mapData; // Optionnel : données de la carte
    private final List<PlayerInitDTO> players;

    public InitGameMessage(int lobbyId, String mapData, List<PlayerInitDTO> players) {
        this.lobbyId = lobbyId;
        this.mapData = mapData;
        this.players = players;
    }

    @Override
    public MessageType getType() {
        return MessageType.INIT_GAME;
    }

    public int getLobbyId() { return lobbyId; }
    public String getMapData() { return mapData; }
    public List<PlayerInitDTO> getPlayers() { return players; }

    public static class PlayerInitDTO implements Serializable {
        public final int id;
        public final String name;
        public final double startX;
        public final double startY;

        public PlayerInitDTO(int id, String name, double startX, double startY) {
            this.id = id;
            this.name = name;
            this.startX = startX;
            this.startY = startY;
        }
    }
}
