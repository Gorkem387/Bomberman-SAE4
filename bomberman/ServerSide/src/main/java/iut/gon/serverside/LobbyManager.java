package iut.gon.serverside;

import iut.gon.bomberman.common.model.labyrinthe.TypeLab;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.Lob.Lobby;

import java.util.Collections;
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

    /**
     * Créer un lobby côté serveur
     * @param owner
     * @param nom_lobby
     * @param nbJMax
     * @param labyrinthe_type
     * @param lab_size_x
     * @param lab_size_y
     * @return
     */
    public synchronized Lobby createLobby(Joueur owner, String nom_lobby, int nbJMax, TypeLab labyrinthe_type, int lab_size_x, int lab_size_y) {
        int id = nextLobbyId++;
        Lobby lobby = new Lobby(id, nom_lobby, owner, nbJMax, labyrinthe_type, lab_size_x, lab_size_y);
        lobbies.put(id, lobby);
        return lobby;
    }

    /**
     * Supprime le lobby
     * @param lobbyId
     */
    public synchronized void removeLobby(int lobbyId) {
        lobbies.remove(lobbyId);
    }

    // Getter
    public synchronized Lobby getLobby(int id){
        return lobbies.get(id);
    }

    public synchronized Map<Integer, Lobby> getLobbies() {
        return Collections.unmodifiableMap(lobbies);
    }
}
