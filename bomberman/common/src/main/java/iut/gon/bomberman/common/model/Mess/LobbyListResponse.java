package iut.gon.bomberman.common.model.Mess;

import java.io.Serializable;
import java.util.List;

public class LobbyListResponse implements Message {
    private final List<LobbyDTO> lobbies;

    public LobbyListResponse(List<LobbyDTO> lobbies) {
        this.lobbies = lobbies;
    }

    @Override
    public MessageType getType() {
        return MessageType.LOBBY_LIST_RESPONSE;
    }

    public List<LobbyDTO> getLobbies() {
        return lobbies;
    }

    public static class LobbyDTO implements Serializable {
        public final int id;
        public final String name;
        public final int currentPlayers;
        public final int maxPlayers;

        public LobbyDTO(int id, String name, int currentPlayers, int maxPlayers) {
            this.id = id;
            this.name = name;
            this.currentPlayers = currentPlayers;
            this.maxPlayers = maxPlayers;
        }
    }
}
