package client.websocket;

import chess.ChessGame;
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

    //TODO: Methods for each userGame command
//    public void enterPetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.ENTER, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//    public void getGame(String auth,Integer gameId) throws ResponseException{
//        try {
//            UserGameCommand command = new UserGameCommand(auth, UserGameCommand.CommandType.GET_GAME, null, String.valueOf(gameId));
//            this.session.getBasicRemote().sendText(new Gson().toJson(command));
//        } catch (IOException i) {
//            throw new ResponseException(500, i.getMessage());
//        }
//
//    }

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
}
