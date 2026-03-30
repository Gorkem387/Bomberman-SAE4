package iut.gon.serverside;

import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Threads.ClientHandler;

import java.util.HashMap;
import java.util.Map;

public class LobbyManager {
    private final Map<Integer, Lobby> lobbies = new HashMap<>();
    private int nextLobbyId = 1;

    /*public synchronized Lobby createLobby(ClientHandler owner,String nom_lobby, int nbJMax, LabyrintheType labyrinthe_type) {
        int id = nextLobbyId++;
        Lobby lobby = new Lobby(id,nom_lobby, owner, nbJMax, labyrinthe_type);
        lobbies.put(id, lobby);
        return lobby;
    }*/

    public synchronized void removeLobby(int lobbyId) {
        lobbies.remove(lobbyId);
    }


}
