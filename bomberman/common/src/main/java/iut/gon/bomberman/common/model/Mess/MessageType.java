package iut.gon.bomberman.common.model.Mess;

/**
 * Représente les différents types de message pouvant être envoyé entre le client et le serveur
 */
public enum MessageType {
    CREATE_LOBBY_REQUEST,
    CREATE_LOBBY_RESPONSE,
    JOIN_LOBBY_REQUEST,
    JOIN_LOBBY_RESPONSE,
    LOBBY_LIST_REQUEST,
    LOBBY_LIST_RESPONSE,
    LOBBY_DETAILS_REQUEST,
    LOBBY_DETAILS_RESPONSE,
    MOVE_REQUEST,
    CHAT_MESSAGE,
    READY_STATUS,
    START_GAME_REQUEST,
    GAME_UPDATE,
    INIT_GAME,
    COUNTDOWN_UPDATE,
    PLACE_BOMB_REQUEST, // Nouveau message pour la pose de bombe
    BOMB_UPDATE, // Nouveau message pour la mise à jour des bombes (peut être intégré à GAME_UPDATE)
    LEAVE_LOBBY_REQUEST
}
