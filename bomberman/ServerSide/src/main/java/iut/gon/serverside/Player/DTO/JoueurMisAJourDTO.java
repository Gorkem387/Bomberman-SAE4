package iut.gon.serverside.Player.DTO;

import iut.gon.bomberman.common.model.Mess.MessageType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class JoueurMisAJourDTO implements IDTO {

    public static final int TYPE = 2;

    public int id;
    public int x;
    public int y;
    public List<MinimDTO> positionsAll = null;

    public JoueurMisAJourDTO() {
    }

    /**
     * Met à jour la position du joueur
     * @param id
     * @param x
     * @param y
     */
    public JoueurMisAJourDTO(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    /**
     * Permet d'envoyer les données d'un joueur aux clients
     * @param out
     * @throws IOException
     */
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

    // Getter
    public MessageType getType(){
        return MessageType.GAME_UPDATE;
    }
}
