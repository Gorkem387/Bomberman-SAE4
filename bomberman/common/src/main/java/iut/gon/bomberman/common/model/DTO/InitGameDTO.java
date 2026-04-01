package iut.gon.bomberman.common.model.DTO;

import iut.gon.bomberman.common.model.Mess.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InitGameDTO implements IDTO{

    public static final int TYPE = 1;

    // Liste complète des joueurs au démarrage de la partie
    public List<PlayerInitDTO> positionsAll = new ArrayList<>();
    // Informations concernant ce joueur (pseudo, id, skin et position)
    public String pseudo;
    public int id;
    public int skin;
    public MinimDTO minimDTO;

    public InitGameDTO() {}

    public InitGameDTO(String pseudo, int id, int skin, MinimDTO minimDTO, List<PlayerInitDTO> positionsAll) {
        this.pseudo = pseudo;
        this.id = id;
        this.skin = skin;
        this.minimDTO = minimDTO;
        this.positionsAll = positionsAll != null ? positionsAll : new ArrayList<>();
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(TYPE);
        // write local player info
        out.writeUTF(pseudo != null ? pseudo : "");
        out.writeInt(id);
        out.writeInt(skin);
        // write local position
        if (minimDTO != null) minimDTO.write(out); else { out.writeInt(-1); out.writeInt(-1); }
        // write number of players
        out.writeInt(positionsAll.size());
        for (PlayerInitDTO p : positionsAll) {
            p.write(out);
        }
        out.flush();
    }

    public static InitGameDTO read(DataInputStream in) throws IOException {
        InitGameDTO dto = new InitGameDTO();
        dto.pseudo = in.readUTF();
        dto.id     = in.readInt();
        dto.skin   = in.readInt();
        int posX = in.readInt();
        int posY = in.readInt();
        if (posX >= 0 && posY >= 0) dto.minimDTO = new MinimDTO(dto.id, posX, posY);
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            dto.positionsAll.add(PlayerInitDTO.read(in));
        }
        return dto;
    }

    public MessageType getType(){
        return MessageType.INIT_GAME;
    }

    // DTO auxiliaire décrivant un joueur complet à l'initialisation
    public static class PlayerInitDTO {
        public int id;
        public String pseudo;
        public int skin;
        public int x;
        public int y;

        public PlayerInitDTO() {}

        public PlayerInitDTO(int id, String pseudo, int skin, int x, int y) {
            this.id = id;
            this.pseudo = pseudo;
            this.skin = skin;
            this.x = x;
            this.y = y;
        }

        public void write(DataOutputStream out) throws IOException {
            out.writeInt(id);
            out.writeUTF(pseudo != null ? pseudo : "");
            out.writeInt(skin);
            out.writeInt(x);
            out.writeInt(y);
        }

        public static PlayerInitDTO read(DataInputStream in) throws IOException {
            PlayerInitDTO p = new PlayerInitDTO();
            p.id = in.readInt();
            p.pseudo = in.readUTF();
            p.skin = in.readInt();
            p.x = in.readInt();
            p.y = in.readInt();
            return p;
        }
    }
}
