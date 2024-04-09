package server.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final DatabaseService databaseService;
    private final GameService gameService;
    private final UserService userService;

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(DatabaseService databaseService, GameService gameService, UserService userService) {
        this.databaseService = databaseService;
        this.gameService = gameService;
        this.userService = userService;
    }

//    @OnWebSocketMessage
//    public void onMessage(Session session, String message) throws ResponseException {
//        UserGameCommand userGameCommandGen = new Gson().fromJson(message, UserGameCommand.class);
//
//        try {
//            switch (userGameCommandGen.getCommandType()) {
//                case JOIN_PLAYER -> joinPlayer(userGameCommandGen, session);
////                case JOIN_OBSERVER -> joinObs(action.visitorName());
////                case MAKE_MOVE -> makeMove();
//                case LEAVE -> leave(userGameCommandGen);
////                case RESIGN -> resign();
//            }
//        } catch (IOException i) {
//            throw new ResponseException(500, i.getMessage());
//        }
//    }
@OnWebSocketMessage
public void onMessage(Session session, String message) throws ResponseException {
    UserGameCommand userGameCommandGen = new Gson().fromJson(message, UserGameCommand.class);

    try {
        processUserGameCommand(userGameCommandGen, session);
    } catch (IOException i) {
        throw new ResponseException(500, i.getMessage());
    }
}

    private void processUserGameCommand(UserGameCommand userGameCommand, Session session) throws IOException {
        if (userGameCommand instanceof JoinPlayer) {
            JoinPlayer joinGameCommand = (JoinPlayer) userGameCommand;
            // Now you can access additional data specific to JoinGame using joinGameCommand
            joinPlayer(joinGameCommand, session);
        } else if (userGameCommand instanceof Leave) {
            leave((Leave) userGameCommand);
        } else {
            // Handle other command types if needed
        }
    }

//    private UserGameCommand convertUserGameCommand(UserGameCommand userGameCommandGen) {
//        switch (userGameCommandGen.getCommandType()) {
//            case JOIN_PLAYER -> {
//                JoinPlayer userGameCommand = (JoinPlayer) userGameCommandGen;
//                userGameCommand.setGameID(userGameCommand.getGameID());
//                return userGameCommand;
//            }
//            case LEAVE -> userGameCommandGen;
//            default -> return userGameCommandGen;
////                case JOIN_OBSERVER -> joinObs(action.visitorName());
//////                case MAKE_MOVE -> makeMove();
////            case LEAVE -> leave(userGameCommand);
////            case GET_GAME -> getGame(userGameCommand, session);
//////                case RESIGN -> resign();
////        }
//        }
//    }

    private void joinPlayer(UserGameCommand userGameCommandGen, Session session) throws IOException {
        JoinPlayer userGameCommand = (JoinPlayer) userGameCommandGen;
        var a = "a";
        userGameCommand.setGameID(userGameCommand.getGameID()); //TODO: Figure out if this is needed
        connections.add(userGameCommand.getGameID(), userGameCommand.getAuthString(), session);
        // TODO: Make a call to DAOs to get username
        var message = String.format("%s has joined the game as %s", userGameCommand.getAuthString(), userGameCommand.getPlayerColor());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(userGameCommand.getGameID(),userGameCommand.getAuthString(), notification);

        // TODO: Access DAOs to get most recent game, then Send LOAD_GAME

//        connections.send();
    }

    private void leave(UserGameCommand userGameCommandGen) throws IOException {
        Leave userGameCommand = (Leave) userGameCommandGen;
        userGameCommand.setGameID(userGameCommand.getGameID()); //TODO: Figure out if this is necessary
        connections.remove(userGameCommand.getGameID(), userGameCommand.getAuthString());
        // TODO: Make a call to DAOs to get username
        var message = String.format("%s has left the game.", userGameCommand.getAuthString());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(userGameCommand.getGameID(),userGameCommand.getAuthString(), notification);
    }

}