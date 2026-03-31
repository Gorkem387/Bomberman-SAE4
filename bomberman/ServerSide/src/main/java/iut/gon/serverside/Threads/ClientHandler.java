package iut.gon.serverside.Threads;

import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Lob.Lobby;
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
    }

    @Override
    public void run() {
        try {
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

    public synchronized void send(Message message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();
                out.reset();
            }
        } catch (IOException e) {
            logger.log(LogTypes.ERROR, "Erreur d'envoi de message au client.");
        }
    }

    /**
     * Ferme proprement les flux et la socket.
     * Si le client était propriétaire d'un lobby, celui-ci est supprimé.
     */
    private void disconnect() {
        try {
            logger.log(LogTypes.INFO, "Déconnexion d'un client (" + (joueur != null ? joueur.getNom() : socket.getInetAddress()) + ").");
            
            // Nettoyage des lobbies si nécessaire
            if (lobbyId != -1) {
                LobbyManager lm = LobbyManager.getInstance();
                Lobby lobby = lm.getLobby(lobbyId);
                
                if (lobby != null && joueur != null) {
                    // Si le joueur qui se déconnecte est le propriétaire, on supprime le lobby
                    if (lobby.getProprietaire() != null && lobby.getProprietaire().getId() == joueur.getId()) {
                        logger.log(LogTypes.WARNING, "Le propriétaire s'est déconnecté. Suppression du lobby: " + lobby.getNom());
                        lm.removeLobby(lobbyId);
                    } else {
                        // Sinon, on retire juste le joueur de la liste des invités du lobby
                        lobby.removeJoueur(joueur);
                        logger.log(LogTypes.INFO, "Le joueur " + joueur.getNom() + " a quitté le lobby " + lobby.getNom());
                    }
                }
            }

            ThreadPrincipal.removeClient(this);
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            logger.log(LogTypes.ERROR, "Erreur lors de la déconnexion d'un client.");
        }
    }

    // Getters et Setters
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public Joueur getJoueur() { return joueur; }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }
    public int getLobbyId() { return lobbyId; }
    public void setLobbyId(int lobbyId) { this.lobbyId = lobbyId; }
}
