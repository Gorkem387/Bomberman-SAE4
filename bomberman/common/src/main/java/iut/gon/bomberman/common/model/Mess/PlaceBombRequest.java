package iut.gon.bomberman.common.model.Mess;

public class PlaceBombRequest implements Message {
    @Override
    public MessageType getType() {
        return MessageType.PLACE_BOMB_REQUEST;
    }
}
