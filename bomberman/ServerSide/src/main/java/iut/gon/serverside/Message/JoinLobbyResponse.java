package iut.gon.serverside.Message;

public class JoinLobbyResponse implements Message {
    private final boolean success;
    private final String message;
    private final int lobbyId;

    public JoinLobbyResponse(boolean success, String message, int lobbyId) {
        this.success = success;
        this.message = message;
        this.lobbyId = lobbyId;
    }

    @Override
    public MessageType getType() {
        return MessageType.JOIN_LOBBY_RESPONSE;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getLobbyId() {
        return lobbyId;
    }
}
