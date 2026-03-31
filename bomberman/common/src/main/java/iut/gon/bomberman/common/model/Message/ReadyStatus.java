package iut.gon.bomberman.common.model.message;

public class ReadyStatus implements Message {
    private final boolean ready;
    private final int lobbyId;

    public ReadyStatus(boolean ready, int lobbyId) {
        this.ready = ready;
        this.lobbyId = lobbyId;
    }

    @Override
    public MessageType getType() {
        return MessageType.READY_STATUS;
    }

    public boolean isReady() { return ready; }
    public int getLobbyId() { return lobbyId; }
}
