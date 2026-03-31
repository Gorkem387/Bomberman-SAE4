package iut.gon.serverside.Threads;

import iut.gon.serverside.LobbyManager;
import iut.gon.serverside.Logger.LogTypes;
import iut.gon.serverside.Logger.Logger;
import iut.gon.serverside.Message.Message;
import iut.gon.serverside.Threads.PlayerInputHandling.MessageDispatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread principal qui attend et accepte les connexions des clients.
 * Il instancie le dispatcher qui sera partagé entre les clients.
 */
public class ThreadPrincipal {

    private static final int PORT = 3001;
    private static final Logger logger = Logger.getInstance();
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    // Le dispatcher est créé une seule fois et passé à chaque client
    private static final MessageDispatcher dispatcher = new MessageDispatcher();
    
    public LobbyManager lobbyManager = LobbyManager.getInstance();

    public ThreadPrincipal() {}

    public static void main(String[] args) {
        logger.log(LogTypes.INFO, "Démarrage du serveur sur le port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Attente d'une nouvelle connexion client
                Socket socket = serverSocket.accept();
                logger.log(LogTypes.INFO, "Nouveau client connecté: " + socket.getInetAddress());

                // Création du gestionnaire de client avec le dispatcher
                ClientHandler client = new ClientHandler(socket, dispatcher);
                clients.add(client);
                client.start();
            }
        } catch (IOException e) {
            logger.log(LogTypes.ERROR, "Erreur critique serveur: " + e.getMessage());
        }
    }

    /**
     * Envoie un message à tous les clients connectés.
     */
    public static void broadcast(Message message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.send(message);
            }
        }
    }

    /**
     * Supprime un client de la liste des clients actifs.
     */
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
