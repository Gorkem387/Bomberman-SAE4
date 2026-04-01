package iut.gon.bomberman.client.network;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import iut.gon.bomberman.common.model.Mess.Message;
import iut.gon.bomberman.common.model.Mess.MessageType;
import javafx.application.Platform;

public class NetworkManager{

    private static NetworkManager network;
    private final Map<MessageType, List<ServerMessageListener>> listeners = new EnumMap<>(MessageType.class);    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected = false;
    private Thread listenerThread;

    private String localPlayerName;
    private int localPlayerId;
    private int currentLobbyId;


    private NetworkManager (){}

    public static synchronized NetworkManager getInstance(){
        if (network == null){
            network = new NetworkManager();
        }
        return network;
    }

    /**
     * Fonction qui permet de créer un socket pour connecter le client au serveur
     * @param host
     * @param port
     */
    public void connectToServer(String host, int port){
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Permet d'envoyer un message du client au serveur
     * @param message
     */
    public synchronized void send(Message message) {
        if (out != null && isConnected) {
            try{
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet au client de recevoir et de traiter les messages envoyés par le serveur
     */
    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                while (isConnected) {
                    Object obj = in.readObject();
                    if (obj instanceof Message message) {
                        Platform.runLater(() -> notifyListeners(message));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                isConnected = false;
                e.printStackTrace();
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Permet au client de se déconnecter du serveur
     */
    public void disconnect() {
        try {
            if (socket != null) socket.close();
            isConnected = false;
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'exécuter certaines fonctions selon le type de message reçut
     * @param type
     * @param l
     */
    public void addServerMessageListener(MessageType type, ServerMessageListener l) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(l);
    }

    /**
     * Permet de ne pas exécuter certaines fonctions selon le type de message reçut
     * @param type
     * @param l
     */
    public void removeServerMessageListener(MessageType type, ServerMessageListener l) {
        List<ServerMessageListener> list = listeners.get(type);
        if (list != null) list.remove(l);
    }

    /**
     * Permet de prévenir de la reception d'un message
     * @param message
     */
    private void notifyListeners(Message message) {
        List<ServerMessageListener> list = listeners.get(message.getType());
        if (list != null) {
            List<ServerMessageListener> copy = new ArrayList<>(list);
            copy.forEach(l -> l.onServerMessage(message));
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public int getLocalPlayerId() {
        return localPlayerId;
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public void setLocalPlayerId(int localPlayerId) {
        this.localPlayerId = localPlayerId;
    }

    public void setLocalPlayerName(String localPlayerName){
        this.localPlayerName = localPlayerName;
    }

    public int getCurrentLobbyId() {
        return currentLobbyId;
    }

    public void setCurrentLobbyId(int currentLobbyId) {
        this.currentLobbyId = currentLobbyId;
    }
}
