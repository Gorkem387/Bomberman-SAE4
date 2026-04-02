package iut.gon.bomberman.common.model.Mess;

import java.io.Serializable;
import java.util.List;

public class LobbyDetailsResponse implements Message {
    private final int lobbyId;
    private final String lobbyName;
    private final int maxPlayers;
    private final PlayerDTO owner; // L'owner
    private final List<PlayerDTO> players; // Tous les joueurs

    public LobbyDetailsResponse(int lobbyId, String lobbyName, int maxPlayers, PlayerDTO owner, List<PlayerDTO> players) {
        this.lobbyId = lobbyId;
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.owner = owner;
        this.players = players;
    }

    @Override
    public MessageType getType() {
        return MessageType.LOBBY_DETAILS_RESPONSE;
    }

    // Getter
    public int getLobbyId() { return lobbyId; }
    public String getLobbyName() { return lobbyName; }
    public int getMaxPlayers() { return maxPlayers; }
    public PlayerDTO getOwner() { return owner; }
    public List<PlayerDTO> getPlayers() { return players; }

    // DTO pour représenter un joueur dans le lobby
    public static class PlayerDTO implements Serializable {
        public final int id;
        public final String name;
        public final boolean isReady;
        public final boolean isOwner; // Pour l'affichage distinct

        public PlayerDTO(int id, String name, boolean isReady, boolean isOwner) {
            this.id = id;
            this.name = name;
            this.isReady = isReady;
            this.isOwner = isOwner;
        }

        @Override
        public String toString() {
            String status = isReady ? " [PRÊT]" : " [PAS PRÊT]";
            String ownerTag = isOwner ? " ⭐" : "";
            return name + ownerTag + status;
        }
    }
}
