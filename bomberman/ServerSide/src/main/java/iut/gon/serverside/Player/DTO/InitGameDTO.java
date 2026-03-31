package iut.gon.serverside.Player.DTO;

import iut.gon.bomberman.common.model.message.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InitGameDTO implements IDTO{

    public static final int TYPE = 1;

    public String pseudo;
    public int id;
    public int skin;       // apparence choisie
    public int x;
    public int y;          // position initiale

    public InitGameDTO() {}

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(TYPE);
        out.writeUTF(pseudo);
        out.writeInt(id);
        out.writeInt(skin);
        out.writeInt(x);
        out.writeInt(y);
        out.flush();
    }

    public static InitGameDTO read(DataInputStream in) throws IOException {
        InitGameDTO dto = new InitGameDTO();
        dto.pseudo = in.readUTF();
        dto.id     = in.readInt();
        dto.skin   = in.readInt();
        dto.x      = in.readInt();
        dto.y      = in.readInt();
        return dto;
    }

    public MessageType getType(){
        return MessageType.GAME_UPDATE;
    }

}
