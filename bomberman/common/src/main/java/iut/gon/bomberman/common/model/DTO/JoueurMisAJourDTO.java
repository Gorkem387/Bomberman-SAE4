package iut.gon.bomberman.common.model.DTO;

import iut.gon.bomberman.common.model.Mess.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import iut.gon.bomberman.common.model.Mess.Message;

public class JoueurMisAJourDTO implements IDTO, Message {

    public static final int TYPE = 2;

    public int id;
    public int x;
    public int y;
    public List<MinimDTO> positionsAll = null;

    public JoueurMisAJourDTO() {
        this.positionsAll = new ArrayList<>();
    }

    public JoueurMisAJourDTO(List<MinimDTO> positionsAll) {
        this.positionsAll = positionsAll;
    }

    public JoueurMisAJourDTO(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(TYPE);
        // write number of players
        out.writeInt(positionsAll.size());
        for (MinimDTO m : positionsAll) {
            m.write(out);
        }
        out.flush();
    }

    public static JoueurMisAJourDTO read(DataInputStream in) throws IOException {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            dto.positionsAll.add(MinimDTO.read(in));
        }
        return dto;
    }

    public MessageType getType(){
        return MessageType.GAME_UPDATE;
    }
}
