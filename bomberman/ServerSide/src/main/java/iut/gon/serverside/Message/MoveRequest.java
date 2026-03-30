package iut.gon.serverside.Message;

public class MoveRequest implements Message {
    private final int x;
    private final int y;
    private final String direction; // "UP", "DOWN", "LEFT", "RIGHT"

    public MoveRequest(int x, int y, String direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    @Override
    public MessageType getType() {
        return MessageType.MOVE_REQUEST;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getDirection() { return direction; }
}
