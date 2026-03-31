package iut.gon.bomberman.common.model.Mess;

import iut.gon.bomberman.common.model.labyrinthe.TypeLab;

public class CreateLobbyRequest implements Message {
    private final String lobbyName;
    private final int maxPlayers;
    private TypeLab labyrintheType;
    private int sizeX;
    private int sizeY;

    public CreateLobbyRequest(String lobbyName, int maxPlayers, TypeLab labType, int sizeX, int sizeY) {
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.labyrintheType = labType;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
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

    public TypeLab getLabyrintheType() {
        return labyrintheType;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }
}
