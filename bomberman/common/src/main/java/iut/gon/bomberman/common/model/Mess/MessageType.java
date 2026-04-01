package iut.gon.bomberman.common.model.Mess;

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
    START_GAME_REQUEST, // Ajouté pour le lancement manuel par l'owner
    GAME_UPDATE,
}
