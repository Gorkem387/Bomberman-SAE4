package iut.gon.bomberman.common.model.Mess;

public class LeaveLobbyRequest implements Message {
    private final int lobbyId;

    public LeaveLobbyRequest(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getLobbyId() { return lobbyId; }

    @Override
    public MessageType getType() {
        return MessageType.LEAVE_LOBBY_REQUEST;
    }
}
