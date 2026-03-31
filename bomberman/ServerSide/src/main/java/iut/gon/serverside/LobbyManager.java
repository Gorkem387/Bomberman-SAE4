package iut.gon.serverside;

import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.Player.Joueur;
import iut.gon.serverside.Threads.ClientHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire des lobbies (Salons de jeu).
 * Implémenté en tant que Singleton pour être accessible depuis tous les Handlers.
 * Utilise des méthodes synchronisées pour garantir la sécurité entre les Threads.
 */
public class LobbyManager {
    private final Map<Integer, Lobby> lobbies = new HashMap<>();
    private int nextLobbyId = 1;

    // Instance unique du Singleton
    private static LobbyManager instance;

    // Constructeur privé pour empêcher l'instanciation directe
    private LobbyManager() {}

    /**
     * Retourne l'instance unique du LobbyManager.
     * Utilise le "Lazy Initialization" (instanciation au premier appel).
     */
    public static synchronized LobbyManager getInstance() {
        if (instance == null) {
            instance = new LobbyManager();
        }
        return instance;
    }

    public synchronized Lobby createLobby(Joueur owner, String nom_lobby, int nbJMax, LabyrintheType labyrinthe_type) {
        int id = nextLobbyId++;
        Lobby lobby = new Lobby(id, nom_lobby, owner, nbJMax, labyrinthe_type);
        lobbies.put(id, lobby);
        return lobby;
    }

    public synchronized void removeLobby(int lobbyId) {
        lobbies.remove(lobbyId);
    }

    public synchronized Lobby getLobby(int id){
        return lobbies.get(id);
    }

    public synchronized void joinLobby(int lobbyId, Joueur joueur){
        lobbies.get(lobbyId).addJoueur(joueur);
    }
}
