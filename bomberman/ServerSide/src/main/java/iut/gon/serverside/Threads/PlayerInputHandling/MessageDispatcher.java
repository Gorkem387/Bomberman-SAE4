package iut.gon.serverside.Threads.PlayerInputHandling;

import iut.gon.bomberman.common.model.Message.Message;
import iut.gon.bomberman.common.model.Message.MessageType;
import iut.gon.serverside.Threads.ClientHandler;

import java.util.EnumMap;
import java.util.Map;

/**
 * Dispatcher qui contient toutes les stratégies de traitement de messages.
 * Il délègue l'exécution à la stratégie correspondant au type de message reçu.
 */
public class MessageDispatcher {
    private final Map<MessageType, MessageHandler<?>> handlers = new EnumMap<>(MessageType.class);

    public MessageDispatcher() {
        // Enregistrement des stratégies (Handlers)
        handlers.put(MessageType.CREATE_LOBBY_REQUEST, new CreateLobbyHandler());
        handlers.put(MessageType.JOIN_LOBBY_REQUEST, new JoinLobbyHandler());
        handlers.put(MessageType.MOVE_REQUEST, new MoveHandler());
        handlers.put(MessageType.CHAT_MESSAGE, new ChatMessageHandler());
        handlers.put(MessageType.READY_STATUS, new ReadyStatusHandler());
        
        // GAME_UPDATE est normalement envoyé du serveur vers le client, 
        // donc pas besoin de handler côté serveur pour cela (sauf si ACK attendu).
    }

    /**
     * Dispatch un message vers le handler approprié.
     */
    @SuppressWarnings("unchecked")
    public <T extends Message> void dispatch(T message, ClientHandler client) {
        MessageHandler<T> handler = (MessageHandler<T>) handlers.get(message.getType());
        if (handler != null) {
            handler.handle(message, client);
        } else {
            System.err.println("Aucun handler de stratégie trouvé pour le type: " + message.getType());
        }
    }
}
