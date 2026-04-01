package iut.gon.serverside.Threads;

import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.Lob.Lobby;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.bomberman.common.model.Mess.Message;
import iut.gon.serverside.Threads.PlayerInputHandling.MessageDispatcher;

import java.io.*;
import java.net.Socket;

/**
 * Gère la communication réseau avec un client unique.
 * Ne contient aucune logique métier (déléguée au MessageDispatcher).
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final MessageDispatcher dispatcher;
    private final Logger logger = Logger.getInstance();

    private int playerId;
    private Joueur joueur;
    private int lobbyId = -1;

    public ClientHandler(Socket socket, MessageDispatcher dispatcher) {
        this.socket = socket;
        this.dispatcher = dispatcher;
        // Initialisation temporaire du Joueur avec un constructeur valide du module common
        this.joueur = new Joueur(-1, "");
    }

    public ClientHandler(Socket socket, MessageDispatcher dispatcher, int lobbyId) {
        this.socket = socket;
        this.dispatcher = dispatcher;
        // Initialisation temporaire du Joueur avec un constructeur valide du module common
        this.joueur = new Joueur(-1, "");
        this.lobbyId = lobbyId;
    }

    @Override
    public void run() {
        try {
            // Utilisation de flux binaire pour les objets Message
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());

            Object obj;
            while (!socket.isClosed() && (obj = in.readObject()) != null) {
                if (obj instanceof Message message) {
                    dispatcher.dispatch(message, this);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(LogTypes.ERROR, "Déconnexion ou erreur avec le client: " + socket.getInetAddress());
        } finally {
            disconnect();
        }
    }

    /**
     * Envoie un message sérialisé au client.
     * @param message L'objet message à envoyer.
     */
    public synchronized void send(Message message) {
        try {
            if (out != null) {
                out.reset();
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            logger.log(LogTypes.ERROR, "Erreur d'envoi de message au client.");
        }
    }

    /**
     * Ferme proprement les flux et la socket.
     */
    private void disconnect() {
        try {
            logger.log(LogTypes.INFO, "Déconnexion d'un client (" + (joueur != null ? joueur.getNom() : socket.getInetAddress()) + ").");

            // Si le joueur était dans un lobby
            if (lobbyId != -1) {
                LobbyManager lm = LobbyManager.getInstance();
                Lobby lobby = lm.getLobby(lobbyId);
                if (lobby != null) {
                    // Arrêter le thread de jeu si c'est le proprio qui part ou si c'était le dernier
                    if (lobby.getThread() != null) {
                        lobby.getThread().stopGame();
                    }
                    // Si c'est le propriétaire qui part, on supprime le lobby
                    if (lobby.getProprietaire() != null && lobby.getProprietaire().equals(this.joueur)) {
                        logger.log(LogTypes.WARNING, "Le propriétaire a quitté. Suppression du lobby: " + lobby.getNom());
                        lm.removeLobby(lobbyId);
                    } else {
                        lobby.removeJoueur(this.joueur);
                        // Si le lobby est vide après le départ
                        if (lobby.getJoueurs().isEmpty()) {
                            lm.removeLobby(lobbyId);
                        }
                    }
                }
            }

            this.lobbyId = -1;
            this.joueur = null;
            ThreadPrincipal.removeClient(this);
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            logger.log(LogTypes.ERROR, "Erreur lors de la déconnexion.");
        }
    }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public Joueur getJoueur() { return joueur; }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }
    public int getLobbyId() { return lobbyId; }
    public void setLobbyId(int lobbyId) { this.lobbyId = lobbyId; }
}
