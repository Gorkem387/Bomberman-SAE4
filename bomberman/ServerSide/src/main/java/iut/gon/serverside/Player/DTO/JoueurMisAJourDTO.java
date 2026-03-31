package iut.gon.serverside.Player.DTO;

import iut.gon.bomberman.common.model.Message.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JoueurMisAJourDTO implements IDTO{

    public static final int TYPE = 2;

    public List<MinimDTO> positionsAll = null;

    public JoueurMisAJourDTO() {
        this.positionsAll = new ArrayList<>();
    }

    public JoueurMisAJourDTO(List<MinimDTO> positionsAll) {
        this.positionsAll = positionsAll;
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
