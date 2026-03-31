package iut.gon.bomberman.common.model.message;

public class JoinLobbyRequest implements Message {
    private final int lobbyId;
    private final String playerName;

    public JoinLobbyRequest(int lobbyId, String playerName) {
        this.lobbyId = lobbyId;
        this.playerName = playerName;
    }

    @Override
    public MessageType getType() {
        return MessageType.JOIN_LOBBY_REQUEST;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public String getPlayerName() {
        return playerName;
    }
}
