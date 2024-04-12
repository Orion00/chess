package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public WebsocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    notificationHandler.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

        public void joinPlayer(String auth,Integer gameId, ChessGame.TeamColor playerColor, String username) throws ResponseException{
        try {
            JoinPlayer command = new JoinPlayer(auth);
            command.setGameID(gameId);
            command.setPlayerColor(playerColor);
            command.setUsername(username);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }
    public void joinObserver(String auth,Integer gameId, String username) throws ResponseException {
        try {
            JoinObserver command = new JoinObserver(auth);

            command.setGameID(gameId);
            command.setUsername(username);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }

    public void makeMove(String auth, Integer gameId, String username, String playerColor, ChessMove move) throws ResponseException {
        try {
            MakeMove command = new MakeMove(auth);
            command.setMove(move);
            command.setGameID(gameId);
            command.setUsername(username);
            command.setPlayerColor(playerColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }

    public void leaveGame(String auth,Integer gameId, ChessGame.TeamColor playerColor, String username) throws ResponseException{
        try {
            Leave command = new Leave(auth);
            command.setGameID(gameId);
            command.setPlayerColor(playerColor);
            command.setUsername(username);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException i) {
            throw new ResponseException(500, i.getMessage());
        }
    }
}
