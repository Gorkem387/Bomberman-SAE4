package iut.gon.bomberman.common.model.Mess;

import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InitGameMessage implements Message {
    private final int lobbyId;
    private final Labyrinthe labyrinthe; 
    private final List<PlayerInitDTO> players;

    public InitGameMessage(int lobbyId, Labyrinthe labyrinthe, List<PlayerInitDTO> players) {
        this.lobbyId = lobbyId;
        this.labyrinthe = labyrinthe;
        this.players = new ArrayList<>(players); // On s'assure que la liste est sérialisable et propre
    }

    @Override
    public MessageType getType() {
        return MessageType.INIT_GAME;
    }

    public int getLobbyId() { return lobbyId; }
    public Labyrinthe getLabyrinthe() { return labyrinthe; }
    public List<PlayerInitDTO> getPlayers() { return players; } // Already unmodifiable

    @Override
    public String toString() {
        return "InitGameMessage{" +
               "lobbyId=" + lobbyId +
               ", labyrinthe=" + (labyrinthe != null ? labyrinthe.getWidth() + "x" + labyrinthe.getHeight() : "null") +
               ", players=" + players +
               '}';
    }


    public static class PlayerInitDTO implements Serializable {
        public final int id;
        public final String name;
        public final double startX;
        public final double startY;

        public PlayerInitDTO(int id, String name, double startX, double startY) {
            this.id = id;
            this.name = name;
            this.startX = startX;
            this.startY = startY;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PlayerInitDTO that = (PlayerInitDTO) o;

            if (id != that.id) return false;
            if (Double.compare(that.startX, startX) != 0) return false;
            if (Double.compare(that.startY, startY) != 0) return false;
            return name != null ? name.equals(that.name) : that.name == null;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = id;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            temp = Double.doubleToLongBits(startX);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(startY);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
}
