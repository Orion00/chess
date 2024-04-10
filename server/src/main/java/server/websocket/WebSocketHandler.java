package server.websocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.List;

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

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException {
        UserGameCommand userGameCommandGen = new Gson().fromJson(message, UserGameCommand.class);

        try {
            switch (userGameCommandGen.getCommandType()) {
                case JOIN_PLAYER -> {
                    JoinPlayer userGameCommand = new Gson().fromJson(message, JoinPlayer.class);
                    joinPlayer(userGameCommand, session);
                }
                case JOIN_OBSERVER -> {
                    JoinObserver userGameCommand = new Gson().fromJson(message, JoinObserver.class);
                    joinObserver(userGameCommand, session);
                }
                case LEAVE -> {
                    Leave userGameCommand = new Gson().fromJson(message, Leave.class);
                    leave(userGameCommand);
                }
//                case JOIN_OBSERVER -> joinObs(action.visitorName());
//                case MAKE_MOVE -> makeMove();
//                case RESIGN -> resign();
            }
        } catch (IOException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }
//

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

    private void joinPlayer(JoinPlayer userGameCommand, Session session) throws ResponseException {
        try {
            connections.add(userGameCommand.getGameID(), userGameCommand.getAuthString(), session);

            var message = String.format("%s has joined the game as %s", userGameCommand.getAuthString(), userGameCommand.getPlayerColor());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(message);
            // TODO: See if this works
            connections.broadcast(userGameCommand.getGameID(),userGameCommand.getAuthString(), notification);

            List<GameData> games = gameService.listGames(new AuthData(userGameCommand.getAuthString(), null));
            for (GameData game : games) {
                if (game.gameID() == userGameCommand.getGameID()) {
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME);
                    loadGame.setGame(game.getGame());
                    connections.send(game.gameID(), userGameCommand.getAuthString(), loadGame);
                    return;
                }
            }
            throw new ResponseException(500, "Can't find game to join");
        } catch (IOException | DataAccessException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }

    private void joinObserver(JoinObserver userGameCommand, Session session) throws ResponseException {
        try {
            connections.add(userGameCommand.getGameID(), userGameCommand.getAuthString(), session);

            var message = String.format("%s has started watching the game.", userGameCommand.getAuthString());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(message);
            // TODO: See if this works
            connections.broadcast(userGameCommand.getGameID(),userGameCommand.getAuthString(), notification);

            List<GameData> games = gameService.listGames(new AuthData(userGameCommand.getAuthString(), null));
            for (GameData game : games) {
                if (game.gameID() == userGameCommand.getGameID()) {
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME);
                    loadGame.setGame(game.getGame());
                    connections.send(game.gameID(), userGameCommand.getAuthString(), loadGame);
                    return;
                }
            }
            throw new ResponseException(500, "Can't find game to join");
        } catch (IOException | DataAccessException i) {
            throw new ResponseException(500, i.getMessage());
        }
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