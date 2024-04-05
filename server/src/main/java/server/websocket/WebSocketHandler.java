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
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(userGameCommand, session);
            case JOIN_OBSERVER -> joinObs(action.visitorName());
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave(userGameCommand.getUserName());
            case RESIGN -> resign();
        }
    }

    private void joinPlayer(UserGameCommand userGameCommand, Session session) throws IOException {
        connections.add(userGameCommand.getGameId(), userGameCommand.getAuthString(), session);
        var message = String.format("%s has joined the game as %s", userGameCommand.getUserName(), userGameCommand.getColor());
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(userGameCommand.getAuthString(), notification);
    }

    private void leave(String authToken) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s has left the game.", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(visitorName, notification);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}