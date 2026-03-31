package iut.gon.serverside.Message;

public class CreateLobbyRequest implements Message {
    private final String lobbyName;
    private final int maxPlayers;
    private LabyrintheType labyrintheType;

    public CreateLobbyRequest(String lobbyName, int maxPlayers, LabyrintheType labType) {
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.labyrintheType = labType;
    }

    @Override
    public MessageType getType() {
        return MessageType.CREATE_LOBBY_REQUEST;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public LabyrintheType getLabyrintheType() {
        return labyrintheType;
    }
}
