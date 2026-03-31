package iut.gon.serverside.Player.DTO;

import iut.gon.bomberman.common.model.Message.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MinimDTO implements IDTO {

    private int id;
    private int x;
    private int y;

    public MinimDTO(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeInt(x);
        out.writeInt(y);
    }

    public static MinimDTO read(DataInputStream in) throws IOException {
        int id = in.readInt();
        int x = in.readInt();
        int y = in.readInt();
        return new MinimDTO(id, x, y);
    }

    public MessageType getType(){
        return MessageType.GAME_UPDATE;
    }
}
