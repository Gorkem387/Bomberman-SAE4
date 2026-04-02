package iut.gon.bomberman.common.model.Mess;

public class MoveRequest implements Message {
    private final double dx;
    private final double dy;

    public MoveRequest(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    // Getter
    @Override
    public MessageType getType() {
        return MessageType.MOVE_REQUEST;
    }

    public double getDx() { return dx; }
    public double getDy() { return dy; }
}
