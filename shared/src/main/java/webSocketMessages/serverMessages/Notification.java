package webSocketMessages.serverMessages;

public class Notification extends ServerMessage{
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Notification(ServerMessageType type, String message) {
        super(type, message);
        this.serverMessageType = ServerMessageType.NOTIFICATION;
    }
}
