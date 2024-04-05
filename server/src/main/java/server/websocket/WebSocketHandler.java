package server.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.io.ConnectionManager;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (userGameCommand.getCommandType()) {
                case JOIN_PLAYER -> joinPlayer(userGameCommand, session);
                case JOIN_OBSERVER -> joinObs(action.visitorName());
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave(userGameCommand);
                case RESIGN -> resign();
            }
        } catch (IOException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }

    private void joinPlayer(UserGameCommand userGameCommand, Session session) throws IOException {
        connections.add(userGameCommand.getGameId(), userGameCommand.getAuthString(), session);
        var message = String.format("%s has joined the game as %s", userGameCommand.getUserName(), userGameCommand.getColor());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(userGameCommand.getAuthString(), notification);
    }

    private void leave(UserGameCommand userGameCommand) throws IOException {
        connections.remove(userGameCommand.getGameId(), userGameCommand.getAuthString());
        var message = String.format("%s has left the game.", userGameCommand.getUserName());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(userGameCommand.getAuthString(), notification);
    }

}