package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.GameplayUI;
import ui.PostloginUI;
import ui.PreloginUI;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.Leave;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;

// This creates
public class ClientHandler implements  NotificationHandler{
    private final String serverUrl;
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;
    private String currentAuthToken;
    private Integer currentGameID;
    private ChessGame.TeamColor currentColor;
    private String currentUsername;

    State state;

    @Override
    public void notify(String message) {
        System.out.println(SET_TEXT_COLOR_RED);
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        try {
            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGame userGameCommand = new Gson().fromJson(message, LoadGame.class);
                    gameplayUI.printBoard(userGameCommand.getGame().getBoard());
                }
                case NOTIFICATION -> {
                    Notification userGameCommand = new Gson().fromJson(message, Notification.class);
                    System.out.print(userGameCommand.getMessage());
                } case ERROR -> {
                    Error userGameCommand = new Gson().fromJson(message, Error.class);
                    System.out.print(userGameCommand.getErrorMessage());
                }
            }
        } catch (ResponseException i) {
            System.out.print(i.getMessage());
        } finally {
            System.out.print(RESET_TEXT_COLOR);
            printPrompt();
        }

    }

    private enum State {
        LOGGEDIN, LOGGEDOUT, GAME
    }


    public ClientHandler(String url) {
        serverUrl = url;
        ServerFacade server = new ServerFacade(url);
        preloginUI = new PreloginUI(url, server);
        postloginUI = new PostloginUI(url, server);
        gameplayUI = new GameplayUI(url, server);
        this.state = State.LOGGEDOUT;
        currentAuthToken = null;
        currentGameID = null;
        currentColor = null;
        currentUsername = null;
    }

    public void run() {
        System.out.print("Welcome to your favorite console based chess player\n");
        System.out.print(preloginUI.help());

        // Implement Logic of console
        String result = "";
        Scanner scanner = new Scanner(System.in);
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (state.equals(State.LOGGEDOUT)) {
                    result = preloginUI.eval(line);
                    if (preloginUI.isAuthorized()) {
                        // SWITCH TO LOGGEDIN
                        state = State.LOGGEDIN;
                        currentAuthToken = preloginUI.getAuthToken();
                        postloginUI.setIsAuthorized();
                    }
                } else if (state.equals(State.LOGGEDIN)) {
                    result = postloginUI.eval(currentAuthToken, line);
                    if (!postloginUI.isAuthorized()) {
                        // Switch to LOGGEDOUT
                        state = State.LOGGEDOUT;
                        currentAuthToken = null;
                        preloginUI.resetIsAuthorized();
                    }
                    if (postloginUI.getCurrentGameId() != null) {
                        // Switch to GAME
                        state = State.GAME;
                        currentGameID = postloginUI.getCurrentGameId();
//                        gameplayUI.setCurrentGameId(currentGameID);
//                        gameplayUI.setCurrentPlayerColor(postloginUI.getCurrentColor());
//                        gameplayUI.setCurrentUsername(preloginUI.getCurrentUsername());
                        handleJoinWebsocket(preloginUI.getAuthToken(), postloginUI.getCurrentGameId(), postloginUI.getCurrentColor(), preloginUI.getCurrentUsername());
                        }
                } else if (state.equals(State.GAME)) {
//                    result = gameplayUI.print();
                    result =gameplayUI.eval(currentAuthToken,line);
                    // TODO: Add function to leave;
                }
                if (!Objects.equals(result, "quit")) {
                    System.out.print(result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
            }
            System.out.println();
        System.out.println("Thanks for playing");
    }

    private void printPrompt() {
        System.out.println();
        System.out.print(">>> ");
    }

//    private boolean isAuthorized() {
//        return currentAuthToken != null;
//    }

    private void handleJoinWebsocket(String currentAuthToken, Integer currentGameID, ChessGame.TeamColor currentColor, String currentUsername) throws ResponseException {
        WebsocketFacade ws = new WebsocketFacade(serverUrl, this);
        gameplayUI.setWebSocketFacade(ws);
        ws.joinPlayer(currentAuthToken, currentGameID,currentColor,currentUsername);
    }
}
