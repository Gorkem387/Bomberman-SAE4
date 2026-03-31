package iut.gon.serverside.Threads;

import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.bomberman.common.model.message.Message;
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
    public int playerId;
    public Joueur joueur;
    private int lobbyId;

    public ClientHandler(Socket socket, MessageDispatcher dispatcher) {
        this.socket = socket;
        this.dispatcher = dispatcher;
        this.joueur = new Joueur(this);
    }

    public ClientHandler(Socket socket, MessageDispatcher dispatcher, int lobbyId) {
        this.socket = socket;
        this.dispatcher = dispatcher;
        this.joueur = new Joueur(this);
        //todo : donner l'id du lobby qlq part au constructeur
        this.lobbyId = lobbyId;
    }

    @Override
    public void run() {
        try {
            // Utilisation de flux binaire pour les objets Message
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            Object obj;
            // Lecture continue des messages envoyés par le client
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Message) {
                    // Pattern STRATÉGIE : On délègue totalement le traitement au dispatcher
                    dispatcher.dispatch((Message) obj, this);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(LogTypes.ERROR, "Perte de connexion avec le client: " + socket.getInetAddress());
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
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            logger.log(LogTypes.ERROR, "Erreur d'envoi de message au client.");
        }
    }

    /**
     * Ferme proprement les flux et la socket.
     */
    private void disconnect() {
        try {
            logger.log(LogTypes.INFO, "Déconnexion d'un client.");
            ThreadPrincipal.removeClient(this);
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            logger.log(LogTypes.ERROR, "Erreur lors de la déconnexion.");
        }
    }

    private int getPlayerId(){
        return playerId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getLobbyId() {
        return lobbyId;
    }
}
