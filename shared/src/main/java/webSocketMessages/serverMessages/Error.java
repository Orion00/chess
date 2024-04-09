package webSocketMessages.serverMessages;

public class Error extends ServerMessage{
    String errorMessage;
    public Error(ServerMessageType type) {
        super(type);
        this.serverMessageType = ServerMessageType.ERROR;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
