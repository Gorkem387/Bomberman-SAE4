package iut.gon.bomberman.common.model.Mess;

public class LobbyListRequest implements Message {
    @Override
    public MessageType getType() {
        return MessageType.LOBBY_LIST_REQUEST;
    }
}
