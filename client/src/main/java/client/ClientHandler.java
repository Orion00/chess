package client;

import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import exception.ResponseException;
import ui.GameplayUI;
import ui.PostloginUI;
import ui.PreloginUI;

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

    State state;

    @Override
    public void notify(String message) {
        System.out.println(SET_TEXT_COLOR_RED + message);
        printPrompt();
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
                        gameplayUI.setCurrentGameId(currentGameID);
                        handleJoinWebsocket();
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

    private void handleJoinWebsocket() throws ResponseException {
        WebsocketFacade ws = new WebsocketFacade(serverUrl, this);
        gameplayUI.setWebSocketFacade(ws);
    }
}
