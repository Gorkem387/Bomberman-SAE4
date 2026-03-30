package iut.gon.serverside.Player.DTO;

import iut.gon.serverside.Message.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JoueurMisAJourDTO implements IDTO{

    public static final int TYPE = 2;

    public List<MinimDTO> positionsAll = null;

    public JoueurMisAJourDTO() {
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(TYPE);
        out.writeInt(id);
        out.writeInt(x);
        out.writeInt(y);
        out.flush();
    }

    /*
    public static JoueurMisAJourDTO read(DataInputStream in) throws IOException {
        return new JoueurMisAJourDTO(in.readInt(), in.readInt(), in.readInt());
    }*/

    public MessageType getType(){
        return MessageType.GAME_UPDATE;
    }
}
