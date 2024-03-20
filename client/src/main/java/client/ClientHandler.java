package client;

import ui.PostloginUI;
import ui.PreloginUI;

import java.util.Scanner;
import static ui.EscapeSequences.*;

// This creates
public class ClientHandler {
    private final String serverUrl;
    private ServerFacade server;
    private PreloginUI preloginUI;
    private PostloginUI postloginUI;
    private String currentAuthToken;

    State state;

    private enum State {
        LOGGEDIN, LOGGEDOUT, GAME
    }


    public ClientHandler(String url) {
        serverUrl = url;
        server = new ServerFacade(url);
        preloginUI = new PreloginUI(url, server);
        postloginUI = new PostloginUI(url, server);
        this.state = State.LOGGEDOUT;
        currentAuthToken = null;
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
                        state = State.LOGGEDIN;
                        currentAuthToken = preloginUI.getAuthToken();
                    }
                } else if (state.equals(State.LOGGEDIN)) {
                    result = postloginUI.eval(currentAuthToken, line);
                    if (!postloginUI.isAuthorized()) {
                        state = State.LOGGEDOUT;
                        currentAuthToken = null;
                    }
                }
                //TODO: Add Game UI
                if (result != "quit") {
                    System.out.print(SET_BG_COLOR_GREEN + result);
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

    private boolean isAuthorized() {
        return currentAuthToken != null;
    }
}
