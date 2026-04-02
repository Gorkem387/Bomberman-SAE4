package iut.gon.bomberman.common.model.Mess;

public class ChatMessage implements Message {
    private final String senderName;
    private final String content;
    private final int lobbyId; // Si le message est envoyé dans un lobby

    public ChatMessage(String senderName, String content, int lobbyId) {
        this.senderName = senderName;
        this.content = content;
        this.lobbyId = lobbyId;
    }

    // Getter

    @Override
    public MessageType getType() {
        return MessageType.CHAT_MESSAGE;
    }

    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
    public int getLobbyId() { return lobbyId; }
}
