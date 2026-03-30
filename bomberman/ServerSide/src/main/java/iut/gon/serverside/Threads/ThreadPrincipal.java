package iut.gon.serverside.Threads;

import iut.gon.serverside.LobbyManager;

import java.io.*;
import java.net.*;
import java.util.*;

public class ThreadPrincipal {

    private static final int PORT = 3001;

    public LobbyManager lobbyManager;

    public ThreadPrincipal() {
        this.lobbyManager = new LobbyManager();
    }

    private static final List<ClientHandler> clients =
            Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                clients.add(client);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Diffusion à tous les clients
    public static void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.send(message);
            }
        }
    }

    // Suppression d’un client
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}