package iut.gon.bomberman.common.model.Mess;

public class StartGameRequest implements Message {
    private final int lobbyId;

    public StartGameRequest(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    // Getter
    @Override
    public MessageType getType() {
        return MessageType.START_GAME_REQUEST;
    }

    public int getLobbyId() {
        return lobbyId;
    }
}
