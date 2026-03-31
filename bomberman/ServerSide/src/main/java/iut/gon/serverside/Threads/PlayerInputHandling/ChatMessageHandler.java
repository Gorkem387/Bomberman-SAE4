package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Mess.ChatMessage;
import iut.gon.serverside.Threads.ClientHandler;
import iut.gon.serverside.Threads.ThreadPrincipal;

public class ChatMessageHandler implements MessageHandler<ChatMessage> {

    @Override
    public void handle(ChatMessage message, ClientHandler client) {
        System.out.println("Message de chat reçu de " + message.getSenderName() + ": " + message.getContent());
        
        // Rediffusion du message (broadcast) à tous les clients
        // On pourrait limiter au lobby concerné via message.getLobbyId()
        ThreadPrincipal.broadcast(message);
    }
}
