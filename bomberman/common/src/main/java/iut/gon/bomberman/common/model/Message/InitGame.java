package iut.gon.bomberman.common.model.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Représente le message d'initialisation de la partie envoyé par le serveur
 * Contient les informations du joueur local (pseudo,id,skin,position) ainsi que
 * la liste complète des joueurs présents et leurs positions initiales.
 *
 * Le format binaire attendu (le type a déjà été lu par le dispatch) :
 * - UTF pseudoLocal
 * - int idLocal
 * - int skinLocal
 * - int xLocal
 * - int yLocal
 * - int nbPlayers
 * - pour chaque joueur : int id; UTF pseudo; int skin; int x; int y
 */
public class InitGame implements Message {

    public final String pseudoLocal;
    public final int idLocal;
    public final int skinLocal;
    public final int xLocal;
    public final int yLocal;

    // map id -> PlayerInitDTO
    private final Map<Integer, PlayerInitDTO> players;

    public InitGame(String pseudoLocal, int idLocal, int skinLocal, int xLocal, int yLocal, Map<Integer, PlayerInitDTO> players) {
        this.pseudoLocal = pseudoLocal;
        this.idLocal = idLocal;
        this.skinLocal = skinLocal;
        this.xLocal = xLocal;
        this.yLocal = yLocal;
        this.players = players != null ? players : new HashMap<>();
    }

    public Map<Integer, PlayerInitDTO> getPlayers() {
        return players;
    }

    @Override
    public MessageType getType() {
        return MessageType.INIT_GAME;
    }

    public static InitGame read(DataInputStream in) throws IOException {
        String pseudo = in.readUTF();
        int id = in.readInt();
        int skin = in.readInt();
        int x = in.readInt();
        int y = in.readInt();
        int size = in.readInt();
        Map<Integer, PlayerInitDTO> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            PlayerInitDTO p = PlayerInitDTO.read(in);
            map.put(p.id, p);
        }
        return new InitGame(pseudo, id, skin, x, y, map);
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(pseudoLocal != null ? pseudoLocal : "");
        out.writeInt(idLocal);
        out.writeInt(skinLocal);
        out.writeInt(xLocal);
        out.writeInt(yLocal);
        out.writeInt(players.size());
        for (PlayerInitDTO p : players.values()) {
            p.write(out);
        }
        out.flush();
    }

    public static class PlayerInitDTO implements Serializable {
        public final int id;
        public final String pseudo;
        public final int skin;
        public final int x;
        public final int y;

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
            int id = in.readInt();
            String pseudo = in.readUTF();
            int skin = in.readInt();
            int x = in.readInt();
            int y = in.readInt();
            return new PlayerInitDTO(id, pseudo, skin, x, y);
        }
    }
}
