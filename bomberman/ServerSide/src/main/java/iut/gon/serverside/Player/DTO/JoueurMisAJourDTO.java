package iut.gon.serverside.Player.DTO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JoueurMisAJourDTO implements IDTO{

    public static final int TYPE = 2;

    public int id;
    public int x;
    public int y;

    public JoueurMisAJourDTO(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(TYPE);
        out.writeInt(id);
        out.writeInt(x);
        out.writeInt(y);
        out.flush();
    }

    public static JoueurMisAJourDTO read(DataInputStream in) throws IOException {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO();
        dto.id = in.readInt();
        dto.x  = in.readInt();
        dto.y  = in.readInt();
        return dto;
    }
}
