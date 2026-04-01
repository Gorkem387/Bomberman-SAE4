package iut.gon.bomberman.common.model.DTO;

import iut.gon.bomberman.common.model.Mess.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MinimDTO implements IDTO {

    private int id;
    private int x;
    private int y;
    private int pv;
    private int nb_bombes;
    private int range;
    private float speed;

    public MinimDTO(int id, int x, int y, int pv, int nb_bombes, int range, float speed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.pv = pv;
        this.nb_bombes = nb_bombes;
        this.range = range;
        this.speed = speed;
    }

    public MinimDTO(int id, int x, int y) {
        this(id, x, y, 3, 1, 2, 1.0f); // legacy fallback
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

    public int getPv() { return pv; }
    public int getNb_bombes() { return nb_bombes; }
    public int getRange() { return range; }
    public float getSpeed() { return speed; }

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
        out.writeInt(pv);
        out.writeInt(nb_bombes);
        out.writeInt(range);
        out.writeFloat(speed);
    }

    public static MinimDTO read(DataInputStream in) throws IOException {
        MinimDTO dto = new MinimDTO(in.readInt(), in.readInt(), in.readInt());
        try {
            dto.pv = in.readInt();
            dto.nb_bombes = in.readInt();
            dto.range = in.readInt();
            dto.speed = in.readFloat();
        } catch(Exception e) {
            // legacy catch
        }
        return dto;
    }

    public MessageType getType(){
        return MessageType.GAME_UPDATE;
    }
}
