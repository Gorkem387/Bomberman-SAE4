package iut.gon.bomberman.common.model.Mess;

public class CountdownUpdate implements Message {
    private final int remainingSeconds;

    public CountdownUpdate(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    @Override
    public MessageType getType() {
        return MessageType.COUNTDOWN_UPDATE;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }
}
