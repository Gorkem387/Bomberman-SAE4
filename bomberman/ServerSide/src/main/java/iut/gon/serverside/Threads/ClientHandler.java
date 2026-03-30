package iut.gon.serverside.Threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = in.readLine()) != null) {
                ThreadPrincipal.broadcast(message);
            }
        } catch (IOException e) {
            System.out.println("Erreur avec un client.");
        } finally {
            disconnect();
        }
    }

    public void send(String message) {
        out.println(message);
    }

    private void disconnect() {
        try {
            ThreadPrincipal.broadcast("Joueur a quitté la discussion.");
            ThreadPrincipal.removeClient(this);
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

