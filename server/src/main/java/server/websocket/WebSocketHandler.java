package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
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
import service.GameService;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.List;

@WebSocket
public class WebSocketHandler {
    private final GameService gameService;

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
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
                case RESIGN -> {
                    Resign userGameCommand = new Gson().fromJson(message, Resign.class);
                    checkIfGameOver(userGameCommand.getAuthString(), userGameCommand.getGameID());
                    resign(userGameCommand);
                }
                case MAKE_MOVE -> {
                    MakeMove userGameCommand = new Gson().fromJson(message, MakeMove.class);
                    checkIfGameOver(userGameCommand.getAuthString(), userGameCommand.getGameID());
                    makeMove(userGameCommand);
                }
            }
        } catch (IOException | ResponseException | DataAccessException i) {
            Error error = new Error(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage(i.getMessage());
            session.getRemote().sendString(error.toString());
        }
    }

    private void resign(Resign userGameCommand) throws DataAccessException, ResponseException, IOException {
        if (userGameCommand.getPlayerColor() == null) {
            throw new ResponseException(400, "Observers can't resign.");
        }
        connections.remove(userGameCommand.getGameID(), userGameCommand.getAuthString());
        GameDAO gameDAO = new DBGameDAO();
        GameData game = gameDAO.getGame(userGameCommand.getGameID());
        ChessGame updatedGame = game.getGame();
        String message;
        if (userGameCommand.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            message = String.format("%s has resigned.", userGameCommand.getUsername());
            updatedGame.setWinner(ChessGame.Winner.BLACK);
        } else {
            message = String.format("%s has resigned.", userGameCommand.getUsername());
            updatedGame.setWinner(ChessGame.Winner.WHITE);
        }
        gameDAO.updateGames(new GameData(game.gameID(), game.whiteUsername(), game.whiteUsername(), game.gameName(), updatedGame));
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(userGameCommand.getGameID(),null, notification);

        String endGameMessage = String.format("%s team wins!", updatedGame.getWinner().toString());
        transmitGameEnd(endGameMessage, userGameCommand.getGameID());
    }

    private void joinPlayer(JoinPlayer userGameCommand, Session session) throws ResponseException {
        try {
            connections.add(userGameCommand.getGameID(), userGameCommand.getAuthString(), session);

            var message = String.format("%s has joined the game as %s", userGameCommand.getUsername(), userGameCommand.getPlayerColor());
            sendInfoAboutNewParticipant(message, userGameCommand.getGameID(), userGameCommand.getAuthString());
        } catch (IOException | DataAccessException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }

    private void joinObserver(JoinObserver userGameCommand, Session session) throws ResponseException {
        try {
            connections.add(userGameCommand.getGameID(), userGameCommand.getAuthString(), session);

            var message = String.format("%s has started watching the game.", userGameCommand.getUsername());
            sendInfoAboutNewParticipant(message, userGameCommand.getGameID(), userGameCommand.getAuthString());
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
            if (userGameCommand.getPlayerColor() == null) {
                throw new ResponseException(400, "You can't make moves as an observer");
            }
            // Checks the game turn, the piece moving, and the person sending all have the same color
            if ((propGame.getGame().getTeamTurn() != userGameCommand.getPlayerColor()) ||
                    propGame.getGame().getBoard().getPiece(propMove.getStartPosition()).getTeamColor() != userGameCommand.getPlayerColor()) {
                throw new ResponseException(400, "You can't make a move for "+propGame.getGame().getBoard().getPiece(propMove.getStartPosition()).getTeamColor());

            }
            ChessPiece propPieceMoving = propGame.getGame().getBoard().getPiece(propMove.getStartPosition());
            propGame.getGame().makeMove(propMove);

            // Make move in DB
            GameDAO gameDAO = new DBGameDAO();
            gameDAO.updateGames(propGame);

            // send and broadcast (or broadcast with null authtoken)
            GameData game = getGame(userGameCommand.getAuthString(), userGameCommand.getGameID());
            if (!propGame.equals(game)) {
                throw new ResponseException(500, "Update for game failed in DB");
            }

            Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION);
            StringBuilder message = new StringBuilder();
            message.append(userGameCommand.getUsername());
            message.append(" has moved ");
            message.append(propPieceMoving.toString());
            message.append(" from ");
            message.append(propMove.getStartPosition().prettyToString());
            message.append(" to ");
            message.append(propMove.getEndPosition().prettyToString());
            if (propMove.getPromotionPiece() !=  null) {
                message.append("(promoting to ");
                message.append(propMove.getPromotionPiece().toString());
                message.append(")");
            }
            notification.setMessage(message.toString());
            connections.broadcast(game.gameID(), userGameCommand.getAuthString(), notification);

            LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game.getGame());
            connections.broadcast(game.gameID(), null, loadGame);
        } catch (DataAccessException | IOException i) {
            throw new ResponseException(500, i.getMessage());
        } catch (InvalidMoveException e) {
            throw new ResponseException(500, e.getMessage());
        }

    }

    private void leave(UserGameCommand userGameCommandGen) throws IOException, DataAccessException {
        Leave userGameCommand = (Leave) userGameCommandGen;
        connections.remove(userGameCommand.getGameID(), userGameCommand.getAuthString());
        GameDAO gameDAO = new DBGameDAO();
        GameData game = gameDAO.getGame(userGameCommand.getGameID());
        GameData updatedGame = game;
        if (userGameCommand.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.getGame());
        } else if (userGameCommand.getPlayerColor() == ChessGame.TeamColor.BLACK) {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.getGame());
        } else {
            // Observer
        }

        gameDAO.updateGames(updatedGame);
        var message = String.format("%s has left the game.", userGameCommand.getUsername());
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
    private void sendInfoAboutNewParticipant(String broadcastMessage, Integer gameId, String auth) throws IOException, ResponseException, DataAccessException {
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(broadcastMessage);
        connections.broadcast(gameId,auth, notification);

        GameData game = getGame(auth, gameId);
        LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGame.setGame(game.getGame());
        connections.send(game.gameID(), auth, loadGame);
    }

    private void transmitGameEnd(String broadcastMessage, Integer gameId) throws IOException {
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(broadcastMessage);
        connections.broadcast(gameId,null, notification);
    }

    private void checkIfGameOver(String auth,Integer gameId) throws ResponseException, DataAccessException {
        GameData game = getGame(auth, gameId);
        ChessGame chessGame = game.getGame();
        if (chessGame != null) {
            if (chessGame.getWinner() != null) {
                String endGameMessage = String.format("Game has ended. %s team won.", chessGame.getWinner().toString());
                throw new ResponseException(500, endGameMessage);
            }
        }
    }
    // If we get through here, the game is still going
}