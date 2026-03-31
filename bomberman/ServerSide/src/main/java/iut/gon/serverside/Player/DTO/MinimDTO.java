package iut.gon.serverside.Player.DTO;

import iut.gon.bomberman.common.model.Message.MessageType;

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


    public MessageType getType(){
        return MessageType.GAME_UPDATE;
    }
}
