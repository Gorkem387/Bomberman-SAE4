package iut.gon.bomberman.common.model.Mess;

public class LobbyDetailsRequest implements Message {
    private final int lobbyId;

    public LobbyDetailsRequest(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    @Override
    public MessageType getType() {
        return MessageType.LOBBY_DETAILS_REQUEST;
    }

    public int getLobbyId() {
        return lobbyId;
    }
}
