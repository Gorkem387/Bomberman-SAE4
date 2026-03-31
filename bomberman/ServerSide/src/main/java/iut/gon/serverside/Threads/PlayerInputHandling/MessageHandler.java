package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.serverside.Message.Message;
import iut.gon.serverside.Threads.ClientHandler;

/**
 * Interface Stratégie pour le traitement d'un message spécifique.
 * @param <T> Le type de message géré.
 */
public interface MessageHandler<T extends Message> {
    void handle(T message, ClientHandler client);
}
