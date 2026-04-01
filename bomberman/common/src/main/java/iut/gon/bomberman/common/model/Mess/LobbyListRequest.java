package iut.gon.bomberman.common.model.Mess;

public class LobbyListRequest implements Message {

    // Getter
    @Override
    public MessageType getType() {
        return MessageType.LOBBY_LIST_REQUEST;
    }
}
