package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DBGameDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
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
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
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
                case MAKE_MOVE -> {
                    MakeMove userGameCommand = new Gson().fromJson(message, MakeMove.class);
                    makeMove(userGameCommand);
                }
//                case RESIGN -> resign();
            }
        } catch (IOException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }

    private void joinPlayer(JoinPlayer userGameCommand, Session session) throws ResponseException {
        try {
            connections.add(userGameCommand.getGameID(), userGameCommand.getAuthString(), session);

            var message = String.format("%s has joined the game as %s", userGameCommand.getAuthString(), userGameCommand.getPlayerColor());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(message);
            // TODO: See if this works
            connections.broadcast(userGameCommand.getGameID(),userGameCommand.getAuthString(), notification);

            GameData game = getGame(userGameCommand.getAuthString(), userGameCommand.getGameID());
            LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game.getGame());
            connections.send(game.gameID(), userGameCommand.getAuthString(), loadGame);

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

            GameData game = getGame(userGameCommand.getAuthString(), userGameCommand.getGameID());
            LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game.getGame());
            connections.send(game.gameID(), userGameCommand.getAuthString(), loadGame);
            // TODO: Throw new type of exception that gets turned into ServerMessage.Error
            throw new ResponseException(500, "Can't find game to join");
        } catch (IOException | DataAccessException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }

    private void makeMove(MakeMove userGameCommand) throws ResponseException {

        try {
            // Validate move
            ChessMove propMove = userGameCommand.getMove();
            GameData propGame = getGame(userGameCommand.getAuthString(), userGameCommand.getGameID());

            // Check if valid and correct team color is done in makeMove()
            // Changing turns is done in makeMove()
            propGame.getGame().makeMove(propMove);

            // Make move in DB
            GameDAO gameDAO = new DBGameDAO();
            gameDAO.updateGames(propGame);

            // send and broadcast (or broadcast with null authtoken)
            GameData game = getGame(userGameCommand.getAuthString(), userGameCommand.getGameID());
            if (propGame != game) {
                throw new ResponseException(500, "Update for game failed in DB");
            }

            LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game.getGame());
            connections.broadcast(game.gameID(), null, loadGame);
            // Alternately, broadcast and send
        } catch (DataAccessException | IOException i) {
            throw new ResponseException(500, i.getMessage());
        } catch (InvalidMoveException e) {
            throw new ResponseException(500, e.getMessage());
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

    private GameData getGame(String authString, Integer gameID) throws ResponseException, DataAccessException {
        List<GameData> games = gameService.listGames(new AuthData(authString, null));
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new ResponseException(500, "GameID wasn't found");
    }
}